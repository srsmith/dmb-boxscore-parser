package com.hofl.parser.v2.vo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Scott
 */
public class Roster {
    
    private List<Player> hitters;
    private List<Player> startingPitchers;
    private List<Player> bullpenPitchers;
    
    public Roster () {
        this.hitters = new ArrayList<Player>();
        this.startingPitchers = new ArrayList<Player>();
        this.bullpenPitchers = new ArrayList<Player>();
    }

    public void addHitter(Player p) throws Exception {
        if (this.hitters.contains(p)) {
            throw new Exception("Hitter " + p.getShortName() + " already added to the roster");
        } else {
            this.hitters.add(p);
        }
    }
     
    public void addStartingPitcher(Player p) throws Exception {
        if (this.startingPitchers.contains(p)) {
            throw new Exception("Starting pitcher " + p.getShortName() + " already added to the roster");
        } else {
            this.startingPitchers.add(p);
        }
    }
    
    public void addBullpenPitcher(Player p) throws Exception {
        if (this.bullpenPitchers.contains(p.getShortName())) {
            throw new Exception("Bullpen pitcher " + p.getShortName() + " already added to the roster");
        } else {
            this.bullpenPitchers.add(p);
        }
    }
    
    public List<Player> getHitters() {
        return hitters;
    }

    public List<Player> getStartingPitchers() {
        return startingPitchers;
    }

    public List<Player> getBullpenPitchers() {
        return bullpenPitchers;
    }
    
}
