/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hofl.parser.v2.pbp;

import com.hofl.parser.v2.notations.PitchingChangeNotation;
import java.util.regex.Pattern;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

/**
 *
 * @author Scott
 */

@JsonPropertyOrder({"ordinal", "inningPart", "inning", "outs", "eventType", "description", "newPitcherName"})
public class PitchingChange extends AbstractEvent {

    private String newPitcherName;
    public static final String NOTATION_TYPE = "PitchingChange";
    private static final String DESCRIPTION_KEY = " now pitching";
    
    private PitchingChangeNotation notation;
    
    public PitchingChange(String description) throws Exception {
        super(description);
        parseDescription();
    }  
    
    private void parseDescription() throws Exception {
        if (this.description.indexOf(DESCRIPTION_KEY) == -1) {
            throw new Exception("Not a PitchingChange event description! " + this.description);
        }
        this.setNewPitcherName(this.description.substring(0, this.description.indexOf(DESCRIPTION_KEY)));
    }
    
    public String getEventType() {
        return NOTATION_TYPE;
    }

    public String getNewPitcherName() {
        return newPitcherName;
    }

    public void setNewPitcherName(String newPitcherName) throws Exception {
        this.newPitcherName = newPitcherName;
    }

    @Override
    protected void processFielderChanges() throws Exception {
        this.setFielder(newPitcherName, "P");
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

    @JsonIgnore
    public PitchingChangeNotation getNotation() {
        return notation;
    }

    public void setNotation(PitchingChangeNotation notation) {
        this.notation = notation;
    }

    
}
