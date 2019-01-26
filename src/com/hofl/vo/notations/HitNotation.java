package com.hofl.vo.notations;

public class HitNotation extends AbstractNotation {

    public static final String NOTATION_TYPE = "Hit";
    
    public static final String[] HIT_TYPES = {"S","D","T","HR"};
    public static final String[] HIT_TYPES_DESCRIPTIONS = {"single", "double", "triple", "homerun"};
    
    public static void main(String[] args) {
    	try {
    		HitNotation test = new HitNotation("k/b");
    		System.out.println("Success :( - note type = " + test.getNotationType());
    		System.out.println("isSingle? " + test.isSingle(test.getNotationValue()));
    		System.out.println("isDouble? " + test.isDouble(test.getNotationValue()));
    		System.out.println("isTriple? " + test.isTriple(test.getNotationValue()));
    		System.out.println("isHomerun? " + test.isHomerun(test.getNotationValue()));
    	}
    	catch (Throwable t) {
    		System.out.println(t);
    	}
    }
    
    public HitNotation(String notation) throws Exception {
        super(notation);
    }

    public boolean isOfType(String notation) {
        if (isSingle(notation) || isDouble(notation) 
                || isTriple(notation) || isHomerun(notation))
            return true;
        return false;
    }

    public String getDescription() {
        if (isSingle(notation))
            return HIT_TYPES_DESCRIPTIONS[0];
        else if (isDouble(notation))
            return HIT_TYPES_DESCRIPTIONS[1];
        else if (isTriple(notation))
            return HIT_TYPES_DESCRIPTIONS[2];
        else if (isHomerun(notation))
            return HIT_TYPES_DESCRIPTIONS[3];
        else
            return "ten run homerun";
    }

    public static boolean isSingle(String notation) {
    	if (notation.indexOf("S") == 0 &&
    			(notation.length() == 1 || 
    			 notation.indexOf("S.") == 0 ||
			     notation.indexOf("S;") == 0 ||
				 notation.indexOf("S/") == 0)) {
    		return true;
    	}
        return false;
    }
    
    public static boolean isDouble(String notation) {
    	if (notation.indexOf("D") == 0 &&
    			(notation.length() == 1 || 
    			 notation.indexOf("D.") == 0 ||
			     notation.indexOf("D;") == 0 ||
				 notation.indexOf("D/") == 0)) {
    		return true;
    	}
        return false;
    }
    
    public static boolean isTriple(String notation) {
    	if (notation.indexOf("T") == 0 &&
    			(notation.length() == 1 || 
    			 notation.indexOf("T.") == 0 ||
			     notation.indexOf("T;") == 0 ||
				 notation.indexOf("T//") == 0)) {
    		return true;
    	}
        return false;
    }
    
    public static boolean isHomerun(String notation) {
    	if (notation.indexOf("HR") == 0 &&
    			(notation.length() == 2 || 
    	    			 notation.indexOf("HR.") == 0 ||
    				     notation.indexOf("HR;") == 0 ||
    					 notation.indexOf("HR/") == 0)) {
    		return true;
    	}
        return false;
    }
    
    public String getNotationType() {
    	return NOTATION_TYPE;
    }
}
