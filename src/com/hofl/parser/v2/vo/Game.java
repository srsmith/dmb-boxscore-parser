package com.hofl.parser.v2.vo;

import com.hofl.parser.v2.pbp.AbstractEvent;
import com.hofl.parser.v2.pbp.GameEvent;
import com.hofl.parser.v2.pbp.GameDescriptionEvent;
import java.util.*;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

/**
 *
 * @author Scott
 */

@JsonPropertyOrder({"boxscoreId", "gameDate", "teams", "ballpark", "weather", 
"linescores", "teamTotals", "homeRoster", "awayRoster" ,"batting", "pitching"})
public class Game {
    
    // Meta information about the game
    private String boxscoreId;
    
    private Calendar gameDate;
    private Teams teams;
    private Ballpark ballpark;
    private Weather weather;
    
    // Builds the game line
    private Map<Integer, InningScore> linescores;
    private TeamTotals teamTotals;
    
    // Totals for hitters and pitchers
    private Batting batting;
    private Pitching pitching;
    
    private Roster homeRoster;
    private Roster awayRoster;
    
    // Game substitutions
    private List<Substitution> homeSubs;
    private List<Substitution> awaySubs;
    
    private AbstractEvent awayPlayByPlay;
    private AbstractEvent homePlayByPlay;
    
    // Next - figure out how to implement the box score notes totals.  IE, E-Griffey Jr., W. Clark 
    
    // After that, it's the box score notations - might be good to add the A1, D4, etc for ordering?
    
    
    // Should we persist the Pitchers IN OUT ER?  I think so - could be useful in assembling a parsed_box line
    
    // Lastly, the play by play.  We need this to synch up exactly with the box score notations
    
    // Once these objects are all added to Game, then we should create some helper constructor
    // methods that will make creating the elements as easy as passing in a raw line and having
    // the object be responsible for parsing it out.  We should also create helper methods in Game
    // that get a snapshot of the game at a certain state, be it A4 or inning and ordinal.  Consider
    // changing the ordinal to be inning-ordinal, as in the order of the play within the inning.
    
    // Then, go back to BoxScoreAssembler.parseLines() in the v2 package.  Couple of options:
    // 1. Make the line test against different line chompers for the newly-added objects, 
    //    or in some cases, map to newly created object types.
    // 2. Populate this Game object with those newly created objects.
    // 3. Replace the functionality that the StatFactory provides with a cleaner insert
    //    based on the new Game object.  The only thing that should need to be calculated is 
    //    whether or not there was an RBI on a run-scoring hit and potentially if a run is
    //    an earned run
    
    
    public Game() {
        gameDate = Calendar.getInstance();
        
        linescores = new HashMap<Integer, InningScore>();
        teamTotals = new TeamTotals();
        
        pitching = new Pitching();
        batting = new Batting();
        
        homeSubs = new ArrayList<Substitution>();
        awaySubs = new ArrayList<Substitution>();
        
        homeRoster = new Roster();
        awayRoster = new Roster();
    }
    
    public Game(String boxscoreId, Teams teams, String ballpark) {
        this();
        this.boxscoreId = boxscoreId;
        
        gameDate.set(Calendar.YEAR, Integer.parseInt(boxscoreId.substring(0, 4)));        
        gameDate.set(Calendar.MONTH, Integer.parseInt(boxscoreId.substring(4, 6))-1);
        gameDate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(boxscoreId.substring(6, 8))); 
        
