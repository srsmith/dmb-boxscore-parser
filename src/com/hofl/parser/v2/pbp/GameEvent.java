package com.hofl.parser.v2.pbp;

import com.hofl.parser.v2.notations.AbstractNotation;
import com.hofl.parser.v2.notations.AbstractNotation.Baserunner;
import com.hofl.parser.v2.notations.BalkNotation;
import com.hofl.parser.v2.notations.ErrorNotation;
import com.hofl.parser.v2.notations.OutNotation;
import com.hofl.parser.v2.notations.PinchHitterSubNotation;
import com.hofl.parser.v2.notations.PitchingChangeNotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author Scott
 */
@JsonPropertyOrder({"ordinal", "inningPart", "inning", "outs", "playerAtBat", 
    "awayScore", "homeScore", "runnersOnBaseBefore", "strikes", "balls", 
    "eventType", "description", "pitchSequence", "pitches", "runnersOnBaseAfter"})
public class GameEvent extends RunnersOnBaseEvent {
    
    protected Map<String, String> playerAtBat;
    
    private int homeScore;
    private int awayScore;
    
    private int strikes;
    private int balls;
    
    private Map<String, String> runnersOnBaseStart;
    
    private String pitchSequence;
    private ArrayList<Pitch> pitches;
    
    
    public static String typePattern = "[\\s\\d][\\d][\\-][\\d][\\s\\d]";
    
    public GameEvent(String description) throws Exception {
        super(description);
        pitches = new ArrayList<Pitch>();
        parseDescription();
    }
    
    
    public GameEvent(String description, AbstractNotation notation) throws Exception {
        this(description);
        notation.setStartOuts(this.getOuts());
        this.setNotation(notation);
    }

    private void parseDescription() {
        String gameStateText = description.substring(0, 14);
        StringTokenizer tok = new StringTokenizer(gameStateText.trim(), " ");

        setScore(tok.nextToken());
        setOuts(Integer.parseInt(tok.nextToken()));
        setRunnersOnBase(tok.nextToken());
        setCount(tok.nextToken());

        String pbpText;
        if (description.indexOf("(") == -1) {
            // Balk event that occurs before 
            pbpText = description.substring(14).trim();
            pitchSequence = "-";
        } else {
            // Typical GameEvent that has at least one pitches thrown
            pbpText = description.substring(14, description.indexOf("(")).trim();
            pitchSequence = description.substring(description.indexOf("(")+1, description.indexOf(")")).trim();
        }
        setPitches(pitchSequence);
        this.description = pbpText;

    }
    
    public void setNotation(AbstractNotation notation) throws Exception {
        Pitch lastPitch = this.pitches.get(this.pitches.size()-1);
        lastPitch.setNotation(notation);
        this.playerAtBat = new LinkedHashMap<String, String>();
        this.playerAtBat.put("name", notation.getPlayerAtBat());
        this.playerAtBat.put("position", notation.getPlayerAtBatPosition());
        this.playerAtBat.put("battingOrder", notation.getPlayerAtBatBattingOrder().toString());
    }
    
    public void setResponsiblePitcher(String pitcherName) {
        this.playerAtBat.put("responsiblePitcher", pitcherName);
    }
    
    private String getResponsiblePitcher() {
        return this.playerAtBat.get("responsiblePitcher");
    }
    
    @Override
    protected void processFielderChanges() throws Exception {
        this.setFielder(this.playerAtBat.get("name"), "BA");  
    }
    
    @JsonIgnore
    public AbstractNotation getNotation() {
        return this.pitches.get(this.pitches.size()-1).getNotation();
    }
    
