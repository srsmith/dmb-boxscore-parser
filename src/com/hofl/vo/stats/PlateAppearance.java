package com.hofl.vo.stats;

import com.hofl.vo.PlayByPlayNote;
import java.util.HashMap;
import java.util.StringTokenizer;

public class PlateAppearance {

    String boxScoreId;
    String stadium;
    String playerName;
    String playerPosition;
    String teamName;
    String pitcherName;
    String pitcherTeamName;
    String homeAway;
    private String originalNotation;

    int inning;
    int boxScoreOrdinal;
    int outs;
    int outcome;
    int rbi;
    boolean r;
    String[] runnersOnBase;
    
    PlayByPlayNote playByPlayNote;
    int battingOrder;
    int org;
    int seasonType;

    HashMap fielders;

    public static final int SACRIFICE_HIT = -1;
    public static final int SACRIFICE_FLY = 0;
    public static final int SINGLE = 1;
    public static final int DOUBLE = 2;
    public static final int TRIPLE = 3;
    public static final int HOMERUN = 4;
    public static final int WALK = 5;
    public static final int INTENTIONAL_WALK = 6;
    public static final int STRIKEOUT = 7;
    public static final int REACHED_ON_ERROR = 8;
    public static final int FLYOUT = 9;
    public static final int GROUNDOUT = 10;
    public static final int HIT_BY_PITCH = 11;
    
    public static final String RBI = "rbi";
    public static final String RUN = "r";
    
//    public static final String HEADER_CSV = "GAME_DATE,BoxScoreId,Stadium,Team,Player,POS,Ordinal,INN,OUTS,PA,AB,1B,2B,3B,HR,BB,IW,SO,HBP,SH,SF,RBI,RO1B,RO2B,RO3B,Pitcher,PitcherTeam";
// Version 1.0    public static final String HEADER_CSV = "game_month,game_day,game_year,boxscore_id,ballpark_name,player_team,player_name,player_position,boxscore_ordinal,inning,outs,pa,ab,s,db,tr,hr,bb,iw,k,hbp,sh,sf,rbi,ro1b,ro2b,ro3b,pitcher_name,pitcher_team";
    public static final String HEADER_CSV = "game_month,game_day,game_year,boxscore_id,org,ballpark_name,player_team,player_name,player_position,boxscore_ordinal,inning,outs,pa,ab,s,db,tr,hr,bb,iw,k,hbp,sh,sf,rbi,ro1b,ro2b,ro3b,pitcher_name,pitcher_team,batting_order,strikes,balls,pitch_count,description";

    private static final String OLD_INSERT = String.format("INSERT INTO PARSED_PA (%s)",HEADER_CSV);

    
    private static final String FLATTENED_COLUMNS = "game_month, game_day, game_year, boxscore_id, org, type, "+
	"ballpark_name, player_team, player_name, player_position, home_away, "+
	"boxscore_ordinal, inning, outs, hitting_pa, hitting_ab, "+
	"hitting_s, hitting_db, hitting_tr, hitting_hr, hitting_bb,"+ 
	"hitting_iw, hitting_k, hitting_hbp, hitting_sh, hitting_sf, "+
	"hitting_rbi, hitting_ro1b, hitting_ro2b, hitting_ro3b, "+
	"pitcher_name, pitcher_team, hitting_batting_order, hitting_strikes, " + 
        "hitting_balls, hitting_pitch_count,hitting_description, original_notation, " +
        "fielder_catcher, fielder_first, fielder_second, " +
        "fielder_third, fielder_short, fielder_left, fielder_center, fielder_right";
    private static final String FLATTENED_INSERT = String.format("INSERT INTO parsed_boxes (%s) ",FLATTENED_COLUMNS);
    
    public static final String WHOS_ON_FIRST = "'Tomorrow', 'Today', 'Who', 'What', 'I Dont Know', " +
            "'I Dont Give a Damn', 'Because', 'Nobody'";
    
    // R    H   2B 3B  HR  RBI  SB CS  BB  SO   BA   OBP   SLG   TB   SH  SF IBB HBP GDP 
    private PlateAppearance() {
          r = false;
        rbi = 0;
    }

