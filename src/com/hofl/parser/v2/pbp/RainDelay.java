package com.hofl.parser.v2.pbp;

import java.util.regex.Pattern;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
/**
 *
 * @author Scott
 */

@JsonPropertyOrder({"ordinal", "inningPart", "inning", "outs", "eventType", "description", "delayMinutes"})
public class RainDelay extends RainEvent {

    private int delayMinutes;
    public static final String NOTATION_TYPE = "RainDelay";
    private static final String DESCRIPTION_KEY = "Game delayed by rain for ";
    
    public RainDelay(String description) throws Exception {
        super(description);
        parseDescription();
    }  
    
    private void parseDescription() throws Exception {
        this.setDelayMinutes(this.description.substring(this.description.indexOf(DESCRIPTION_KEY) + DESCRIPTION_KEY.length(), 
                this.description.indexOf(" minutes")));
    }
    
    public String getEventType() {
        return NOTATION_TYPE;
    }

    public int getDelayMinutes() {
        return delayMinutes;
    }

    public void setDelayMinutes(String delayMinutes) {
        this.delayMinutes = Integer.parseInt(delayMinutes);
    }
    
    public void setDelayMinutes(int delayMinutes) {
        this.delayMinutes = delayMinutes;
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
