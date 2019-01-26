package com.hofl.parser.v2.pbp;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Scott
 */
public class GameDescriptionEvent extends AbstractEvent {
    
    private GameEvent gameEvent;
    private String description;

    public GameDescriptionEvent(String description) throws Exception {
        super(description);
    }  
    
    public String getEventType() {
        return "GameplayDescription";
    }

    public boolean isOfType(String description) {
        return true;  // probably should try to make a Info event and if it fails, return true.  But I am lazy right now
    }

    public String getDescription() {
        return "Gameplay Description";
    }

}
