package com.hofl.parser.v2.pbp;

import java.util.regex.Pattern;
import org.codehaus.jackson.annotate.JsonProperty;
/**
 *
 * @author Scott
 */
public class StartedRaining extends RainEvent {
    
    public static final String NOTATION_TYPE = "StartedRaining";
    
    public StartedRaining(String description) throws Exception {
        super(description);
        // TODO Auto-generated constructor stub
    }
    
    public String getEventType() {
        return NOTATION_TYPE;
    }

    public boolean isOfType(String description) {
        if (description.indexOf("Started raining") > -1) {
            return true;
        }
        return false;
    }

    public String getDescription() {
        return description;
    }
    
}
