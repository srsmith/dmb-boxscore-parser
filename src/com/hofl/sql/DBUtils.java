/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.hofl.sql;

import java.sql.*;
import java.util.Properties;
import com.hofl.utility.ApplicationProperties;
/**
 * Utility class for making connections to the HoFL database
 * @author Scott
 */
public class DBUtils {

    public static Properties props = ApplicationProperties.loadProperties("application.properties");
    private static final String MY_SQL_DRIVER_NAME = "org.gjt.mm.mysql.Driver"; // MySQL MM JDBC driver
    private static final String DB_CONN_URL = "jdbc:mysql://%s/%s"; // a JDBC url

    public static final String SELECT_BOXES_SINGLE_DATE = "select boxscore_id, type from scores where game_month = %s " +
            " and game_day = %s and game_year = %s and organization = %s order by boxscore_id";

    public static Connection getConnection() throws Exception {
//        System.out.println(props.getProperty("db.host") + ", " +
//                props.getProperty("db.username") + ", " +props.getProperty("db.password") + ", " +props.getProperty("db.database"));
        return getConnection(props.getProperty("db.host"),
                props.getProperty("db.username"), props.getProperty("db.password"), props.getProperty("db.database"));
    }

    public static Connection getConnection(String h, String u, String p, String db) throws Exception {
        Class.forName(MY_SQL_DRIVER_NAME);

//        System.out.println("NEW DB settings: " + String.format(DB_CONN_URL, h, db)
//                + "," + u + "," + p);
        return DriverManager.getConnection(String.format(DB_CONN_URL, h, db),
                u, p);
    }

    public static void close(Connection conn, Statement stmt, ResultSet rs)
    {
        try {
            rs.close();
        }
        catch (Throwable t) {}
        finally {
            rs = null;
        }
        try {
            stmt.close();
            //System.out.println("---stmt closed");
        }
        catch (Throwable t) {}
        finally {
            stmt = null;
        }
        try {
            conn.close();
            //System.out.println("---conn closed");
        }
        catch (Throwable t) {}
        finally {
            conn = null;
        }
    }
}
