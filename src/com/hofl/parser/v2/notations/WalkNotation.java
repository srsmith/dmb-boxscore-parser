package com.hofl.parser.v2.notations;

import org.codehaus.jackson.annotate.JsonProperty;

public class WalkNotation extends AbstractNotation {

    public static final String NOTATION_TYPE = "Walk";
	
    public WalkNotation(String notation) throws Exception {
        super(notation);
        // TODO Auto-generated constructor stub
    }

    public boolean isOfType(String notation) {
    	if (notation.indexOf("IW") == 0)
    		return true;
        else if (notation.indexOf("W") == 0 &&
        			(notation.length() == 1 || 
        			 notation.indexOf(".") == 1 ||
				     notation.indexOf(";") == 1 ||
                     notation.indexOf("+") == 1)) {
        	return true;
        }
        return false;
    }

    public boolean isIntentionalWalk() {
        if (notation.indexOf("IW") == 0)
            return true;
        return false;
    }
    
    public String getDescription() {
        // TODO Auto-generated method stub
        return "walk";
    }

    
    public String getDetailedDescription() {
        if (isIntentionalWalk())
            return "intentional walk";
        else
            return getDescription();
    }
    
    public String getNotationType() {
    	return NOTATION_TYPE;
    }
    
    protected void calculateRBI() {        
        this.rbi = 0;
        if (basesLoaded() && runnerScored(RUNNER_ON_THIRD_SCORED)) {
            this.rbi++;
        }
    }
    
    
    // Make sure that the batter:
    //
    // a) Isn't already on base
    // b) First base isn't already occupied
    // c) And the batter didn't get out already
    //
    // If none of the above occured, trot the batter down to first
    protected void moveRunners() throws Exception {
        super.moveRunners();        

        if (!isPlayerOnBaseEnd(this.getPlayerAtBat()) && this.runnersOnBaseEnd.get("1B").getPlayerName() == ""
                && this.notation.indexOf("Bx") == -1) {
            this.runnersOnBaseEnd.put("1B", new Baserunner(this.getPlayerAtBat(), ""));
        }
    }

    @Override
    protected void populateBreakdown() {
        initBreakdown();
        this.breakdown.put("PA", 1);
        this.breakdown.put("BB", 1);
        this.breakdown.put("IW", isIntentionalWalk() ? 1 : 0);
        this.breakdown.put("RBI", this.getRbi());
    }
    
}
