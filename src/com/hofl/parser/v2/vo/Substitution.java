/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hofl.parser.v2.vo;

/**
 *
 * @author Scott
 */
public class Substitution {
    private String playerName;
    private ChangeType changeType;
    private int inning;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public ChangeType getChangeType() {
        return changeType;
    }

    public void setChangeType(ChangeType changeType) {
        this.changeType = changeType;
    }

    public int getInning() {
        return inning;
    }

    public void setInning(int inning) {
        this.inning = inning;
    }

    private abstract class ChangeType {
        public abstract String getType();
    }
    
    private class BattedFor extends ChangeType {
        
        private String replacedPlayer;
        
        public String getType() {
            return "batted for";
        }

        public String getReplacedPlayer() {
            return replacedPlayer;
        }

        public void setReplacedPlayer(String replacedPlayer) {
            this.replacedPlayer = replacedPlayer;
        }
    }

    private class MovedTo extends ChangeType {
        
        private String position;
        
        public String getType() {
            return "moved to";
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }
    }

    private class InsertedAt extends ChangeType {
        
        private String position;
        
        public String getType() {
            return "moved to";
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }
    }

    
}
