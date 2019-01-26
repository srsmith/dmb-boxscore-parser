package com.hofl.parser.v2.notations;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class ErrorNotation extends AbstractNotation {
    
    public static final String NOTATION_TYPE = "Error";
    
    @JsonIgnore
    public static final Map<String, String> ERRORS_MAP = createErrorMap();
    
    @JsonIgnore
    public static Map<String, String> createErrorMap() {
        Map<String, String> tmpMap = new LinkedHashMap<String, String>();
        tmpMap.put("e1", "P");
        tmpMap.put("e2", "C");
        tmpMap.put("e3", "1B");
        tmpMap.put("e4", "2B");
        tmpMap.put("e5", "3B");
        tmpMap.put("e6", "SS");
        tmpMap.put("e7", "LF");
        tmpMap.put("e8", "CF");
        tmpMap.put("e9", "RF");
        return Collections.unmodifiableMap(tmpMap);
    }
    private String errorPosition;
    private ErrorDetails errorDetails;
    
    private Map<String, String> fielders;
    
    public ErrorNotation(String notation) throws Exception {
        super(notation);
        parseNotation();
    }

    private void parseNotation() {
        Iterator<String> iter = ERRORS_MAP.keySet().iterator();
        while (iter.hasNext()) {
            String errorKey = iter.next();
            if (notation.indexOf(errorKey) > -1) {
                this.errorPosition = ERRORS_MAP.get(errorKey);
                break;
            }
        }
    }
    
    public boolean isOfType(String notation) {
        return noteContainsError(notation);
    }

    protected void calculateRBI() {
        this.rbi = 0;
        int outs = this.getStartOuts();
        Pattern basesLoaded = Pattern.compile("e[3-5].3-H;2-3;1-2;B-1");
        if (runnerScored(RUNNER_ON_THIRD_SCORED) && outs < 2) {
            // This MAY result in an RBI, but we need to check some more things first
            if (notation.indexOf("SF") > -1) {
                this.rbi = 1;  // SF would've scored the runner anyways
            } else {
                String[] validRBIErrors = {"e1","e3","e4","e5","e6"};
                for (int i=0; i < validRBIErrors.length; i++) {
                    boolean basesAreLoaded = basesLoaded.matcher(notation).find();
                    if (notation.indexOf(validRBIErrors[i] + ".3-H") > -1 && !basesAreLoaded) {
                        // Don't count RBIs on errors on the infield when the bases are loaded?
                        this.rbi = 1;
                    } else if (basesAreLoaded && outs == 0) {
                        this.rbi = 1;
                    }
                }
            }
        }
    }
    
    public static boolean noteContainsError(String notation) {
        Iterator<String> iter = ERRORS_MAP.keySet().iterator();
        while (iter.hasNext()) {
            if (notation.indexOf(iter.next()) > -1) {
                return true;
            }
        }
        return false;
    }

    public String getDescription() {
        return "Error";
    }
    
    public String getDetailedDescription() {
        return "Error on the " + errorPosition;
    }

    public String getNotationType() {
    	return NOTATION_TYPE;
    }
    
    public ErrorDetails getErrorDetails() {
        if (this.errorDetails == null) {
            String fielderName = (this.fielders != null && this.fielders.get(errorPosition) != null) ? this.fielders.get(errorPosition) : "";
            this.errorDetails = new ErrorDetails(errorPosition, fielderName, notation.indexOf("/th") > -1 ? "Throwing" : "Fielding");
        }
        return this.errorDetails;
    }    

    public static boolean isOutfieldError(String notation) {
        if (notation.indexOf("e7") > -1 || notation.indexOf("e8") > -1 || notation.indexOf("e9") > -1) {
            return true;
        }
        return false;
    }
    
    public void setFielders(Map<String, String> fielders) {
        this.fielders = fielders;
    }

    @Override
    protected void populateBreakdown() {
        initBreakdown();
        this.breakdown.put("PA", 1);
        // If error occured on an attempted sacrifice, it's not an AB
        if (this.getNotationValue().indexOf("SH") > -1) {
            this.breakdown.put("AB", 0);
            this.breakdown.put("SH", 1);
        } else if (this.getNotationValue().indexOf("SF") > -1) {
            this.breakdown.put("AB", 0);
            this.breakdown.put("SF", 1);    
        } else {
            this.breakdown.put("AB", 1);
        }
        this.breakdown.put("RBI", this.getRbi());
    }    
    
    public class ErrorDetails {
        
        private String position;
        private String fielderName;
        private String errorType;
        
        private ErrorDetails() {
            
        }
        
        public ErrorDetails(String position, String fielderName, String errorType) {
            this.position = position;
            this.fielderName = fielderName;
            this.errorType = errorType;
        }

        public String getPosition() {
            return position;
        }

        public String getFielderName() {
            return fielderName;
        }
        
        public String getErrorType() {
            return errorType;
        }
    }
}
