package com.hofl.parser.v2.notations;

import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonProperty;

public class PickoffNotation extends AbstractNotation {
    public static final String NOTATION_TYPE = "Pickoff";
    public static final String[] BASES = {"all your base", "first", "second", "third"};    
    public PickoffNotation(String notation) throws Exception {
        super(notation);
        // TODO Auto-generated constructor stub
    }

    public boolean isOfType(String notation) {
        if (notation.indexOf("PO") == 0) {
            return true;
        }
        return false;
    }

    public String getDescription() {
        return "runner picked off";
    }
    
    public String getDetailedDescription() {
        try {
            int baseIdx = new Integer(notation.substring(2,3)).intValue();
            return getDescription() + " " + BASES[baseIdx];
        }
        catch (Throwable t) {
            return getDescription();
        }
    }
    
    public String getNotationType() {
    	return NOTATION_TYPE;
    }
    
    protected void calculateRBI() {
        this.rbi = 0;
    }
    
    public boolean isRunnerOut() {
    	if (this.notation.indexOf("SB") > -1) {
    		return false;
    	}
        else if (this.notation.indexOf("POE1") > -1 && this.notation.indexOf("1-") > -1) {
            return false;
        }
        else if (this.notation.indexOf("POE2") > -1 && this.notation.indexOf("2-") > -1) {
            return false;
        }
        else if (this.notation.indexOf("POE3") > -1 && this.notation.indexOf("3-") > -1) {
            return false;
        }
    	return true;
    }
    
    public Map<String, String> getPickedOff () {
        Map<String, String> pickedOff = new LinkedHashMap<String, String>();
        if (this.isRunnerOut()) {
            String base = this.notation.substring(2, 3) + "B";
            String playerName = this.getRunnersOnBaseStart().get(base).getPlayerName();
            pickedOff.put(base, playerName);
        }
        return pickedOff;
    }
    
//    public String getEndBase() {
//    	if (this.notation.indexOf("H") > 0) {
//    		return "H";
//    	}
//    	else if (this.notation.indexOf("3") > 0) {
//    		return "3";
//    	}
//    	else if (this.notation.indexOf("2") > 0) {
//    		return "2";
//    	}
//    	else if (this.notation.indexOf("1") > 0) {
//    		return "1";
//    	}
//    	return "";
//    }
}
