package com.hofl.vo;

public abstract class AbstractBoxScoreItem {

    private String rawLine;
    
    private AbstractBoxScoreItem() {
        
    }
    
    public AbstractBoxScoreItem(String rawLine) {
        this.rawLine = rawLine;
    }
    
    public static boolean isOfType(String rawLine) {
        return false;
    }

}
