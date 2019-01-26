/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hofl.parser.v2.vo;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author Scott
 */
public class Pitching {
    
    private List<PitchingLine> homePitching;
    private List<PitchingLine> awayPitching;

    public Pitching () {
        homePitching = new ArrayList<PitchingLine>();
        awayPitching = new ArrayList<PitchingLine>();
    }
    
    public List<PitchingLine> getHomePitching() {
        return homePitching;
    }

    public List<PitchingLine> getAwayPitching() {
        return awayPitching;
    }

    public void addPitcher(String line, boolean isAwayTeamPitching) {
        List<PitchingLine> lines = isAwayTeamPitching ? this.awayPitching : this.homePitching;
        PitchingLine pitcher = new PitchingLine(line);
        pitcher.setFinished(true); // always assume most recently added pitcher is the last pitcher
        if (lines.size() == 0) {
            pitcher.setStarted(true);
        } else {
           PitchingLine lastPitcher = lines.get(lines.size()-1);
           lastPitcher.setFinished(false); // no longer the last pitcher
           lines.set(lines.size()-1, lastPitcher);
        }
        lines.add(pitcher);
        
    }
    
    public class PitchingLine {

        private String pitcherName;
        
        private boolean started;
        private boolean finished;
        
        private List<Decision> decisions;
        
        private double innings;
        private int hits;
        private int runs;
        private int earnedRuns;
        private int walks;
        private int strikeouts;
        private int pitches;
        private int strikes;
        private double era;

        public PitchingLine() {
            
        }
        
        public PitchingLine(String rawLine) {
            this.decisions = new ArrayList<Decision>();
            this.parseRawLine(rawLine);
        }
        
        private void parseRawLine (String rawLine) {
            this.pitcherName = rawLine.substring(0,17).trim();
            if (rawLine.substring(17,32).trim().length() > 0) {
                StringTokenizer decisions = new StringTokenizer(rawLine.substring(17,32).trim(), ",");
                while (decisions != null && decisions.hasMoreTokens()) {
                    String decision = decisions.nextToken();
                    StringTokenizer decisionTok = new StringTokenizer(decision, " ");
                    this.addDecision(decisionTok.nextToken(), decisionTok.nextToken());
                }
                
            }
            StringTokenizer outcomeTok = new StringTokenizer(rawLine.substring(32).trim()," ");
            this.setInnings(Double.parseDouble(outcomeTok.nextToken()));
            this.setHits(Integer.parseInt(outcomeTok.nextToken()));
            this.setRuns(Integer.parseInt(outcomeTok.nextToken()));
            this.setEarnedRuns(Integer.parseInt(outcomeTok.nextToken()));
            this.setWalks(Integer.parseInt(outcomeTok.nextToken()));
            this.setStrikeouts(Integer.parseInt(outcomeTok.nextToken()));
            this.setPitches(Integer.parseInt(outcomeTok.nextToken()));
            this.setStrikes(Integer.parseInt(outcomeTok.nextToken()));
            String eraString = outcomeTok.nextToken();
            try {
                this.setERA(Double.parseDouble(eraString));
            } catch (Throwable t) {
                System.out.println("Unable to set ERA of " + eraString);
            }
        }
        
        public String getPitcherName() {
            return pitcherName;
        }

        public void setPitcherName(String playerName) {
            this.pitcherName = playerName;
        }

        public double getInnings() {
            return innings;
        }

        public void setInnings(double innings) {
            this.innings = innings;
        }

        public int getHits() {
            return hits;
        }

        public void setHits(int hits) {
            this.hits = hits;
        }

        public int getRuns() {
            return runs;
        }

        public void setRuns(int runs) {
            this.runs = runs;
        }

        public int getEarnedRuns() {
            return earnedRuns;
        }

        public void setEarnedRuns(int earnedRuns) {
            this.earnedRuns = earnedRuns;
        }

        public int getWalks() {
            return walks;
        }

        public void setWalks(int walks) {
            this.walks = walks;
        }

        public int getStrikeouts() {
            return strikeouts;
        }

        public void setStrikeouts(int strikeouts) {
            this.strikeouts = strikeouts;
        }

        public int getPitches() {
            return pitches;
        }

        public void setPitches(int pitches) {
            this.pitches = pitches;
        }

        public int getStrikes() {
            return strikes;
        }

        public void setStrikes(int strikes) {
            this.strikes = strikes;
        }

        public double getERA() {
            return era;
        }

        public void setERA(double era) {
            this.era = era;
        }

        public boolean getFinished() {
            return finished;
        }

        public void setFinished(boolean finished) {
            this.finished = finished;
        }

        public boolean getStarted() {
            return started;
        }

        public void setStarted(boolean started) {
            this.started = started;
        }

        public void addDecision(String decision, String record) {
            this.decisions.add(new Decision(decision, record));
        }
        
        public List<Decision> getDecisions() {
            return decisions;
        }
    }

    public class Decision {

        private String decision;
        private String record;
        
        public Decision() {
        }

        private Decision(String decision, String record) {
            this.decision = decision;
            this.record = record;
        }

        public String getDecision() {
            return decision;
        }

        public void setDecision(String decision) {
            this.decision = decision;
        }

        public String getRecord() {
            return record;
        }

        public void setRecord(String record) {
            this.record = record;
        }
    }
}
