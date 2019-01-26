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
public class Teams {
    
    private Team awayTeam;
    private Team homeTeam;

    public Teams () {
        
    }
    
    public Teams (String awayAbbreviation, String homeAbbreviation) {
        awayTeam = new Team(awayAbbreviation);
        homeTeam = new Team(homeAbbreviation);
    }

    public Team getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(Team awayTeam) {
        this.awayTeam = awayTeam;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(Team homeTeam) {
        this.homeTeam = homeTeam;
    }
    
    public void setTeamNicknames(String awayNick, String homeNick) {
        this.awayTeam.setNickname(awayNick);
        this.homeTeam.setNickname(homeNick);
    }
    
    public static class Team {
        
        private String abbreviation;
        private int franchiseId = -1;
        private String city;
        private String nickname;   
            
        public Team() {
         
        }
        
        public Team(String abbreviation) {
            this.abbreviation = abbreviation;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        public void setAbbreviation(String abbreviation) {
            this.abbreviation = abbreviation;
        }

        @JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
        public int getFranchiseId() {
            return franchiseId;
        }

        public void setFranchiseId(int franchiseId) {
            this.franchiseId = franchiseId;
        }
        
        @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

}
