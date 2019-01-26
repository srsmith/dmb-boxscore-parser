package com.hofl.parser.v2.vo;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Jackson representation of the player JSON. 
 * 
 * Example:
 * 
 * {
 *      name_first: "David",
 *      name_last: "Aardsma",
 *      pid: "26611",
 *      id: "aardsda01",
 *      short: "Aardsma",
 *      franchise_id: "2",
 *      team_city: "Coconut Grove",
 *      team_nickname: "Calamity",
 *      level: "2",
 *      pps: "1",
 *      position: "RP",
 *      starter: "0",
 *      grandfather: null
 * 
 *      name_first: "Hank",
        name_last: "Aaron",
        pid: "10001",
        id: "aaronha01",
        short: "Aaron",
        franchise_id: "2",
        team_city: "Coconut Grove",
        team_nickname: "Calamity",
        level: "1",
        pps: "5",
        position: "1B",
        starter: "3",
        grandfather: null,
        bats: "R",
        throws: "R",
        vintage: "1971",
        dfa: "0",
        dfa_protected: "1",
        waivers: null
 * 
 * }
 * @author Scott
 */
public class Player {

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
    
    @JsonProperty("bats")
    private String batsHand;
    @JsonProperty("throws")
    private String throwsHand;
    private int vintage;
    
    private int dfa;
    @JsonProperty("dfa_protected")
    private int dfaProtected;
    private String waivers;
    

    
    public String getNameFirst() {
        return nameFirst;
    }

    public void setNameFirst(String nameFirst) {
        this.nameFirst = nameFirst;
    }

    public String getNameLast() {
        return nameLast;
    }

    public void setNameLast(String nameLast) {
        this.nameLast = nameLast;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public int getFranchiseId() {
        return franchiseId;
    }

    public void setFranchiseId(int franchiseId) {
        this.franchiseId = franchiseId;
    }

    public String getTeamCity() {
        return teamCity;
    }

    public void setTeamCity(String teamCity) {
        this.teamCity = teamCity;
    }

    public String getTeamNickname() {
        return teamNickname;
    }

    public void setTeamNickname(String teamNickname) {
        this.teamNickname = teamNickname;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getPps() {
        return pps;
    }

    public void setPps(int pps) {
        this.pps = pps;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getStarter() {
        return starter;
    }

    public void setStarter(int starter) {
        this.starter = starter;
    }

    public String getGrandfather() {
        return grandfather;
    }

    public void setGrandfather(String grandfather) {
        this.grandfather = grandfather;
    }

    public String getBatsHand() {
        return batsHand;
    }

    public void setBatsHand(String batsHand) {
        this.batsHand = batsHand;
    }

    public String getThrowsHand() {
        return throwsHand;
    }

    public void setThrowsHand(String throwsHand) {
        this.throwsHand = throwsHand;
    }
    
    public int getVintage() {
        return vintage;
    }
    
    public void setVintage(int vintage) {
        this.vintage = vintage;
    }

    public int isDfa() {
        return dfa;
    }

    public void setDfa(int dfa) {
        this.dfa = dfa;
    }

    public int isDfaProtected() {
        return dfaProtected;
    }

    public void setDfaProtected(int dfaProtected) {
        this.dfaProtected = dfaProtected;
    }

    public String getWaivers() {
        return waivers;
    }

    public void setWaivers(String waivers) {
        this.waivers = waivers;
    }
}
