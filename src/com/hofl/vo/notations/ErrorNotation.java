package com.hofl.vo.notations;

public class ErrorNotation extends AbstractNotation {
    public static final String[] ERROR_POSSIBLES = {"e1","e2","e3","e4","e5","e6","e7","e8","e9"};
    public static final String NOTATION_TYPE = "Error";
    
    public ErrorNotation(String notation) throws Exception {
        super(notation);
        // TODO Auto-generated constructor stub
    }

    public boolean isOfType(String notation) {
        return noteContainsError(notation);
    }

    public static boolean noteContainsError(String notation) {
        for (int i=0; i < ERROR_POSSIBLES.length; i++) {
            if (notation.indexOf(ERROR_POSSIBLES[i]) > -1) {
                return true;
            }
        }
        return false;
    }

    public String getDescription() {
        return "error";
    }
    
    public String getDetailedDescription() {
        try {
            int position = new Integer(notation.substring(1,2)).intValue();
            return "error on the " + OutNotation.POSITIONS[position];
        }
        catch (Throwable t) {
            return getDescription();
        }
    }

    public String getNotationType() {
    	return NOTATION_TYPE;
    }
}
