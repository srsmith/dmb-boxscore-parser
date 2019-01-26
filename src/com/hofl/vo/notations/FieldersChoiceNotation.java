package com.hofl.vo.notations;

public class FieldersChoiceNotation extends AbstractNotation {

    public static final String NOTATION_TYPE = "FieldersChoice";
    
    public FieldersChoiceNotation(String notation) throws Exception {
        super(notation);
        // TODO Auto-generated constructor stub
    }

    public boolean isOfType(String notation) {
        if (notation.indexOf("FC") == 0 || notation.indexOf("/f") > -1)
            return true;
        return false;
    }

    public String getDescription() {
        return "fielder's choice";
    }
    
    public String getNotationType() {
    	return NOTATION_TYPE;
    }
}
