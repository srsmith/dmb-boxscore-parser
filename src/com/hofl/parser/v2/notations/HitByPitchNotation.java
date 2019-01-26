package com.hofl.parser.v2.notations;

import org.codehaus.jackson.annotate.JsonProperty;

public class HitByPitchNotation extends AbstractNotation {

    public static final String NOTATION_TYPE = "HitByPitch";

    public HitByPitchNotation(String notation) throws Exception {
        super(notation);
        // TODO Auto-generated constructor stub
    }

    public boolean isOfType(String notation) {
        if ((notation.indexOf("HP") == 0) || notation.indexOf("HBP") == 0)
            return true;
        return false;
    }

    public String getDescription() {
        return "hit by pitch";
    }
    
    public String getNotationType() {
    	return NOTATION_TYPE;
    }

    protected void calculateRBI() {
        if (basesLoaded() && runnerScored(RUNNER_ON_THIRD_SCORED)) {
            this.rbi = 1;
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
        this.breakdown.put("AB", 0);
        this.breakdown.put("HBP", 1);
        this.breakdown.put("RBI", this.getRbi());
    }
}
