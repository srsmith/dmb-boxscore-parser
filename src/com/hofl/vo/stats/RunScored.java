/*
 * Created on Mar 3, 2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.hofl.vo.stats;

import com.hofl.vo.PlayByPlayNote;

/**
 * @author smithsc4
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RunScored
{
    
    private String boxScoreId;
    private String playerName;
    private String playerTeamName;
    private String playerPosition;
    private String stadium;
    private int org;
    private int seasonType;
    private int inning;
    private int boxScoreOrdinal;
    private int outs;
    private String pitcherName;
    private String pitcherTeamName;
    private String homeAway;
    private PlayByPlayNote playByPlayNote;
    
    // public static final String HEADER_CSV = "GAME_DATE,BoxScoreId,Team,Player,Ordinal,INN,OUTS,Pitcher,PitcherTeam";
    public static final String HEADER_CSV = "game_month,game_day,game_year,boxscore_id,org,ballpark_name,player_team,player_name,player_position,home_away,boxscore_ordinal,inning,outs,pitcher_name,pitcher_team,r";
    
    private static final String OLD_INSERT = String.format("INSERT INTO PARSED_RUNS (%s)",HEADER_CSV);
    
    private static final String FLATTENED_COLUMNS = "game_month, game_day, game_year, boxscore_id, org, type, "+
	"ballpark_name, player_team, player_name, player_position, home_away, "+
	"boxscore_ordinal, inning, outs, pitcher_name, pitcher_team, baserunning_run_scored, hitting_description ";
    private static final String FLATTENED_INSERT = String.format("INSERT INTO parsed_boxes (%s) ",FLATTENED_COLUMNS);
    
    public RunScored(String playerTeamName, String playerName, int inning, int boxScoreOrdinal,
            int outs, int org, int seasonType, String playerPosition, String stadium, String homeAway)
    {
        if (playerName == null || playerName.trim().length() == 0)
            this.playerName = "FIXME!";
//            throw new RuntimeException("Player name cannot be null for entry team: " + playerTeamName + ", inning:" +
//                    inning + ", ordinal: " + boxScoreOrdinal + ", outs:" + outs);
        if (playerName != null && playerName.trim().length() == 0)
//            this.playerName = null;
            this.playerName = "FIXME!";
        else
            this.playerName = playerName;
        this.playerTeamName = playerTeamName;
        
        this.inning = inning;
        this.boxScoreOrdinal = boxScoreOrdinal;
        this.outs = outs;
        this.org = org;
        this.seasonType = seasonType;
        this.playerPosition = playerPosition;
        this.stadium = stadium;
        this.homeAway = homeAway;
    }
    
    public String getPlayerName()
    {
        return this.playerName;
    }
    
    public int getInning()
    {
        return this.inning;
    }
    
    public int getBoxScoreOrdinal()
    {
        return this.boxScoreOrdinal;
    }
    
    public int getOuts()
    {
        return this.outs;
    }
    
    public String getBoxScoreId()
    {
        return this.boxScoreId;
    }
    
    public void setBoxScoreId(String boxScoreId)
    {
        this.boxScoreId = boxScoreId;
    }
    
    public void setPlayerTeamName(String teamName)
    {
        this.playerTeamName = teamName;
    }
    
    public String getPlayerTeamName()
    {
        return this.playerTeamName;
    }
    
    public void setPitcherName(String pitcherName)
    {
        this.pitcherName = pitcherName;
    }
    
    public String getPitcherName()
    {
        return this.pitcherName;
    }
    
    public void setPitcherTeamName(String teamName)
    {
        this.pitcherTeamName = teamName;
    }
    
    public String getPitcherTeamName()
    {
        return this.pitcherTeamName;
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
    
    // Format is BoxScoreId,Team,Player,Ordinal,INN,OUTS,Pitcher,PitcherTeam
    public String getAsCSVLine()
    {
        StringBuffer b = new StringBuffer();
        
        b.append(this.boxScoreId.subSequence(4,6) + ",");
        b.append(this.boxScoreId.subSequence(6,8) + ",");
        b.append(this.boxScoreId.subSequence(0,4) + ",");
        b.append(this.boxScoreId + ",");
        b.append(this.org + ",");
        b.append(this.seasonType + ",");
        b.append("'" + this.stadium.replaceAll("'", "\\\\'") + "','");
        b.append(this.playerTeamName + ",");
        
        if (this.playerName.indexOf(",") > -1)
        {
            b.append("\"" + this.playerName + "\",'");
        }
        else
        {
            b.append(this.playerName + ",'");
        }
        
        b.append(this.playerPosition + "','");
        b.append(this.homeAway + "',");
        b.append(this.boxScoreOrdinal + ",");
        b.append(this.inning + ",");
        b.append(this.outs + ",");
        if (this.pitcherName.indexOf(",") > -1)
        {
            b.append("\"" + this.pitcherName + "\",");
        }
        else
        {
            b.append(this.pitcherName + ",");
        }
        b.append(this.pitcherTeamName);
        
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
        b.append("'" + this.playerTeamName + "','");
        b.append(this.getPlayerName().trim().replaceAll("'", "\\\\'") + "',");
        b.append("'" + this.playerPosition + "',");
        b.append("'" + this.homeAway + "',");
        b.append(this.boxScoreOrdinal + ",");
        b.append(this.inning + ",");
        b.append(this.outs + ",");
        // Add pitcher info
        b.append("'" + this.pitcherName.trim().replaceAll("'", "\\\\'") + "','");
        b.append(this.pitcherTeamName + "',1,");
        b.append("'" + this.getPlayerName().trim().replaceAll("'", "\\\\'") + " scored');");
    }

    public String getStadium()
    {
        return stadium;
    }

    public void setStadium(String stadium)
    {
        this.stadium = stadium;
    }

    public String getPlayerPosition()
    {
        return playerPosition;
    }

    public void setPlayerPosition(String playerPosition)
    {
        this.playerPosition = playerPosition;
    }

    /**
     * @return the playByPlayNote
     */
    public PlayByPlayNote getPlayByPlayNote() {
        return playByPlayNote;
    }

    /**
     * @param playByPlayNote the playByPlayNote to set
     */
    public void setPlayByPlayNote(PlayByPlayNote playByPlayNote) {
        this.playByPlayNote = playByPlayNote;
    }
}