        this.teams = teams;
        this.ballpark = new Ballpark(ballpark);
    }

    public void setPlayByPlay(AbstractEvent event, boolean isAway) {
        if (isAway) {
            this.awayPlayByPlay = event;
        } else {
            this.homePlayByPlay = event;
        }
    }
    
    public List<AbstractEvent> getPlayByPlay() throws Exception {
        List<AbstractEvent> playByPlay = new ArrayList<AbstractEvent>();
        int inning = 1;
        int gameOrdinal = 1;
        AbstractEvent awayEvent = this.awayPlayByPlay.getFirstEvent();
        AbstractEvent homeEvent = this.homePlayByPlay.getFirstEvent();
        
        Map<String, String> awayFielders = awayEvent.getFielders();
        Map<String, String> homeFielders = homeEvent.getFielders();
        while (awayEvent != null || homeEvent != null) {
            while (awayEvent != null && awayEvent.getInning() == inning) {
                awayEvent.setOrdinal(gameOrdinal);
                if (awayEvent.getFielders().get("P").equals("")) {
                    awayEvent.setFielders(awayFielders, true);
                }
                awayFielders = awayEvent.getFielders();
                gameOrdinal++;
                playByPlay.add(awayEvent);
                awayEvent = awayEvent.getNextEvent();
            }
            while (homeEvent != null && homeEvent.getInning() == inning) {
                homeEvent.setOrdinal(gameOrdinal);
                if (homeEvent.getFielders().get("P").equals("")) {
                    homeEvent.setFielders(homeFielders, true);
                }
                homeFielders = homeEvent.getFielders();
                gameOrdinal++;
                playByPlay.add(homeEvent);
                homeEvent = homeEvent.getNextEvent();
            }
            inning++;
        }
        return playByPlay;
    }     


    public void setWeather(String weatherLine) {
        weather = new Weather(weatherLine);
    }
    
    public Calendar getGameDate() {
        return gameDate;
    }

    public void setGameDate(Calendar gameDate) {
        this.gameDate = gameDate;
    }

    public Teams getTeams() {
        return teams;
    }

    public void setTeams(Teams teams) {
        this.teams = teams;
    }
    
    public Ballpark getBallpark() {
        return ballpark;
    }

    public void setBallpark(Ballpark ballpark) {
        this.ballpark = ballpark;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public String getBoxscoreId() {
        return boxscoreId;
    }

    public void setBoxscoreId(String boxscoreId) {
        this.boxscoreId = boxscoreId;
    }

    public void parseLineScore(String headerLine, String awayLine, String homeLine) {
        StringTokenizer headTok = new StringTokenizer(headerLine.substring(22, headerLine.length()), " ");
        StringTokenizer awayTok = new StringTokenizer(awayLine.substring(22, awayLine.length()), " ");
        StringTokenizer homeTok = new StringTokenizer(homeLine.substring(22, homeLine.length()), " ");
        Integer inning = new Integer(headTok.nextToken());
        while (inning != null) {
            String awayScore = awayTok.nextToken();
            String homeScore = homeTok.nextToken();
            if (!linescores.containsKey(inning)) {
                linescores.put(inning, 
                    new InningScore(inning.intValue(), 
                        Integer.parseInt(awayScore),
                        (homeScore.equalsIgnoreCase("x")) ? null : Integer.parseInt(homeScore)));
            }
            try {
                inning = new Integer(headTok.nextToken());
            } catch (Throwable t) {
                inning = null;
            }
        }
        // Set the team totals
        // R  H  E   LOB DP
        if (headerLine.indexOf("LOB") > -1) {
            teamTotals.setAwayTeamRuns(new Integer(awayTok.nextToken()).intValue());
            teamTotals.setHomeTeamRuns(new Integer(homeTok.nextToken()).intValue());
            teamTotals.setAwayTeamHits(new Integer(awayTok.nextToken()).intValue());
            teamTotals.setHomeTeamHits(new Integer(homeTok.nextToken()).intValue());
            teamTotals.setAwayTeamErrors(new Integer(awayTok.nextToken()).intValue());
            teamTotals.setHomeTeamErrors(new Integer(homeTok.nextToken()).intValue());
            teamTotals.setAwayTeamLOB(new Integer(awayTok.nextToken()).intValue());
            teamTotals.setHomeTeamLOB(new Integer(homeTok.nextToken()).intValue());
            teamTotals.setAwayTeamDP(new Integer(awayTok.nextToken()).intValue());
            teamTotals.setHomeTeamDP(new Integer(homeTok.nextToken()).intValue());
        }
        // Set the nicknames if they don't exist already
        this.teams.setTeamNicknames(awayLine.substring(5,22).trim(), homeLine.substring(5,22).trim());
    }

    public Map<Integer, InningScore> getInningScores() {
        return linescores;
    }
    
    public void addPitchingLine(String pitchingLine, boolean isAwayTeamPitching) {
        this.pitching.addPitcher(pitchingLine, isAwayTeamPitching);
    }

    public Pitching getPitching() {
        return pitching;
    }

    public void parseBattingLine(String rawLine) throws Exception {
        String awayLine;
        String homeLine;
        
        awayLine = rawLine.substring(0,38);
        homeLine = rawLine.length() == 80 ? rawLine.substring(42,rawLine.length()) : "";

        batting.addBatterLine(awayLine, true);
        batting.addBatterLine(homeLine, false);
    }

    public Batting getBatting() {
        return batting;
    }

    public void addBatterToRoster(Player p, boolean isAway) throws Exception {
        Roster roster = isAway ? awayRoster : homeRoster;
        roster.addHitter(p);
    }
    
    public void addStartingPitcherToRoster(Player p, boolean isAway) throws Exception {
        Roster roster = isAway ? awayRoster : homeRoster;
        roster.addStartingPitcher(p);
    }
    
    public void addBullpenPitcherToRoster(Player p, boolean isAway) throws Exception {
        Roster roster = isAway ? awayRoster : homeRoster;
        roster.addBullpenPitcher(p);
    }

    public Roster getHomeRoster() {
        return homeRoster;
    }

    public Roster getAwayRoster() {
        return awayRoster;
    }

    public TeamTotals getTeamTotals() {
        return teamTotals;
    }

    public Map<Integer, InningScore> getLinescores() {
        return linescores;
    }
    
}
