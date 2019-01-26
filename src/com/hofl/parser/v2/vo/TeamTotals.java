/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hofl.parser.v2.vo;

import org.codehaus.jackson.annotate.JsonProperty;
/**
 *
 * @author Scott
 */
public class TeamTotals {
    private int homeTeamRuns;
    private int homeTeamHits;
    private int homeTeamErrors;
    private int homeTeamLOB;
    private int homeTeamDP;
    
    private int awayTeamRuns;
    private int awayTeamHits;
    private int awayTeamErrors;
    private int awayTeamLOB;
    private int awayTeamDP;

    public int getHomeTeamRuns() {
        return homeTeamRuns;
    }

    public void setHomeTeamRuns(int homeTeamRuns) {
        this.homeTeamRuns = homeTeamRuns;
    }

    public int getHomeTeamHits() {
        return homeTeamHits;
    }

    public void setHomeTeamHits(int homeTeamHits) {
        this.homeTeamHits = homeTeamHits;
    }

    public int getHomeTeamErrors() {
        return homeTeamErrors;
    }

    public void setHomeTeamErrors(int homeTeamErrors) {
        this.homeTeamErrors = homeTeamErrors;
    }

    public int getHomeTeamLOB() {
        return homeTeamLOB;
    }

    public void setHomeTeamLOB(int homeTeamLOB) {
        this.homeTeamLOB = homeTeamLOB;
    }

    public int getHomeTeamDP() {
        return homeTeamDP;
    }

    public void setHomeTeamDP(int homeTeamDP) {
        this.homeTeamDP = homeTeamDP;
    }

    public int getAwayTeamRuns() {
        return awayTeamRuns;
    }

    public void setAwayTeamRuns(int awayTeamRuns) {
        this.awayTeamRuns = awayTeamRuns;
    }

    public int getAwayTeamHits() {
        return awayTeamHits;
    }

    public void setAwayTeamHits(int awayTeamHits) {
        this.awayTeamHits = awayTeamHits;
    }

    public int getAwayTeamErrors() {
        return awayTeamErrors;
    }

    public void setAwayTeamErrors(int awayTeamErrors) {
        this.awayTeamErrors = awayTeamErrors;
    }

    public int getAwayTeamLOB() {
        return awayTeamLOB;
    }

    public void setAwayTeamLOB(int awayTeamLOB) {
        this.awayTeamLOB = awayTeamLOB;
    }

    public int getAwayTeamDP() {
        return awayTeamDP;
    }

    public void setAwayTeamDP(int awayeTeamDP) {
        this.awayTeamDP = awayeTeamDP;
    }
}
