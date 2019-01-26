package com.hofl.vo.stats;

import com.hofl.vo.PlayByPlayNote;

public class OtherBaseRunning {

    private String boxScoreId;
    private String playerName;
    private String playerTeamName;
    private String playerPosition;
    private String stadium;

    private PlayByPlayNote playByPlayNote;

    private int inning;
    private int boxScoreOrdinal;
    private int outs;
    private String pitcherName;
    private String pitcherTeamName;
    private int org;
    private int seasonType;
    private String homeAway;

    private String base;
    private int isWildPitch;
    private int isPassedBall;
    private int isPickOffError;

    public static final String FIRST_BASE = "1"; // to handle for pickoffs
    public static final String SECOND_BASE = "2";
    public static final String THIRD_BASE = "3";
    public static final String HOME_PLATE = "H";

    private static final String FLATTENED_COLUMNS = "game_month, game_day, game_year, boxscore_id, org, type, "+
            "ballpark_name, player_team, player_name, home_away,"+
            "boxscore_ordinal, inning, outs, baserunning_wp, baserunning_pb, baserunning_poe, " +
            "baserunning_base, pitcher_name, pitcher_team, hitting_description ";

    private static final String FLATTENED_INSERT = String.format("INSERT INTO parsed_boxes (%s) ",FLATTENED_COLUMNS);

    private OtherBaseRunning() {

    }

    public OtherBaseRunning(String playerTeamName, String playerName, int inning, int boxScoreOrdinal,
            int outs, String base, int isWildPitch, int isPassedBall, int isPickOffError,
            int org, int seasonType, String playerPosition,
            String stadium, String homeAway)
    {
        this();
        if (playerName == null || playerName.trim().length() == 0)
            throw new RuntimeException("Player name cannot be null for entry team: " + playerTeamName + ", inning:" +
                    inning + ", ordinal: " + boxScoreOrdinal + ", outs:" + outs);
        if (playerName != null && playerName.trim().length() == 0)
            this.playerName = null;
        else
            this.playerName = playerName;
        this.playerTeamName = playerTeamName;
        this.inning = inning;
        this.boxScoreOrdinal = boxScoreOrdinal;

        if (outs > 2 || outs < 0)
            throw new RuntimeException("Outs cannot be less than 0 or greater than 2");
        this.outs = outs;

        if (StealAttempt.isValidBase(base))
            this.base = base;
        else
            throw new RuntimeException(base + " is not a valid base notation.  Must be either " +
                    FIRST_BASE + ", " + SECOND_BASE + ", " + THIRD_BASE + ", or " + HOME_PLATE);

        this.isWildPitch = isWildPitch;
        this.isPassedBall = isPassedBall;
        this.isPickOffError = isPickOffError;
        this.org = org;
        this.seasonType = seasonType;
        this.stadium = stadium;
        this.homeAway = homeAway;
    }

    public static boolean isValidBase(String base)
    {
        if (base.equals(FIRST_BASE) || base.equals(SECOND_BASE) || base.equals(THIRD_BASE) || base.equals(HOME_PLATE))
            return true;
        return false;
    }
    
    public String getAsFlattenedSQLInsert()
    {
        StringBuffer b = new StringBuffer();
        b.append(FLATTENED_INSERT);
        appendValues(b);
        return b.toString();
    }

