/*
 * Created on Mar 6, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hofl.vo;

/**
 * @author smithsc4
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PitcherLine {

	String pitcherName;
	int startOrdinal;
	
	PitcherLine prevPitcher;
	PitcherLine nextPitcher;
	
	public PitcherLine(String pitcherName) {
		this.pitcherName = pitcherName;
		prevPitcher = null;
		nextPitcher = null;
	}
	
	public PitcherLine(String pitcherName, int startOrdinal) {
		this(pitcherName);
		this.startOrdinal = startOrdinal;
	}
	
	public PitcherLine addNextPitcher(String pitcherName, int startOrdinal) {
		PitcherLine newPitcher = new PitcherLine(pitcherName, startOrdinal);
		this.nextPitcher = newPitcher;
		this.getNextPitcher().prevPitcher = this;
		return this.getNextPitcher();
	}
	
	public PitcherLine getNextPitcher() {
		return this.nextPitcher;
	}
	
	public PitcherLine getPreviousPitcher() {
		return this.prevPitcher;
	}
	
	public PitcherLine getFirstPitcher() {
	    PitcherLine tmpLine = this;
	    while (tmpLine.getPreviousPitcher() != null) {
	    	tmpLine = tmpLine.getFirstPitcher();
	    }
	    return tmpLine;
	}
	
	public String getPitcherName() {
		return this.pitcherName;
	}
	
	public int getStartOrdinal() {
		return this.startOrdinal;
	}
    
	public static boolean isOfType(String rawLine) {
        if (rawLine.indexOf("IN OUT ER") > 0)
            return true;
        return false;
    }
}