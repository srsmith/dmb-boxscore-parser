/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hofl.parser.v2.jackson;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
/**
 *
 * @author Scott
 */
public class TestPlayerJSONIngestion {
    
    public static final String JSON_URL = "http://hofl.com/api/json/players/";
    public static final String JSON_FILE = "/Users/Scott/NetBeansProjects/boxscore-parser/boxscore_parser_svn/jsontest/roster.json";
    
    public static void main(String args[]) {
        try {
            ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally
//            TestPlayerJSONIngestion.HoflPlayer[] array = mapper.readValue(new File(JSON_FILE), 
//                    TestPlayerJSONIngestion.HoflPlayer[].class);
            TestPlayerJSONIngestion.HoflPlayer[] array = mapper.readValue(new URL(JSON_URL), 
                    TestPlayerJSONIngestion.HoflPlayer[].class);
            System.out.println("Player count: " + array.length);
        } catch (Exception ex) {
            Logger.getLogger(TestPlayerJSONIngestion.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Sample 
{
    name_first: "David",
    name_last: "Aardsma",
    pid: "26611",
    id: "aardsda01",
    short: "Aardsma",
    franchise_id: "2",
    team_city: "Coconut Grove",
    team_nickname: "Calamity",
    level: "2",
    pps: "1",
    position: "RP",
    starter: "0",
    grandfather: null
}
     
     */
    
    public static class HoflPlayer {
        @JsonProperty("name_first")
        private String nameFirst;
        
        @JsonProperty("name_last")
        private String nameLast;
        private int pid;
        private String id;
        
        @JsonProperty("short")
        private String shortName;
        
        @JsonProperty("franchise_id")
        private int franchiseId;
        
        @JsonProperty("team_city")
        private String teamCity;
        
        @JsonProperty("team_nickname")
        private String teamNickname;
        private int level;
        private int pps;
        private String position;
        private int starter;
        private String grandfather;

        /**
         * @return the nameFirst
         */
        public String getNameFirst() {
            return nameFirst;
        }

        /**
         * @param nameFirst the nameFirst to set
         */
        public void setNameFirst(String nameFirst) {
            this.nameFirst = nameFirst;
        }

        /**
         * @return the nameLast
         */
        public String getNameLast() {
            return nameLast;
        }

        /**
         * @param nameLast the nameLast to set
         */
        public void setNameLast(String nameLast) {
            this.nameLast = nameLast;
        }

        /**
         * @return the pid
         */
        public int getPid() {
            return pid;
        }

        /**
         * @param pid the pid to set
         */
        public void setPid(int pid) {
            this.pid = pid;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id the id to set
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the shortName
         */
        public String getShortName() {
            return shortName;
        }

        /**
         * @param shortName the shortName to set
         */
        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        /**
         * @return the franchiseId
         */
        public int getFranchiseId() {
            return franchiseId;
        }

        /**
         * @param franchiseId the franchiseId to set
         */
        public void setFranchiseId(int franchiseId) {
            this.franchiseId = franchiseId;
        }

        /**
         * @return the teamCity
         */
        public String getTeamCity() {
            return teamCity;
        }

        /**
         * @param teamCity the teamCity to set
         */
        public void setTeamCity(String teamCity) {
            this.teamCity = teamCity;
        }

        /**
         * @return the teamNickname
         */
        public String getTeamNickname() {
            return teamNickname;
        }

        /**
         * @param teamNickname the teamNickname to set
         */
        public void setTeamNickname(String teamNickname) {
            this.teamNickname = teamNickname;
        }

        /**
         * @return the level
         */
        public int getLevel() {
            return level;
        }

        /**
         * @param level the level to set
         */
        public void setLevel(int level) {
            this.level = level;
        }

        /**
         * @return the pps
         */
        public int getPps() {
            return pps;
        }

        /**
         * @param pps the pps to set
         */
        public void setPps(int pps) {
            this.pps = pps;
        }

        /**
         * @return the position
         */
        public String getPosition() {
            return position;
        }

        /**
         * @param position the position to set
         */
        public void setPosition(String position) {
            this.position = position;
        }

        /**
         * @return the starter
         */
        public int getStarter() {
            return starter;
        }

        /**
         * @param starter the starter to set
         */
        public void setStarter(int starter) {
            this.starter = starter;
        }

        /**
         * @return the grandfather
         */
        public String getGrandfather() {
            return grandfather;
        }

        /**
         * @param grandfather the grandfather to set
         */
        public void setGrandfather(String grandfather) {
            this.grandfather = grandfather;
        }
    }
    
}
