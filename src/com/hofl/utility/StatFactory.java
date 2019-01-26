// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StatFactory.java

package com.hofl.utility;

import com.hofl.vo.PinchRunners;
import com.hofl.vo.notations.*;
import com.hofl.vo.stats.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class StatFactory
{

    private StatFactory()
    {
        plateAppearances = new ArrayList();
        runsScored = new ArrayList();
        stolenBaseAttempts = new ArrayList();
        outs = 0;
        resetRunnersOnBase();
        tmpHash = new Hashtable();
        org = 1;
    }

    public StatFactory(AbstractNotation awayNotes, AbstractNotation homeNotes, PinchRunners pinchRunners, int org, int seasonType)
    {
        this();
        this.org = org;
        this.seasonType = seasonType;
        this.awayNotes = awayNotes;
        this.homeNotes = homeNotes;
        this.pinchRunners = pinchRunners;
        resetRunnersOnBase();
        outs = 0;
        processNotation(awayNotes.getFirstNotation(), null, "A");
        resetRunnersOnBase();
        outs = 0;
        processNotation(homeNotes.getFirstNotation(), null, "H");
    }

    public StatFactory(AbstractNotation awayNotes, AbstractNotation homeNotes, PinchRunners pinchRunners, Hashtable awayBattingOrder, Hashtable homeBattingOrder, int org, int seasonType)
    {
        this();
        this.org = org;
        this.awayNotes = awayNotes;
        this.homeNotes = homeNotes;
        this.pinchRunners = pinchRunners;
        this.seasonType = seasonType;
        resetRunnersOnBase();
        outs = 0;
        processNotation(awayNotes.getFirstNotation(), awayBattingOrder, "A");
        resetRunnersOnBase();
        outs = 0;
        processNotation(homeNotes.getFirstNotation(), homeBattingOrder, "H");
    }

    private void processNotation(AbstractNotation note, Hashtable battingOrder, String homeAway)
    {
        int paType = -1;
        int rbi = 0;
        String notation = note.getNotationValue();
        if(note.getNotationType().equals("PitchingChange"))
            pitcherName = note.getNotationValue().trim();
        else
        if(note.getNotationType().equals("Out"))
            paType = 9;
        else
        if(note.getNotationType().equals("FieldersChoice"))
            paType = 10;
        else
        if(note.getNotationType().equals("Hit"))
        {
            if(HitNotation.isSingle(notation))
                paType = 1;
            else
            if(HitNotation.isDouble(notation))
                paType = 2;
            else
            if(HitNotation.isTriple(notation))
                paType = 3;
            else
            if(HitNotation.isHomerun(notation))
            {
                paType = 4;
                rbi++;
            } else
            {
                throw new RuntimeException("What kind of shithole you running?");
            }
        } else
        if(note.getNotationType().equals("Walk"))
        {
            if(((WalkNotation)note).isIntentionalWalk())
                paType = 6;
            else
                paType = 5;
        } else
        if(note.getNotationType().equals("Error"))
            paType = 8;
        else
        if(note.getNotationType().equals("Strikeout"))
            paType = 7;
        else
        if(note.getNotationType().equals("HitByPitch"))
            paType = 11;
        if(paType > -1)
        {
            if(note.getNotationValue().indexOf("/SF") > -1)
                paType = 0;
            else
            if(note.getNotationValue().indexOf("/SH") > -1)
                paType = -1;
            String playerName = note.getPlayerAtBat();
            String position = note.getPlayerPosition();
            AbstractNotation tmpNote = note;
            while(playerName == null || playerName.trim().length() == 0) 
            {
                tmpNote = tmpNote.getPreviousNotation();
                playerName = tmpNote.getPlayerAtBat();
                position = tmpNote.getPlayerPosition();
            }
            PlateAppearance pa = new PlateAppearance(playerName, pitcherName, note.getInning(), note.getBoxScoreOrdinal(), outs, paType, org, getSeasonType(), homeAway, null);
            pa.setBoxScoreId(note.getBoxScoreId());
            pa.setStadium(note.getStadium());
            pa.setPlayerPosition(position);
            pa.setPlayerTeamName(note.getTeamName());
            pa.setPitcherTeamName(note.getOpposingTeamName());
            pa.setOriginalNotation(note.getNotationValue());
            if(note.getNotationType().equals("Hit") || note.getNotationType().equals("Out") || note.getNotationType().equals("Walk") 
                    || note.getNotationType().equals("HitByPitch") || note.getNotationType().equals("FieldersChoice") 
                    || note.getNotationType().equals("Error") && isRBIChanceOnError((ErrorNotation)note))
            {
                String noteValue = note.getNotationValue();
                String scoringNotes[] = {
                    "3-H", "2-H", "1-H"
                };
                for(int rbiIdx = 0; rbiIdx < scoringNotes.length; rbiIdx++)
                {
                    if (noteValue.indexOf(scoringNotes[rbiIdx]) <= -1 || noteValue.indexOf("gdp") != -1) {
                        continue;
                    }
                    
                    String subNote = noteValue.substring(noteValue.indexOf(scoringNotes[rbiIdx]), noteValue.length());
                    if(subNote.indexOf("(") == 3 || subNote.indexOf("e") != -1 && subNote.indexOf(")") <= subNote.indexOf("e")) {
                        continue;
                    }
                    
                    // If 
                    if(!note.getNotationType().equals("FieldersChoice")) {
                        rbi++;
                        continue;
                    }
                    // Give an RBI if runner on third scores on fielders choice or error
                    if((note.getNotationType().equals("FieldersChoice") || note.getNotationType().equals("Error")) && rbiIdx == 0) {
                        rbi++;
                    }
                }

            }
            pa.setRBI(rbi);
            pa.setRunnersOnBase(runnersOnBase);
            if(paType != 8 || note.getNotationValue().indexOf("/fl") <= -1 && (note.getNotationValue().indexOf("B-") != -1 || note.getNotationValue().indexOf("Bx") != -1))
                plateAppearances.add(pa);
            if(paType == 4)
            {
                RunScored rs = new RunScored(note.getTeamName(), playerName.trim(), note.getInning(), note.getBoxScoreOrdinal(), outs, org, getSeasonType(), "", note.getStadium(), homeAway);
                rs.setBoxScoreId(note.getBoxScoreId());
                rs.setPitcherName(pitcherName);
                rs.setPitcherTeamName(note.getOpposingTeamName());
                runsScored.add(rs);
            }
            if(note.getPlayByPlayNote() != null)
                pa.setPlayByPlayNote(note.getPlayByPlayNote());
            if(battingOrder != null)
                pa.setBattingOrder(((Integer)battingOrder.get(pa.getPlayerName().trim())).intValue());
        }
        moveRunners(note, homeAway);
        incrementOuts(note);
        if(note.getNextNotation() != null)
            processNotation(note.getNextNotation(), battingOrder, homeAway);
    }

    private boolean isRBIChanceOnError(ErrorNotation note)
    {
        String noteValue = note.getNotationValue();
        if(basesLoaded() && outs < 1)
            return true;
        if(runnersOnBase[3] != null && runnersOnBase[3].trim().length() > 0 && outs < 2)
        {
            if(outs == 0)
                return true;
            if(outs == 1)
            {
                if(noteValue.indexOf("e7") > -1 || noteValue.indexOf("e8") > -1 || noteValue.indexOf("e9") > -1)
                    return true;
                if((noteValue.indexOf("e6") > -1 || noteValue.indexOf("e5") > -1 || noteValue.indexOf("e4") > -1 || noteValue.indexOf("e3") > -1) && (runnersOnBase[1] == null || runnersOnBase[1].trim().length() == 0))
                    return true;
            } else
            if(outs == 2)
                return false;
        }
        return false;
    }

    private boolean basesLoaded()
    {
        return runnersOnBase[1] != null && runnersOnBase[1].trim().length() > 0 && runnersOnBase[2] != null && runnersOnBase[2].trim().length() > 0 && runnersOnBase[3] != null && runnersOnBase[3].trim().length() > 0;
    }

    public String getNotesAsSQLInserts()
    {
        StringBuffer b = new StringBuffer();
        for(int i = 0; i < plateAppearances.size(); i++)
        {
            PlateAppearance pa = (PlateAppearance)plateAppearances.get(i);
            b.append((new StringBuilder()).append(pa.getAsFlattenedSQLInsert()).append(System.getProperty("line.separator")).toString());
        }

        return b.toString();
    }

    public String getNotesAsCSVLines()
    {
        StringBuffer b = new StringBuffer();
        b.append("game_month,game_day,game_year,boxscore_id,ballpark_name,player_team,player_name,player_position,boxscore_ordinal,inning,outs,pa,ab,s,db,tr,hr,bb,iw,k,hbp,sh,sf,rbi,ro1b,ro2b,ro3b,pitcher_name,pitcher_team\n");
        for(int i = 0; i < plateAppearances.size(); i++)
        {
            PlateAppearance pa = (PlateAppearance)plateAppearances.get(i);
            b.append((new StringBuilder()).append(pa.getAsCSVLine()).append(System.getProperty("line.separator")).toString());
        }

        return b.toString();
    }

    public String getRunsScoredAsCSVLines()
    {
        StringBuffer b = new StringBuffer();
        b.append("game_month,game_day,game_year,boxscore_id,player_team,player_name,boxscore_ordinal,inning,outs,pitcher_name,pitcher_team,r\n");
        for(int i = 0; i < runsScored.size(); i++)
            b.append((new StringBuilder()).append(((RunScored)runsScored.get(i)).getAsCSVLine()).append(System.getProperty("line.separator")).toString());

        return b.toString();
    }

    public String getRunsScoredAsSQLInserts()
    {
        StringBuffer b = new StringBuffer();
        for(int i = 0; i < runsScored.size(); i++)
            b.append((new StringBuilder()).append(((RunScored)runsScored.get(i)).getAsFlattenedSQLInsert()).append(System.getProperty("line.separator")).toString());

        return b.toString();
    }

    public String getStealAttemptsAsCSVLines()
    {
        if(stolenBaseAttempts.size() == 0)
            return "";
        StringBuffer b = new StringBuffer();
        b.append("game_month,game_day,game_year,boxscore_id,player_team,player_name,boxscore_ordinal,inning,outs,sba,sb,cs,base,pitcher_name,pitcher_team\n");
        for(int i = 0; i < stolenBaseAttempts.size(); i++)
            b.append((new StringBuilder()).append(((StealAttempt)stolenBaseAttempts.get(i)).getAsCSVLine()).append(System.getProperty("line.separator")).toString());

        return b.toString();
    }

    public String getStealAttemptsAsSQLInserts()
    {
        if(stolenBaseAttempts.size() == 0)
            return "";
        StringBuffer b = new StringBuffer();
        for(int i = 0; i < stolenBaseAttempts.size(); i++)
            b.append((new StringBuilder()).append(((StealAttempt)stolenBaseAttempts.get(i)).getAsFlattenedSQLInsert()).append(System.getProperty("line.separator")).toString());

        return b.toString();
    }

    public int getNumberOfNotations()
    {
        return plateAppearances.size();
    }

    private void resetRunnersOnBase()
    {
        runnersOnBase = new String[4];
        runnersOnBase[0] = null;
        runnersOnBase[1] = null;
        runnersOnBase[2] = null;
        runnersOnBase[3] = null;
    }

    private String printRunnersOnBase()
    {
        StringBuffer b = new StringBuffer();
        for(int i = 0; i < runnersOnBase.length; i++)
            b.append((new StringBuilder()).append(runnersOnBase[i]).append(",").toString());

        return b.toString();
    }

    private void incrementOuts(AbstractNotation note)
    {
        int tmpOuts = outs;
        if(note.getNotationValue().indexOf("dp") > -1 || note.getNotationValue().indexOf("dp") > -1)
        {
            outs++;
            outs++;
        } else
        if(note.getNotationValue().indexOf("tp") > -1 || note.getNotationValue().indexOf("tp") > -1)
            outs = 3;
        else
        if(note.getNotationType().equals("Out"))
        {
            int baseOuts = 0;
            for(int i = 0; i < BASERUNNING_OUT_NOTATIONS.length; i++)
                if(note.getNotationValue().indexOf(BASERUNNING_OUT_NOTATIONS[i]) > -1)
                    baseOuts++;

            if(baseOuts > 0)
                outs = outs + baseOuts;
            else
                outs++;
        } else
        if(note.getNotationType().equals("Strikeout") && note.getNotationValue().indexOf("B-") == -1)
            outs++;
        else
        if(note.getNotationType().equals("Baserunning"))
        {
            BaserunningNotation _tmp = (BaserunningNotation)note;
            if(BaserunningNotation.isCaughtStealing(note.getNotationValue()))
                outs++;
        } else
        if(note.getNotationType().equals("Pickoff") && ((PickoffNotation)note).isRunnerOut())
            outs++;
        if(!note.getNotationType().equals("Out"))
        {
            for(int i = 0; i < BASERUNNING_OUT_NOTATIONS.length; i++)
            {
                int baseOuts = -1;
                if(note.getNotationValue().indexOf("/f") == -1)
                    baseOuts++;
                if(note.getNotationValue().indexOf(BASERUNNING_OUT_NOTATIONS[i]) > -1)
                    baseOuts++;
                if(baseOuts > 0)
                    outs = outs + baseOuts;
            }

        }
        if(outs >= 3)
        {
            outs = 0;
            resetRunnersOnBase();
        }
    }

    private void moveRunners(AbstractNotation note, String homeAway)
    {
        String noteValue = note.getNotationValue();
        String playerAtBat = note.getPlayerAtBat();
        int batterBaseIdx = getEndingBaseIdxForBatter(note);
        AbstractNotation tmpNote = note;
        if(batterBaseIdx > -1 || noteValue.indexOf("B-") > -1)
        {
            for(; playerAtBat == null || playerAtBat.trim().length() == 0; playerAtBat = tmpNote.getPlayerAtBat())
                tmpNote = tmpNote.getPreviousNotation();

            playerAtBat = playerAtBat.trim();
        }
        if(playerAtBat != null)
            playerAtBat = playerAtBat.trim();
        for(int i = 0; i < MOVE_RUNNERS_ORDER.length; i++)
        {
            if(noteValue.indexOf(MOVE_RUNNERS_ORDER[i]) <= -1)
                continue;
            int startBaseIdx;
            int endBaseIdx;
            if(containsStolenBaseNotation(noteValue))
            {
                String endBase = noteValue.substring(noteValue.indexOf(MOVE_RUNNERS_ORDER[i]) + 2, noteValue.indexOf(MOVE_RUNNERS_ORDER[i]) + 3);
                endBaseIdx = getIndexForValue(endBase, BASES);
                startBaseIdx = endBaseIdx - 1;
                if(noteValue.indexOf("POCS") == -1 && noteValue.indexOf("POSB") == -1 && noteValue.indexOf("PO") > -1)
                    startBaseIdx++;
                String _tmp = runnersOnBase[startBaseIdx];
                if(containsStolenBaseNotation(MOVE_RUNNERS_ORDER[i]))
                {
                    StealAttempt sa = new StealAttempt(note.getTeamName(), runnersOnBase[startBaseIdx], note.getInning(), note.getBoxScoreOrdinal(), outs, endBase, noteValue.indexOf("SB") > -1, org, getSeasonType(), "", note.getStadium(), homeAway);
                    sa.setBoxScoreId(note.getBoxScoreId());
                    sa.setPitcherName(pitcherName);
                    sa.setPitcherTeamName(note.getOpposingTeamName());
                    if(noteValue.indexOf("PO") > -1 && noteValue.indexOf("SB") == -1 && noteValue.indexOf("CS") == -1)
                        sa.setPickoffNoStealAttempt(true);
                    stolenBaseAttempts.add(sa);
                    if(!sa.getSucccess() && endBaseIdx == 4 && noteValue.indexOf("3-H") == -1)
                        endBaseIdx = 3;
                }
                if(noteValue.indexOf("POCS2") > -1 && noteValue.indexOf("1-") > -1)
                {
                    endBaseIdx = -1;
                    startBaseIdx = -1;
                } else
                if(noteValue.indexOf("POCS3") > -1 && noteValue.indexOf("2-") > -1)
                {
                    endBaseIdx = -1;
                    startBaseIdx = -1;
                } else
                if(noteValue.indexOf("POCSH") > -1 && noteValue.indexOf("3-") > -1)
                {
                    endBaseIdx = -1;
                    startBaseIdx = -1;
                }
            } else
            {
                String startBase = noteValue.substring(noteValue.indexOf(MOVE_RUNNERS_ORDER[i]), noteValue.indexOf(MOVE_RUNNERS_ORDER[i]) + 1);
                String endBase;
                if(MOVE_RUNNERS_ORDER[i].indexOf("-") > -1)
                    endBase = noteValue.substring(noteValue.indexOf(MOVE_RUNNERS_ORDER[i]) + 2, noteValue.indexOf(MOVE_RUNNERS_ORDER[i]) + 3);
                else
                    endBase = noteValue.substring(noteValue.indexOf(MOVE_RUNNERS_ORDER[i]), noteValue.indexOf(MOVE_RUNNERS_ORDER[i]) + 1);
                startBaseIdx = getIndexForValue(startBase, BASES);
                endBaseIdx = getIndexForValue(endBase, BASES);
            }
            String runnerName = null;
            if(startBaseIdx == 0)
                runnerName = playerAtBat;
            else
            if(startBaseIdx > 0)
                runnerName = runnersOnBase[startBaseIdx];
            if(endBaseIdx == 4)
            {
                RunScored rs = new RunScored(note.getTeamName(), runnersOnBase[startBaseIdx], note.getInning(), note.getBoxScoreOrdinal(), outs, org, getSeasonType(), "", note.getStadium(), homeAway);
                rs.setBoxScoreId(note.getBoxScoreId());
                rs.setPitcherName(pitcherName);
                rs.setPitcherTeamName(note.getOpposingTeamName());
                runsScored.add(rs);
            } else
            if(endBaseIdx > -1)
                runnersOnBase[endBaseIdx] = runnerName;
            if(startBaseIdx > -1)
                runnersOnBase[startBaseIdx] = null;
        }

        if(batterBaseIdx > -1)
            runnersOnBase[batterBaseIdx] = playerAtBat;
        if(pinchRunners.getNumberOfPinchRunnerNotations() > 0 && pinchRunners.containsPinchRunner(note.getInning(), playerAtBat))
        {
            PinchRunnerNotation prNote = pinchRunners.getPinchRunnerNotation(note.getInning(), playerAtBat);
            for(int pr = 0; pr < runnersOnBase.length; pr++)
                if(runnersOnBase[pr] != null && runnersOnBase[pr].trim().equals(prNote.getPlayerAtBat().trim()))
                    runnersOnBase[pr] = prNote.getNotationValue();

        }
    }

    private int getEndingBaseIdxForBatter(AbstractNotation note)
    {
        int idx = -1;
        if(note.getNotationValue().indexOf("Bx") > -1)
            return idx;
        if (note.getNotationType().equals("Hit")) {
            HitNotation _tmp = (HitNotation) note;
            if (HitNotation.isSingle(note.getNotationValue())) {
                idx = 1;
            } else {
                HitNotation _tmp1 = (HitNotation) note;
                if (HitNotation.isDouble(note.getNotationValue())) {
                    idx = 2;
                } else {
                    HitNotation _tmp2 = (HitNotation) note;
                    if (HitNotation.isTriple(note.getNotationValue())) {
                        idx = 3;
                    }
                }
            }
        } else if (note.getNotationType().equals("Walk") || note.getNotationType().equals("HitByPitch")) {
            idx = 1;
        }
        return idx;
    }

    public int getIndexForValue(String value, String array[])
    {
        for(int i = 0; i < array.length; i++)
            if(array[i].equals(value))
                return i;

        return -1;
    }

    private boolean containsStolenBaseNotation(String notation)
    {
        return notation.indexOf("POE") == -1 && (notation.indexOf("SB") > -1 || notation.indexOf("CS") > -1 || notation.indexOf("PO") > -1);
    }

    public int getSeasonType()
    {
        return seasonType;
    }

    public void setSeasonType(int seasonType)
    {
        this.seasonType = seasonType;
    }

    private AbstractNotation awayNotes;
    private AbstractNotation homeNotes;
    private PinchRunners pinchRunners;
    private ArrayList plateAppearances;
    private ArrayList stolenBaseAttempts;
    private ArrayList runsScored;
    private String pitcherName;
    private int outs;
    private String runnersOnBase[];
    private Hashtable tmpHash;
    private int org;
    private int seasonType;
    private static String BASES[] = {
        "B", "1", "2", "3", "H"
    };
    private static String STARTING_BASE_CORRELATION[] = {
        "", "B", "1", "2", "3"
    };
    private static String MOVE_RUNNERS_ORDER_OLDEST[] = {
        "3-H", "3xH", "SBH", "CSH", "POCSH", "2-H", "2xH", "1-H", "1xH", "B-H", 
        "BxH", "2-3", "2x3", "SB3", "CS3", "PO3", "1-3", "1x3", "B-3", "Bx3", 
        "1-2", "1x2", "SB2", "CS2", "PO2", "B-2", "Bx2", "B-1", "Bx1"
    };
    private static String MOVE_RUNNERS_ORDER_OLD[] = {
        "3-H", "3xH", "SBH", "CSH", "2-H", "2xH", "2-3", "2x3", "SB3", "CS3", 
        "PO3", "1-H", "1xH", "1-3", "1x3", "1-2", "1x2", "SB2", "CS2", "PO2", 
        "PO1", "B-H", "BxH", "B-3", "Bx3", "B-2", "Bx2", "B-1", "Bx1"
    };
    private static String MOVE_RUNNERS_ORDER[] = {
        "SBH", "CSH", "SB3", "CS3", "PO3", "SB2", "CS2", "PO2", "PO1", "3-H", 
        "3xH", "2-H", "2xH", "2-3", "2x3", "1-H", "1xH", "1-3", "1x3", "1-2", 
        "1x2", "B-H", "BxH", "B-3", "Bx3", "B-2", "Bx2", "B-1", "Bx1"
    };
    private static String BASERUNNING_OUT_NOTATIONS[] = {
        "3xH", "2x3", "2xH", "1xH", "1x3", "1x2", "Bx1", "Bx2", "Bx3", "BxH", 
        "+CS2", "+CS3", "+CSH"
    };

}
