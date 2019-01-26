package com.hofl.vo.notations;

public class BalkNotation extends AbstractNotation {

    public static final String NOTATION_TYPE = "Balk";
	
    public BalkNotation(String notation) throws Exception {
        super(notation);
        // TODO Auto-generated constructor stub
    }

    public boolean isOfType(String notation) {
        if (notation.toUpperCase().indexOf("BK") == 0)
            return true;
        return false;
    }

    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public String getNotationType() {
    	return NOTATION_TYPE;
    }

}
