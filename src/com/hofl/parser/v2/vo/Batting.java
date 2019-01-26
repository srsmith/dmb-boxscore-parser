/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hofl.parser.v2.vo;

import java.util.*;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 *
 * @author Scott
 */
public class Batting {
    
    private List<BattingLine> homeBatting;
    private List<BattingLine> awayBatting;

    public Batting () {
        homeBatting = new ArrayList<BattingLine>();
        awayBatting = new ArrayList<BattingLine>();
    }
    
    public List<BattingLine> getHomeBatting() {
        return this.homeBatting;
    }
    
    public List<BattingLine> getAwayBatting() {
        return this.awayBatting;
    }
    
    @JsonIgnore
    public BattingLine getBattingLine(String playerName, boolean isAway) throws Exception {
        List<BattingLine> lines = isAway ? awayBatting : homeBatting;
        for (int i=0; i < lines.size(); i++) {
            if (lines.get(i).getPlayerName().equals(playerName)) {
                return lines.get(i);
            }
        }
        throw new Exception ("Cannot find player in " + (isAway ? " away " : " home ") 
                + "batting lines with the name '" + playerName + "'");
    }
    
    void addBatterLine(String rawLine, boolean isAway) {
        
        if (rawLine == null || rawLine.trim().length() < 31) {
            return;
        }
        
        List<BattingLine> battingLines = isAway ? this.awayBatting : this.homeBatting;
        
        int battingOrder;
        int battingOrderOrdinal;
        boolean isSub = rawLine.substring(0, 1).equals(" ") ? true : false;
        
        if (battingLines.size() > 0) {
            BattingLine lastBatter = battingLines.get(battingLines.size()-1);
            battingOrder = isSub ? lastBatter.getBattingOrder() : lastBatter.getBattingOrder()+1;
            battingOrderOrdinal = isSub ? lastBatter.getBattingOrderOrdinal()+1 : 1;
        } else {
            battingOrder = 1;
            battingOrderOrdinal = 1;
        }
        
        BattingLine battingLine = new BattingLine(rawLine.substring(0,18).trim(), battingOrder, battingOrderOrdinal);
        
        StringTokenizer battingTok = new StringTokenizer(rawLine.substring(18), " ");
        StringTokenizer debugTok = new StringTokenizer(rawLine.substring(18), " ");

        battingLine.setPosition(battingTok.nextToken());
        
        battingLine.setAtbats(Integer.parseInt(battingTok.nextToken()));
        battingLine.setRuns(Integer.parseInt(battingTok.nextToken()));
        battingLine.setHits(Integer.parseInt(battingTok.nextToken()));
        battingLine.setRbi(Integer.parseInt(battingTok.nextToken()));
        battingLine.setAvg(Float.parseFloat(battingTok.nextToken()));
        
        battingLines.add(battingLine);
        
    }

    public static class BattingLine {

        private String playerName;
        private String position;

        private int battingOrder;
        private int battingOrderOrdinal;
        private int atbats;
        private int runs;
        private int hits;
        private int rbi;
        private float avg;

        public BattingLine () {
            
        }

        private BattingLine(String playerName, int battingOrder, int battingOrderOrdinal) {
            this.playerName = playerName;
            this.battingOrder = battingOrder;
            this.battingOrderOrdinal = battingOrderOrdinal;
        }
        
        public String getPlayerName() {
            return playerName;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        public String getPosition() {
            return position == null ? null : position.toUpperCase();
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public int getAtbats() {
            return atbats;
        }

        public void setAtbats(int atbats) {
            this.atbats = atbats;
        }

        public int getRuns() {
            return runs;
        }

        public void setRuns(int runs) {
            this.runs = runs;
        }

        public int getHits() {
            return hits;
        }

        public void setHits(int hits) {
            this.hits = hits;
        }

        public int getRbi() {
            return rbi;
        }

        public void setRbi(int rbi) {
            this.rbi = rbi;
        }

        public float getAvg() {
            return avg;
        }

        public void setAvg(float avg) {
            this.avg = avg;
        }

        public int getBattingOrder() {
            return battingOrder;
        }

        public void setBattingOrder(int battingOrder) {
            this.battingOrder = battingOrder;
        }

        public int getBattingOrderOrdinal() {
            return battingOrderOrdinal;
        }

        public void setBattingOrderOrdinal(int battingOrderOrdinal) {
            this.battingOrderOrdinal = battingOrderOrdinal;
        }

        
    }

    
}
