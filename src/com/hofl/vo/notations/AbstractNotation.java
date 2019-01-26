package com.hofl.vo.notations;

import com.hofl.vo.PlayByPlayNote;

public abstract class AbstractNotation {

    private String boxScoreId;
    private String stadium;
    String notation;
    private int boxScoreOrdinal;
    private int inning;
    private String playerAtBat;
    private String position;
    private String teamName;
    private String opposingTeamName;
    private PlayByPlayNote playByPlayNote;
    
    AbstractNotation previousNotation;
    AbstractNotation nextNotation;
    
    public AbstractNotation(String notation) throws Exception {
        if (isOfType(notation))
            this.notation = notation;
        else
            throw new Exception("Notation " + notation + " is not of type " + this.getClass().getName());
    }
    
    public AbstractNotation(String notation, int inning, String playerName) throws Exception {
        this(notation);
        this.setInning(inning);
        this.setPlayerAtBat(playerName);
    }
    
    public String getNotationValue() {
    	return this.notation;
    }
    
    public AbstractNotation addChildNotation(String notation) throws Exception {
        AbstractNotation nextNotation = AbstractNotation.getNotation(notation);
        return this.addChildNotation(nextNotation);
    }
    
    public AbstractNotation addChildNotation(AbstractNotation nextNotation) throws Exception {
        this.setNextNotation(nextNotation);
        nextNotation.setPreviousNotation(this);
        return nextNotation;
    }
    
    public AbstractNotation getFirstNotation() {
        AbstractNotation tmpNote = this;
        while (tmpNote.getPreviousNotation() != null) {
            tmpNote = tmpNote.getPreviousNotation();
        }
        return tmpNote;
    }
    
    public void setBoxScoreOrdinal(int boxOrdinal) {
    	this.boxScoreOrdinal = boxOrdinal;
    }
    
    public int getBoxScoreOrdinal() {
    	return this.boxScoreOrdinal;
    }
    
    public void setInning(int inning) {
        this.inning = inning;
    }
    
    public int getInning() {
        return inning;
    }
    
    public void setPlayerAtBat(String playerName) {
        this.playerAtBat = playerName;
    }
    
    public String getPlayerAtBat() {
        return this.playerAtBat;
    }
    
    public String getBoxScoreId() {
    	return this.boxScoreId;
    }
    
    public void setBoxScoreId(String boxScoreId) {
    	this.boxScoreId = boxScoreId;
    }
    
    public void setPlayerPosition(String position) {
    	this.position = position;
    }
    
    public String getPlayerPosition() {
    	return this.position;
    }
    
    public void setTeamName(String teamName) {
    	this.teamName = teamName;
    }
    
    public String getTeamName() {
    	return this.teamName;
    }

    public void setOpposingTeamName(String teamName) {
    	this.opposingTeamName = teamName;
    }
    
    public String getOpposingTeamName() {
    	return this.opposingTeamName;
    }
    
    public void setStadium(String stadium) {
        this.stadium = stadium;
    }
    
    public String getStadium() {
        return this.stadium;
    }
    
    public void setPlayByPlayNote(String s) {
        this.playByPlayNote = new PlayByPlayNote(s);
    }
    
    public PlayByPlayNote getPlayByPlayNote() {
        return this.playByPlayNote;
    }
    
    private void setPreviousNotation(AbstractNotation prevNote) {
        this.previousNotation = prevNote;
    }
    
    private void setNextNotation(AbstractNotation nextNote) {
        this.nextNotation = nextNote;
    }
    
    public AbstractNotation getNextNotation() {
        if (this.nextNotation != null)
            return this.nextNotation;
        else
            return null;
    }
    
    public AbstractNotation getPreviousNotation() {
        if (this.previousNotation != null)
            return this.previousNotation;
        else
            return null;
    }
    
    public static final AbstractNotation getNotation(String notation) throws Exception {
        Class[] pArray = new Class[] {String.class};
        Object[] oArray = new Object[] {notation};
        for (int i=0; i < NOTATION_TYPE_CLASS_NAMES.length; i++) {
            try {
                Class c = Class.forName("com.hofl.vo.notations." + NOTATION_TYPE_CLASS_NAMES[i]);
                AbstractNotation aNote = (AbstractNotation)c.getConstructor(pArray).newInstance(oArray);
                //System.out.println("Found notation type " +NOTATION_TYPE_CLASS_NAMES[i]+ " for '" + notation + "'");
                return aNote;
            }
            catch (Throwable t) {
                //System.out.println("Not a " + NOTATION_TYPE_CLASS_NAMES[i] + ": " + t);
            }
        }
        return new PitchingChangeNotation(notation);
//        throw new Exception("Notation does not exist that is described by the notation '" + notation + "'");
    }
    
    public abstract boolean isOfType(String notation);
    
    public abstract String getNotationType();
    
    public abstract String getDescription();
    
    public static final String[] NOTATION_TYPE_CLASS_NAMES = {
        "OutNotation",
        "HitNotation",
        "StrikeoutNotation",
        "WalkNotation",
        "BalkNotation",
        "BaserunningNotation",
        "CatcherInterferenceNotation",
        "ErrantPitchNotation",
        "ErrorNotation",
        "FieldersChoiceNotation",
        "HitByPitchNotation",         
        "PickoffNotation"};
    
}
