package com.hofl.parser.v2.notations;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class OutNotation extends AbstractNotation {

    public static final String NOTATION_TYPE = "Out";

    private Map<String, String> fielders;
    
    @JsonIgnore
    public static final Map<String, String> NUMBERS_BASE_MAP = createNumberBaseMap();

    @JsonIgnore
    public static Map<String, String> createNumberBaseMap() {
        Map<String, String> tmpMap = new LinkedHashMap<String, String>();
        tmpMap.put("1", "P");
        tmpMap.put("2", "C");
        tmpMap.put("3", "1B");
        tmpMap.put("4", "2B");
        tmpMap.put("5", "3B");
        tmpMap.put("6", "SS");
        tmpMap.put("7", "LF");
        tmpMap.put("8", "CF");
        tmpMap.put("9", "RF");
        return Collections.unmodifiableMap(tmpMap);
    }    
    
    @JsonIgnore
    public static final Map<String, String> OUT_TYPES = createOutTypesMap();

    @JsonIgnore
    public static Map<String, String> createOutTypesMap() {
        Map<String, String> tmpMap = new LinkedHashMap<String, String>();
        tmpMap.put("fl", "popped");
        tmpMap.put("f", "forced");
        tmpMap.put("g", "grounded");
        tmpMap.put("l", "lined");
        return Collections.unmodifiableMap(tmpMap);
    }
    private OutDetails outDetails;
            
    
    public OutNotation(String notation) throws Exception {
        super(notation);
    }
    
    public boolean isOfType(String notation) {
        try {
            Integer testInteger = new Integer(notation.substring(0,1));
            for (int i=0; i < ERROR_POSSIBLES.length; i++) {
                if (notation.indexOf(ERROR_POSSIBLES[i]) > -1)
                    return false;
            }
            //            if (notation.indexOf("/f") == -1)  // force out, runner not out
                return true;
//            return false;
        }
        catch (Throwable t) {
            // System.out.println("Throwable caught in isOfType()" + t);
            return false;
        }
    }
    
    protected void calculateRBI() {
        this.rbi = 0;
        if (runnerScored(RUNNER_ON_THIRD_SCORED) && getStartOuts() < 2 && !isDoublePlay() && !throwingErrorOnInfielder()) {
            this.rbi++;  // No RBI on outs made with 2 outs (duh) or on double plays.
        }
    }

     public boolean isDoublePlay() {
        if (notation.indexOf("gdp") > -1) {
            return true;
        }
        return false;
    }
    
    public boolean throwingErrorOnInfielder() {
        if (notation.indexOf("/th") > -1 && !ErrorNotation.isOutfieldError(notation)) {
            return true;
        }
        return false;
    }   
    
    public static final String[] ERROR_POSSIBLES = {"e1.","e2.","e3.","e4.","e5.","e6.","e7.","e8.","e9."};
    
    public String getDescription() {
        return "Out recorded";
    }
    
    public String getNotationType() {
    	return NOTATION_TYPE;
    }
    
    protected void moveRunners() throws Exception {
        super.moveRunners();

        AbstractNotation nextNote = this.getNextNotation();
        
        if (nextNote != null && nextNote.getInning() != this.getInning() || notation.indexOf("tp") > -1) {
            runnersOnBaseEnd.put("1B", new Baserunner("", ""));
            runnersOnBaseEnd.put("2B", new Baserunner("", ""));
            runnersOnBaseEnd.put("3B", new Baserunner("", ""));
        } else if (notation.indexOf("dp") > -1 || notation.indexOf("/f") > 1) {
            // Figure out which runner to erase.
            String fielderSequence = notation.substring(0, notation.indexOf("/"));
            int seqLen = fielderSequence.length();
            String lastFielder = fielderSequence.substring(seqLen-1, seqLen);
            if (Integer.parseInt(lastFielder) == 3 && fielderSequence.length() > 1) {
                // Means the last out of the double play was the batter - don't need to remove him
                // Get the previous fielder to determine removing the batter
                //System.out.print(">> notation: " + notation + ", fielderSequence: = " + fielderSequence);
                lastFielder = fielderSequence.substring(seqLen-2, seqLen-1);
            }
            
            
            if (Integer.parseInt(lastFielder) == 2) {
                Baserunner runnerOnBaseBefore = runnersOnBaseStart.get("3B");
                Baserunner runnerOnBaseEnd = runnersOnBaseEnd.get("3B");   
                if (runnerOnBaseEnd.equals(runnerOnBaseBefore)) {                 
                    runnersOnBaseEnd.put("3B", new Baserunner("", "")); // Remove runner on 3rd - catcher made the play
                }
            } else if (Integer.parseInt(lastFielder) == 5) {
                Baserunner runnerOnBaseBefore = runnersOnBaseStart.get("2B");
                Baserunner runnerOnBaseEnd = runnersOnBaseEnd.get("2B");
                if (runnerOnBaseEnd.equals(runnerOnBaseBefore)) {
                    runnersOnBaseEnd.put("2B", new Baserunner("", "")); // Remove the runner on 2nd - 3B made the play
                }
            } else if (Integer.parseInt(lastFielder) == 4 || Integer.parseInt(lastFielder) == 6) {
                Baserunner runnerOnBaseBefore = runnersOnBaseStart.get("1B");
                Baserunner runnerOnBaseEnd = runnersOnBaseEnd.get("1B");
                if (runnerOnBaseEnd.equals(runnerOnBaseBefore)) {
                    runnersOnBaseEnd.put("1B", new Baserunner("", "")); // Remove the runner on 1st - 2B/SS made the play
                }  
            }
            
                    
        }
        
    }

    public void setFielders(Map<String, String> fielders) {
        this.fielders = fielders;
    }
    
    @Override
    protected void populateBreakdown() {
        initBreakdown();
        this.breakdown.put("PA", 1);
        if (this.getNotationValue().indexOf("SH") > -1) {
            this.breakdown.put("AB", 0);
            this.breakdown.put("SH", 1);
        } else if (this.getNotationValue().indexOf("SF") > -1) {
            this.breakdown.put("AB", 0);
            this.breakdown.put("SF", 1);    
        } else {
            this.breakdown.put("AB", 1);
        }
        this.breakdown.put("RBI", this.getRbi());
    }
    
    public OutDetails getOutDetails() {
        if (this.outDetails == null) {
            this.outDetails = new OutDetails();
            for (int i = 0; i < notation.length(); i++) {
                String position = NUMBERS_BASE_MAP.get(notation.substring(i, i+1));
                if (position == null) {
                    break;
                }
                String fielderName = (this.fielders != null && this.fielders.get(position) != null) ? this.fielders.get(position) : "";
                outDetails.addFielder(fielderName, position);
            }

            String outType = this.getPlayByPlayDescription().substring(this.getPlayerAtBat().length()+1);
            outType = outType.substring(0, outType.indexOf("out ")+3).trim();
            outDetails.setOutType(outType);
                
        }
        return this.outDetails;
    }
    
    private class OutDetails {
        
        private Map<String, String> fielderSequence;
        private String outType;
        
        public OutDetails() {
            this.fielderSequence = new LinkedHashMap<String, String>();
        }
        
        public OutDetails(String outType) {
            this();
            this.setOutType(outType);
        }
        
        public void addFielder(String playerName, String position) {
            this.fielderSequence.put(position, playerName);
        }
        
        public Map<String, String> getFielderSequence() {
            return this.fielderSequence;
        }
        
        public String getOutType() {
            return outType;
        }
        
        public void setOutType(String outType) {
            this.outType = outType;
        }
    }
    
}
