package com.hofl.parser.v2.pbp;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

/**
 *
 * @author Scott
 */

@JsonPropertyOrder({"ordinal", "inningPart", "inning", "outs", "eventType", "description", "playerName", "position"})
public abstract class DefensiveEvent extends AbstractEvent {

    private String playerName;
    private String position;


    public DefensiveEvent(String description) throws Exception {
        super(description);
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) throws Exception {
        if (!validPosition(position)) {
            throw new Exception("Position " + position + " is not a valid position value.");
        }
        if (AbstractEvent.POSITION_MAP.containsValue(position.trim())) {
            this.position = position.trim();
        } else if (AbstractEvent.POSITION_MAP.containsKey(position.trim())) {
            this.position = AbstractEvent.POSITION_MAP.get(position.trim());
        }
    }
    
    @Override
    protected void processFielderChanges() throws Exception {
        this.setFielder(this.getPlayerName(), this.getPosition());
    }
}