    private void appendValues(StringBuffer b)
    {
        b.append(" VALUES (");
        b.append(this.boxScoreId.subSequence(4,6) + ",");
        b.append(this.boxScoreId.subSequence(6,8) + ",");
        b.append(this.boxScoreId.subSequence(0,4) + ",");
        b.append(this.boxScoreId + ",");
        b.append(this.org + ",");
        b.append(this.seasonType + ",");
        b.append("'" + this.stadium.replaceAll("'", "\\\\'") + "',");
        b.append("'" + this.playerTeamName + "','");
        b.append(this.playerName.trim().replaceAll("'", "\\\\'") + "',");
        b.append("'" + this.homeAway + "',");
        b.append(this.boxScoreOrdinal + ",");
        b.append(this.inning + ",");
        b.append(this.outs + ",");
        b.append(this.isWildPitch + "," + this.isPassedBall + "," + this.isPickOffError);
        b.append("," + this.base + ",");
        // Add pitcher info
        b.append("'" + this.pitcherName.trim().replaceAll("'", "\\\\'") + "','");
        b.append(this.pitcherTeamName  + "','");
        b.append(this.playByPlayNote.getDetails().replaceAll("'", "\\\\'")  + "');");
//        b.append(this.playByPlayNote.getDetails().replaceAll("'", "\\\\'")  + "','");
//        b.append(this.playByPlayNote.getPitchSequenceEscaped() + "');");
    }

    /**
     * @return the boxScoreId
     */
    public String getBoxScoreId() {
        return boxScoreId;
    }

    /**
     * @param boxScoreId the boxScoreId to set
     */
    public void setBoxScoreId(String boxScoreId) {
        this.boxScoreId = boxScoreId;
    }

    /**
     * @return the playerName
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @param playerName the playerName to set
     */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    /**
     * @return the playerTeamName
     */
    public String getPlayerTeamName() {
        return playerTeamName;
    }

    /**
     * @param playerTeamName the playerTeamName to set
     */
    public void setPlayerTeamName(String playerTeamName) {
        this.playerTeamName = playerTeamName;
    }

    /**
     * @return the playerPosition
     */
    public String getPlayerPosition() {
        return playerPosition;
    }

    /**
     * @param playerPosition the playerPosition to set
     */
    public void setPlayerPosition(String playerPosition) {
        this.playerPosition = playerPosition;
    }

    /**
     * @return the stadium
     */
    public String getStadium() {
        return stadium;
    }

    /**
     * @param stadium the stadium to set
     */
    public void setStadium(String stadium) {
        this.stadium = stadium;
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

    /**
     * @return the inning
     */
    public int getInning() {
        return inning;
    }

    /**
     * @param inning the inning to set
     */
    public void setInning(int inning) {
        this.inning = inning;
    }

    /**
     * @return the boxScoreOrdinal
     */
    public int getBoxScoreOrdinal() {
        return boxScoreOrdinal;
    }

    /**
     * @param boxScoreOrdinal the boxScoreOrdinal to set
     */
    public void setBoxScoreOrdinal(int boxScoreOrdinal) {
        this.boxScoreOrdinal = boxScoreOrdinal;
    }

    /**
     * @return the outs
     */
    public int getOuts() {
        return outs;
    }

    /**
     * @param outs the outs to set
     */
    public void setOuts(int outs) {
        this.outs = outs;
    }

    /**
     * @return the pitcherName
     */
    public String getPitcherName() {
        return pitcherName;
    }

    /**
     * @param pitcherName the pitcherName to set
     */
    public void setPitcherName(String pitcherName) {
        this.pitcherName = pitcherName;
    }

    /**
     * @return the pitcherTeamName
     */
    public String getPitcherTeamName() {
        return pitcherTeamName;
    }

    /**
     * @param pitcherTeamName the pitcherTeamName to set
     */
    public void setPitcherTeamName(String pitcherTeamName) {
        this.pitcherTeamName = pitcherTeamName;
    }

    /**
     * @return the org
     */
    public int getOrg() {
        return org;
    }

    /**
     * @param org the org to set
     */
    public void setOrg(int org) {
        this.org = org;
    }

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

    /**
     * @return the homeAway
     */
    public String getHomeAway() {
        return homeAway;
    }

    /**
     * @param homeAway the homeAway to set
     */
    public void setHomeAway(String homeAway) {
        this.homeAway = homeAway;
    }

    /**
     * @return the base
     */
    public String getBase() {
        return base;
    }

    /**
     * @param base the base to set
     */
    public void setBase(String base) {
        this.base = base;
    }
}
