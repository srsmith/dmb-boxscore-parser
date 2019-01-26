package com.hofl.vo.notations;

public class PinchRunnerNotation extends AbstractNotation {
    public static final String NOTATION_TYPE = "PinchRunner";
    public PinchRunnerNotation(String notation) throws Exception {
        super(notation);
        throw new Exception("Don't use this constructor!!!");
        // TODO Auto-generated constructor stub
    }

    // NOTE: unlike all other notations, the playerName does not refer to the player at bat,
    // but rather the player who is being replaced on base.  You still get that player's
    // name by calling getPlayerAtBat() so that we can conform to the base class, but it is
    // really the player on base.  The notation value is the name of the player who is pinch
    // running for the player on base.
    public PinchRunnerNotation(String notation, int inning, String playerName)
            throws Exception {
        super(notation, inning, playerName);
    }

    public boolean isOfType(String notation) {
        return true;
    }

    public String getNotationType() {
        // TODO Auto-generated method stub
        return NOTATION_TYPE;
    }

    public String getDescription() {
        // TODO Auto-generated method stub
        return notation + " pinch running for " + this.getPlayerAtBat() + ", inning: " + this.getInning();
    }
    
}
