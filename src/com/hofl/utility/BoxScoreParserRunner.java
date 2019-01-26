/*
 * BoxScoreParserRunner.java
 *
 * Created on March 16, 2008, 10:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.hofl.utility;

import com.hofl.utility.*;
import java.io.*;
/**
 *
 * @author Scott
 */
public class BoxScoreParserRunner
{
    public static final String BASE_BOX_DIR = "C:\\Docs\\Personal\\HoFL\\boxscores\\";
    public static final String BOX_PARSED_DIR = "parsed";
    public static final String BASE_SQL_FILENAME = "sqlinserts";
    public static final String[] ORGS = new String[] {"1","3"};
    //public static final String[] ORGS = new String[] {"1","2","3"};
    public static final String[] DIRS = new String[] {"2008"};
    
    public static void main(String[] args)
    {
        //BoxScoreFetcher.main(args);
        runBoxScoreParser();
        moveParsedFiles();
    }
    
    public static void runBoxScoreParser()
    {
        BoxScoreParser parser = null;
        
        for (String year: DIRS)
        {
            for (String org: ORGS)
            {
                parser = new BoxScoreParser();
                parser.addProperty(BoxScoreParser.BOXSCORE_DIR, BASE_BOX_DIR + year + "\\" + org);
                parser.addProperty(BoxScoreParser.ORG, org);
                parser.addProperty(BoxScoreParser.OUTPUT_FILE, BASE_BOX_DIR
                        + BASE_SQL_FILENAME + "-" + org + "-" + year + ".sql");
                parser.addProperty(BoxScoreParser.PROCESS_EOL, "true");
                try
                {
                    parser.parseFiles();
                }
                catch (Throwable t)
                {
                    System.out.println("Throwable caught: " + t.getMessage());
                    t.printStackTrace(System.out);
                }
            }
        }
    }
    
    private static void moveParsedFiles()
    {
        for (String year: DIRS)
        {
            for (String org: ORGS)
            {
                File boxScoreFiles = new File(BASE_BOX_DIR + year + "\\" + org);
                for (File f : boxScoreFiles.listFiles())
                {
                    if (!f.isDirectory()) 
                    {
                        boolean moved = f.renameTo(new File(boxScoreFiles.getAbsolutePath() + "\\"
                                + BOX_PARSED_DIR + "\\" + f.getName()));
                        if (!moved)
                        {
                            System.out.println("Unable to move " + f.getAbsolutePath() + " to " +
                                boxScoreFiles.getAbsolutePath() + "\\"
                                + BOX_PARSED_DIR + "\\" + f.getName());
                        }
                    }
                }
            }
        }
        
    }
}
