package com.hofl.parser;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

import com.hofl.utility.StatFactory;
import com.hofl.vo.FielderMap;
import com.hofl.vo.Notes;
import com.hofl.vo.PinchRunners;
import com.hofl.vo.PitcherLine;
import com.hofl.vo.PlayerLine;
import com.hofl.vo.notations.AbstractNotation;
import com.hofl.vo.notations.PinchRunnerNotation;
import com.hofl.vo.notations.PitchingChangeNotation;
import com.hofl.vo.stats.PitchingLine;

public class BoxScore implements Serializable {

    public static final String HOME = "H";
    public static final String AWAY = "A";
    // private File boxScoreFile;
    private BufferedReader reader;
    private String boxScoreId;
    private String homeTeamName;
    private String awayTeamName;
    private String stadium;
    private Vector homePlayerLines;
    private Vector awayPlayerLines;
    private Notes homeBoxNotes;
    private Notes awayBoxNotes;
    private String homeStartingPitcher;
    private String awayStartingPitcher;
    private PinchRunners pinchRunners;
    private Vector homePlayByPlay;
    private Vector awayPlayByPlay;
    private Vector homePitchingLines;
    private Vector awayPitchingLines;
    private AbstractNotation homeNotations;
    private AbstractNotation awayNotations;
    private Hashtable homeBattingOrder;
    private Hashtable awayBattingOrder;
    private int homeOrderOrdinal;
    private int awayOrderOrdinal;
    private int battingOrderHome;
    private int battingOrderAway;
    private int org;
    private int seasonType;
    // Fielder mappings
    private FielderMap fielders;

    private void init() {
        // initialize vectors
        homePlayerLines = new Vector();
        awayPlayerLines = new Vector();

        homePlayByPlay = new Vector();
        awayPlayByPlay = new Vector();

        pinchRunners = new PinchRunners();

        homeBattingOrder = new Hashtable();
        awayBattingOrder = new Hashtable();

        battingOrderHome = 0;
        battingOrderAway = 0;

        org = 1; // default is the majors

        homePitchingLines = new Vector();
        awayPitchingLines = new Vector();

        setSeasonType(0); // season type is not defined
    }

    public BoxScore(String path) {
        this(path, 1); // Defaults to the majors
    }

    public BoxScore(URL url) {
        this(url, 1, 0); // Defaults to the majors, no season type defined
    }

    public BoxScore(String path, int org) {
        this(new File(path), org, 0); // no season type defined
    }

    public BoxScore(String path, String org) {
        this(path, Integer.parseInt(org));
    }

    public BoxScore(String path, String org, int type) {
        this(new File(path), Integer.parseInt(org), type);
    }

    public BoxScore(File boxScoreFile) {
        this(boxScoreFile, 1, 0); // defaults to the majors, no season type defined
    }

    public BoxScore(File boxScoreFile, String org) {
        this(boxScoreFile, Integer.parseInt(org), 0); // no season type defined
    }

    public BoxScore(URL url, int org, int seasonType) {
        try {
            boxScoreId = url.toExternalForm().substring(
                    (url.toExternalForm().lastIndexOf("/") + 1),
                    url.toExternalForm().indexOf(".box"));
            //System.out.println("Opening reader for " + url);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            //System.out.println("Thread opened for " + url);
            parseBoxScore(boxScoreId, org, seasonType);
        } catch (IOException e) {
            try {
                // Make sure it's really not there - try again.
                System.out.println("Failed 1st attempt to fetch " + url.toExternalForm() + ", retrying...");
                reader = new BufferedReader(new InputStreamReader(url.openStream()));
                parseBoxScore(boxScoreId, org, seasonType);
            } catch (Throwable t) {
                throw new RuntimeException("Specified box score  " + url.toExternalForm() + " does not exist");
            }
        } catch (Exception e) {
            throw new RuntimeException("Exception caught while parsing box score " + url.toExternalForm() + ": " + e.getMessage(), e);
        }

    }

    public BoxScore(File boxScoreFile, int org, int seasonType) {
        try {
            boxScoreId = boxScoreFile.getName().substring(0, boxScoreFile.getName().indexOf(".box"));
            reader = new BufferedReader(new FileReader(boxScoreFile));
            parseBoxScore(boxScoreId, org, seasonType);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Specified box score " + boxScoreFile.getName()
                    + " does not exist");
        } catch (IOException e) {
            throw new RuntimeException("IOException caught while parsing box score " + boxScoreFile.getName() + ": " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Exception caught while parsing box score " + boxScoreFile.getName() + ": " + e.getMessage(), e);
        }
    }

