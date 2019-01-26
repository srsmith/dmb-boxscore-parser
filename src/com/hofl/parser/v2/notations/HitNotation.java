package com.hofl.parser.v2.notations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class HitNotation extends AbstractNotation {

    public static final String NOTATION_TYPE = "Hit";

    @JsonIgnore
    public static final Map<String, String> HITS_MAP = createHitsMap();
    
    @JsonIgnore
    public static Map<String, String> createHitsMap() {
        Map<String, String> tmpMap = new LinkedHashMap<String, String>();
        tmpMap.put("S", "Single");
        tmpMap.put("D", "Double");
        tmpMap.put("T", "Triple");
        tmpMap.put("HR", "Homerun");
        return Collections.unmodifiableMap(tmpMap);
    }
    private HitDetails hitDetails;
    
    
    public HitNotation(String notation) throws Exception {
        super(notation);
    }

    public boolean isOfType(String notation) {
        if (isSingle(notation) || isDouble(notation) 
                || isTriple(notation) || isHomerun(notation))
            return true;
        return false;
    }

    public String getDescription() {
        if (isSingle(notation))
            return HITS_MAP.get("S");
        else if (isDouble(notation))
            return HITS_MAP.get("D");
        else if (isTriple(notation))
            return HITS_MAP.get("T");
        else if (isHomerun(notation))
            return HITS_MAP.get("HR");
        else
            return "ten run googly";
    }

    public static boolean isSingle(String notation) {
    	if (notation.indexOf("S") == 0 &&
    			(notation.length() == 1 || 
    			 notation.indexOf("S.") == 0 ||
			     notation.indexOf("S;") == 0 ||
				 notation.indexOf("S/") == 0)) {
    		return true;
    	}
        return false;
    }
    
    public static boolean isDouble(String notation) {
    	if (notation.indexOf("D") == 0 &&
    			(notation.length() == 1 || 
    			 notation.indexOf("D.") == 0 ||
			     notation.indexOf("D;") == 0 ||
				 notation.indexOf("D/") == 0)) {
    		return true;
    	}
        return false;
    }
    
    public static boolean isTriple(String notation) {
    	if (notation.indexOf("T") == 0 &&
    			(notation.length() == 1 || 
    			 notation.indexOf("T.") == 0 ||
			     notation.indexOf("T;") == 0 ||
				 notation.indexOf("T//") == 0)) {
    		return true;
    	}
        return false;
    }
    
    public static boolean isHomerun(String notation) {
    	if (notation.indexOf("HR") == 0 &&
    			(notation.length() == 2 || 
    	    			 notation.indexOf("HR.") == 0 ||
    				     notation.indexOf("HR;") == 0 ||
    					 notation.indexOf("HR/") == 0)) {
    		return true;
    	}
        return false;
    }

    protected void moveRunners() throws Exception {
        super.moveRunners();
        String base = null;
        if (isSingle(notation)) {
            base = "1B";
        } else if (isDouble(notation)) {
            base = "2B";
        } else if (isTriple(notation)) {
            base = "3B";
        } else {
            this.runnersScored.put("B", new BaserunnerScored(this.getPlayerAtBat(), "", true));
        }
        if (!isPlayerOnBaseEnd(this.getPlayerAtBat()) && this.runnersOnBaseEnd.get("1B").getPlayerName() == "" 
                && this.notation.indexOf("Bx") == -1 && base != null) {
            this.runnersOnBaseEnd.put(base, new Baserunner(this.getPlayerAtBat(), ""));
        }
        
    }
    
    public HitDetails getHitDetails() {
        if (this.hitDetails == null) {
            this.hitDetails = new HitDetails(this.getPlayerAtBat(), this.getDescription(), this.getPlayByPlayDescription());
        }
        return this.hitDetails;
    }
    
    public String getNotationType() {
    	return NOTATION_TYPE;
    }
       
    protected void calculateRBI() {
        this.rbi = 0;
        if (isHomerun(notation) || isTriple(notation)) {
            this.rbi = isHomerun(notation) ? 1 : 0; // Add the RBI for the homerun
            for (int i=0; i < SCORING_NOTATIONS.length; i++) {
                if (runnerScored(SCORING_NOTATIONS[i])) {
                    this.rbi++; // Add for each runner on base who scores
                }
            }
        } else if (isDouble(notation)) {
            // Runners on 3B and 2B automatic RBI if they don't score on an error 
            for (int i=0; i < SCORING_NOTATIONS.length-1; i++) { // ONLY CHECKING 3B and 2B
                if (runnerScored(SCORING_NOTATIONS[i]) && !runnerScoredOnError(SCORING_NOTATIONS[i])) {
                    this.rbi++;
                }
            }
            // Only give RBI to runner on first scoring if the batter didn't advance on a fielding error
            if (runnerScored(RUNNER_ON_FIRST_SCORED) && !runnerScoredOnError(RUNNER_ON_FIRST_SCORED) &&
                    !batterAdvancedOnOutfieldFieldingErrror()) {
                this.rbi++;
            }
        } else if (isSingle(notation)) {
            // Runners on 3B is an automatic RBI if they don't score on an error 
            if (runnerScored(RUNNER_ON_THIRD_SCORED) && !runnerScoredOnError(RUNNER_ON_THIRD_SCORED)) {
                this.rbi++;
            }
            // Only give RBI to runner on 2B or 1B scoring if the batter didn't advance on a fielding error
            for (int i=1; i < SCORING_NOTATIONS.length; i++) { // ONLY CHECKING 2B and 1B
                if (runnerScored(SCORING_NOTATIONS[i]) && !runnerScoredOnError(SCORING_NOTATIONS[i]) &&
                    !batterAdvancedOnOutfieldFieldingErrror()) {
                    this.rbi++;
                }
            }            
        }
 
    }   
    
    @Override
    protected void populateBreakdown() {
        initBreakdown();
        this.breakdown.put("PA", 1);
        this.breakdown.put("AB", 1);
        this.breakdown.put("SI", this.isSingle(this.getNotationValue()) ? 1 : 0);
        this.breakdown.put("DB", this.isDouble(this.getNotationValue()) ? 1 : 0);
        this.breakdown.put("TR", this.isTriple(this.getNotationValue()) ? 1 : 0);
        this.breakdown.put("HR", this.isHomerun(this.getNotationValue()) ? 1 : 0);
        this.breakdown.put("RBI", this.getRbi());
    }
    
    
    @JsonIgnore
    public static final Map<String, String> HIT_TYPES_MAP = createHitTypesMap();
    
    @JsonIgnore
    public static Map<String, String> createHitTypesMap() {
        Map<String, String> tmpMap = new LinkedHashMap<String, String>();
        tmpMap.put("bunt", "Bunt hit");
        tmpMap.put("lined", "Line drive");
        tmpMap.put("grounded", "Ground ball");
        tmpMap.put("infield", "Infield hit");
        tmpMap.put("singled down", "Down the line");
        tmpMap.put("doubled down", "Down the line");
        tmpMap.put("tripled down", "Down the line");
        tmpMap.put("homered", "Home run");
        tmpMap.put("deep","Deep fly ball");
        return Collections.unmodifiableMap(tmpMap);
    }
    
    @JsonIgnore
    public static Map<String, String> HIT_LOCATIONS = createHitLocations();
    
    @JsonIgnore
    public static Map<String, String> createHitLocations() {
        Map<String, String> tmpMap = new LinkedHashMap<String, String>();
        tmpMap.put("pitcher", "Pitcher");
        tmpMap.put("catcher", "Catcher");
        tmpMap.put("to first", "First base");
        tmpMap.put("to second", "Second base");
        tmpMap.put("to third", "Third base");
        tmpMap.put("to short", "Shortstop");
        tmpMap.put("between third and short", "Between third and short");
        tmpMap.put("between first and second", "Between first and second");
        tmpMap.put("first base line", "First base line");
        tmpMap.put("third base line", "Third base line");
        tmpMap.put("left-field line", "Left field line");
        tmpMap.put("right-field line", "Right field line");
        tmpMap.put("up the middle", "Up the middle");
        tmpMap.put("to left center", "Left center field");
        tmpMap.put("to right center", "Right center field");
        tmpMap.put("left field line", "Left field line");
        tmpMap.put("right field line", "Right field line");
        tmpMap.put("left", "Left field");
        tmpMap.put("center", "Center field");
        tmpMap.put("right", "Right field");
        tmpMap.put("bunted", "Infield");
        return Collections.unmodifiableMap(tmpMap);
    }
    
    private class HitDetails {
        
        
        String playerAtBat;
        private String hit;
        private String hitType;
        private String hitLocation;
        private String hitLength;
        String description;
        
        String[] HIT_LENGTH = new String[]{"deep", "shallow"};
        
        private HitDetails() {
            hitType = null;
            hitLocation = null;
            hitLength = null;
        }
        
        public HitDetails(String playerAtBat, String hit, String description) {
            this();
            this.playerAtBat = playerAtBat;
            this.hit = hit;
            this.description = description;
            parseDescription();
        }
        
        private void parseDescription() {
            String hitDescription = description.substring(this.playerAtBat.length()+1);
            
            // Trim the description at the first comma for multiple-event plays
            hitDescription = hitDescription.indexOf(",") > -1 ? 
                    hitDescription.substring(0, hitDescription.indexOf(",")).trim() : hitDescription.trim();
            
            // Set the hit type;
            Iterator<String> hitTypeIterator = HIT_TYPES_MAP.keySet().iterator();
            while (hitTypeIterator.hasNext()) {
                String key = hitTypeIterator.next();
                if (hitDescription.indexOf(key) > -1) {
                    this.hitType = HIT_TYPES_MAP.get(key);
                    break;
                }
            }
            if (this.hitType == null) {
                this.hitType = "UNKNOWN (" + hitDescription + ")";
            }
            
            // Set the hit location;
            Iterator<String> hitLocationIterator = HIT_LOCATIONS.keySet().iterator();
            while (hitLocationIterator.hasNext()) {
                String key = hitLocationIterator.next();
                if (hitDescription.indexOf(key) > -1) {
                    this.hitLocation = HIT_LOCATIONS.get(key);
                    break;
                }
            }
            if (this.hitLocation == null) {
                this.hitLocation = "UNKNOWN (" + hitDescription + ")";
            }
            
            // Set the hit depth
            if (hitDescription.indexOf("deep") > -1) {
                this.hitLength = "Deep";
            } else if (hitDescription.indexOf("shallow") > -1) {
                this.hitLength = "Shallow";
            } else {
                this.hitLength = "Normal";
            }
        }

        public String getHit() {
            return hit;
        }

        public String getHitType() {
            return hitType;
        }

        public String getHitLocation() {
            return hitLocation;
        }

        public String getHitLength() {
            return hitLength;
        }
    }
}
