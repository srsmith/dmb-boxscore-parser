/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hofl.parser.v2.vo;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
/**
 *
 * @author Scott
 */
class InningScore {
    private Integer inning;
    private Integer awayTeamRuns;
    private Integer homeTeamRuns;

    InningScore(Integer inning, Integer awayTeamRuns, Integer homeTeamRuns) {
        this.inning = inning;
        this.awayTeamRuns = awayTeamRuns;
        this.homeTeamRuns = homeTeamRuns;
    }

    @JsonIgnore
    public Integer getInning() {
        return inning;
    }

    public void setInning(Integer inning) {
        this.inning = inning;
    }

    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    public Integer getAwayTeamRuns() {
        return awayTeamRuns;
    }

    public void setAwayTeamRuns(Integer awayTeamRuns) {
        this.awayTeamRuns = awayTeamRuns;
    }

    @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
    public Integer getHomeTeamRuns() {
        return homeTeamRuns;
    }

    public void setHomeTeamRuns(Integer homeTeamRuns) {
        this.homeTeamRuns = homeTeamRuns;
    }
}