    private void parseBoxScore(String boxscoreId, int org, int seasonType) throws Exception {
        // Set up the vectors and array lists
        init();

        // Set the macro level variables
        this.boxScoreId = boxScoreId;
        this.org = org;
        this.setSeasonType(seasonType);

        // Parse the first line to get stadium and teams information.
        setTeamsAndStadium(reader.readLine());

        // Run through all the lines of the boxscore
        parseLines();

        // Assemble all the notations for home and away teams.
        awayNotations = assembleNotations(true);
        homeNotations = assembleNotations(false);
    }

    public void printDetails() {
        printVectorStrings(homePlayByPlay);
        printVectorStrings(awayPlayByPlay);
    }

    private void printVectorStrings(Vector v) {
        System.out.println("[START] Number of elements in Vector: " + v.size());
        for (int i = 0; i < v.size(); i++) {
            System.out.println(i + ": " + (String) v.get(i));
        }
        System.out.println("[END] Number of elements in Vector: " + v.size());

    }

    public String getBoxScoreId() {
        return this.boxScoreId;
    }

    public StatFactory getStatFactory() throws Exception {
        return new StatFactory(awayNotations, homeNotations, this.pinchRunners, this.awayBattingOrder, this.homeBattingOrder, this.org, this.getSeasonType());
    }

    private void setTeamsAndStadium(String headerLine) {
        StringTokenizer tok = new StringTokenizer(headerLine, ",");
        tok.nextToken();
        String teamStr = tok.nextToken();
        StringTokenizer teamTok = new StringTokenizer(teamStr, "-");
        awayTeamName = teamTok.nextToken().trim().substring(0, 3);
        try {
            int dummyInt = new Integer(awayTeamName.substring(2, 3)).intValue();
            // remove the last character - two letter team name.
            awayTeamName = awayTeamName.substring(0, 2);
        } catch (Throwable t) {
            // do nothing - it's a valid team name.
        }
        homeTeamName = teamTok.nextToken().trim().substring(0, 3);
        try {
            int dummyInt = new Integer(homeTeamName.substring(2, 3)).intValue();
            // remove the last character - two letter team name.
            homeTeamName = homeTeamName.substring(0, 2);
        } catch (Throwable t) {
            // do nothing - it's a valid team name.
        }
        stadium = tok.nextToken().trim();
        if (stadium.indexOf("game") >= 0) // Handle for double headers
        {
            stadium = tok.nextToken().trim();
        }
    }

    private void parseLines() throws Exception {
        String line;

        int headerLineCt = 0;
        int headerArrayIdx = -1;
        int asterixLineCt = 0;
        int asterixInning = 0;
        boolean awayTeamPitching = true;
        boolean started = true;

        while ((line = reader.readLine()) != null) {
            line = line.replaceAll("\f", ""); // Get rid of line feeds that mess up parsing.
            if (line.indexOf(PitchingLine.PITCHING_HEADER) > -1) {
                line = reader.readLine();
                int awayPitcherOrder = 1;
                int homePitcherOrder = 1;
                while (line.substring(0, 12).trim().length() > 0) {
                    if (awayTeamPitching) {
                        awayPitchingLines.add(new PitchingLine(line, this.boxScoreId, this.awayTeamName,
                                this.stadium, AWAY, this.org, this.getSeasonType(), started, awayPitcherOrder));
                        awayPitcherOrder++;
                    } else {
                        homePitchingLines.add(new PitchingLine(line, this.boxScoreId, this.homeTeamName,
                                this.stadium, HOME, this.org, this.getSeasonType(), started, homePitcherOrder));
                        homePitcherOrder++;
                    }

                    started = false;
                    line = reader.readLine();
                }
                // Attempt to set the finished attribute on the last player in the Vector
                if (awayTeamPitching) {
                    PitchingLine lastPitcher =
                            (PitchingLine) awayPitchingLines.get(awayPitchingLines.size() - 1);
                    lastPitcher.setFinished(true);
                    awayPitchingLines.set(awayPitchingLines.size() - 1, lastPitcher);
                } else {
                    PitchingLine lastPitcher =
                            (PitchingLine) homePitchingLines.get(homePitchingLines.size() - 1);
                    lastPitcher.setFinished(true);
                    homePitchingLines.set(homePitchingLines.size() - 1, lastPitcher);
                }
                awayTeamPitching = false;
                started = true;
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
            } else if (line.indexOf("**************") > -1) {
                // this is a indicator of a new inning beginning
                asterixLineCt++;
                if (asterixLineCt % 2 != 0) {
                    asterixInning++;
                }
            } else if (line.indexOf(" pinch running for ") > -1) {
                String pinchRunner = line.substring(0, line.indexOf(" pinch running for ")).trim();
                String runnerOnBase = line.substring(line.indexOf(" pinch running for ") + 19, line.length()).trim();
                PinchRunnerNotation prNote = new PinchRunnerNotation(pinchRunner, asterixInning, runnerOnBase);
                pinchRunners.addPinchRunnerNotation(prNote);
            } else if (asterixLineCt > 0) {
                // Means we're into the last part of the box
                addPlayByPlayNote(line, asterixLineCt, asterixInning);
            }
        }
        try {
            reader.close();
        } catch (Throwable t) {
        } finally {
            reader = null;
        }
    }

