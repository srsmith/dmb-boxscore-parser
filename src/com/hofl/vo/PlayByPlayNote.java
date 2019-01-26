package com.hofl.vo;

import java.util.regex.*;
import java.util.Vector;

public class PlayByPlayNote
{
    /**
     * @todo Need to add the inning and parsing the player's name, plus add the getters
     * to assist in the alignment of PlayByPlayNotes to the AbstractNotations.
     * Also, need to parse the type of hit, depth, direction of hit.
     */
    
    private String rawLine;
    
    private int homeScore;
    private int awayScore;
    
    private int ballCount;
    private int strikeCount;
    
    private Vector detailPitchCounts;
    
    private String details;
    private String pitchSequence;
    
    /** Notations found for pitches in the play by play portion of the boxscore
     *
     *	P Pitchout
     *	H Hit by pitch
     *	I Intentional ball
     *	B Called ball
     *	C Called strike
     *	S Swinging strike
     *	F Foul ball
     *	X Ball put in play
     *  -- Next set not implemented yet --
     *	1 Pitcher made pickoff throw to 1st
     *	2 Pitcher made pickoff throw to 2nd
     *	3 Pitcher made pickoff throw to 3rd
     *	+1 Catcher made pickoff throw to 1st
     *	+2 Catcher made pickoff throw to 2nd
     *  +3 Catcher made pickoff throw to 3rd
     *  b Batter was bunting
     *  p Pitchout
     */
    
    private PlayByPlayNote()
    {
    }
    
    public PlayByPlayNote(String s)
    {
        this.detailPitchCounts = new Vector();
        int i=0;
        while (i < 8)
        {
            this.detailPitchCounts.add(i,new Integer(0));
            i++;
        }
        this.rawLine = s;
        parseRawLine();
    }
    
    private void parseRawLine()
    {
        this.ballCount = Integer.parseInt(rawLine.substring(12,13));
        this.strikeCount = Integer.parseInt(rawLine.substring(13,14));
        try
        {
            this.details = rawLine.substring(14, rawLine.indexOf("(")).trim();
            String tmpPitches = rawLine.substring(rawLine.indexOf("(")+1,
                    rawLine.indexOf(")"));
            this.pitchSequence = tmpPitches;
            generateDetailPitchCounts(tmpPitches);
        }
        catch (StringIndexOutOfBoundsException e)
        {
            this.details = rawLine.substring(14, rawLine.length());
        }
        
    }
    
    private void generateDetailPitchCounts(String pitches)
    {
        int totalPitches = 0;
        for (int i=0; i < pitches.length(); i++)
        {
            String p = pitches.substring(i,i+1);
            for (int j=0; j < allPatterns.length; j++)
            {
                if (allPatterns[j].matcher(p).find())
                {
                    Integer tmpI = (Integer)detailPitchCounts.get(j);
                    tmpI = new Integer(tmpI.intValue()+1);
                    detailPitchCounts.set(j,tmpI);
                    totalPitches++;
                }
            }
        }
        // Set the total pitches
        detailPitchCounts.add(8,new Integer(totalPitches));
    }
    
    public int getHomeScore()
    {
        return homeScore;
    }
    
    public int getAwayScore()
    {
        return awayScore;
    }
    
    public int getBallCount()
    {
        return ballCount;
    }
    
    public int getStrikeCount()
    {
        return strikeCount;
    }
    
    public String getDetails()
    {
        return details;
    }
    
    public int getDetailedPitchCount(String type)
    {
        if (type.equals(ALL_PITCHES))
        {
            return ((Integer)detailPitchCounts.get(8)).intValue();
        }
        for (int i=0; i<allPatterns.length; i++)
        {
            if (allPatterns[i].matcher(type).find())
            {
                return ((Integer)detailPitchCounts.get(i)).intValue();
            }
        }
        return -1;
    }
    
    
    public String getHitType()
    {
        if (details == null || details.trim().length() == 0)
            return "";

        return null;
    }
    
    public String getHitLocation()
    {
        if (details == null || details.trim().length() == 0)
            return "";
    
        return null;
    }
    
    
    public static final String ALL_PITCHES = "ALL";
    
    public static final String PITCHOUT = "P";
    public static final String HIT_BY_PITCH = "H";
    public static final String INTENTIONAL_BALL = "I";
    public static final String BALL = "B";
    public static final String CALLED_STRIKE = "C";
    public static final String SWINGING_STRIKE = "S";
    public static final String FOUL_BALL = "F";
    public static final String BALL_IN_PLAY = "X";
    
    public static final Pattern pitchoutPattern = Pattern.compile(PITCHOUT);
    public static final Pattern hbpPattern = Pattern.compile(HIT_BY_PITCH);
    public static final Pattern ibbPattern = Pattern.compile(INTENTIONAL_BALL);
    public static final Pattern ballPattern = Pattern.compile(BALL);
    public static final Pattern callstrikePattern = Pattern.compile(CALLED_STRIKE);
    public static final Pattern swingstrikePattern = Pattern.compile(SWINGING_STRIKE);
    public static final Pattern foulPattern = Pattern.compile(FOUL_BALL);
    public static final Pattern inplayPattern = Pattern.compile(BALL_IN_PLAY);
    
    public static final Pattern[] allPatterns = new Pattern[] {
        swingstrikePattern, ballPattern, callstrikePattern, inplayPattern,
        foulPattern, pitchoutPattern, hbpPattern, ibbPattern
    };
    
    public static final Pattern hitToPitcherPattern = Pattern.compile("pitcher");
    public static final Pattern hitTo1BPattern = Pattern.compile("first");

    /**
     * @return the allPitchNotes
     */
    public String getPitchSequence() {
        return pitchSequence;
    }

    public String getPitchSequenceEscaped() {
        return getPitchSequence();
    }

    /**
     * @param allPitchNotes the allPitchNotes to set
     */
    public void setPitchSequence(String allPitchNotes) {
        this.pitchSequence = allPitchNotes;
    }
    
}
