/**
 *
 */
package com.hofl.vo.stats;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 * @author smithsc4
 * CREATE TABLE `parsed_pitching` (
 * `game_month` int(2) NOT NULL,
 * `game_day` int(2) NOT NULL,
 * `game_year` int(4) NOT NULL,
 * `boxscore_id` bigint(13) NOT NULL,
 * `org` int(1) NOT NULL default '1',
 * `pitcher_name` varchar(40) NOT NULL,
 * `pitcher_team` varchar(5) NOT NULL,
 * `ballpark_name` varchar(50) NOT NULL,
 * `inn` double(3,1) NOT NULL,
 * `h` int(2) NOT NULL,
 * `r` int(2) NOT NULL,
 * `er` int(2) NOT NULL,
 * `bb` int(2) NOT NULL,
 * `k` int(2) NOT NULL,
 * `pch` int(3) NOT NULL,
 * `str` int(3) NOT NULL,
 * `era` double(6,2) NOT NULL,
 * `started` tinyint(1) default NULL,
 * PRIMARY KEY  (`boxscore_id`,`org`,`pitcher_name`)
 * )
 */
public class PitchingLine
{
    
    private String rawLine;
    private String boxScoreId;
    private String pitcherTeamName;
    private String stadium;
    private boolean started;
    private boolean finished;
    private int org;
    private int seasonType;
    private String homeAway;

    //Outcome variables;
    private int win = 0;
    private int loss = 0;
    private int hold = 0;
    private int save = 0;
    private int blownsave = 0;
    private int runsallowed = 0;
    private int orderInGame = -1;

    public static final String PITCHING_HEADER = "INN  H  R ER BB  K PCH STR   ERA";
    
    public static final String HEADER_CSV = "game_month,game_day,game_year,boxscore_id,org,ballpark_name,pitcher_team,pitcher_name,home_away,inn,h,r,er,bb,k,pch,str,era,started";
    
    private static final String OLD_INSERT = String.format("INSERT INTO PARSED_PITCHING (%s)",HEADER_CSV);
    
    private static final String FLATTENED_COLUMNS = "game_month, game_day, game_year, boxscore_id, org, type, "+
	" ballpark_name, pitcher_team, home_away, pitcher_name, pitching_inn, pitching_h, pitching_r, pitching_er, "+
	" pitching_bb, pitching_k, pitching_pch, pitching_str, pitching_era, pitching_started, pitching_finished ";

    // New outcome variables
    private static final String OUTCOME_COLUMNS = ", pitching_win, pitching_loss, pitching_hold, pitching_save, " +
            "pitching_blown_save, pitching_cg, pitching_shutout, pitching_order";
   
    private static final String FLATTENED_INSERT = String.format("INSERT INTO parsed_boxes (%s) ",FLATTENED_COLUMNS + OUTCOME_COLUMNS);

    public PitchingLine(String line, String boxscoreId, String team, String stadium,
            String homeAway, int org, int seasonType, boolean started, int orderInGame)
    {
        rawLine = line;
        this.boxScoreId = boxscoreId;
        this.org = org;
        this.seasonType = seasonType;
        this.pitcherTeamName = team;
        this.stadium = stadium;
        this.started = started;
        this.finished = false;
        this.runsallowed = -1;
        this.homeAway = homeAway;
        this.orderInGame = orderInGame;
    }
    
    
    public String getDateString()
    {
        StringBuffer b = new StringBuffer();
        b.append(this.boxScoreId.substring(0,4));
        b.append("-");
        b.append(this.boxScoreId.subSequence(4,6));
        b.append("-");
        b.append(this.boxScoreId.subSequence(6,8));
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
        
        b.append("'" + this.stadium.replaceAll("'", "\\\\'") + "',");
        b.append("'" + this.pitcherTeamName + "',");
        b.append("'" + this.homeAway + "',");
        b.append("'" + this.rawLine.substring(0,17).trim().replaceAll("'", "\\\\'") + "'");
        
        String pitchingLines = this.rawLine.substring(32).trim();
        StringTokenizer tok = new StringTokenizer(pitchingLines," ");
        int i=0;
        while (tok.hasMoreTokens())
        {
            String val = tok.nextToken();
            i++;
            if (i == 3) {
                try {
                    this.runsallowed = Integer.parseInt(val);
                }
                catch (Throwable t) {
                }
            }
            b.append("," + val);
        }
        if (started)
            b.append(",1");
        else
            b.append(",0");

        if (finished)
            b.append(",1");
        else
            b.append(",0");

        // Added ordinal for game
        //b.append("," + this.orderInGame + ",");

        // Add outcomes to insert
//        String[] matches = outcomePattern.split(this.rawLine.substring(17,32).trim());
//        for (String m: matches) {
        StringTokenizer tok2 = new StringTokenizer(this.rawLine.substring(17,32),",");
        while (tok2.hasMoreTokens()) {
            String m = tok2.nextToken().trim();
            if (m.startsWith("W")) {
                this.win = 1;
            }
            else if (m.startsWith("L")) {
                this.loss = 1;
            }
            else if (m.startsWith("H")) {
                this.hold = 1;
            }
            else if (m.startsWith("S")) {
                this.save = 1;
            }
            else if (m.startsWith("BS")) {
                this.blownsave = 1;
            }
        }

        b.append("," + this.win + "," + this.loss + "," + this.hold + "," + this.save + "," + this.blownsave);
        // Check for complete game
        if (this.started && this.finished) {
            b.append(",1"); // Complete game
            if (this.runsallowed == 0)
                b.append(",1");  // Shutout
            else
                b.append(",0"); // No shutout
        }
        else {
            b.append(",0,0"); // No shutout no complete game
        }
        b.append("," + this.orderInGame);
        b.append(");");
    }

    /**
     * @return the finished
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * @param finished the finished to set
     */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    /**
     * @return the orderInGame
     */
    public int getOrderInGame() {
        return orderInGame;
    }

    /**
     * @param orderInGame the orderInGame to set
     */
    public void setOrderInGame(int orderInGame) {
        this.orderInGame = orderInGame;
    }
}
