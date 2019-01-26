package com.hofl.vo.notations;

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
}
