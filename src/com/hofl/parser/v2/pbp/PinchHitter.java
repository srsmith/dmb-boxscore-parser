package com.hofl.parser.v2.pbp;

import java.util.regex.Pattern;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
/**
 *
 * @author Scott
 */

@JsonPropertyOrder({"ordinal", "inningPart", "inning", "outs", "eventType", "description", "pinchHitterName", "hittingForName"})
public class PinchHitter extends AbstractEvent {

    private String pinchHitterName;
    private String hittingForName;
    public static final String NOTATION_TYPE = "PinchHitter";
    public static final String DESCRIPTION_KEY = " pinch hitting for ";

    
    public PinchHitter(String description) throws Exception {
        super(description);
        parseDescription();
    }  
    
    private void parseDescription() throws Exception {
        this.setPinchHitterName(this.description.substring(0, this.description.indexOf(DESCRIPTION_KEY)));
        this.setHittingForName(this.description.substring(this.description.indexOf(DESCRIPTION_KEY) + DESCRIPTION_KEY.length(), 
                this.description.length()));
    }
    
    public String getEventType() {
        return NOTATION_TYPE;
    }
    
    public String getPinchHitterName() {
        return pinchHitterName;
    }

    public void setPinchHitterName(String pinchHitterName) throws Exception {
        this.pinchHitterName = pinchHitterName;
    }

    @Override
    protected void processFielderChanges() throws Exception {
        this.setFielder(this.pinchHitterName, "BA");
    }
    
    public String getHittingForName() {
        return hittingForName;
    }

    public void setHittingForName(String hittingForName) {
        this.hittingForName = hittingForName;
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