    public PlateAppearance(String playerName, String pitcherName,
    					   int inning, int boxScoreOrdinal, int outs, int outcome, int org,
                           int seasonType, String homeAway, HashMap fielders) {
        this(playerName, pitcherName, inning, boxScoreOrdinal, outs, outcome, org, seasonType, homeAway);
        this.fielders = fielders;
    }

    private PlateAppearance(String playerName, int inning, int boxScoreOrdinal,
    		               int outs, int outcome, int org, int seasonType, String homeAway) {
        this();
        this.playerName = playerName;
        this.inning = inning;
        this.boxScoreOrdinal = boxScoreOrdinal;
        this.setOuts(outs);
        this.rbi = 0;
        this.runnersOnBase = new String[] {"","","",""};
        this.stadium = "undefined";
        
        if (outcome < -1 || outcome > 11) {
            throw new RuntimeException("Invalid outcome value of " + outcome + " passed in");
        }
        this.outcome = outcome;
        
        // If the outcome is a homerun, automatically mark the run scored
        if (this.outcome == 4)
            this.setBatterScored(true);
        this.org = org;
        this.seasonType = seasonType;
        this.homeAway = homeAway;
    }
    
    private PlateAppearance(String playerName, String pitcherName,
    					   int inning, int boxScoreOrdinal, int outs, int outcome, int org,
                           int seasonType, String homeAway) {
    	this(playerName, inning, boxScoreOrdinal, outs, outcome, org, seasonType, homeAway);
    	this.pitcherName = pitcherName;
    }
    
    public String getBoxScoreId() {
    	return this.boxScoreId;
    }
    
    public void setBoxScoreId(String boxScoreId) {
    	this.boxScoreId = boxScoreId;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }

    public String getPitcherName() {
        return this.pitcherName;
    }
    
    public int getInning() {
        return this.inning;
    }
    
    public int getBoxScoreOrdinal() {
        return this.boxScoreOrdinal;
    }
    
    public int getOutcome() {
        return this.getOutcome();
    }

    public void setOriginalNotation(String notationValue) {
        //String tmpVal = notationValue.replaceAll("\n", ";").replaceAll("\r", ";");
        //String tmpVal = notationValue.replace("\\n", "").replace("\\r", "");
        this.originalNotation = notationValue;
    }

    public String getOriginalNotation() {
        return this.originalNotation;
    }

    public void setRBI(int rbi) { 
        this.rbi = rbi;
    }
    
    public int getRBI() {
        return this.rbi;
    }
    
    public boolean getDidBatterScore() {
        return this.r;
    }
    
    public void setOuts(int outs) {
    	if (outs < 0 || outs > 3)
    		throw new RuntimeException("What kind of moron are you?");
    	this.outs = outs;
    }
    
    public int getOuts() {
    	return this.outs;
    }
    
    public void setPlayerPosition(String position) {
    	this.playerPosition = position;
    }
    
    public String getPlayerPosition() {
    	return this.playerPosition;
    }
    
    public void setPlayerTeamName(String teamName) {
    	this.teamName = teamName;
    }
    
    public String getPlayerTeamName() {
    	return this.teamName;
    }

    public void setPitcherTeamName(String teamName) {
    	this.pitcherTeamName = teamName;
    }
    
    public String getPitcherTeamName() {
    	return this.pitcherTeamName;
    }
    
    public void setStadium(String stadium) {
        this.stadium = stadium;
    }
    
    public String getStadium() {
        return this.stadium;
    }
    
    public String getDateString() {
        StringBuffer b = new StringBuffer();
        b.append(this.boxScoreId.substring(0,4));
        b.append("-");
        b.append(this.boxScoreId.subSequence(4,6));
        b.append("-");
        b.append(this.boxScoreId.subSequence(6,8));
        return b.toString();
    }
    
    public void setRunnersOnBase(String[] runners) {
        if (runners.length != 4)
            throw new RuntimeException("Dumbass - array should be 4 long.  YOU KNOW, LIKE FOUR BASES??");
        for (int i=0; i < runners.length; i++) {
        	runnersOnBase[i] = runners[i];
        }
    }
    
    public void setBatterScored(boolean didScore) {
        if (didScore) {
            if (this.outcome == 0 || this.outcome >= 8)
                throw new RuntimeException("Runner cannot score on a sacrifice, flyout, or groundout.  Outcome = " 
                        + this.outcome);
            else 
                r = true;
        }
        else
            r = false;
    }
    
