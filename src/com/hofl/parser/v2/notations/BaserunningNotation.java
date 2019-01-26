package com.hofl.parser.v2.notations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public class BaserunningNotation extends AbstractNotation {

    public static final String NOTATION_TYPE = "Baserunning";
    
    
    @JsonIgnore
    public static final Map<String, String> STOLEN_BASE_MAP = createStolenBaseMap();
    
    @JsonIgnore
    public static Map<String, String> createStolenBaseMap() {
        Map<String, String> tmpMap = new LinkedHashMap<String, String>();
        tmpMap.put("1", "First base");
        tmpMap.put("2", "Second base");
        tmpMap.put("3", "Third base");
        tmpMap.put("H", "Home");
        return Collections.unmodifiableMap(tmpMap);
    }
    
    private List<StolenBase> stolenBases;
    private List<CaughtStealing> caughtStealing;
    
    public static void main (String[] args) {
        System.out.println("SB2 isStolenBase() ? " + BaserunningNotation.isStolenBase("SB2"));
        System.out.println("SB2;SB3 isStolenBase() ? " + BaserunningNotation.isStolenBase("SB2;SB3"));
        System.out.println("CS3(25) isCaughtStealing() ? " + BaserunningNotation.isCaughtStealing("CS3(25)"));
    }
    
    public BaserunningNotation(String notation) throws Exception {
        super(notation);
    }
    
    private void parseNotation() {
        String subNotation = "";
        try {
            stolenBases = new ArrayList<StolenBase>();
            caughtStealing = new ArrayList<CaughtStealing>();

            String baserunning = notation.indexOf(".") > -1 ? notation.substring(0, notation.indexOf(".")) : notation;
            StringTokenizer tok = new StringTokenizer(baserunning, ";");
            Map<String, Baserunner> baserunners = this.getRunnersOnBaseStart();
            while (tok.hasMoreTokens()) {
                String targetBaseKey;
                subNotation = tok.nextToken().trim();
                if (isStolenBase(subNotation)) {
                    targetBaseKey = subNotation.substring(subNotation.indexOf("B") + 1, subNotation.indexOf("B") + 2);
                    int startBaseKey = targetBaseKey.equals("H") ? 3 : new Integer(targetBaseKey) - 1;
                    
                    String startBase = STOLEN_BASE_MAP.get("" + startBaseKey);
                    String targetBase = STOLEN_BASE_MAP.get(targetBaseKey);
                    String baserunner = baserunners.get(startBaseKey + "B").getPlayerName();
                    
                    stolenBases.add(new StolenBase(startBase, targetBase, baserunner));
                    
                } else if (isCaughtStealing(subNotation)) {
                    targetBaseKey = subNotation.substring(subNotation.indexOf("S") + 1, subNotation.indexOf("S") + 2);                    
                    int startBaseKey = targetBaseKey.equals("H") ? 3 : new Integer(targetBaseKey) - 1;
                    
                    String startBase = STOLEN_BASE_MAP.get("" + startBaseKey);
                    String targetBase = STOLEN_BASE_MAP.get(targetBaseKey);
                    String baserunner = baserunners.get(startBaseKey + "B").getPlayerName();
                    
                   caughtStealing.add(new CaughtStealing(startBase, targetBase, baserunner));
                   
                }

            }
        }
        catch (Throwable t) {
            System.out.println("Woah I caught a throwable in BaserunningNotation.parseNotation()!  Notation: " + subNotation);
            t.printStackTrace(System.out);
        }
    }

    public boolean isOfType(String notation) {
        if (this.isStolenBase(notation) || 
                this.isCaughtStealing(notation) || 
                this.isOtherBaserunningEvent(notation)) {
            return true;
        }
        return false;
    }

    public String getDescription() {
        StringBuffer description = new StringBuffer();
        
        String baserunning = notation.indexOf(".") > -1 ? notation.substring(0, notation.indexOf(".")) : notation;
        StringTokenizer tok = new StringTokenizer(baserunning, ";");
        //Map<String, Baserunner> baserunners = this.getRunnersOnBaseStart();
        
        while (tok.hasMoreTokens()) {
            String subNotation = tok.nextToken().trim();
            if (description.length() > 0) {
                description.append("; ");
            }
            
            if (isStolenBase(subNotation)) {
                description.append("Stolen base");
            }
            else if (isCaughtStealing(subNotation)) {
                description.append("Caught stealing");
            }
            else if (isOtherBaserunningEvent(subNotation)) {
                String playByPlay = this.getPlayByPlayDescription();
                if (playByPlay.indexOf("defensive indifference") > -1) {
                    description.append("Advance on defensive indifference");
                } else if (playByPlay.indexOf("attempting to advance") > -1) {
                    description.append("Attempt to advance on play");
                } else {
                    description.append("Uncategorized baserunning event");             
                }
            }
            else {
                description.append("Stole your girl");
            }    
        }
        
        
        return description.toString();
    }

    public String getDetailedDescription() {
        return this.getPlayByPlayDescription();
    }
    
    public static boolean isStolenBase(String notation) {
        if(notation.indexOf("SB") > -1)
            return true;
        return false;
    }
 
    public static boolean isCaughtStealing(String notation) {
        if(notation.indexOf("CS") > -1)
            return true;
        return false; 
    }
   
    public static boolean isOtherBaserunningEvent(String notation) {
        if(notation.indexOf("OA") > -1 || notation.indexOf("k+pb") > -1  
                || notation.indexOf("k+wp") > -1)
            return true;
        return false; 
    }
    
    
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
    public List<StolenBase> getStolenBases() {
        if (this.stolenBases == null) {        
            parseNotation();
        }
        return this.stolenBases;
    }
    
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
    public List<CaughtStealing> getCaughtStealing() {
        if (this.caughtStealing == null) {        
            parseNotation();
        }
        return this.caughtStealing;
    }
    
    public String getNotationType() {
    	return NOTATION_TYPE;
    }
    
    protected  void calculateRBI() {
        this.rbi = 0;
    }
    
    protected class StolenBase {
        private String startBase;
        private String stolenBase;
        private String baserunner;
        
        private StolenBase() {
            
        }
        
        public StolenBase(String startBase, String stolenBase, String baserunner) {
            this.startBase = startBase;
            this.stolenBase = stolenBase;
            this.baserunner = baserunner;
        }

        public String getStartBase() {
            return startBase;
        }

        public String getStolenBase() {
            return stolenBase;
        }

        public String getBaserunner() {
            return baserunner;
        }
                
    }

    protected class CaughtStealing {
        private String startBase;
        private String targetBase;
        private String baserunner;
        
        private CaughtStealing() {
            
        }
        
        public CaughtStealing(String startBase, String targetBase, String baserunner) {
            this.startBase = startBase;
            this.targetBase = targetBase;
            this.baserunner = baserunner;
        }

        public String getStartBase() {
            return startBase;
        }

        public String getTargetBase() {
            return targetBase;
        }

        public String getBaserunner() {
            return baserunner;
        }
                
    }
    
    
    
}
