package com.hofl.parser.v2.notations;

import org.codehaus.jackson.annotate.JsonProperty;

public class FieldersChoiceNotation extends OutNotation {

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
    
    protected void calculateRBI() {
        this.rbi = 0;
        if (runnerScored(RUNNER_ON_THIRD_SCORED) && getStartOuts() < 2 && !isDoublePlay() && !throwingErrorOnInfielder()) {
            this.rbi++;  // No RBI on outs made with 2 outs (duh) or on double plays.
        }
    }    
    
    @Override
    protected void populateBreakdown() {
        initBreakdown();
        this.breakdown.put("PA", 1);
        this.breakdown.put("AB", 1);
        this.breakdown.put("RBI", this.getRbi());
    }    
}
