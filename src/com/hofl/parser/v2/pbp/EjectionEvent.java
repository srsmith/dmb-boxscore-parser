package com.hofl.parser.v2.pbp;

import java.util.regex.Pattern;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

/**
 *
 * @author Scott
 */

@JsonPropertyOrder({"ordinal", "inningPart", "inning", "outs", "eventType", "description", "playerName"})
public class EjectionEvent extends AbstractEvent {

    private String playerName;
    public static final String NOTATION_TYPE = "EjectedEvent";
    private static final String DESCRIPTION_KEY = " was ejected";
        
    public EjectionEvent(String description) throws Exception {
        super(description);

        parseDescription();
    }  
    
    private void parseDescription() throws Exception {
        this.setPlayerName(this.description.substring(0, this.description.indexOf(DESCRIPTION_KEY)));
    }
    
    public String getEventType() {
        return NOTATION_TYPE;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public boolean isOfType(String description) {
        if (description.indexOf(DESCRIPTION_KEY) > -1) {
            return true;
        }
        return false;
    }

    public String getDescription() {
        return description;
    }
    
}