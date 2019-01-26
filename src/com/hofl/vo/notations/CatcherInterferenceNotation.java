package com.hofl.vo.notations;

public class CatcherInterferenceNotation extends AbstractNotation {

    public static final String NOTATION_TYPE = "CatcherInterference";
    
    public CatcherInterferenceNotation(String notation) throws Exception {
        super(notation);
        // TODO Auto-generated constructor stub
    }

    public boolean isOfType(String notation) {
         return notation.indexOf("CI") == 0;
    }

    public String getDescription() {
        return "catcher interference";
    }

    public String getNotationType() {
    	return NOTATION_TYPE;
    }
}
