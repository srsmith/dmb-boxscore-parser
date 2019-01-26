package com.hofl.parser.v2;

/**
 *
 * @author Scott
 */
public class PlayByPlay {
    
    private int inning;
    private String description;
    
    public PlayByPlay(int inning, String description) {
        this.inning = inning;
        this.description = description;
    }

    public int getInning() {
        return inning;
    }

    public void setInning(int inning) {
        this.inning = inning;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}
