package com.hofl.parser.v2.pbp;

import com.hofl.parser.v2.notations.OutNotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 *
 * @author Scott
 */


@JsonPropertyOrder({"ordinal", "inningPart", "inning", "runnersOnBaseBefore", 
    "outs", "eventType", "description"})

public abstract class AbstractEvent {
    
    String description;
    private int inning;
    private Integer outs;
    private int ordinal;
    protected Map<String, String> fielders;
    
    AbstractEvent previousEvent;
    AbstractEvent nextEvent;
    private String inningPart;
    
    @JsonIgnore
    public static final Map<String, String> POSITION_MAP = createMap();

    @JsonIgnore
    public static Map<String, String> createMap() {
        Map<String, String> tmpMap = new LinkedHashMap<String, String>();
        tmpMap.put("pitcher", "P");
        tmpMap.put("catcher", "C");
        tmpMap.put("first base", "1B");
        tmpMap.put("second base", "2B");
        tmpMap.put("third base", "3B");
        tmpMap.put("shortstop", "SS");
        tmpMap.put("left field", "LF");
        tmpMap.put("center field", "CF");
        tmpMap.put("right field", "RF");
        return Collections.unmodifiableMap(tmpMap);
    }
    
    public AbstractEvent(String description) throws Exception {
        if (isOfType(description)) {
            this.description = description.trim();
        }
        else {
            throw new Exception("Event " + description + " is not of type " + this.getClass().getName());
        }
    }
    
    public AbstractEvent(String description, int inning, int ordinal) throws Exception {
        this(description);
        this.inning = inning;
    }
    
    protected void initFielders() {
        this.fielders = new LinkedHashMap<String, String>();
        this.fielders.put("BA", "");
        Iterator iter = POSITION_MAP.values().iterator();
        while(iter.hasNext()) {
            this.fielders.put((String)iter.next(), "");
        }
    }
    
    public void setFielder(String playerName, String position) throws Exception {
        if (!validPosition(position)) {
            throw new Exception("Position " + position + " is not a valid position value.");
        } else if (this.fielders == null) {
            this.getFielders();
        }
        this.fielders.put(position, playerName);
        
    }
    
    public void setFielders(Map<String, String> allFielders, boolean overwrite) throws Exception {
        if (this.fielders == null) {
            initFielders();
        }
        Iterator<String> iter = allFielders.keySet().iterator();
        while (iter.hasNext()) {
            String position = iter.next();
            String currentFielder = this.getFielders().get(position);
            if (overwrite || (currentFielder == null || currentFielder.equals(""))) {
                this.setFielder(allFielders.get(position), position);
            }
        }
    }
    
    public Map<String, String> getFielders() throws Exception {
        if (this.fielders == null) {
            AbstractEvent previousEvent = this.getPreviousEvent();
            if (previousEvent == null) {
                this.initFielders();
            }
            else {
                this.setFielders(previousEvent.getFielders(), false);
            }    
        }
        this.processFielderChanges(); 
        if (this instanceof GameEvent) {
            ((GameEvent)this).setResponsiblePitcher(this.fielders.get("P"));
        }
        return this.fielders;
        
    }
    
    protected void processFielderChanges() throws Exception {
        // Intentionally blank so that implementing classes may override
    }
    
    public static boolean validPosition(String position) {
        if (position.equals("BA") || POSITION_MAP.containsValue(position.trim()) || POSITION_MAP.containsKey(position.trim())) {
            return true;
        } else {
            return false;
        }
    }
    
    public AbstractEvent addChildEvent(String description) throws Exception {
        AbstractEvent nextEvent = AbstractEvent.getEvent(description);
        return this.addChildEvent(nextEvent);
    }
    
    public AbstractEvent addChildEvent(AbstractEvent nextEvent) throws Exception {
        this.setNextEvent(nextEvent);
        nextEvent.setPreviousEvent(this);
        return nextEvent;
    }

    public AbstractEvent addParentEvent(String description) throws Exception {
        AbstractEvent nextNotation = AbstractEvent.getEvent(description);
        return this.addParentEvent(nextNotation);
    }
    
