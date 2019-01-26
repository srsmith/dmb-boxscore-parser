package com.hofl.parser.v2;

import com.hofl.parser.v2.jackson.PlayerParser;
import com.hofl.parser.v2.notations.AbstractNotation;
import com.hofl.parser.v2.notations.BalkNotation;
import com.hofl.parser.v2.notations.OutNotation;
import com.hofl.parser.v2.notations.PinchHitterSubNotation;
import com.hofl.parser.v2.notations.PitchingChangeNotation;
import com.hofl.parser.v2.pbp.AbstractEvent;
import com.hofl.parser.v2.pbp.GameEvent;
import com.hofl.parser.v2.pbp.GameEvent.Pitch;
import com.hofl.parser.v2.pbp.PinchHitter;
import com.hofl.parser.v2.pbp.PitchingChange;
import com.hofl.parser.v2.vo.Batting.BattingLine;
import com.hofl.parser.v2.vo.Game;
import com.hofl.parser.v2.vo.Player;
import com.hofl.parser.v2.vo.Teams;
import com.hofl.vo.Notes;
import com.hofl.vo.PitcherLine;
import com.hofl.vo.PlayerLine;
import com.hofl.vo.notations.PinchRunnerNotation;
import com.hofl.vo.stats.PitchingLine;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Pattern;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 * This version of BoxScoreAssembler inverts the order of parent/child by making the play
 * by play notes the parent and the boxscore notations children. Reuses a lot of
 * the logic from the original com.hofl.parser.BoxScoreAssembler, but hopefully with a
 * reduction in spaghetti code.
 *
 * @author Scott
 */
public class BoxScoreAssembler implements Serializable {

    private Game game;
    private String boxScoreId;
    private BufferedReader reader;
    private int seasonType;
    private int org;
    private String awayTeamName;
    private String homeTeamName;
    private String stadium;
    private List<PitchingLine> homePitchingLines;
    private List<PitchingLine> awayPitchingLines;
    private List<PlayerLine> homePlayerLines;
    private List<PlayerLine> awayPlayerLines;
    private Map<String, Integer> homeBattingOrderMap;
    private Map<String, Integer> awayBattingOrderMap;
    private Map<String, String> homeStartingFieldersMap;
    private Map<String, String> awayStartingFieldersMap;
    private int currentHomeBattingOrder;
    private int currentAwayBattingOrder;
    private Notes homeBoxNotes;
    private Notes awayBoxNotes;
    private String homeStartingPitcher;
    private String awayStartingPitcher;
    private List<PlayByPlay> homePlayByPlay;
    private List<PlayByPlay> awayPlayByPlay;
   
    private AbstractNotation homeNotations;
    private AbstractNotation awayNotations;
    private Teams teams;
    private AbstractEvent awayEvents;
    private AbstractEvent homeEvents;

    public static void main(String[] args) {
        
        try {
            //PlayerParser playerParser = new PlayerParser("http://hofl.com/api/json/players/");
            PlayerParser playerParser = new PlayerParser("https://hofl.com/api/json/historical/1/2016071600100.json");
            Player[] players = playerParser.getPlayers();
            BoxScoreAssembler box = new BoxScoreAssembler("/Users/Scott/NetBeansProjects/boxscore-parser/boxscore_parser_svn/test/2017032100170.box");
            //BoxScoreAssembler box = new BoxScoreAssembler("/Users/scottsmith/Documents/workingdir/hofl/boxscore_parser_svn/test/2013082100200.box");

            box.setRosters(players, 1);
            printGame(box.getGame());

//            List<PlayerLine> homePlayerLines = box.getHomePlayerLines();
//
//            Map<String, Integer> batOrder = box.getAwayBattingOrderMap();
//            for (Entry<String, Integer> s : batOrder.entrySet()) {
//                int order = s.getValue();
//                System.out.println(s.getValue() + ": " + s.getKey());
//            }
        } catch (Exception e) {
            System.out.println("Exception caught while running BoxScoreAssembler: ");
            e.printStackTrace(System.out);
        }        
    }    
    
