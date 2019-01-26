package com.hofl.parser.v2.pbp;

import java.util.regex.Pattern;
import org.codehaus.jackson.annotate.JsonProperty;
/**
 *
 * @author Scott
 */
public class DefensiveSubstitution extends DefensiveEvent {
    
    public static final String NOTATION_TYPE = "DefensiveSubstitution";
    private static final String DESCRIPTION_KEY = " now playing ";
    
    public DefensiveSubstitution(String description) throws Exception {
        super(description);
        parseDescription();
    }  
    
    private void parseDescription() throws Exception {
        this.setPlayerName(this.description.substring(0, this.description.indexOf(DESCRIPTION_KEY)));
        this.setPosition(this.description.substring(this.description.indexOf(DESCRIPTION_KEY) + DESCRIPTION_KEY.length(), 
                this.description.length()));
    }
    
    public String getEventType() {
        return NOTATION_TYPE;
    }

    public boolean isOfType(String description) {
        if (description.indexOf(DESCRIPTION_KEY) > -1) {
            return true;
        }
        return false;
    }

    public String getDescription() {
        return this.description;
    }
}