    public AbstractEvent addParentEvent(AbstractEvent prevEvent) throws Exception {
        this.setPreviousEvent(prevEvent);
        prevEvent.setNextEvent(this);
        return prevEvent;
    }      
    
    @JsonIgnore
    public AbstractEvent getFirstEvent() {
        AbstractEvent tmpEvent = this;
        while (tmpEvent.getPreviousEvent() != null) {
            tmpEvent = tmpEvent.getPreviousEvent();
        }
        return tmpEvent;
    }
    
    
    private void setPreviousEvent(AbstractEvent prevEvent) {
        this.previousEvent = prevEvent;
    }
    
    private void setNextEvent(AbstractEvent nextEvent) {
        this.nextEvent = nextEvent;
    }
    
    @JsonIgnore
    public AbstractEvent getNextEvent() {
        if (this.nextEvent != null)
            return this.nextEvent;
        else
            return null;
    }
    
    @JsonIgnore
    public AbstractEvent getPreviousEvent() {
        if (this.previousEvent != null)
            return this.previousEvent;
        else
            return null;
    }
    
    @JsonIgnore
    public AbstractEvent getEventAtOrdinal(int ordinal) {
        AbstractEvent ordinalEvent = this.getFirstEvent();
        while (ordinalEvent != null && ordinalEvent.getOrdinal() != ordinal) {
            ordinalEvent = ordinalEvent.getNextEvent();
        }
        return ordinalEvent;
    }
    
    @JsonIgnore
    public static final AbstractEvent getEvent(String description) throws Exception {
        Class[] pArray = new Class[] {String.class};
        Object[] oArray = new Object[] {description};
        for (int i=0; i < NOTATION_TYPE_CLASS_NAMES.length; i++) {
            try {
                Class c = Class.forName("com.hofl.parser.v2.pbp." + NOTATION_TYPE_CLASS_NAMES[i]);
                AbstractEvent aEvent = (AbstractEvent)c.getConstructor(pArray).newInstance(oArray);
                //System.out.println("Found description type " +NOTATION_TYPE_CLASS_NAMES[i]+ " for '" + description + "'");
                return aEvent;
            }
            catch (Throwable t) {
                //System.out.println("Not a " + NOTATION_TYPE_CLASS_NAMES[i] + ": " + t);
            }
        }
        return new GameEvent(description);
//        throw new Exception("Event does not exist that is described by the description '" + description + "'");
    }
    
    /**
     * Returns this AbstractEvent without a previous or next event.
     */
    @JsonIgnore
    private AbstractEvent getSoloClone() {
        AbstractEvent event = this;
        event.previousEvent = null;
        event.nextEvent = null;
        return event;
    }
    
    @JsonIgnore
    public List<AbstractEvent> getEventsAsList() {
        List<AbstractEvent> list = new ArrayList<AbstractEvent>();
        AbstractEvent event = this.getFirstEvent();
        AbstractEvent soloEvent = event;
        while (event.getNextEvent() != null) {
            list.add(event.getSoloClone());
        }
        return list;
    }
    
    public abstract boolean isOfType(String description);
    
    public abstract String getEventType();
    
    public abstract String getDescription();
    
    public static final String[] NOTATION_TYPE_CLASS_NAMES = {
        "DefensiveMovedToPosition",
        "DefensiveSubstitution",
        "EjectionEvent",
        "InjuryEvent",
        "LeftAfterRainDelay",
        "PinchHitter",
        "PinchRunner",
        "PitchingChange",
        "RainDelay",
        "StartedRaining",
        "StoppedRaining"};    
    
    public int getInning() {
        return inning;
    }

    public void setInning(int inning) {
        this.inning = inning;
    }

    public int getOuts() {
        if (this.outs == null && !(this instanceof GameEvent)) {
            this.setOuts(this.getNextEvent() != null ? this.getNextEvent().getOuts() : 0);
        }
        return this.outs.intValue();
    }

    public void setOuts(int outs) {
        this.outs = new Integer(outs);
    }
    
    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public void setInningPart(String inningPart) {
        this.inningPart = inningPart;
    }
    
    public String getInningPart() {
        return this.inningPart;
    }
}
