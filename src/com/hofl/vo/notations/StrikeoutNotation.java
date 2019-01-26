package com.hofl.vo.notations;

public class StrikeoutNotation extends AbstractNotation {

	public static final String NOTATION_TYPE = "Strikeout";
	
    public StrikeoutNotation(String notation) throws Exception {
        super(notation);
        // TODO Auto-generated constructor stub
    }

    public boolean isOfType(String notation) {
        if (notation.indexOf("k") == 0)
            return true;
        return false;
    }

    public String getDescription() {
        return "strikeout";
    }
    
    public String getNotationType() {
    	return NOTATION_TYPE;
    }
}
