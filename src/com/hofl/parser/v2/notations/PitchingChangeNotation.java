/*
 * Created on Mar 7, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hofl.parser.v2.notations;

import org.codehaus.jackson.annotate.JsonProperty;


/**
 * @author smithsc4
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PitchingChangeNotation extends AbstractNotation {

    public static final String NOTATION_TYPE = "PitchingChangeNotation";

    /**
     * @param notation
     * @throws Exception
     */
    public PitchingChangeNotation(String notation) throws Exception {
        super(notation);
        // TODO Auto-generated constructor stub
    }

    /* (non-Javadoc)
     * @see com.hofl.vo.notations.AbstractNotation#isOfType(java.lang.String)
     */
    public boolean isOfType(String notation) {
        // TODO Auto-generated method stub
        return true;
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
