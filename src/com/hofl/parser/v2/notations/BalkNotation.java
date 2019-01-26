package com.hofl.parser.v2.notations;

import org.codehaus.jackson.annotate.JsonProperty;

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
        return "Pitcher balked";
    }
    
    public String getNotationType() {
    	return NOTATION_TYPE;
    }
    
    protected  void calculateRBI() {
        this.rbi = 0;
    }

}