    public int getHomeScore() {
        return homeScore;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public int getStrikes() {
        return strikes;
    }

    public void setStrikes(int strikes) {
        this.strikes = strikes;
    }

    public int getBalls() {
        return balls;
    }

    public void setBalls(int balls) {
        this.balls = balls;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean isOfType(String description) {
        Pattern pattern = Pattern.compile(typePattern);

        if (pattern.matcher(description).find()) {
            return true;
        } else {
            return false;
        }
    }

    public String getEventType() {
        try {
            return this.pitches.get(this.pitches.size()-1).getNotation().getNotationType();
        } catch (Throwable t) {
            return "UnknownGameEvent";        
        }
    }

    private void setScore(String scoreStr) {
        setAwayScore(Integer.parseInt(scoreStr.trim().substring(0, 
                scoreStr.trim().indexOf("-"))));
        setHomeScore(Integer.parseInt(scoreStr.trim().substring(scoreStr.trim().indexOf("-")+1, 
                scoreStr.trim().length())));
    }
    
    private void setRunnersOnBase(String runners) {
        this.runnersOnBaseStart = new LinkedHashMap<String, String>();
        for (int i=0; i < runners.length(); i++) {
            this.runnersOnBaseStart.put(i+1 + "B", runners.substring(i, i+1));
        }
    }
        
    public Map<String, Baserunner> getRunnersOnBaseAfter() throws Exception {
        Pitch lastPitch = this.getPitches().get(this.getPitches().size()-1);
        return lastPitch.getNotation().getRunnersOnBaseEnd();
    }
    
    @JsonIgnore
    public GameEvent getNextGameEvent() {
        AbstractEvent nextGameEvent = this.getNextEvent();
        while (nextGameEvent != null && !(nextGameEvent instanceof GameEvent)) {
            nextGameEvent = nextGameEvent.getNextEvent();
        }
        return (GameEvent)nextGameEvent;
    }
    
    @JsonIgnore
    public GameEvent getPreviousGameEvent() {
        AbstractEvent prevGameEvent = this.getPreviousEvent();
        while (prevGameEvent != null && !(prevGameEvent instanceof GameEvent)) {
            prevGameEvent = prevGameEvent.getPreviousEvent();
        }
        return (GameEvent)prevGameEvent;
    }    

    private void setCount(String countStr) {
        char[] a = countStr.toCharArray();
        setBalls(Character.getNumericValue(a[0]));
        setStrikes(Character.getNumericValue(a[1]));
    }

    public ArrayList<Pitch> getPitches() throws Exception {
        Map<String, Baserunner> runnersOnBase = this.getRunnersOnBaseBefore();
        int currentOuts = this.getOuts();
        for (int i = 0; i < pitches.size(); i++) {
            if (pitches.get(i).getNotation() != null) {
                AbstractNotation currentNotation = pitches.get(i).getNotation();
                currentNotation.setStartOuts(currentOuts);
                currentNotation.setRunnersOnBaseStart(runnersOnBase);
                runnersOnBase = currentNotation.getRunnersOnBaseEnd();
                currentOuts = currentNotation.getStartOuts();
        
                if (currentNotation instanceof OutNotation) {
                    ((OutNotation)currentNotation).setFielders(this.getFielders());
                } 
                if (currentNotation instanceof ErrorNotation) {
                    ((ErrorNotation)currentNotation).setFielders(this.getFielders());
                } 
                
                // Set the pitcher of record on runners who get on base or who score.
                Baserunner b = currentNotation.getBaserunnerOnBaseEnd(currentNotation.getPlayerAtBat());
                if (b != null) {
                    b.setPitcherResponsible(this.getResponsiblePitcher());
                } else {
                    b = currentNotation.getBaserunnerScored(currentNotation.getPlayerAtBat());
                    if (b != null) {
                        b.setPitcherResponsible(this.getResponsiblePitcher());
                    }
                }
            }
        }
        return pitches;
    }

    public Map<String, Baserunner> getRunnersOnBaseBefore() throws Exception {
        RunnersOnBaseEvent prevEvent = this.getPreviousRunnersOnBaseEvent();
        if (prevEvent != null && prevEvent.getInning() == this.getInning()) {
            return prevEvent.getRunnersOnBaseAfter();
        } else {
            Map<String, Baserunner> runners = new LinkedHashMap<String, Baserunner>();
            runners.put("1B", new Baserunner("",""));
            runners.put("2B", new Baserunner("",""));
            runners.put("3B", new Baserunner("",""));            
            return runners;
        }
    }
    
    @JsonIgnore
    public int[] getPitchIndexForSubEvents() {
        List<Integer> integers = new ArrayList<Integer>();
        StringTokenizer tok = new StringTokenizer(pitchSequence.replaceAll(">", "").replaceAll("\\+", ""), ".");
        int tokenCount = tok.countTokens();
        int pitchLength = -1;
        int i = 1;
        while(tok.hasMoreTokens() && i < tokenCount) {
            pitchLength += (tok.nextToken().length());
            integers.add(new Integer(pitchLength));
            i++;
        }

        return convertIntegers(integers);
    }
    
    private int[] convertIntegers(List<Integer> integers) {
        int[] ret = new int[integers.size()];
        for (int i=0; i < ret.length; i++)
        {
            ret[i] = integers.get(i).intValue();
        }
        return ret;
    }
    
    public void setNotationForPitch(AbstractNotation notation, int pitchIdx) throws Exception {

        Pitch p;
        if (notation instanceof BalkNotation) {
            p = new Pitch("Bk");
            this.pitches.add(pitchIdx+1, p);
        } else if (notation instanceof PitchingChangeNotation) {
            p = new Pitch("Pc");
            this.pitches.add(pitchIdx+1, p);
        } else if (notation instanceof PinchHitterSubNotation) {
            p = new Pitch("Ph");
            this.pitches.add(pitchIdx+1, p);
        }
        else {
            p = this.pitches.get(pitchIdx);
        }
        p.setNotation(notation);
    }

    public void setEventForPitch(AbstractEvent event, int pitchIdx) throws Exception {
        Pitch p = new Pitch("UN");
        this.pitches.add(pitchIdx+1, p);
        p.setEvent(event);
    }
    
    private void setPitches(String pitchSequence) {
        pitches = new ArrayList<Pitch>();
        if (pitchSequence == null) {
            return;
        }
        for (int i=0; i < pitchSequence.length(); i++) {
            String pitch = pitchSequence.substring(i, i+1);
            boolean runnersMoving = false;
            
            if (pitch.equals(".")) {
                // Pitch boundry       
                if (pitchSequence.length() == i+1) {
                    break; // . is the last in the sequence - generally indicates a balk
                }

                i++;
                pitch = pitchSequence.substring(i, i+1);
            }
            if (pitch.equals(">")) {
                runnersMoving = true;
                i++;
                pitch = pitchSequence.substring(i, i+1);
            }
            if (pitch.equals("+")) {
                pitch = pitchSequence.substring(i, i+2);
                i++;
            }
            pitches.add(new Pitch(pitch, null, runnersMoving));
            
        }
    }

    public Map<String, String> getPlayerAtBat() {
        return playerAtBat;
    }

    public void setPlayerAtBat(String playerAtBat) {
        this.playerAtBat.put("name", playerAtBat);
    }

    public void setPlayerAtPosition(String playerAtBatPosition) {
        this.playerAtBat.put("position", playerAtBatPosition);
    }


    /** Notations found for pitches in the play by play portion of the boxscore
        *
        *	P Pitchout
        *	H Hit by pitch
        *	I Intentional ball
        *	B Called ball
        *	C Called strike
        *	S Swinging strike
        *	F Foul ball
        *	X Ball put in play
        *	1 Pitcher made pickoff throw to 1st
        *	2 Pitcher made pickoff throw to 2nd
        *	3 Pitcher made pickoff throw to 3rd
        *	+1 Catcher made pickoff throw to 1st
        *	+2 Catcher made pickoff throw to 2nd
        *       +3 Catcher made pickoff throw to 3rd
        *       b Batter was bunting
        *       p Pitchout
        *   -- Added by me to handle non-pitch events happening mid-event
        *       Bk Pitcher committed a balk
        *       Pc Pitching change mid-pitch sequence
        */
    
    public static class Pitch {
        
        private String pitch;
        private String pitchDescription;
        private boolean runnersMoving;
        private AbstractNotation notation;
        private AbstractEvent event;
        
        @JsonIgnore
        private static final Map<String, String> PITCH_TYPES = createMap();

        @JsonIgnore
        private static Map<String, String> createMap() {
            Map<String, String> tmpMap = new HashMap<String, String>();
            tmpMap.put("P", "Pitchout");
            tmpMap.put("H", "Hit By Pitch");
            tmpMap.put("I", "Ball (Intentional)");
            tmpMap.put("B", "Ball");
            tmpMap.put("C", "Strike (Called)");
            tmpMap.put("S", "Strike (Swinging)");
            tmpMap.put("F", "Foul Ball");
            tmpMap.put("X", "In Play");
            tmpMap.put("1", "Pickoff Throw To 1st (Pitcher)");
            tmpMap.put("2", "Pickoff Throw To 2nd (Pitcher)");
            tmpMap.put("3", "Pickoff Throw To 3rd (Pitcher)");
            tmpMap.put("+1", "Pickoff Throw To 3rd (Catcher)");
            tmpMap.put("+2", "Pickoff Throw To 3rd (Catcher)");
            tmpMap.put("+3", "Pickoff Throw To 3rd (Catcher)");
            tmpMap.put("b", "Batter was bunting");
            tmpMap.put("p", "Pitchout");
            tmpMap.put("Bk", "No pitch - Balk was called");
            tmpMap.put("Pc", "Pitching change made during plate appearance");
            tmpMap.put("Ph", "Pinch hitter introduced during plate appearance");
            tmpMap.put("UN", "Unspecified event");
            return Collections.unmodifiableMap(tmpMap);
        }
        
        private Pitch() {
            
        }
        
        public Pitch(String pitch) {
            this.runnersMoving = false;
            this.pitch = pitch;
            this.pitchDescription = PITCH_TYPES.get(pitch);
        }
        
        public Pitch(String pitch, AbstractNotation notation) {
            this(pitch);
            this.notation = notation;
        }
                
        public Pitch(String pitch, AbstractNotation notation, boolean runnersMoving) {
            this(pitch, notation);
            this.runnersMoving = runnersMoving;
        }
        
        public Pitch(String pitch, boolean runnersMoving) {
            this(pitch, null, runnersMoving);
        }

        public String getPitch() {
            return pitch;
        }

        public void setPitch(String pitch) {
            this.pitch = pitch;
        }

        public String getPitchDescription() {
            return pitchDescription;
        }

        public void setPitchDescription(String pitchDescription) {
            this.pitchDescription = pitchDescription;
        }

        public boolean isRunnersMoving() {
            return runnersMoving;
        }

        public void setRunnersMoving(boolean runnersMoving) {
            this.runnersMoving = runnersMoving;
        }
        
        @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
        public AbstractNotation getNotation() {
            return notation;
        }

        public void setNotation(AbstractNotation notation) throws Exception {
            if (this.event != null) {
                throw new Exception("Cannot set a notation when the Pitch already has an event: " + event.getDescription());
            }
            this.notation = notation;
        }

        @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
        public AbstractEvent getEvent() {
            return event;
        }

        public void setEvent(AbstractEvent event) throws Exception {
            if (this.notation != null) {
                throw new Exception("Cannot set an event when the Pitch already has a notation: " + notation.getNotationValue());
            }
            this.event = event;
        }


    }

}
