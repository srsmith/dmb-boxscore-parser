package com.hofl.parser.v2;

import com.hofl.parser.v2.jackson.PlayerParser;
import com.hofl.parser.v2.vo.Player;
import com.hofl.sql.DBUtils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;

/**
 *
 * @author Scott
 */
public class JSONParserService {
    private String dbHost;
    private String dbUser;
    private String dbPassword;
    private String dbName;
    private String boxscoreLoc;
    private String apiUrl;
    
    private Map<String, Player[]> rosters;
    

    public static void main(String[] args) {
        JSONParserService p;
        if (args.length == 5 || args.length == 10) {
            int org = Integer.parseInt(args[0]);

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, Integer.parseInt(args[1].substring(0, 4)));
            c.set(Calendar.MONTH, Integer.parseInt(args[1].substring(4, 6)) - 1);
            c.set(Calendar.DATE, Integer.parseInt(args[1].substring(6, 8)));

            boolean b = Boolean.parseBoolean(args[2]);
            if (args.length == 10) {
                p = new JSONParserService(org, c, b, args[3], args[4], args[5], args[6], args[7], args[8], args[9]);
            } else {
                p = new JSONParserService(org, c, b, args[3], args[4]);
            }
        } else if (args.length == 6 || args.length == 11) {
            int org = Integer.parseInt(args[0]);

            Calendar c1 = Calendar.getInstance();
            c1.set(Calendar.YEAR, Integer.parseInt(args[1].substring(0, 4)));
            c1.set(Calendar.MONTH, Integer.parseInt(args[1].substring(4, 6)) - 1);
            c1.set(Calendar.DATE, Integer.parseInt(args[1].substring(6, 8)));

            Calendar c2 = Calendar.getInstance();
            c2.set(Calendar.YEAR, Integer.parseInt(args[2].substring(0, 4)));
            c2.set(Calendar.MONTH, Integer.parseInt(args[2].substring(4, 6)) - 1);
            c2.set(Calendar.DATE, Integer.parseInt(args[2].substring(6, 8)));

            boolean b = Boolean.parseBoolean(args[3]);
            if (args.length == 11) {
                p = new JSONParserService(org, c1, c2, b, args[4], args[5], args[6], args[7], args[8], args[9], args[10]);
            } else {
                p = new JSONParserService(org, c1, c2, b, args[4], args[5]);
            }
        } // 2 20041231 20110701 true 127.0.0.1 root password hofl_parser_2012
        else {
            System.out.println(args.length);
            System.out.println("Signature: org, date (20080401), overwrite, or org, dateFrom, dateTo, overwrite.");
        }
    }
    private String outputLocation;

    public JSONParserService(int org, Calendar date, boolean overwrite, String outputLocation, String apiUrl) {
        this.outputLocation = outputLocation;
        this.apiUrl = apiUrl;
        parse(org, date, overwrite);
    }

    public JSONParserService(int org, Calendar fromDate, Calendar toDate, boolean overwrite, String outputLocation, String apiUrl) {
        this.outputLocation = outputLocation;
        this.apiUrl = apiUrl;
        while (!fromDate.after(toDate)) {
            parse(org, fromDate, overwrite);
            fromDate.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    public JSONParserService(int org, Calendar date, boolean overwrite,
            String dbHost, String dbUser, String dbPassword, String dbName, 
            String boxscoreLoc, String outputLocation, String apiUrl) {
        this.dbHost = dbHost;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbName = dbName;
        this.boxscoreLoc = boxscoreLoc;
        this.outputLocation = outputLocation;
        this.apiUrl = apiUrl;
        parse(org, date, overwrite);
    }

    public JSONParserService(int org, Calendar fromDate, Calendar toDate, boolean overwrite,
            String dbHost, String dbUser, String dbPassword, String dbName, 
            String boxscoreLoc, String outputLocation, String apiUrl) {
        this.dbHost = dbHost;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbName = dbName;
        this.boxscoreLoc = boxscoreLoc;
        this.outputLocation = outputLocation;
        this.apiUrl = apiUrl;
        while (!fromDate.after(toDate)) {
            parse(org, fromDate, overwrite);
            fromDate.add(Calendar.DAY_OF_YEAR, 1);
        }
    }
    
    private void parse(int org, Calendar date, boolean overwrite) {
        int success = 0;
        int failure = 0;
        
        String month = "" + (date.get(Calendar.MONTH)+1 < 10 ? "0" : "") + (date.get(Calendar.MONTH) + 1);
        String day = "" + (date.get(Calendar.DATE) < 10 ? "0" : "") + date.get(Calendar.DATE);
        String year = "" + date.get(Calendar.YEAR);
        String datestring = year + month + day;
        
        String boxScoreLoc = this.boxscoreLoc != null ? this.boxscoreLoc : DBUtils.props.getProperty("boxscores.location." + org);
        System.out.println("boxScoreLoc = " + boxScoreLoc);
        boolean isURL = (boxScoreLoc.indexOf("http") > -1);

        List<Map> boxes = getBoxesForDate(month, day, year, org, boxScoreLoc);
        System.out.println(">> " + boxes.size() + " boxscores to parse for "
                + month + "/" + day + "/" + year + " for organization " + org
                + " (overwrite = " + overwrite + ")");


        BoxScoreAssembler b = null;
        for (Map box : boxes) {
            try {
                if (isURL) {
                    System.out.println("Getting " + String.format(boxScoreLoc, year) + (String) box.get("boxscoreId") + ".box");
                    b = new BoxScoreAssembler(new URL(String.format(boxScoreLoc, year) + (String) box.get("boxscoreId") + ".box"),
                            org, Integer.parseInt((String) box.get("boxscoreType")));
                } else {
                    System.out.println("Getting " + boxScoreLoc + (String) box.get("boxscoreId") + ".box");
                    b = new BoxScoreAssembler(new File(boxScoreLoc + (String) box.get("boxscoreId") + ".box"),
                            org, Integer.parseInt((String) box.get("boxscoreType")));
                }
                b.setRosters(this.getRosters(datestring, org), org);
                serialize(b, org, overwrite);
                success++;
            } catch (Throwable t) {
                failure++;
                System.out.println("Throwable caught parsing boxscore id " + box.get("boxscoreId") + ": " + t);
                t.printStackTrace(System.out);
            }
        }
        System.out.println(">> Parsing complete: " + success + " of " + boxes.size() + " boxscores successfully parsed for "
            + month + "/" + day + "/" + year + " for organization " + org);
    }
    
    private void serialize(BoxScoreAssembler box, int org, boolean overwrite) throws IOException {
        
        File jsonFile = new File(this.outputLocation + "/" + box.getBoxScoreId() + ".json");
        System.out.println("Writing: " + this.outputLocation + "/" + box.getBoxScoreId() + ".json");
        if (jsonFile.getParentFile() == null) {
            jsonFile.getParentFile().mkdir();
        }
        jsonFile.createNewFile();
        
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        mapper.configure(SerializationConfig.Feature.WRITE_EMPTY_JSON_ARRAYS, false);
        mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
        try {
            mapper.writeValue(jsonFile, box.getGame());
        } catch (Throwable e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    
    private List<Map> getBoxesForDate(String month, String day, String year, int org, String boxScoreLoc) {

        boolean isURL = (boxScoreLoc.indexOf("http") > -1);
        List<Map> boxes;
        if (isURL) {
            boxes = getBoxesForDate(month, day, year, org);
        } else {
            boxes = new ArrayList<Map>();
            File files = new File(boxScoreLoc);
            String boxscorePrefix = year + (month.length() == 1 ? "0" : "") + month + day;

            if (files.isDirectory()) {
                for (File f : files.listFiles()) {
                    String fname = f.getName();
                    //System.out.println("processing " + fname + " - fname.indexOf(" + boxscorePrefix + ") ==  " + fname.indexOf(boxscorePrefix));
                    if (!f.isDirectory() && fname.indexOf(boxscorePrefix) == 0) {
                        Map tmpMap = new HashMap();
                        tmpMap.put("boxscoreId", fname.substring(0, fname.indexOf(".box")));
                        tmpMap.put("boxscoreType", "" + org);
                        boxes.add(tmpMap);
                    }
                }
            }
        }
        return boxes;

    }

    private List<Map> getBoxesForDate(String month, String day, String year, int org) {
        List<Map> boxes = new ArrayList<Map>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            //System.out.println(String.format(DBUtils.SELECT_BOXES_SINGLE_DATE, month, day, year, org));
            ps = conn.prepareStatement(String.format(DBUtils.SELECT_BOXES_SINGLE_DATE, month, day, year, org));
            rs = ps.executeQuery();
            while (rs.next()) {
                Map tmpMap = new HashMap();
                tmpMap.put("boxscoreId", rs.getString(1));
                tmpMap.put("boxscoreType", rs.getString(2));
                boxes.add(tmpMap);
            }
        } catch (Throwable t) {
            // HANDLE
            System.out.println("Throwable caught: " + t);
            t.printStackTrace(System.out);
        } finally {
            DBUtils.close(conn, ps, rs);
        }
        return boxes;
    }
    
    private Player[] getRosters(String gamedatestring, int org) {
        if (rosters == null) {
            rosters = new LinkedHashMap<String, Player[]>();
        }
        String key = gamedatestring + "_" + org;
        if (!rosters.containsKey(key)) {
            Player[] players;
            try {
                System.out.println("[[ Getting rosters for new date: " + this.apiUrl + org + "/" + gamedatestring + " ... ]]");
                PlayerParser playerParser = new PlayerParser(this.apiUrl + org + "/" + gamedatestring);
                players = playerParser.getPlayers();
            }
            catch (Throwable t) {
                System.out.println("WARNING: Could not retrieve rosters for " + gamedatestring + ", org: " + org + ".  Setting rosters to blank.");
                players = new Player[0];
            }
            rosters.put(key, players);
        }
        return rosters.get(key);
    }
    
    private Connection getConnection() throws Exception {
        if (this.dbHost != null) {
            return DBUtils.getConnection(dbHost, dbUser, dbPassword, dbName);
        }

        return DBUtils.getConnection();
    }
    
}
