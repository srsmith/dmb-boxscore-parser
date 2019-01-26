package com.hofl.vo;

import java.util.*;
import com.hofl.vo.notations.PinchRunnerNotation;

public class PinchRunners {

    private HashMap pinchRunners;
    
    public PinchRunners() {
        pinchRunners = new HashMap();
    }
    
    public void addPinchRunnerNotation(PinchRunnerNotation notation) throws Exception {
        if (containsPinchRunner(notation.getInning(), notation.getPlayerAtBat())) {
            throw new Exception("Pinch runner notation already exist for " + notation.getPlayerAtBat()  +
                        " in inning " + notation.getInning());
        }
        else 
            pinchRunners.put(notation.getPlayerAtBat() + notation.getInning(), notation);
    }

    public boolean containsPinchRunner(int inning, String playerName) {
        if (pinchRunners.containsKey(playerName + inning))
            return true;
        else
            return false;
    }
    
    public PinchRunnerNotation getPinchRunnerNotation(int inning, String playerName) {
        if (containsPinchRunner(inning, playerName))
            return (PinchRunnerNotation)pinchRunners.get(playerName + inning);
        else
            return null;
    }
    
    public int getNumberOfPinchRunnerNotations() {
        return this.pinchRunners.size();
    }
    
    public PinchRunnerNotation[] getAllPinchRunners() {
        if (this.getNumberOfPinchRunnerNotations() < 1) {
            return new PinchRunnerNotation[0];
        }
        else {
            return (PinchRunnerNotation[])this.pinchRunners.values()
            .toArray(new PinchRunnerNotation[this.pinchRunners.size()]);
        }
    }

}
