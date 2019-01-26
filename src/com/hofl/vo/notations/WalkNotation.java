package com.hofl.vo.notations;

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
}
