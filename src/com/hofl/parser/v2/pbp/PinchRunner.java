package com.hofl.parser.v2.pbp;

import com.hofl.parser.v2.notations.AbstractNotation;
import com.hofl.parser.v2.notations.AbstractNotation.Baserunner;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

/**
 *
 * @author Scott
 */


@JsonPropertyOrder({"ordinal", "inningPart", "inning", "outs", "eventType", "description", 
    "runnersOnBaseBefore", "pinchRunnerName", "runningForName", "runnersOnBaseAfter"})
public class PinchRunner extends RunnersOnBaseEvent {

    private String pinchRunnerName;
    private String runningForName;
    public static final String NOTATION_TYPE = "PinchRunner";
    private static final String DESCRIPTION_KEY = " pinch running for ";

    public PinchRunner(String description) throws Exception {
        super(description);
        parseDescription();
    }  
    
    private void parseDescription() throws Exception {
        this.setPinchRunnerName(this.description.substring(0, this.description.indexOf(DESCRIPTION_KEY)));
        this.setRunningForName(this.description.substring(this.description.indexOf(DESCRIPTION_KEY) + DESCRIPTION_KEY.length(), 
                this.description.length()));
    }
    
    public String getEventType() {
        return NOTATION_TYPE;
    }

    public String getPinchRunnerName() {
        return pinchRunnerName;
    }

    public void setPinchRunnerName(String pinchRunnerName) {
        this.pinchRunnerName = pinchRunnerName;
    }

    public String getRunningForName() {
        return runningForName;
    }

    public void setRunningForName(String runningForName) {
        this.runningForName = runningForName;
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

    @Override
    protected void processFielderChanges() throws Exception {
        this.setFielder("", "BA");
    }
        
    
    public Map<String, Baserunner> getRunnersOnBaseAfter() throws Exception {
        Map<String, Baserunner> runnersOnBaseBefore = getRunnersOnBaseBefore();
        Map<String, Baserunner> runnersOnBaseAfter = new LinkedHashMap<String, Baserunner>();
        
        runnersOnBaseAfter.putAll(runnersOnBaseBefore);
        Iterator iter = runnersOnBaseAfter.keySet().iterator();
        while (iter.hasNext()) {
            String base = (String)iter.next();
            Baserunner baseRunner = runnersOnBaseAfter.get(base);
            if (baseRunner.getPlayerName().equals(runningForName)) {
                baseRunner.setPlayerName(pinchRunnerName);
            }
        }
        return runnersOnBaseAfter;
    }
}
