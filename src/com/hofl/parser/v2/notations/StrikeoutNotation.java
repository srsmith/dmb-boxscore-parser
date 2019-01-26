package com.hofl.parser.v2.notations;

import com.hofl.parser.v2.notations.BaserunningNotation.CaughtStealing;
import com.hofl.parser.v2.notations.BaserunningNotation.StolenBase;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;


public class StrikeoutNotation extends AbstractNotation {

	public static final String NOTATION_TYPE = "Strikeout";
    private BaserunningNotation baserunningNote;
	
    public StrikeoutNotation(String notation) throws Exception {
        super(notation);
        parseNotation();
    }
    
    private void parseNotation() {
        if (notation.indexOf("+") > -1) {
            String subNotation = notation.substring(notation.indexOf("+")+1);
            try {
                baserunningNote = new BaserunningNotation(subNotation);
            }
            catch (Throwable t) {
                System.out.println("Index of a + caught but init of the BaserunningNotation failed for subNotation: " + subNotation);
            }
        }
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

    @JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
    public List<StolenBase> getStolenBases() throws Exception {
        if (baserunningNote != null) {
            baserunningNote.setRunnersOnBaseStart(this.getRunnersOnBaseStart());
            return baserunningNote.getStolenBases();
        }
        return new ArrayList<StolenBase>();
    }
    
    @JsonSerialize(include=JsonSerialize.Inclusion.NON_EMPTY)
    public List<CaughtStealing> getCaughtStealing() throws Exception {
        if (baserunningNote != null) {        
            baserunningNote.setRunnersOnBaseStart(this.getRunnersOnBaseStart());
            return baserunningNote.getCaughtStealing();
        }
        return new ArrayList<CaughtStealing>();
    }    
    

    protected  void calculateRBI() {
        this.rbi = 0;
    }
    
    @Override
    protected void populateBreakdown() {
        initBreakdown();
        this.breakdown.put("PA", 1);
        this.breakdown.put("AB", 1);
        this.breakdown.put("SO", 1);
        this.breakdown.put("RBI", this.getRbi());
    }    
    
}
