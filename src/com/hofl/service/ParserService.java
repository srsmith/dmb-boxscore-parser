/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hofl.service;

import com.hofl.parser.BoxScore;
import com.hofl.sql.*;
import com.hofl.utility.StatFactory;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 *
 * @author scottrsmith07
 */
public class ParserService {

    private String dbHost;
    private String dbUser;
    private String dbPassword;
    private String dbName;
    private String boxscoreLoc;

    public static void main(String[] args) {
        ParserService p;
        if (args.length == 3 || args.length == 8) {
            int org = Integer.parseInt(args[0]);

            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, Integer.parseInt(args[1].substring(0, 4)));
            c.set(Calendar.MONTH, Integer.parseInt(args[1].substring(4, 6)) - 1);
            c.set(Calendar.DATE, Integer.parseInt(args[1].substring(6, 8)));

            boolean b = Boolean.parseBoolean(args[2]);
            if (args.length == 8) {
                p = new ParserService(org, c, b, args[3], args[4], args[5], args[6], args[7]);
            } else {
                p = new ParserService(org, c, b);
            }
        } else if (args.length == 4 || args.length == 9) {
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
            if (args.length == 9) {
                p = new ParserService(org, c1, c2, b, args[4], args[5], args[6], args[7], args[8]);
            } else {
                p = new ParserService(org, c1, c2, b);
            }
        } // 2 20041231 20110701 true 127.0.0.1 root password hofl_parser_2012
        else {
            System.out.println(args.length);
            System.out.println("Signature: org, date (20080401), overwrite, or org, dateFrom, dateTo, overwrite.");
        }
    }

    public ParserService(int org, Calendar date, boolean overwrite) {
        parse(org, date, overwrite);
    }

    public ParserService(int org, Calendar fromDate, Calendar toDate, boolean overwrite) {
        while (!fromDate.after(toDate)) {
            parse(org, fromDate, overwrite);
            fromDate.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    public ParserService(int org, Calendar date, boolean overwrite,
            String dbHost, String dbUser, String dbPassword, String dbName, String boxscoreLoc) {
        this.dbHost = dbHost;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbName = dbName;
        this.boxscoreLoc = boxscoreLoc;
        parse(org, date, overwrite);
    }

    public ParserService(int org, Calendar fromDate, Calendar toDate, boolean overwrite,
            String dbHost, String dbUser, String dbPassword, String dbName, String boxscoreLoc) {
        this.dbHost = dbHost;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbName = dbName;
        this.boxscoreLoc = boxscoreLoc;
        while (!fromDate.after(toDate)) {
            parse(org, fromDate, overwrite);
            fromDate.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    private void parse(int org, Calendar date, boolean overwrite) {
        String month = "" + (date.get(Calendar.MONTH) + 1);
        String day = "" + date.get(Calendar.DATE);
        String year = "" + date.get(Calendar.YEAR);

        
        String boxScoreLoc = this.boxscoreLoc != null ? this.boxscoreLoc : DBUtils.props.getProperty("boxscores.location." + org);
        boolean doInsert = Boolean.parseBoolean(DBUtils.props.getProperty("db.doinsert"));

        System.out.println("boxScoreLoc = " + boxScoreLoc);
        boolean isURL = (boxScoreLoc.indexOf("http") > -1);

        List<Map> boxes = getBoxesForDate(month, day, year, org, boxScoreLoc);
        System.out.println(">> " + boxes.size() + " boxscores to parse for "
                + month + "/" + day + "/" + year + " for organization " + org
                + " (overwrite = " + overwrite + ")");


        BoxScore b = null;
        for (Map box : boxes) {
            try {
                if (isURL) {
                    System.out.println("Getting " + String.format(boxScoreLoc, year) + (String) box.get("boxscoreId") + ".box");

                    insert(new BoxScore(new URL(String.format(boxScoreLoc, year) + (String) box.get("boxscoreId") + ".box"),
                            org, Integer.parseInt((String) box.get("boxscoreType"))), org, overwrite);
                } else {
                    insert(new BoxScore(new File(boxScoreLoc + (String) box.get("boxscoreId") + ".box"),
                            org, Integer.parseInt((String) box.get("boxscoreType"))), org, overwrite);
                }
                // System.out.println("Parsed " + box.get("boxscoreId"));
            } catch (Throwable t) {
                System.out.println("Throwable caught parsing boxscore id " + box.get("boxscoreId") + ": " + t);
                t.printStackTrace(System.out);
                if (t instanceof java.sql.BatchUpdateException) {
                    int[] updateCounts = ((java.sql.BatchUpdateException)t).getUpdateCounts();
                    System.out.println("Succesfull updates before error: " + updateCounts.length);
                }
            }
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

    private void insert(BoxScore boxScore, int org, boolean overwrite) throws Throwable {
        //System.out.println("insert(" + boxScore.getBoxScoreId() + ")");

        StringBuilder b = new StringBuilder();

        StatFactory f = boxScore.getStatFactory();
        //System.out.println("season type is " + f.getSeasonType());

        b.append(f.getNotesAsSQLInserts());
        b.append(f.getRunsScoredAsSQLInserts());
//        b.append(f.getOtherBaseRunningsAsSQLInserts());
        b.append(f.getStealAttemptsAsSQLInserts());
        b.append(boxScore.getPitchingLinesAsSQLInserts());

        //System.out.println(b);
        
        Connection conn = getConnection();
        //System.out.println("overwrite == " + overwrite);
        if (overwrite) {
            delete(boxScore.getBoxScoreId(), org);
        }
        Statement s = conn.createStatement();
        StringTokenizer tok = new StringTokenizer(b.toString(), System.getProperty("line.separator"));
        int ct = 0;
        while (tok.hasMoreTokens()) {
            String sql = tok.nextToken();
            if (sql != null && sql.trim().length() > 0) {
                ct++;
                // System.out.println(sql.trim() + "; ## " + ct);
                s.addBatch(sql.trim());
            }
        }
        //System.out.println("ct = " + ct);

        for (int i : s.executeBatch()) {
            if (i == Statement.EXECUTE_FAILED) {
                delete(boxScore.getBoxScoreId(), org);
                System.out.println(s.toString());
                throw new Exception("Insert failed for boxscore id " + boxScore.getBoxScoreId());
            }
        }
        
        //System.out.println("insert() of " + boxScore.getBoxScoreId() + " complete.");
        DBUtils.close(conn, s, null);
    }

    private void delete(String boxScoreId, int org) throws Throwable {
        Connection conn = getConnection();
        Statement s = conn.createStatement();
        String cmd = String.format(DELETE_BOXSCORE_LINES, boxScoreId, org);
        s.execute(String.format(DELETE_BOXSCORE_LINES, boxScoreId, org));
        DBUtils.close(conn, s, null);
    }
    private final static String DELETE_BOXSCORE_LINES = "delete from parsed_boxes where boxscore_id = %s and org = %s";

    private Connection getConnection() throws Exception {
        if (this.dbHost != null) {
            return DBUtils.getConnection(dbHost, dbUser, dbPassword, dbName);
        }

        return DBUtils.getConnection();
    }
}
