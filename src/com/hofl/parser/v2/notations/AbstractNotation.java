package com.hofl.parser.v2.notations;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public abstract class AbstractNotation {

    String notation;
    private int inning;
    
    private String playerAtBat;
    private Integer playerAtBatBattingOrder;
    private String playerAtBatPosition;
    
    private String playByPlayDescription;
    
    AbstractNotation previousNotation;
    AbstractNotation nextNotation;
    
    protected Map<String, Baserunner> runnersOnBaseStart;
    protected Map<String, Baserunner> runnersOnBaseEnd;
    
    protected Map<String, BaserunnerScored> runnersScored;
    
    protected Map<String, Integer> breakdown;
    protected int rbi = 0;
    
    private int startOuts = 0;
    
    protected static String MOVE_RUNNERS_ORDER[] = {
        "SBH", "CSH", 
        "SB3", "CS3", "PO3", 
        "SB2", "CS2", "PO2", 
        "PO1", 
        "3-H", "3xH", "3x3", 
        "2-H", "2xH", "2-3", "2x3", "2x2", 
        "1-H", "1xH", "1-3", "1x3", "1-2", "1x2", "1x1", 
        "B-H", "BxH", "B-3", "Bx3", "B-2", "Bx2", "B-1", "Bx1"
    };
    protected static String BASERUNNING_OUT_NOTATIONS[] = {
        "3xH", "3x3", 
        "2xH", "2x3", "2x2", 
        "1xH", "1x3", "1x2", "1x1", 
        "BxH", "Bx3", "Bx2", "Bx1", 
        "+CSH", "+CS3", "+CS2"
    };
    
    protected static String RUNNER_ON_THIRD_SCORED = "3-H";
    protected static String RUNNER_ON_SECOND_SCORED = "2-H";
    protected static String RUNNER_ON_FIRST_SCORED = "1-H";
    protected static String SCORING_NOTATIONS[] = {RUNNER_ON_THIRD_SCORED, RUNNER_ON_SECOND_SCORED, RUNNER_ON_FIRST_SCORED};
    
    public AbstractNotation(String notation) throws Exception {
        if (isOfType(notation)) {
            this.notation = notation;
            runnersScored = new LinkedHashMap<String, BaserunnerScored>();
        } else {
            throw new Exception("Notation " + notation + " is not of type " + this.getClass().getName());            
        }
    }
    
    public String getNotationValue() {
    	return this.notation;
    }
    
    public AbstractNotation addChildNotation(String notation) throws Exception {
        AbstractNotation nextNotation = AbstractNotation.getNotation(notation);
        return this.addChildNotation(nextNotation);
    }
    
    public AbstractNotation addChildNotation(AbstractNotation nextNotation) throws Exception {
        this.setNextNotation(nextNotation);
        nextNotation.setPreviousNotation(this);
        return nextNotation;
    }
    
    public AbstractNotation addParentNotation(String notation) throws Exception {
        AbstractNotation nextNotation = AbstractNotation.getNotation(notation);
        return this.addParentNotation(nextNotation);
    }
    
    public AbstractNotation addParentNotation(AbstractNotation prevNotation) throws Exception {
        this.setPreviousNotation(prevNotation);
        prevNotation.setNextNotation(this);
        return prevNotation;
    }    
    
    @JsonIgnore
    public AbstractNotation getFirstNotation() {
        AbstractNotation tmpNote = this;
        while (tmpNote.getPreviousNotation() != null) {
            tmpNote = tmpNote.getPreviousNotation();
        }
        return tmpNote;
    }
    
    @JsonIgnore
    public AbstractNotation getLastNotation() {
        AbstractNotation tmpNote = this;
        while (tmpNote.getNextNotation() != null) {
            tmpNote = tmpNote.getNextNotation();
        }
        return tmpNote;
    }
    
    private void setPreviousNotation(AbstractNotation prevNote) {
        this.previousNotation = prevNote;
    }
    
    private void setNextNotation(AbstractNotation nextNote) {
        this.nextNotation = nextNote;
    }
    
    @JsonIgnore
    public AbstractNotation getNextNotation() {
        if (this.nextNotation != null)
            return this.nextNotation;
        else
            return null;
    }
    
    @JsonIgnore
    public AbstractNotation getPreviousNotation() {
        if (this.previousNotation != null)
            return this.previousNotation;
        else
            return null;
    }
    
    public static final AbstractNotation getNotation(String notation) throws Exception {
        Class[] pArray = new Class[] {String.class};
        Object[] oArray = new Object[] {notation};
        for (int i=0; i < NOTATION_TYPE_CLASS_NAMES.length; i++) {
            try {
                Class c = Class.forName("com.hofl.parser.v2.notations." + NOTATION_TYPE_CLASS_NAMES[i]);
                AbstractNotation aNote = (AbstractNotation)c.getConstructor(pArray).newInstance(oArray);
                //System.out.println("Found notation type " +NOTATION_TYPE_CLASS_NAMES[i]+ " for '" + notation + "'");
                return aNote;
            }
            catch (Throwable t) {
                //System.out.println("Not a " + NOTATION_TYPE_CLASS_NAMES[i] + ": " + t);
            }
        }
        return new PitchingChangeNotation(notation);
//        throw new Exception("Notation does not exist that is described by the notation '" + notation + "'");
    }
    
    public abstract boolean isOfType(String notation);
    
    public abstract String getNotationType();
    
    public abstract String getDescription();
    
    public static final String[] NOTATION_TYPE_CLASS_NAMES = {
        "OutNotation",
        "HitNotation",
        "StrikeoutNotation",
        "WalkNotation",
        "BalkNotation",
        "BaserunningNotation",
        "CatcherInterferenceNotation",
        "ErrantPitchNotation",
        "ErrorNotation",
        "FieldersChoiceNotation",
        "HitByPitchNotation",         
        "PickoffNotation"};

    public int getInning() {
        return inning;
    }

    public void setInning(int inning) {
        this.inning = inning;
    }
    
    public void setPlayerAtBat(String playerAtBat) {
        this.playerAtBat = playerAtBat;
    }
    
    public String getPlayerAtBat() {
        return this.playerAtBat;
    }

    public String getPlayByPlayDescription() {
        return playByPlayDescription;
    }

    public void setPlayByPlayDescription(String playByPlayDescription) {
        this.playByPlayDescription = playByPlayDescription;
    }

    public Map<String, Baserunner>  getRunnersOnBaseStart() {
        return runnersOnBaseStart;
    }
    
    public void setRunnersOnBaseStart(Map<String, Baserunner> runnersOnBase) throws Exception {
  
        if (this.runnersOnBaseStart == null) {
            this.runnersOnBaseStart = new LinkedHashMap<String, Baserunner>();
            this.runnersOnBaseStart.putAll(runnersOnBase);
            this.moveRunners();
        }
    }

    public Map<String, Baserunner> getRunnersOnBaseEnd() {
        return runnersOnBaseEnd;
    }
    
    
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
    public Map<String, BaserunnerScored> getRunnersScored() {
        return runnersScored;
    }
    
    public boolean isPlayerOnBaseStart(String playerName) {
        return getBaserunner(playerName, true) != null;
    }
    

    public boolean isPlayerOnBaseEnd(String playerName) {
        return getBaserunner(playerName, false) != null;
    }
    
    public Baserunner getBaserunnerOnBaseStart(String playerName) {
        return getBaserunner(playerName, true);
    }
    
    public Baserunner getBaserunnerOnBaseEnd(String playerName) {
        return getBaserunner(playerName, false);
    }
    
    private Baserunner getBaserunner(String playerName, boolean start) {
        Map<String, Baserunner> runnersOnBase = start ? this.runnersOnBaseStart : this.runnersOnBaseEnd;
        Iterator iter = runnersOnBase.keySet().iterator();
        while (iter.hasNext()) {
            Baserunner b = runnersOnBase.get(iter.next());
            if (b.getPlayerName().equals(playerName)) {
                return b;
            }
        }
        return null;
    }
    
    public BaserunnerScored getBaserunnerScored(String playerName) {
        Iterator iter = this.runnersScored.keySet().iterator();
        while (iter.hasNext()) {
            BaserunnerScored b = runnersScored.get(iter.next());
            if (b.getPlayerName().equals(playerName)) {
                return b;
            }
        }
        return null;
    }
    
    /**
     * For notations that have a hitter moving to a base, they should extend
     * this class, call super(), then implement their own moving.
     */
    protected void moveRunners() throws Exception {
        StringTokenizer tok = new StringTokenizer(this.notation, ";");
        Map<String, Baserunner> runners = new LinkedHashMap<String, Baserunner>(); 
        runners.putAll(this.runnersOnBaseStart);

        String event;
        while (tok.hasMoreTokens()) {
            Baserunner runner = null;
            String startBase = null;
            String endBase = null;
            event = tok.nextToken().trim();
            if (event.indexOf("-") > -1) {
                startBase = event.substring(event.indexOf("-")-1, event.indexOf("-"));
                endBase = event.substring(event.indexOf("-")+1,event.indexOf("-")+2);   
            } else if (event.indexOf("x") > -1) {
                startBase = event.substring(event.indexOf("x")-1, event.indexOf("x"));
            } else if (event.indexOf("SB") > -1 || event.indexOf("CS") >-1 || event.indexOf("PO") > -1) {
                if (event.substring(event.length()-1, event.length()).equals("H")) {
                    startBase = "3";
                    endBase = "H";
                } else {
                    int tmpInt = -1;
                    if (event.indexOf("H") > -1) {
                        tmpInt = 2;
                    } else if (event.indexOf("SB") > -1) {
                        endBase = event.substring(event.indexOf("SB")+2, event.indexOf("SB")+3);
                        tmpInt = Integer.parseInt(endBase)-1;
                    } else if(event.indexOf("CS") > -1) {
                        tmpInt = Integer.parseInt(event.substring(event.indexOf("CS")+2, event.indexOf("CS")+3))-1;
                    } else if(event.indexOf("PO") > -1 && event.indexOf("CS") == -1) {
                        tmpInt = Integer.parseInt(event.substring(event.indexOf("PO")+2, event.indexOf("PO")+3));
                    }
                    startBase = new Integer(tmpInt).toString();
                }
            }
            
            //System.out.println("Moving notation: " + event + " startBase: " + startBase + "  endBase: " + endBase);
            if (endBase != null) {
                
                runner = startBase.equals("B") ? new Baserunner(this.playerAtBat, "") : this.runnersOnBaseStart.get(startBase+"B");
                if (endBase.equals("H")) {
                    if (runnersScored.containsValue(runner)) {
                        throw new Exception("You can't score twice in one play dummy: " + runner.getPlayerName());
                    }
                    runnersScored.put(startBase.equals("B") ? "BA" : startBase + "B", new BaserunnerScored(runner));
                    
                } else {
                    runners.put(endBase + "B", runner);
                }
            }
            if (startBase != null && !startBase.equals("B")) {
                runners.put(startBase + "B", new Baserunner("",""));
            } 
        }
        
        if (this.runnersOnBaseEnd == null) {
            this.runnersOnBaseEnd = new LinkedHashMap<String, Baserunner>();
        }
        this.runnersOnBaseEnd.putAll(runners);
        calculateRBI();
    }
    
    protected abstract void calculateRBI();
    
    //protected abstract void assignPitcherResponsibility();
    
    protected boolean runnerScored(String runnerScoredNotation) {
        String noteValue = this.getNotationValue();
        if (noteValue.indexOf(runnerScoredNotation) > -1) {
            return true;
        }
        return false;
    }

    protected boolean runnerScoredOnError(String runnerScoredNotation) {
        String noteValue = this.getNotationValue();
        if (noteValue.indexOf(runnerScoredNotation + "(") > -1) {
            return true;
        }
        return false;
    }
    
    protected boolean basesLoaded() {
        Iterator<Baserunner> iter = this.runnersOnBaseStart.values().iterator();
        while (iter.hasNext()) {
            if (iter.next() == null) {
                return false;
            }
        }
        return true;
    }
    
    protected boolean batterAdvancedExtraBase() {
        if (notation.indexOf("B-") > -1) {
            return true;
        }
        return false;
    }
    
    protected boolean batterAdvancedOnOutfieldFieldingErrror() {
        Pattern pattern = Pattern.compile("B-[2-3]\\(e[7-9]\\)");
        if (pattern.matcher(notation).find()) {
            return true;
        }
        return false;
    }
    protected boolean batterAdvancedOnThrowingErrror() {
        Pattern pattern = Pattern.compile("B-[1-3]\\(e[1-9]/th\\)");
        if (pattern.matcher(notation).find()) {
            return true;
        }
        return false;
    }
    
    @JsonIgnore
    public int getStartOuts() {
        return startOuts;
    }

    public void setStartOuts(int startOuts) throws Exception {
        if (startOuts < 0) {
            throw new Exception("startOuts cannot be less than zero!");
        }
                
        this.startOuts = startOuts;
    }

    public int getRbi() {
        return rbi;
    }
    
    protected String[] BREAKDOWN_CATEGORIES = {"PA", "AB", "SI", "DB", "TR", "HR", "BB", "IW", "SO", "HBP", "SH", "SF", "RBI"};
    
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
    public Map<String, Integer> getBreakdown() {
        
        breakdown = new LinkedHashMap<String, Integer>();
        populateBreakdown();
        return breakdown;
    }
    
    protected void populateBreakdown() {
        // left null intentionally
    }
    
    protected void initBreakdown() {
        for (int i=0; i < BREAKDOWN_CATEGORIES.length; i++) {
            breakdown.put(BREAKDOWN_CATEGORIES[i], 0);
        }
    }

    @JsonIgnore
    public Integer getPlayerAtBatBattingOrder() {
        return playerAtBatBattingOrder;
    }

    public void setPlayerAtBatBattingOrder(Integer playerAtBatBattingOrder) {
        this.playerAtBatBattingOrder = playerAtBatBattingOrder;
    }

    @JsonIgnore
    public String getPlayerAtBatPosition() {
        return playerAtBatPosition;
    }

    public void setPlayerAtBatPosition(String playerAtBatPosition) {
        this.playerAtBatPosition = playerAtBatPosition;
    }

    public static class Baserunner {

        private String playerName;
        private String pitcherResponsible;
        protected boolean earned;
           
        
        public Baserunner() {
            
        }
        
        public Baserunner(String playerName, String pitcherResponsible) {
            setPlayerName(playerName);
            setPitcherResponsible(pitcherResponsible);
            setEarned(true);
        }
        
        public Baserunner(String playerName, String pitcherResponsible, boolean earned) {
            this(playerName, pitcherResponsible);
            setEarned(earned);
        }
        
        public String getPlayerName() {
            return playerName;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        @JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
        public String getPitcherResponsible() {
            return pitcherResponsible;
        }

        public void setPitcherResponsible(String pitcherResponsible) {
            this.pitcherResponsible = pitcherResponsible;
        }

        public void setEarned(boolean earned) {
            if (!this.earned && earned) {
                // throw new RuntimeException("You cannot change a runner from unearned to earned!");
            }
            this.earned = earned;
        }
        
        @JsonIgnore
        public boolean isEarned() {
            return this.earned;
        }
    }
    
    public static class BaserunnerScored extends Baserunner {
        public BaserunnerScored(String playerName, String pitcherResponsible, boolean earned) {
            super(playerName, pitcherResponsible, earned);
        }

        private BaserunnerScored(Baserunner runner) {
            this(runner.getPlayerName(), runner.getPitcherResponsible(), runner.isEarned());
        }
        public boolean getEarnedRun() {
            return this.earned;
        }
    }
}
