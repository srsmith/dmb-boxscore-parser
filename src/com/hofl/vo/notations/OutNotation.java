package com.hofl.vo.notations;

public class OutNotation extends AbstractNotation {

    public static final String NOTATION_TYPE = "Out";
    
    public static final String ONE = "pitcher";
    public static final String TWO = "catcher";
    public static final String THREE = "first baseman";
    public static final String FOUR = "second  baseman";
    public static final String FIVE = "third baseman";
    public static final String SIX = "shortstop";
    public static final String SEVEN = "left fielder";
    public static final String EIGHT = "center fielder";
    public static final String NINE = "right fielder";
    
    
    public static final String[] POSITIONS = new String[] {"your mom",
        ONE,TWO,THREE,FOUR,FIVE,SIX,SEVEN,EIGHT,NINE};
    
    public static void main(String[] args) {
        try {
            OutNotation out1 = new OutNotation("3");
            OutNotation out2 = new OutNotation("91");
            OutNotation out3 = new OutNotation("345");
            
            System.out.println("out1 = " + out1.getDetailedDescription());
            System.out.println("out2 = " + out2.getDetailedDescription());
            System.out.println("out3 = " + out3.getDetailedDescription());
            
            
        }
        catch (Throwable t) {
            System.out.println("exception caught" + t);
        }
    }
    
    public OutNotation(String notation) throws Exception {
        super(notation);
    }
    
    public boolean isOfType(String notation) {
        try {
            Integer testInteger = new Integer(notation.substring(0,1));
            for (int i=0; i < ERROR_POSSIBLES.length; i++) {
                if (notation.indexOf(ERROR_POSSIBLES[i]) > -1)
                    return false;
            }
            //            if (notation.indexOf("/f") == -1)  // force out, runner not out
                return true;
//            return false;
        }
        catch (Throwable t) {
            // System.out.println("Throwable caught in isOfType()" + t);
            return false;
        }
    }
    
    public static final String[] ERROR_POSSIBLES = {"e1.","e2.","e3.","e4.","e5.","e6.","e7.","e8.","e9."};
    
    public String getDescription() {
        return "Out recorded";
    }
    
    public String getDetailedDescription() {
        StringBuffer desc = new StringBuffer(getDescription());
        if (notation.length() == 1) {
            int posIdx = new Integer(notation).intValue();
            desc.append(", " + POSITIONS[posIdx] + " unassisted.");

            return desc.toString();
        }
        for (int i=0; i<notation.length(); i++) {
            int tmpPosIdx = new Integer(notation.substring(i,i+1)).intValue();
            if (i==0) {
                desc.append(", ");
            }
            else {
                desc.append(" to ");
            }
            desc.append(POSITIONS[tmpPosIdx]);
        }
        desc.append(".");
        return desc.toString();
    }
    
    public String getNotationType() {
    	return NOTATION_TYPE;
    }
}
