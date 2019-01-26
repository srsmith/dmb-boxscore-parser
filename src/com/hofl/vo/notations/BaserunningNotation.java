package com.hofl.vo.notations;

public class BaserunningNotation extends AbstractNotation {

    public static final String NOTATION_TYPE = "Baserunning";
    
    public static final String[] BASES = {"2","3","H"};
    public static final String[] BASE_DESCRIPTIONS = {"second base", "third base", "home plate"};
    
    public BaserunningNotation(String notation) throws Exception {
        super(notation);
        // TODO Auto-generated constructor stub
    }

    public boolean isOfType(String notation) {
        if (this.isStolenBase(notation) || 
                this.isCaughtStealing(notation) || 
                this.isOtherBaserunningEvent(notation)) {
            return true;
        }
        return false;
    }

    public String getDescription() {
        if (isStolenBase(notation))
            return "stolen base";
        else if (isCaughtStealing(notation))
            return "caught stealing";
        else if (isOtherBaserunningEvent(notation))
            return "other baserunning event";
        else
            return "stole your girl";
    }

    public String getDetailedDescription() {
        String stolenBase = null;
        try {
            String baseNotation = notation.substring(2,3);
            for (int i=0; i<BASES.length; i++) {
                if (baseNotation.equals(BASES[i])) {
                    stolenBase = BASE_DESCRIPTIONS[i];
                    break;
                }
            }
        }
        catch (Throwable t) {
            // Most likely this means it's an OBA
        }
        
        if (isStolenBase(notation))
            return "stole " + stolenBase;
        else if (isCaughtStealing(notation)) 
            return "caught stealing " + stolenBase;
        else if (isOtherBaserunningEvent(notation))
            return "other baserunning event";
        else
            return getDescription();
    }
    
    public static boolean isStolenBase(String notation) {
        if(notation.indexOf("SB") == 0)
            return true;
        return false;
    }
 
    public static boolean isCaughtStealing(String notation) {
        if(notation.indexOf("CS") == 0)
            return true;
        return false; 
    }
   
    public static boolean isOtherBaserunningEvent(String notation) {
        if(notation.indexOf("OA") == 0)
            return true;
        return false; 
    }
    
    public static void main(String[] args) {
        try {
            System.out.println("BaserunningNotation b1 = new BaserunningNotation(\"SB\");");
            BaserunningNotation b1 = new BaserunningNotation("SB");
            System.out.println("BaserunningNotation b1 = new BaserunningNotation(\"CS\");");
            BaserunningNotation b2 = new BaserunningNotation("CS");
            System.out.println("BaserunningNotation b1 = new BaserunningNotation(\"OA\");");
            BaserunningNotation b3 = new BaserunningNotation("OA");
            System.out.println("Success!");
        }
        catch (Throwable t) {
            System.out.println("Throwable caught in testing: " + t);
        }
    }
    
    public String getNotationType() {
    	return NOTATION_TYPE;
    }

}
