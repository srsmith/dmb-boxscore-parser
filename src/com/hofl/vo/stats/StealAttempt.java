package com.hofl.vo.stats;

public class StealAttempt {

    private String boxScoreId;
    private String playerName;
    private String playerTeamName;
    private String playerPosition;
    private String stadium;
    private int inning;
    private int boxScoreOrdinal;
    private int outs;
    private String pitcherName;
    private String pitcherTeamName;
    private int org;
    private int seasonType;
    private String homeAway;
    private boolean pickoffNoStealAttempt;
    String base;
    boolean success;
    public static final String FIRST_BASE = "1";
    public static final String SECOND_BASE = "2";
    public static final String THIRD_BASE = "3";
    public static final String HOME_PLATE = "H";
    public static final String HEADER_CSV = "game_month,game_day,game_year,boxscore_id,org,ballpark_name,player_team,player_name,player_position,home_away,boxscore_ordinal,inning,outs,sba,sb,cs,base,pitcher_name,pitcher_team";
    private static final String OLD_INSERT = String.format("INSERT INTO PARSED_SB (%s)", new Object[]{
                "game_month,game_day,game_year,boxscore_id,org,ballpark_name,player_team,player_name,player_position,home_away,boxscore_ordinal,inning,outs,sba,sb,cs,base,pitcher_name,pitcher_team"
            });
    private static final String FLATTENED_COLUMNS = "game_month, game_day, game_year, boxscore_id, org, type, ballpark_name, player_team, player_name, home_away,boxscore_ordinal, inning, outs, baserunning_sba, baserunning_sb, baserunning_cs, baserunning_pickoff, baserunning_base, pitcher_name, pitcher_team";
    private static final String FLATTENED_INSERT = String.format("INSERT INTO parsed_boxes (%s) ", new Object[]{
                "game_month, game_day, game_year, boxscore_id, org, type, ballpark_name, player_team, player_name, home_away,boxscore_ordinal, inning, outs, baserunning_sba, baserunning_sb, baserunning_cs, baserunning_pickoff, baserunning_base, pitcher_name, pitcher_team"
            });

    private StealAttempt() {
    }

    public StealAttempt(String playerTeamName, String playerName, int inning, int boxScoreOrdinal, int outs, String base, boolean success,
            int org, int seasonType, String playerPosition, String stadium, String homeAway) {
        this();
        if (playerName == null || playerName.trim().length() == 0) {
            throw new RuntimeException((new StringBuilder()).append("Player name cannot be null for entry team: ").append(playerTeamName).append(", inning:").append(inning).append(", ordinal: ").append(boxScoreOrdinal).append(", outs:").append(outs).toString());
        }
        if (playerName != null && playerName.trim().length() == 0) {
            this.playerName = null;
        } else {
            this.playerName = playerName;
        }
        this.playerTeamName = playerTeamName;
        this.inning = inning;
        this.boxScoreOrdinal = boxScoreOrdinal;
        if (outs > 2 || outs < 0) {
            throw new RuntimeException("Outs cannot be less than 0 or greater than 2");
        }
        this.outs = outs;
        if (isValidBase(base)) {
            this.base = base;
        } else {
            throw new RuntimeException((new StringBuilder()).append(base).append(" is not a valid base notation.  Must be either ").append("1").append(", ").append("2").append(", ").append("3").append(", or ").append("H").toString());
        }
        this.success = success;
        this.org = org;
        this.seasonType = seasonType;
        this.stadium = stadium;
        this.homeAway = homeAway;
        pickoffNoStealAttempt = false;
    }

    public static boolean isValidBase(String base) {
        return base.equals("1") || base.equals("2") || base.equals("3") || base.equals("H");
    }

    public boolean getSucccess() {
        return success;
    }

