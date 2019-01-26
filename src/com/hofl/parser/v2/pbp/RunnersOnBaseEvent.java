/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hofl.parser.v2.pbp;

import com.hofl.parser.v2.notations.AbstractNotation.Baserunner;
import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 *
 * @author scottsmith
 */
public abstract class RunnersOnBaseEvent extends AbstractEvent {
    
    public RunnersOnBaseEvent(String description) throws Exception {
        super(description);
    }

    @JsonIgnore
    public RunnersOnBaseEvent getPreviousRunnersOnBaseEvent() {
        AbstractEvent prevGameEvent = this.getPreviousEvent();
        while (prevGameEvent != null && !(prevGameEvent instanceof RunnersOnBaseEvent)) {
            prevGameEvent = prevGameEvent.getPreviousEvent();
        }
        return (RunnersOnBaseEvent)prevGameEvent;
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
    
    public abstract Map<String, Baserunner> getRunnersOnBaseAfter() throws Exception;
    
}
