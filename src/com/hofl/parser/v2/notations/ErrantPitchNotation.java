package com.hofl.parser.v2.notations;

import org.codehaus.jackson.annotate.JsonProperty;

public class ErrantPitchNotation extends AbstractNotation {

    public static final String NOTATION_TYPE = "ErrantPitch";
    
    public ErrantPitchNotation(String notation) throws Exception {
        super(notation);
        //System.out.println("Found an ErrantPitch " + notation);
    }

    public boolean isOfType(String notation) {
        if (isWildPitch(notation) || isPassedBall(notation))
            return true;
        return false;
    }

    public static boolean isWildPitch(String notation) {
    	if (notation.trim().toUpperCase().indexOf("WP") == 0)
    		return true;
    	return false;
    }
    
    public static boolean isPassedBall(String notation) {
        if (notation.trim().toUpperCase().indexOf("PB") == 0)
        	return true;
        return false;
    }

    public String getDescription() {
      if (isWildPitch(this.notation))
          return "wild pitch";
      else if (isPassedBall(this.notation))
          return "passed ball";
      else 
          return "passed wild gas";
    }
    
    public String getNotationType() {
    	return NOTATION_TYPE;
    }
    
    protected  void calculateRBI() {
        this.rbi = 0;
    }

    /**
     * Diamond Mind Baseball charges a run that scores on a passed ball as unearned,
     * unlike official MLB scoring (rule 9.16), which treats passed balls the same as
     * wild pitches -- wild-pitch-scored runs are unaffected here.
     */
    @Override
    protected boolean runsScoredAreUnearned() {
        return isPassedBall(this.notation);
    }
}