    private void init() {
        homePitchingLines = new ArrayList<PitchingLine>();
        awayPitchingLines = new ArrayList<PitchingLine>();

        homePlayerLines = new ArrayList<PlayerLine>();
        awayPlayerLines = new ArrayList<PlayerLine>();

        homeBattingOrderMap = new LinkedHashMap<String, Integer>();
        awayBattingOrderMap = new LinkedHashMap<String, Integer>();
        
        homeStartingFieldersMap = new LinkedHashMap<String, String>();
        awayStartingFieldersMap = new LinkedHashMap<String, String>();

        currentHomeBattingOrder = 0;
        currentAwayBattingOrder = 0;

        homePlayByPlay = new ArrayList<PlayByPlay>();
        awayPlayByPlay = new ArrayList<PlayByPlay>();      
    }

    private BoxScoreAssembler() {
        
    }

    public BoxScoreAssembler(String path) {
        this(path, 1); // Defaults to the majors
    }

    public BoxScoreAssembler(URL url) {
        this(url, 1, 0); // Defaults to the majors, no season type defined
    }

    public BoxScoreAssembler(String path, int org) {
        this(new File(path), org, 0); // no season type defined
    }

    public BoxScoreAssembler(String path, String org) {
        this(path, Integer.parseInt(org));
    }

    public BoxScoreAssembler(String path, String org, int type) {
        this(new File(path), Integer.parseInt(org), type);
    }

    public BoxScoreAssembler(File file) {
        this(file, 1, 0); // defaults to the majors, no season type defined
    }

    public BoxScoreAssembler(File file, String org) {
        this(file, Integer.parseInt(org), 0); // no season type defined
    }