    public String getBase() {
        return base;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getInning() {
        return inning;
    }

    public int getOuts() {
        return outs;
    }

    public int getBoxScoreOrdinal() {
        return boxScoreOrdinal;
    }

    public String getBoxScoreId() {
        return boxScoreId;
    }

    public void setBoxScoreId(String boxScoreId) {
        this.boxScoreId = boxScoreId;
    }

    public void setPlayerTeamName(String teamName) {
        playerTeamName = teamName;
    }

    public String getPlayerTeamName() {
        return playerTeamName;
    }

    public void setPitcherName(String pitcherName) {
        this.pitcherName = pitcherName;
    }

    public String getPitcherName() {
        return pitcherName;
    }

    public void setPitcherTeamName(String teamName) {
        pitcherTeamName = teamName;
    }

    public String getPitcherTeamName() {
        return pitcherTeamName;
    }

    public String getDateString() {
        StringBuffer b = new StringBuffer();
        b.append(boxScoreId.substring(0, 4));
        b.append("-");
        b.append(boxScoreId.subSequence(4, 6));
        b.append("-");
        b.append(boxScoreId.subSequence(6, 8));
        return b.toString();
    }

    public String getAsCSVLine() {
        StringBuffer b = new StringBuffer();
        b.append((new StringBuilder()).append(boxScoreId.subSequence(4, 6)).append(",").toString());
        b.append((new StringBuilder()).append(boxScoreId.subSequence(6, 8)).append(",").toString());
        b.append((new StringBuilder()).append(boxScoreId.subSequence(0, 4)).append(",").toString());
        b.append((new StringBuilder()).append(boxScoreId).append(",").toString());
        b.append((new StringBuilder()).append(org).append(",").toString());
        b.append((new StringBuilder()).append(seasonType).append(",").toString());
        b.append((new StringBuilder()).append("'").append(stadium.replaceAll("'", "\\\\'")).append("',").toString());
        b.append((new StringBuilder()).append(playerTeamName).append(",").toString());
        if (playerName.indexOf(",") > -1) {
            b.append((new StringBuilder()).append("\"").append(playerName).append("\",").toString());
        } else {
            b.append((new StringBuilder()).append(playerName).append(",").toString());
        }
        b.append((new StringBuilder()).append(boxScoreOrdinal).append(",").toString());
        b.append((new StringBuilder()).append(inning).append(",").toString());
        b.append((new StringBuilder()).append(outs).append(",").toString());
        b.append("1,");
        if (success) {
            b.append("1,0,");
        } else {
            b.append("0,1,");
        }
        b.append((new StringBuilder()).append(base).append(",").toString());
        if (pitcherName.indexOf(",") > -1) {
            b.append((new StringBuilder()).append("\"").append(pitcherName).append("\",").toString());
        } else {
            b.append((new StringBuilder()).append(pitcherName).append(",").toString());
        }
        b.append(pitcherTeamName);
        return b.toString();
    }

    public String getAsFlattenedSQLInsert() {
        StringBuffer b = new StringBuffer();
        b.append(FLATTENED_INSERT);
        appendValues(b);
        return b.toString();
    }

    public String getAsSQLInsert() {
        StringBuffer b = new StringBuffer();
        b.append(OLD_INSERT);
        appendValues(b);
        return b.toString();
    }

    private void appendValues(StringBuffer b) {
        b.append(" VALUES (");
        b.append((new StringBuilder()).append(boxScoreId.subSequence(4, 6)).append(",").toString());
        b.append((new StringBuilder()).append(boxScoreId.subSequence(6, 8)).append(",").toString());
        b.append((new StringBuilder()).append(boxScoreId.subSequence(0, 4)).append(",").toString());
        b.append((new StringBuilder()).append(boxScoreId).append(",").toString());
        b.append((new StringBuilder()).append(org).append(",").toString());
        b.append((new StringBuilder()).append(seasonType).append(",").toString());
        b.append((new StringBuilder()).append("'").append(stadium.replaceAll("'", "\\\\'")).append("',").toString());
        b.append((new StringBuilder()).append("'").append(playerTeamName).append("','").toString());
        b.append((new StringBuilder()).append(playerName.trim().replaceAll("'", "\\\\'")).append("',").toString());
        b.append((new StringBuilder()).append("'").append(homeAway).append("',").toString());
        b.append((new StringBuilder()).append(boxScoreOrdinal).append(",").toString());
        b.append((new StringBuilder()).append(inning).append(",").toString());
        b.append((new StringBuilder()).append(outs).append(",").toString());
        if (isPickoffNoStealAttempt()) {
            b.append("0,0,0,1,");
        } else if (success) {
            b.append("1,1,0,0,");
        } else {
            b.append("1,0,1,0,");
        }
        b.append((new StringBuilder()).append("'").append(base).append("',").toString());
        b.append((new StringBuilder()).append("'").append(pitcherName.trim().replaceAll("'", "\\\\'")).append("','").toString());
        b.append((new StringBuilder()).append(pitcherTeamName).append("');").toString());
    }

    public boolean isPickoffNoStealAttempt() {
        return pickoffNoStealAttempt;
    }

    public void setPickoffNoStealAttempt(boolean pickoffNoStealAttempt) {
        this.pickoffNoStealAttempt = pickoffNoStealAttempt;
    }
}