    public AbstractNotation assembleNotations(boolean isAway) throws Exception {
        AbstractNotation notation = null;
        String startingPitcher = null;
        Vector lines = null;
        int inning = 0;
        int boxScoreOrdinal = 0;
        int playByPlayNoteOrdinal = 0;
        if (isAway) {
            lines = awayPlayerLines;
            startingPitcher = homeStartingPitcher;
            //System.out.println("Starting home pitcher: " + startingPitcher);
        } else {
            lines = homePlayerLines;
            startingPitcher = awayStartingPitcher;
            //System.out.println("Starting visiting pitcher: " + startingPitcher);
        }

        notation = new PitchingChangeNotation(startingPitcher);
        notation.setInning(1);
        notation.setBoxScoreId(getBoxScoreId());
        notation.setBoxScoreOrdinal(boxScoreOrdinal);

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
                            //System.out.println("problem getting notes idx for: " + outcome);
                            //t.printStackTrace();
                        }
                        try {
                            if (isAway) {
                                note = awayBoxNotes.getNextNote(notesIdx);
                            } else {
                                note = homeBoxNotes.getNextNote(notesIdx);
                            }
                        } catch (Throwable t) {
                            //System.out.println("Throwable caught trying to get notesIdx " + notesIdx + ", isAway = " + isAway + " for playerName: " + pl.getPlayerName());

                            if (isAway) {
                                if (awayBoxNotes == null) {
                                    //System.out.println("awayBoxNotes IS null");
                                } else {
                                    //System.out.println("awayBoxNotes not null");
                                }
                            } else if (homeBoxNotes == null) {
                                //System.out.println("homeBoxNotes IS null");
                            } else {
                                //System.out.println("homeBoxNotes not null");
                            }

                            throw new RuntimeException("BLECH!");
                        }
                        outcome = note;
                    }

                    // Add notation
                    try {
                        if (notation == null) {
                            notation = AbstractNotation.getNotation(outcome);
                        } else {
                            notation = notation.addChildNotation(outcome);
                        }

                        notation.setInning(inning);
                        notation.setPlayerAtBat(pl.getPlayerName());
                        notation.setBoxScoreId(getBoxScoreId());
                        notation.setStadium(stadium);
                        notation.setBoxScoreOrdinal(boxScoreOrdinal++);
                        notation.setPlayerPosition(pl.getPosition());

                        if (isAway) {
                            notation.setTeamName(awayTeamName);
                            notation.setOpposingTeamName(homeTeamName);
                            if (!notation.getNotationType().equals(PitchingChangeNotation.NOTATION_TYPE)) {
                                try {
                                    notation.setPlayByPlayNote((String) awayPlayByPlay.get(playByPlayNoteOrdinal));
                                    playByPlayNoteOrdinal++;
                                } catch (ArrayIndexOutOfBoundsException aioob) {
                                    System.out.println(notation.getBoxScoreId() + ": Tried to add " + notation.getPlayerAtBat() + " "
                                            + notation.getNotationValue() + " (inn " + notation.getInning()
                                            + ") to awayPlayByPlay (size=" + awayPlayByPlay.size() + ") at index " + playByPlayNoteOrdinal);
                                }
                            }
                        } else {
                            notation.setTeamName(homeTeamName);
                            notation.setOpposingTeamName(awayTeamName);

                            if (!notation.getNotationType().equals(PitchingChangeNotation.NOTATION_TYPE)) {
                                try {
                                    notation.setPlayByPlayNote((String) homePlayByPlay.get(playByPlayNoteOrdinal));
                                    playByPlayNoteOrdinal++;
                                } catch (ArrayIndexOutOfBoundsException aioob) {
                                    System.out.println(notation.getBoxScoreId() + ": Tried to add " + notation.getPlayerAtBat() + " "
                                            + notation.getNotationValue() + " (inn " + notation.getInning()
                                            + ") to homePlayByPlay (size=" + homePlayByPlay.size() + ") at index " + playByPlayNoteOrdinal);
                                }
                            }
                        }
                        //System.out.println("Notation added : ordinal:" + notation.getBoxScoreOrdinal()
                        //    + " type: " + notation.getNotationType() + " orignal notation: " +outcome);
                    } catch (NumberFormatException nfe) {
                        //System.out.println(nfe + " '" + (String)homePlayByPlay.get(playByPlayNoteOrdinal) + "'");
                        if (isAway) {
                            System.out.println("NFE Caught: " + notation.getBoxScoreId() + ".box : " + notation.getNotationValue() + " inn " + notation.getInning()
                                    + " - playByPlayNoteOrdinal = " + playByPlayNoteOrdinal
                                    + ", awayPlayByPlay.size() = " + awayPlayByPlay.size());
                        } else {
                            System.out.println("NFE Caught: " + notation.getBoxScoreId() + ".box : "
                                    + notation.getPlayerAtBat() + " "
                                    + notation.getNotationValue() + " inn " + notation.getInning()
                                    + " - playByPlayNoteOrdinal = " + playByPlayNoteOrdinal
                                    + ", homePlayByPlay.size() = " + homePlayByPlay.size());
                        }
                        nfe.printStackTrace(System.out);

                    } catch (StringIndexOutOfBoundsException sioobe) {
                        System.out.println(sioobe + " '" + (String) homePlayByPlay.get(playByPlayNoteOrdinal) + "'");
                        sioobe.printStackTrace();
                    } catch (Throwable t) {
                        t.printStackTrace();
                        //System.out.println("Caught exception adding a notation, should be a pitcher: " + outcome);
                    }
                }
            }
        }
        return notation;
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
        // If the headerLineCt is an odd number, it's a visitor line.  If it's even, it's a home.
        PlayerLine pLine = new PlayerLine(line, (6 * headerArrayIdx));
        if (headerLineCt % 2 == 0) {
            // THERE IS A PROBLEM WITH GAMES THAT GO INTO MULTIPLE LINES PASSING THE WRONG BATTING ORDER (3/12)
            homePlayerLines.add(homePlayerLines.size(), pLine);
            battingOrderHome = setBattingOrder(pLine, homeBattingOrder, battingOrderHome);
        } else {
            awayPlayerLines.add(awayPlayerLines.size(), pLine);
            battingOrderAway = setBattingOrder(pLine, awayBattingOrder, battingOrderAway);
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
        System.out.println("addPlayByPlayNote(" + line + "," + asterixLineCt + "," + asterixInning);
        Pattern pattern = Pattern.compile(detailPatternString);
        boolean newLine = pattern.matcher(line).find();
        if (newLine) {
            // Add the new line to the notes.
            if (asterixLineCt % 2 == 0) {
                homePlayByPlay.add(line);
            } else {
                awayPlayByPlay.add(line);
            }
        } else {
            // Get the previous detail note out and append the current line.
            String tmpLine;
            if (asterixLineCt % 2 == 0 && homePlayByPlay.size() > 0) {
                tmpLine = (String) homePlayByPlay.lastElement();
                if (tmpLine.indexOf(")") < 0) {
                    homePlayByPlay.remove(homePlayByPlay.size() - 1);
                    homePlayByPlay.add(tmpLine + " " + line.trim());
                }
            } else if (awayPlayByPlay.size() > 0) {
                tmpLine = (String) awayPlayByPlay.lastElement();
                if (tmpLine.indexOf(")") < 0) {
                    awayPlayByPlay.remove(awayPlayByPlay.size() - 1);
                    awayPlayByPlay.add(tmpLine + " " + line.trim());
                }
            }
        }
    }

    private int setBattingOrder(PlayerLine playerLine, Hashtable battingOrderMap, int battingOrder) {
        String playerName = playerLine.getPlayerName();
        if (!battingOrderMap.containsKey(playerName.trim()) && playerName.trim().length() > 0) {
            if (playerName.indexOf(" ") == 0 && playerName.trim().length() > 0) {
                // If there's a space in the first line of the name, it's a pinch hitter,
                // so keep the batting order the same.  But make sure it's not an empty line
            } else {
                // Increment the order otherwise.
                battingOrder++;
            }
            battingOrderMap.put(playerName.trim(), new Integer(battingOrder));
        }

        return battingOrder;
    }

    public String getPitchingLinesAsSQLInserts() {
        StringBuffer b = new StringBuffer();
        for (int i = 0; i < awayPitchingLines.size(); i++) {
            b.append(((PitchingLine) awayPitchingLines.get(i)).getAsFlattenedSQLInsert() + ";" + System.getProperty("line.separator"));
        }
        for (int i = 0; i < homePitchingLines.size(); i++) {
            b.append(((PitchingLine) homePitchingLines.get(i)).getAsFlattenedSQLInsert() + ";" + System.getProperty("line.separator"));
        }

        return b.toString();
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

    /**
     * @return the seasonType
     */
    public int getSeasonType() {
        return seasonType;
    }

    /**
     * @param seasonType the seasonType to set
     */
    public void setSeasonType(int seasonType) {
        this.seasonType = seasonType;
    }
}