    public BoxScoreAssembler(URL url, int org, int seasonType) {
        try {
            this.setBoxScoreId(url.toExternalForm().substring(
                    (url.toExternalForm().lastIndexOf("/") + 1),
                    url.toExternalForm().indexOf(".box")));
            this.setOrg(org);
            this.setSeasonType(seasonType);

            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            parseBoxScore();
        } catch (IOException e) {
            try {
                // Make sure it's really not there - try again.
                System.out.println("Failed 1st attempt to fetch " + url.toExternalForm() + ", retrying...");
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                parseBoxScore();
            } catch (Throwable t) {
                throw new RuntimeException("Specified box score  " + url.toExternalForm() + " does not exist");
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception caught while parsing box score " + url.toExternalForm() + ": " + e.getMessage(), e);
        }
    }

    public BoxScoreAssembler(File file, int org, int seasonType) {
        try {
            boxScoreId = file.getName().substring(0, file.getName().indexOf(".box"));
            this.setSeasonType(seasonType);
            this.setOrg(org);

            reader = new BufferedReader(new FileReader(file));
            parseBoxScore();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Specified box score " + file.getName()
                    + " does not exist");
        } catch (IOException e) {
            throw new RuntimeException("IOException caught while parsing box score " + file.getName() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Exception caught while parsing box score " + file.getName() + ": " + e.getMessage(), e);
        }
    }

    /**
     *
     * TO-DO: actual parsing :)
     */
    public void parseBoxScore() throws Exception {
        // Set up the vectors and array lists
        init();

        // Parse the first line to get stadium and teams information.
        setTeamsAndStadium(reader.readLine());
        
        // Initialize the Game object
        game = new Game(this.boxScoreId, this.teams, this.getStadium());

        // Run through all the lines of the boxscore
        parseLines();
        
        // Assemble all the notations for home and away teams.
        awayNotations = assembleNotations(true);
        homeNotations = assembleNotations(false);
        
        //awayEvents = assembleEvents(true, awayNotations);
        //homeEvents = assembleEvents(false, homeNotations);
        
        awayEvents = assembleEventsInReverse(true, awayNotations);
        homeEvents = assembleEventsInReverse(false, homeNotations);
        
        game.setPlayByPlay(awayEvents, true);
        game.setPlayByPlay(homeEvents, false);
        
        //printEvent(awayEvents.getFirstEvent());
        //printEvent(homeEvents.getFirstEvent());
    }

    public static void printGame(Game game) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        mapper.configure(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS, false);
        //mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS, false);
        try {
            System.out.println(mapper.defaultPrettyPrintingWriter().writeValueAsString(game));

        } catch (Throwable e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setTeamsAndStadium(String headerLine) {
        StringTokenizer tok = new StringTokenizer(headerLine, ",");
        tok.nextToken(); // Skip this one

        String teamStr = tok.nextToken();
        StringTokenizer teamTok = new StringTokenizer(teamStr, "-");
        
        String awayTeamName = teamTok.nextToken().trim().substring(0, 3);
        try {
            Integer.parseInt(awayTeamName.substring(2, 3));
            // remove the last character - two letter team name.
            awayTeamName = awayTeamName.substring(0, 2);
        } catch (Throwable t) {
            // do nothing - it's a valid team name.
        }        
        String homeTeamName = teamTok.nextToken().trim().substring(0, 3);
        try {
            Integer.parseInt(homeTeamName.substring(2, 3));
            // remove the last character - two letter team name.
            homeTeamName = homeTeamName.substring(0, 2);
        } catch (Throwable t) {
            // do nothing - it's a valid team name.
        }
        
        this.setAwayTeamName(awayTeamName);
        this.setHomeTeamName(homeTeamName);

        this.teams = new Teams(this.getAwayTeamName(), this.getHomeTeamName());
        
        String stadiumName = tok.nextToken().trim();
        if (stadiumName.indexOf("game") >= 0) {
            // Handle for double headers and skip to the next token for the stadium
            stadiumName = tok.nextToken().trim();
        }
        this.setStadium(stadiumName);
    }

    private void parseLines() throws Exception {
        // This may need some serious revisiting.  The logic on parsing the lines
        // is solid, but this could use some refactoring.  One thing to consider
        // might be to handle for the pinch runners, pinch hitters, defensive
        // substitutions, and defensive changes using the existing logic that currently
        // only handles for pinch runners.  Shoudl skip the pitching changes?  Already
        // handled for in the notes area.  Look into this - it already works the current
        // way so do we want to mix that up?
        String line;
        boolean awayTeamIsPitching = true;

        int asterixLineCt = 0;
        int asterixInning = 0;
        int headerLineCt = 0;
        int headerArrayIdx = -1;


        while ((line = reader.readLine()) != null) {
            line = line.replaceAll("\f", ""); // Get rid of line feeds that mess up parsing.

            if (line.indexOf(PitchingLine.PITCHING_HEADER) > -1) {
                parsePitchingLines(awayTeamIsPitching); // Away comes first
                awayTeamIsPitching = false; // Switch it to home team for next round
            } else if (PlayerLine.isOfType(line)) {
                if (checkIfHeaderLine(line)) {
                    headerLineCt++;
                    if (headerLineCt % 2 != 0) {
                        // Increment this every time we hit the visitor so we know which
                        // header value to append (if we have extra inning games, there will
                        // be more than just A-E...
                        headerArrayIdx++;
                    }
                } else {
                    // Not a header line - parse into PlayerLine
                    addPlayerLine(line, headerLineCt, headerArrayIdx);
                }
            } else if (Notes.isOfType(line)) {
                addNote(line, headerLineCt);
            } else if (PitcherLine.isOfType(line)) {
                line = reader.readLine();
                awayStartingPitcher = line.substring(0, 17).trim();
                homeStartingPitcher = line.substring(44, 61).trim();                
            } else if (line.trim().indexOf("Temperature:") > -1) {
                game.setWeather(line);
            } else if (line.indexOf("**************") > -1) {
                // this is a indicator of a new inning beginning
                asterixLineCt++;
                if (asterixLineCt % 2 != 0) {
                    asterixInning++;
                }
            } else if (line.indexOf("AB  R  H BI   AVG") > -1) {
                int awayOrder = 1;
                int homeOrder = 1;
                line = reader.readLine();
                while (line.length() > 37) {
                    game.parseBattingLine(line);
                    line = reader.readLine();
                }
                
            } else if (linescoreHeaderPattern.matcher(line).find()) {
                game.parseLineScore(line, reader.readLine(), reader.readLine());
            } else if (asterixLineCt > 0) {
                // Means we're into the last part of the box
                addPlayByPlayNote(line, asterixLineCt, asterixInning);
            }
        }
        
        // Close up the reader and nullify
        try {
            reader.close();
        } catch (Throwable t) {
            // Do nothing
        } finally {
            reader = null;
        }
    }

    private void parsePitchingLines(boolean awayTeamIsPitching) throws Exception {
        String line = reader.readLine();

        boolean started = true;
        int pitcherOrder = 1;
        
        while (line.substring(0, 12).trim().length() > 0) {
            game.addPitchingLine(line, awayTeamIsPitching);
            line = reader.readLine();
        }
    }
    

    private boolean checkIfHeaderLine(String line) {
        for (int i = 0; i < HEADER_ARRAY.length; i++) {
            if (line.trim().indexOf(HEADER_ARRAY[i]) > -1) {
                return true;
            }
        }
        return false;
    }

    private void addPlayerLine(String line, int headerLineCt, int headerArrayIdx) {

        PlayerLine playerLine = new PlayerLine(line, (6 * headerArrayIdx));
        String playerName = playerLine.getPlayerName();
        
        List<PlayerLine> playerLines;
        Map<String, Integer> battingOrderMap;
        Map<String, String> startingFieldersMap;
        int battingOrder = -1;

        // If the headerLineCt is an odd number, it's a visitor line.  If it's even, it's a home.
        if (headerLineCt % 2 == 0) {
            playerLines = this.homePlayerLines;
            battingOrderMap = this.homeBattingOrderMap;
            battingOrder = getCurrentHomeBattingOrder();
            startingFieldersMap = this.homeStartingFieldersMap;
        } else {
            playerLines = this.awayPlayerLines;
            battingOrderMap = this.awayBattingOrderMap;
            battingOrder = getCurrentAwayBattingOrder();
            startingFieldersMap = this.awayStartingFieldersMap;
        }

        playerLines.add(playerLines.size(), playerLine);

        if (!battingOrderMap.containsKey(playerName.trim()) && playerName.trim().length() > 0) {
            if (line.indexOf(" ") == 0 && playerName.trim().length() > 0) {
                // If there's a space in the first line of the name, it's a pinch hitter,
                // so keep the batting order the same.  But make sure it's not an empty line
            } else {
                // Increment the order otherwise.
                if (headerLineCt % 2 == 0) {
                    incrementHomeBattingOrder();
                    battingOrder = getCurrentHomeBattingOrder();
                } else {
                    incrementAwayBattingOrder();
                    battingOrder = getCurrentAwayBattingOrder();
                }
                // Set the starting fielder (if not a DH) since it's not a sub 
                if (AbstractEvent.validPosition(playerLine.getPosition())) {
                    startingFieldersMap.put(playerLine.getPosition(), playerName.trim());                
                }
            }
            battingOrderMap.put(playerName.trim(), new Integer(battingOrder));
            
        }
    }

    private void addNote(String line, int headerLineCt) {
        // If the headerLineCt is an odd number, it's a visitor line.  If it's even, it's a home.
        // System.out.println("adding note: " + line);
        if (headerLineCt % 2 == 0) {
            if (homeBoxNotes == null) {
                homeBoxNotes = new Notes(line);
            } else {
                homeBoxNotes.parseRawLine(line);
            }
        } else {
            if (awayBoxNotes == null) {
                awayBoxNotes = new Notes(line);
            } else {
                awayBoxNotes.parseRawLine(line);
            }
        }
    }

    private void addPlayByPlayNote(String line, int asterixLineCt, int asterixInning) {
        List<PlayByPlay> pbpList = asterixLineCt % 2 == 0 ? homePlayByPlay : awayPlayByPlay;
        
        //System.out.println((asterixLineCt % 2 == 0 ? "HOME" : "AWAY") + " addPlayByPlayNote(" + line + "," + asterixLineCt + "," + asterixInning);
        Pattern pattern = Pattern.compile(detailPatternString);

        if (pattern.matcher(line).find() || pbpList.size() == 0) {
            // If pbpList.size() == 0, then there's no previous pbp to append to and it's the first event of the game but not game event
            pbpList.add(new PlayByPlay(asterixInning, line));
        } else if (line.length() > 18 && line.substring(16, 18).trim().length() > 0) {
            // This is an informational aka non game event event.
            pbpList.add(new PlayByPlay(asterixInning, line.trim()));            
        } else {
            // Get the previous detail note out and append the current line if it's
            // a wrapped line.  If it isn't, then add it as it's own line because
            // it is either an inning marker or an information event (substitution, rain delay, etc)
            PlayByPlay lastPbp = (PlayByPlay) pbpList.get(pbpList.size() - 1);
            pbpList.remove(pbpList.size() - 1);
            pbpList.add(new PlayByPlay(asterixInning, lastPbp.getDescription() + " " + line.trim()));
            
        }
    }
    
    
    public AbstractEvent assembleEvents(boolean isAway, AbstractNotation notations) throws Exception {
        AbstractEvent event = null;
        AbstractNotation currentNotation = notations.getFirstNotation();
        List<PlayByPlay> pbpList = isAway ? awayPlayByPlay : homePlayByPlay;
        String inningPart = isAway ? "Top" : "Bottom";
        Iterator<PlayByPlay> pbpiter = pbpList.iterator();
        int ordinal = 1;
        while (pbpiter.hasNext()) {
            PlayByPlay pbp = pbpiter.next();
            if (event == null) {
                event = AbstractEvent.getEvent(pbp.getDescription());
            } else {
                event = event.addChildEvent(pbp.getDescription());
            }
            event.setInning(pbp.getInning());
            event.setOrdinal(ordinal);
            event.setInningPart(inningPart);
            
            if (event instanceof PitchingChange) {
                ((PitchingChange)event).setNotation((PitchingChangeNotation)currentNotation);
            } else if (event instanceof GameEvent) {
                while (currentNotation instanceof PitchingChangeNotation) {
                    currentNotation = currentNotation.getNextNotation();
                }
                ((GameEvent)event).setNotation(currentNotation);
                currentNotation = currentNotation.getNextNotation();
            }
            ordinal++;
        }
        return event;
    }
    
    public AbstractEvent assembleEventsInReverse(boolean isAway, AbstractNotation notations) throws Exception {
        AbstractEvent event = null;
        AbstractNotation currentNotation = notations.getLastNotation();
        List<PlayByPlay> pbpList = isAway ? awayPlayByPlay : homePlayByPlay;
        String inningPart = isAway ? "Top" : "Bottom";
        
        for (int i = (pbpList.size()-1); i > -1; i--) {
            PlayByPlay pbp = pbpList.get(i);
            if (event == null) {
                event = AbstractEvent.getEvent(pbp.getDescription());
            } else {
                event = event.addParentEvent(pbp.getDescription());
            }
            event.setInning(pbp.getInning());
            event.setInningPart(inningPart);
            
            if (event instanceof PitchingChange && currentNotation instanceof PitchingChangeNotation) {
                ((PitchingChange)event).setNotation((PitchingChangeNotation)currentNotation);
                currentNotation.setPlayByPlayDescription(event.getDescription());

            } else if (event instanceof GameEvent) {
                
                while (currentNotation instanceof PitchingChangeNotation) {
                    currentNotation = currentNotation.getPreviousNotation();
                }
                currentNotation.setPlayByPlayDescription(event.getDescription());
                ((GameEvent)event).setNotation(currentNotation);
                currentNotation = currentNotation.getPreviousNotation();
                
                // Handle for sub events
                int[] subEventPitchIdx = ((GameEvent)event).getPitchIndexForSubEvents();
                for (int j=subEventPitchIdx.length-1; j > -1; j--) { 
                    i--;
                    PlayByPlay subPbp = pbpList.get(i);
                    
                    AbstractEvent subEvent = AbstractEvent.getEvent(subPbp.getDescription());
                    
                    PinchHitterSubNotation pinchHitterNotation = null;
                    
                    if (subEvent instanceof PinchHitter) {
                        pinchHitterNotation = new PinchHitterSubNotation(subPbp.getDescription());
                        pinchHitterNotation.setInning(pbp.getInning());
                        ((GameEvent)event).setNotationForPitch(pinchHitterNotation, subEventPitchIdx[j]);
                        ((GameEvent)event).setPlayerAtBat(currentNotation.getPlayerAtBat());
                        ((GameEvent)event).setPlayerAtPosition(currentNotation.getPlayerAtBatPosition());
                        i--;
                        subPbp = pbpList.get(i);
                        subEvent = AbstractEvent.getEvent(subPbp.getDescription());
                    }
                    else if (subEvent instanceof GameEvent) {
                        // Handle for score that changes from a sub event to set the score to the very first sub-event's score.
                        ((GameEvent)event).setAwayScore(((GameEvent)subEvent).getAwayScore());
                        ((GameEvent)event).setHomeScore(((GameEvent)subEvent).getHomeScore());
                    }
                    currentNotation.setPlayByPlayDescription(subEvent.getDescription());
                    ((GameEvent)event).setNotationForPitch(currentNotation, subEventPitchIdx[j]);
                    
                    if (pinchHitterNotation != null) {
                        event.setFielder(pinchHitterNotation.getPinchHitterName(), "BA");
                    }
                    currentNotation = currentNotation.getPreviousNotation();

                }
            }

        }
        // Lastly, set the starting fielders - remember that isAway means set the home 
        if (isAway) {
            event.setFielders(homeStartingFieldersMap, true);
            event.setFielder(this.homeStartingPitcher, "P");
        } else {
            event.setFielders(awayStartingFieldersMap, true);
            event.setFielder(this.awayStartingPitcher, "P");
        }
        
        return event;
    }
 
    public AbstractNotation assembleNotations(boolean isAway) throws Exception {
        AbstractNotation notation = null;
        String startingPitcher = null;
        List<PlayerLine> lines = null;
        Map<String, Integer> battingOrder;
        Notes notes = null;
        int inning = 0;
        
        if (isAway) {
            lines = awayPlayerLines;
            notes = awayBoxNotes;
            battingOrder = this.getAwayBattingOrderMap();
        } else {
            lines = homePlayerLines;
            notes = homeBoxNotes;
            battingOrder = this.getHomeBattingOrderMap();
        }

        for (int hdr = 0; hdr < ALPHABET_ARRAY.length; hdr++) {
            for (int lineIdx = 0; lineIdx < lines.size(); lineIdx++) {
                PlayerLine pl = (PlayerLine) lines.get(lineIdx);
                if (pl.getOutcome(ALPHABET_ARRAY[hdr]) != null
                        && pl.getOutcome(ALPHABET_ARRAY[hdr]).trim().length() > 0) {

                    // Get the outcome
                    String outcome = pl.getOutcome(ALPHABET_ARRAY[hdr]).trim();
                    if (outcome.indexOf(">") > 0) {
                        inning++;
                        outcome = outcome.substring(outcome.indexOf(">") + 1);
                    }
                    if (outcome.indexOf("note") > -1) {
                        String note = null;
                        int notesIdx = -1;
                        try {
                            notesIdx = new Integer(outcome.trim().substring(5, outcome.trim().length())).intValue();
                        } catch (Throwable t) {
                            System.out.println("problem getting notes idx for: " + outcome);
                            t.printStackTrace();
                        }
                        outcome = notes.getNextNote(notesIdx);
                    }

                    // Add notation

                    if (notation == null) {
                        notation = AbstractNotation.getNotation(outcome);
                    } else {
                        notation = notation.addChildNotation(outcome);
                    }
                    notation.setInning(inning);
                    String playerAtBat = pl.getPlayerName().trim();
                    if (playerAtBat.length() == 0) {
                        int tmpLineIdx = lineIdx - 1;
                        while (playerAtBat.length() == 0) {
                            playerAtBat = ((PlayerLine) lines.get(tmpLineIdx)).getPlayerName().trim();
                            tmpLineIdx--;
                        }
                    }
                    notation.setPlayerAtBat(playerAtBat);
                    BattingLine batLine = game.getBatting().getBattingLine(playerAtBat, isAway);
                    if (batLine.getBattingOrder() > 9) {
                        throw new Exception("Batting order cannot be greater than 9 [" 
                                + batLine.getBattingOrder() + " found for " + playerAtBat + " on " + 
                                (isAway ? " away team " : " home team"));
                    }
                    notation.setPlayerAtBatBattingOrder(batLine.getBattingOrder());
                    notation.setPlayerAtBatPosition(batLine.getPosition());

                }
            }
        }
        return notation;
    }   
    

    public void setRosters(Player[] players, int level) throws Exception {
        String awayNickname = game.getTeams().getAwayTeam().getNickname();
        String homeNickname = game.getTeams().getHomeTeam().getNickname();
        
        //System.out.println("awayId = " + awayNickname + ", homeId = " + homeNickname);
        
        for (int i=0; i < players.length; i++) {
            Player p = players[i];
            if (p.getShortName() != null && p.getFranchiseId() > 0 && p.getLevel() == level &&
                    (p.getTeamNickname().equals(awayNickname) || p.getTeamNickname().equals(homeNickname))) {
                boolean isAway = (p.getTeamNickname().equals(awayNickname));
                String pos = p.getPosition();
                if (pos != null && pos.equals("SP")) {
                    game.addStartingPitcherToRoster(p, isAway);
                } else if (pos != null && pos.equals("RP")) {
                    game.addBullpenPitcherToRoster(p, isAway);
                } else {
                    game.addBatterToRoster(p, isAway);
                }
            }
        }
    }    
    
    
    public String getBoxScoreId() {
        return boxScoreId;
    }

    public void setBoxScoreId(String boxScoreId) {
        this.boxScoreId = boxScoreId;
    }

    private void setSeasonType(int seasonType) {
        this.seasonType = seasonType;
    }

    private void setOrg(int org) {
        this.org = org;
    }

    private void setAwayTeamName(String awayTeamName) {
        this.awayTeamName = awayTeamName;
    }

    private void setHomeTeamName(String homeTeamName) {
        this.homeTeamName = homeTeamName;
    }

    private void setStadium(String stadium) {
        this.stadium = stadium;
    }

    public int getSeasonType() {
        return seasonType;
    }

    public int getOrg() {
        return org;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getStadium() {
        return stadium;
    }
    
    public static final String A_HEADER = "-A-";
    public static final String F_HEADER = "-F-";
    public static final String K_HEADER = "-K-";
    public static final String P_HEADER = "-P-";
    public static final String U_HEADER = "-U-";
    public static final String[] HEADER_ARRAY = new String[]{A_HEADER, F_HEADER, K_HEADER, P_HEADER, U_HEADER};
    public static final String[] ALPHABET_ARRAY = new String[]{"A", "B", "C", "D", "E", "F", "G", "H",
        "I", "J", "K", "L", "M", "N", "O", "P",
        "Q", "R", "S", "T", "U", "V", "X", "Y", "Z"};
    
    public static String detailPatternString = "[\\s\\d][\\d][\\-][\\d][\\s\\d]";
    public static Pattern linescoreHeaderPattern = Pattern.compile("([ ]{23}1[ ]{2}2)|([ ]{22}[1-9]{2}[ ][1-9]{2})");

    public List<PitchingLine> getHomePitchingLines() {
        return homePitchingLines;
    }

    public List<PitchingLine> getAwayPitchingLines() {
        return awayPitchingLines;
    }

    public List<PlayerLine> getHomePlayerLines() {
        return homePlayerLines;
    }

    public List<PlayerLine> getAwayPlayerLines() {
        return awayPlayerLines;
    }

    public Map<String, Integer> getHomeBattingOrderMap() {
        return homeBattingOrderMap;
    }

    public Map<String, Integer> getAwayBattingOrderMap() {
        return awayBattingOrderMap;
    }

    public int getCurrentHomeBattingOrder() {
        return currentHomeBattingOrder;
    }
    
    public void incrementHomeBattingOrder() {
        this.currentHomeBattingOrder++;
    }

    public int getCurrentAwayBattingOrder() {
        return currentAwayBattingOrder;
    }

    public void incrementAwayBattingOrder() {
        this.currentAwayBattingOrder++;
    }

    public Game getGame() {
        return game;
    }

}
