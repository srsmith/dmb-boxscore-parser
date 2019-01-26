package com.hofl.parser.v2.notations;

import org.codehaus.jackson.annotate.JsonProperty;

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

    protected void calculateRBI() {
        this.rbi = 0;
        if (basesLoaded() && runnerScored(RUNNER_ON_THIRD_SCORED)) {
            this.rbi++;
        }
    }
    
    @Override
    protected void populateBreakdown() {
        initBreakdown();
        this.breakdown.put("PA", 0);
        this.breakdown.put("AB", 0);
        this.breakdown.put("RBI", this.getRbi());
    }
    
}