    public void setPlayByPlayNote(PlayByPlayNote pbpnote) {
        this.playByPlayNote = pbpnote;
    }
    
    public PlayByPlayNote getPlayByPlayNote() {
        return this.playByPlayNote;
    }
    
    public void setBattingOrder(int i) {
        if (i > 9 || i < 1) {
            throw new RuntimeException("Batting order must be between 1 and 9 " + (i) + 
                    " - player: " + this.playerName);
        }
        this.battingOrder = i;
    }
    
    public int getBattingOrder() {
        return this.battingOrder;
    }

    public String getAsCSVLine() {
    	// Format is Game_Date BoxScoreId,Stadium,Team,Player,POS,Ordinal,INN,OUTS,PA,AB,1B,2B,3B,HR,BB,IW,SO,HBP,RBI,RO1B,RO2B,RO3B,Pitcher,PitcherTeam
    	StringBuffer b = new StringBuffer();
    	
        b.append(this.boxScoreId.subSequence(4,6) + ",");
        b.append(this.boxScoreId.subSequence(6,8) + ",");
        b.append(this.boxScoreId.subSequence(0,4) + ",");
    	b.append(this.boxScoreId + ",");
    	b.append(this.org + ",");
        b.append(this.seasonType + ",");
        
        if (this.stadium.indexOf(",") >0)
            b.append("\"" + this.stadium + "\",");
        else 
            b.append(this.stadium + ",");
        
    	b.append(this.teamName + ",");
    	
    	if (this.playerName.indexOf(",") >0)
    		b.append("\"" + this.playerName.trim() + "\",");
    	else 
    		b.append(this.playerName.trim() + ",");

    	b.append(this.playerPosition + ",");
        b.append("'" + this.homeAway + "',");
    	b.append(this.boxScoreOrdinal + ",");
    	b.append(this.inning + ",");
    	b.append(this.outs + ",");

    	b.append("1,"); // Plate appearance for every entry
    	
    	if (this.outcome == SACRIFICE_HIT || this.outcome == SACRIFICE_FLY || this.outcome ==  WALK 
    			|| this.outcome == INTENTIONAL_WALK || this.outcome == HIT_BY_PITCH) 
    		b.append("0,"); // no at bat for a walk or sacrifice or hit by pitch
    	else
    		b.append("1,"); // at bat for everything else
    	
    	for (int i=1; i < 8; i++) {
    		b.append(getOutcomeForLine(i) + ",");
    	}
    	b.append(getOutcomeForLine(11) + ",");  // hit by pitch
        b.append(getOutcomeForLine(-1) + ",");  // sac hit
        b.append(getOutcomeForLine(0) + ",");  // sac fly
    	b.append(this.rbi + ",");
        // Runners on base
        for (int j=1; j < this.runnersOnBase.length; j++) {
            if (this.runnersOnBase[j] != null && this.runnersOnBase[j].trim().length() > 0) {
            	if (this.runnersOnBase[j].indexOf(",") >0)
            		b.append("\"" + this.runnersOnBase[j] + "\",");
                    //b.append("1");
            	else
            		b.append(this.runnersOnBase[j] + ",");
                    //b.append("0");
            }
                
            else
                b.append("0,");
        }
        
        // Add pitcher info

        if (this.pitcherName.indexOf(",") > -1) 
            b.append("\"" + pitcherName + "\",");
        else 
            b.append(this.pitcherName + ",");
        
    	b.append(this.pitcherTeamName + ",");
    	
        // Version 2.0 with count and description
        b.append(this.battingOrder + ",");
        b.append(playByPlayNote.getStrikeCount() + ",");
        b.append(playByPlayNote.getBallCount() + ",");
        b.append(playByPlayNote.getDetailedPitchCount(PlayByPlayNote.ALL_PITCHES) + ",");
        b.append(playByPlayNote.getDetails().trim() + ",");
        b.append("'" + this.getOriginalNotation().trim() + "'");
        
        
    	return b.toString();
    }
    
    /**
     * Returns in the new, flattened table format
     *
     * @return String SQL for the new parsed_boxes table
     */
    public String getAsFlattenedSQLInsert()
    {
      StringBuffer b = new StringBuffer();
      b.append(FLATTENED_INSERT);
      appendValues(b);
      return b.toString();
    }
    
