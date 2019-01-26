/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hofl.vo;

import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author Scott
 */
public class FielderMap {

    private Map awayFielders;
    private Map homeFielders;

    public static String[] POSITIONS = {"catcher",
        "firstbase",
        "secondbase",
        "thirdbase",
        "shortstop",
        "leftfield",
        "centerfield",
        "rightfield",};

    public FielderMap() {
        awayFielders = new HashMap();
        homeFielders = new HashMap();
        this.initMap(awayFielders);
        this.initMap(homeFielders);
    }

    private void initMap(Map m) {
        for (int i = 0; i < POSITIONS.length; i++) {
            // SHOULD I BE USING LISTS HERE??? THEY KEEP ORDER???
            m.put(POSITIONS[i], new HashMap());
        }
    }

    public void addFielder(String homeAway, String playerName, String position, int enteredGameOrdinal) throws Exception {
        Fielder f = getFielder(playerName, position, enteredGameOrdinal);
        addFielder(homeAway, f);
    }

    public void addFielder(String homeAway, Fielder f) throws Exception {
        if (homeAway.equalsIgnoreCase("home")) {
            ((Map)homeFielders.get(f.getPosition())).put(f.enteredGameOrdinal,f);
        }
        else if (homeAway.equalsIgnoreCase("away")) {
            ((Map)awayFielders.get(f.getPosition())).put(f.enteredGameOrdinal,f);
        }
        else {
            throw new Exception ("Expecting 'home' or 'away' as key for map");
        }
    }

    public String getFielderCSV(String homeAway, int gameOrdinal) throws Exception {
        StringBuffer b = new StringBuffer();
        Map m;
        if (homeAway.equalsIgnoreCase("home")) {
            m = homeFielders;
        }
        else if (homeAway.equalsIgnoreCase("away")) {
            m = awayFielders;
        }
        else {
            throw new Exception ("Expecting 'home' or 'away' as key for map");
        }
        for (int i = 0; i < POSITIONS.length; i++) {
            Map fm = (Map)m.get(POSITIONS[i]);
        }
        return b.toString();
    }

    public static final Fielder getFielder(String playerName, String position, int enteredGameOrdinal) throws Exception {
        Class[] pArray = new Class[]{String.class};
        Object[] oArray = new Object[]{position};
        for (int i = 0; i < FIELDER_TYPE_CLASS_NAMES.length; i++) {
            try {
                Class c = Class.forName("com.hofl.vo.Fielder." + FIELDER_TYPE_CLASS_NAMES[i]);
                Fielder aFielder = (Fielder) c.getConstructor(pArray).newInstance(oArray);
                //System.out.println("Found notation type " +NOTATION_TYPE_CLASS_NAMES[i]+ " for '" + notation + "'");
                return aFielder;
            } catch (Throwable t) {
                //System.out.println("Not a " + NOTATION_TYPE_CLASS_NAMES[i] + ": " + t);
            }
        }
        throw new Exception("Fielder does not exist that is described by the String '" + position + "'");
    }
    public static final String[] FIELDER_TYPE_CLASS_NAMES = {
        "Catcher",
        "Firstbase",
        "Secondbase",
        "Thirdbase",
        "Shortstop",
        "Leftfield",
        "Rightfield",
        "Leftfield"};

    public abstract class Fielder {

        private String position;
        private String posShort;
        private String playerName;
        private int enteredGameOrdinal;

        private Fielder() {
        }

        public Fielder(String playerName, int enteredGameOrdinal) {
            this.setPlayerName(playerName);
            this.setEnteredGameOrdinal(enteredGameOrdinal);
        }

        public String getPosition() {
            return position;
        }

        public String getPlayerName() {
            return playerName;
        }

        public void setPlayerName(String playerName) {
            this.playerName = playerName;
        }

        public int getEnteredGameOrdinal() {
            return enteredGameOrdinal;
        }

        public void setEnteredGameOrdinal(int enteredGameOrdinal) {
            this.enteredGameOrdinal = enteredGameOrdinal;
        }

        public boolean isOfType(String position) {
            if (position.equals(position)) {
                return true;
            }
            return false;
        }

        public String getPosShort() {
            return posShort;
        }
    }

    public class Catcher extends Fielder {

        private String position = "catcher";
        private String posShort = "C";

        private Catcher() {
        }

        public Catcher(String playerName, int enteredGameOrdinal) {
            super(playerName, enteredGameOrdinal);
        }
    }

    public class FirstBase extends Fielder {

        private String position = "first base";
        private String posShort = "1B";

        private FirstBase() {
        }

        public FirstBase(String playerName, int enteredGameOrdinal) {
            super(playerName, enteredGameOrdinal);
        }
    }

    public class SecondBase extends Fielder {

        private String position = "second base";
        private String posShort = "2B";

        private SecondBase() {
        }

        public SecondBase(String playerName, int enteredGameOrdinal) {
            super(playerName, enteredGameOrdinal);
        }
    }

    public class ThirdBase extends Fielder {

        private String position = "third base";
        private String posShort = "3B";

        private ThirdBase() {
        }

        public ThirdBase(String playerName, int enteredGameOrdinal) {
            super(playerName, enteredGameOrdinal);
        }
    }

    public class Shortstop extends Fielder {

        private String position = "shortstop";
        private String posShort = "SS";

        private Shortstop() {
        }

        public Shortstop(String playerName, int enteredGameOrdinal) {
            super(playerName, enteredGameOrdinal);
        }
    }

    public class Leftfield extends Fielder {

        private String position = "left field";
        private String posShort = "LF";

        private Leftfield() {
        }

        public Leftfield(String playerName, int enteredGameOrdinal) {
            super(playerName, enteredGameOrdinal);
        }
    }

    public class Centerfield extends Fielder {

        private String position = "center field";
        private String posShort = "CF";

        private Centerfield() {
        }

        public Centerfield(String playerName, int enteredGameOrdinal) {
            super(playerName, enteredGameOrdinal);
        }
    }

    public class Rightfield extends Fielder {

        private String position = "right field";
        private String posShort = "RF";

        private Rightfield() {
        }

        public Rightfield(String playerName, int enteredGameOrdinal) {
            super(playerName, enteredGameOrdinal);
        }
    }
}
