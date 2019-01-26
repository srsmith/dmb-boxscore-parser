/*
 * Created on Mar 7, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hofl.parser.v2.notations;

import com.hofl.parser.v2.pbp.PinchHitter;
import org.codehaus.jackson.annotate.JsonProperty;


/**
 * @author smithsc4
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PinchHitterSubNotation extends AbstractNotation {

    public static final String NOTATION_TYPE = "PinchHiterSubNotation";
    private String pinchHitterName;
    private String hittingForName;

    /**
     * @param notation
     * @throws Exception
     */
    public PinchHitterSubNotation(String notation) throws Exception {
        super(notation);
        parseNotation();
    }
    
    private void parseNotation() throws Exception {
        this.setPinchHitterName(this.notation.substring(0, this.notation.indexOf(PinchHitter.DESCRIPTION_KEY)));
        this.setHittingForName(this.notation.substring(this.notation.indexOf(PinchHitter.DESCRIPTION_KEY) + PinchHitter.DESCRIPTION_KEY.length(), 
                this.notation.length()));
        this.setPlayerAtBat(pinchHitterName);
        this.setPlayByPlayDescription(notation);
    }
    
    public String getPinchHitterName() {
        return pinchHitterName;
    }

    public void setPinchHitterName(String pinchHitterName) throws Exception {
        this.pinchHitterName = pinchHitterName;
    }

    
    public String getHittingForName() {
        return hittingForName;
    }

    public void setHittingForName(String hittingForName) {
        this.hittingForName = hittingForName;
    }

    public boolean isOfType(String notation) {
        if (notation.indexOf(PinchHitter.DESCRIPTION_KEY) > -1) {
            return true;
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.hofl.vo.notations.AbstractNotation#getNotationType()
     */
    public String getNotationType() {
        // TODO Auto-generated method stub
        return NOTATION_TYPE;
    }

    /* (non-Javadoc)
     * @see com.hofl.vo.notations.AbstractNotation#getDescription()
     */
    public String getDescription() {
        // TODO Auto-generated method stub
        return "Pitching change";
    }
    
    protected void calculateRBI() {
        this.rbi = 0;
    }

    
}