    /**
     * Old table format 
     *
     * @return String SQL for the old parsed_pitching table
     */
    public String getAsSQLInsert()
    {
        StringBuffer b = new StringBuffer();
        b.append(OLD_INSERT);
        appendValues(b);
        return b.toString();
    }

    private void appendValues(StringBuffer b) {
        b.append(" VALUES (");
        b.append(this.boxScoreId.subSequence(4,6) + ",");
        b.append(this.boxScoreId.subSequence(6,8) + ",");
        b.append(this.boxScoreId.subSequence(0,4) + ",");
        b.append(this.boxScoreId + ",");
    	b.append(this.org + ",");
    	b.append(this.seasonType + ",");
        b.append("'" + this.stadium.replaceAll("'", "\\\\'") + "','");
        b.append(this.teamName + "','");
        
        b.append(this.playerName.trim().replaceAll("'", "\\\\'") + "','");

        b.append(this.playerPosition + "','");
        b.append(this.homeAway + "',");
        b.append(this.boxScoreOrdinal + ",");
        b.append(this.inning + ",");
        b.append(this.outs + ",");

        b.append("1,"); // Plate appearance for every entry

        if (this.outcome == SACRIFICE_HIT || this.outcome == SACRIFICE_FLY || this.outcome ==  WALK 
    			|| this.outcome == INTENTIONAL_WALK || this.outcome == HIT_BY_PITCH)
            b.append("0,"); // no at bat for a walk or sacrifice
        else
            b.append("1,"); // at bat for everything else
        
        for (int i=1; i < 8; i++) {
            b.append(getOutcomeForLine(i) + ",");
        }
        b.append(getOutcomeForLine(11) + ",");  // hit by pitch
        b.append(getOutcomeForLine(-1) + ",");  // sac hit
        b.append(getOutcomeForLine(0) + ",");  // sac fly
        b.append(this.rbi + ",");
        // Runners on base
        for (int j=1; j < this.runnersOnBase.length; j++) {
            if (this.runnersOnBase[j] != null && this.runnersOnBase[j].trim().length() > 0) 
               b.append("1,");
            else
               b.append("0,");
        }
        
        // Add pitcher info
        b.append("'" + this.pitcherName.trim().replaceAll("'", "\\\\'") + "','");
        b.append(this.pitcherTeamName + "',");
        
        // Version 2.0 with count and description
        b.append(this.battingOrder + ",");
        
        if (playByPlayNote == null)
        {
            System.out.println("WARNING: boxScoreId.boxScoreOrdinal = " + 
                    this.boxScoreId + "." + this.boxScoreOrdinal + " has no playByPlayNote!");
            b.append(",,,'No PBP Note'");
        }
        else {
            b.append(playByPlayNote.getStrikeCount() + ",");
            b.append(playByPlayNote.getBallCount() + ",");
            b.append(playByPlayNote.getDetailedPitchCount(PlayByPlayNote.ALL_PITCHES) + ",'");
            b.append(playByPlayNote.getDetails().trim().replaceAll("'", "\\\\'"));
            b.append("',");
        }

//        b.append("'" + this.playByPlayNote.getPitchSequenceEscaped() + "','" + this.originalNotation + "',");
        b.append("'" + this.getOriginalNotation().trim() + "',");
        // Temporary until I figure out fielders - then use this as a null check on the map
        b.append(WHOS_ON_FIRST + ");");
    }
    
    private String getOutcomeForLine(int type) {
    	if (type == this.outcome)
    		return "1";
        else if (this.outcome == INTENTIONAL_WALK && type == WALK) {
            return "1";
        }
        else {
            return "0";
        }
    }
	
    public static boolean isAtBatOutcome(int testValue) {
        // Only return false if it's a sacrifice or a walk
        if (testValue == 0 || testValue == 5) 
            return false;
        return true;   
    }
    
    public static String replaceTickMark(String line) {
        if (line.indexOf("'") == -1)
            return line;
        StringBuffer b = new StringBuffer();
        b.append(line.substring(0,line.indexOf("'")));
        b.append("\'");
        b.append(line.substring(line.indexOf("'")+1));
        return b.toString();
    }

}
