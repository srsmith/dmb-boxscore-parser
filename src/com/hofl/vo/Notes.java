package com.hofl.vo;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Notes extends AbstractBoxScoreItem {

    private HashMap notesHash;
    private HashMap noteRetrievalCounts;
        
    public Notes(String rawLine) {
        super(rawLine);
        notesHash = new HashMap();
        noteRetrievalCounts = new HashMap();
        parseRawLine(rawLine);
    }
    
    public static boolean isOfType(String rawLine) {
        try {
            Integer foo = new Integer(rawLine.substring(rawLine.indexOf(":")-1, rawLine.indexOf(":")));
            foo = null;
            return true;
        }
        catch (Throwable t) {
            return false;
        }
    }
    
    public String getNextNote(int idx) {
        String tmpNote = null;
    	if (notesHash.containsKey(new Integer(idx))) {
        	ArrayList tmpList = (ArrayList)notesHash.get(new Integer(idx));
        	int noteIdx;
        	if (noteRetrievalCounts.containsKey(new Integer(idx))) {
        		noteIdx = ((Integer)noteRetrievalCounts.get(new Integer(idx))).intValue();
//        		System.out.println("Getting the note at a non-zero index: " + noteIdx
//        				+ ": " + (String)tmpList.get(noteIdx));
        	}
        	else {
        		noteIdx = 0;
        	}
        	tmpNote = (String)tmpList.get(noteIdx);
        	noteIdx++;
        	noteRetrievalCounts.put(new Integer(idx), new Integer(noteIdx));
        }
        return tmpNote;
   
    }
    
    public void parseRawLine(String line) {
        StringTokenizer tok = new StringTokenizer(line.trim(), ":", false);
        int startIdx = new Integer(tok.nextToken()).intValue();
        while (tok.hasMoreTokens()) {
        	ArrayList noteList;
        	String nextNote = tok.nextToken().trim();
            if (nextNote.indexOf(" ") > -1) {
                if (nextNote.lastIndexOf(" ") - nextNote.lastIndexOf(",") == 1)
                    nextNote.trim();
                else
                    nextNote = nextNote.substring(0,nextNote.lastIndexOf(" "));
            }
        	if (notesHash.get(new Integer(startIdx)) != null) {
        		noteList = (ArrayList)notesHash.get(new Integer(startIdx));
        	}
        	else {
        		noteList = new ArrayList();
        	}
        	noteList.add(noteList.size(), nextNote);
        	notesHash.put(new Integer(startIdx), noteList);
            startIdx++;
        }
    }
  
        
}
