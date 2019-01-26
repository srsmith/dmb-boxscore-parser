package com.hofl.parser.v2.jackson;

import com.hofl.parser.v2.vo.Player;
import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * PlayerParser uses the Jackson library to read in the roster
 *
 * @author Scott
 */
public class PlayerParser {

    private String path;
    private ObjectMapper mapper;
    
    public PlayerParser(String path) {
        this.setPath(path);
        mapper = new ObjectMapper();
    }

    public Player[] getPlayers() throws Exception {
        if (path.indexOf("https://") > -1) {
            return getPlayersFromURL();
        }
        else {
            return getPlayersFromFile();
        }
    }
    
    private Player[] getPlayersFromURL() throws Exception {
        return getMapper().readValue(new URL(path), Player[].class);        
    }
    
    private Player[] getPlayersFromFile() throws Exception {
        return getMapper().readValue(new File(path), Player[].class);
    }    

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }
    
    
    public static void main (String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: pass a valid file or url as only argument.\n" + 
                    "  Example: com.hofl.parser.v2.jackson.PlayerParser https://hofl.com/api/json/players/");
            System.exit(0);
        }
        
        // http://hofl.com/api/json/players/
        PlayerParser parser = new PlayerParser(args[0]);
        try {
            Player[] players = parser.getPlayers();
            System.out.println("Parsed " + players.length + " players.");
            ObjectMapper mapper = parser.getMapper();
            for (Player p : players) {
                if (p.getFranchiseId() == 20 && p.getLevel() == 1 && p.getPosition().equals("SP")) {
                    System.out.println(mapper.writeValueAsString(p));
                }
                //System.out.println("found " + p.getShortName());
            }
        }
        catch (Throwable t) {
            System.out.println("Exception caught: " + t);
        }
    }
}
