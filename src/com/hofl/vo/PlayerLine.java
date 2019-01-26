package com.hofl.vo;

import java.util.HashMap;

import com.hofl.parser.BoxScore;

public class PlayerLine extends AbstractBoxScoreItem  {

    private String playerName;
    private String position;
    private String teamName;
    private HashMap outcomes;

    private int chompLength = 13;
    
    public PlayerLine(String rawLine, int headerStart) {
        super(rawLine);
        outcomes = new HashMap();
        parseRawLine(rawLine, headerStart);
    }

    public static boolean isOfType(String rawLine) {
        if (rawLine.length() == 85  && !Notes.isOfType(rawLine))
            return true;
        return false;
    }

    public String getPlayerName() {
        return this.playerName.trim();
    }
    
    public String getPosition() {
        return this.position.trim().toUpperCase();
    }
    
    public String getOutcome(String header) {
        if (outcomes.containsKey(header))
            return (String)outcomes.get(header);
        else 
            return null;
    }
    
    public void setTeamName(String teamName) {
    	this.teamName = teamName;
    }
    
    public String getTeamName() {
    	return this.teamName;
    }
    
    private void parseRawLine(String line, int headerStart) {
        playerName = line.substring(0,18);
        position = line.substring(18,20);
        chompLine(line, headerStart);
    }
    
    private void chompLine(String line, int headerStart) {
        line = line.substring(21, line.length());
        int i=headerStart;
        while (line.length() >= chompLength) {
            outcomes.put(BoxScore.ALPHABET_ARRAY[i], line.substring(0,chompLength));
            line = line.substring(chompLength, line.length());
            i++;
        }
        if (line.trim().length() > 0) {
        	outcomes.put(BoxScore.ALPHABET_ARRAY[i], line.trim());
        }
    }
    
}
