/*
 * BoxScoreParser.java
 *
 * Created on February 18, 2008, 3:00 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.hofl.utility;

import java.io.*;
import java.util.*;
import java.util.logging.*;

import com.hofl.parser.BoxScore;

/**
 *
 * @author Scott
 */
public class BoxScoreParser
{
    
    private static final Logger log = Logger.getLogger(BoxScoreParser.class.getName());
    private HashMap<String,String> props;
    private ArrayList<BoxScore> boxscores;
    /** Creates a new instance of BoxScoreParser */
    
    public static final String BOXSCORE_DIR = "-dir";
    public static final String ORG = "-org";
    public static final String DATE_START = "-startdate";
    public static final String DATE_END = "-enddate";
    public static final String OUTPUT_DB = "-db";
    public static final String OUTPUT_FILE = "-file";
    public static final String PROCESS_EOL = "-eol";
    
    public static final String[] OPTIONS =  {
        BOXSCORE_DIR,ORG,OUTPUT_FILE,PROCESS_EOL
    };
    
    public BoxScoreParser()
    {
        props = new HashMap<String,String>();
        boxscores = new ArrayList<BoxScore>();
    }
    
    public void addProperty(String opt, String value)
    {
        props.put(opt,value);
    }
    
    public String getProperty(String opt)
    {
        return props.get(opt);
    }
    
    public String getDebugProps()
    {
        Set<String> keys = props.keySet();
        StringBuilder b = new StringBuilder();
        for (String key : keys)
        {
            if (b.length() > 0)
                b.append(",");
            b.append(key + "=" + props.get(key));
        }
        return b.toString();
    }
    
    /**
     * Useage:
     * BoxScoreParser [options]
     *
     * OPTIONS
     *    -boxes Location of boxscores (file or URL)
     *    -db Write to database
     *    -file Write to file (requires a path and file name)
     *    -season Season
     *    -org Organization
     *    -startdate date to start parsing from (optional, missing means oldest non-parsed for season)
     *    -enddate date to end parsing (optional, missing gets all latest)
     *
     *  Ex: BoxScoreParser -season 2008 -org 1 -boxes /httpdocs/boxscores/majors
     *           Parses the latest unparsed 2008 major league boxes from the location specified
     */
    public static void main(String[] args)
    {
        BoxScoreParser parser = new BoxScoreParser();
        for (int i=0; i < OPTIONS.length; i++)
        {
            for (int k=0; k < args.length; k++)
            {
                if (args[k].equals(OPTIONS[i]))
                {
                    parser.addProperty(OPTIONS[i],args[k++]);
                }
            }
        }
        try 
        {
            parser.parseFiles();
        }
        catch (Exception e)
        {
            log.log(Level.SEVERE, e.getClass().getName() + " caught in parseFiles: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(-1);
        }

    }
    
    public void parseFiles() throws IOException, Exception
    {
        log.log(Level.INFO, "props: " + getDebugProps());
        checkProperties();
        
        if (new Boolean(getProperty(PROCESS_EOL)).booleanValue())
        {
            log.log(Level.INFO, "Removing extended characters in " + getProperty(BOXSCORE_DIR) + "... ");
            EOLConverter.convertDirectory(getProperty(BOXSCORE_DIR), false);
            log.log(Level.INFO, "...done removing extended characters.");
        }
        else
        {
            log.log(Level.INFO, "Skipping extended character removal in " + getProperty(BOXSCORE_DIR) + "... ");
        }
       
       
        File files = new File(getProperty(BOXSCORE_DIR));
        
        if (!files.isDirectory())
        {
            log.log(Level.INFO, "Parsing one file: " + files.getAbsolutePath());
            parseBoxscoreFiles(files);
        }
        else
        {
            for (File f : files.listFiles())
            {
                if (!f.isDirectory())
                    parseBoxscoreFiles(f);
            }
        }
        
        log.log(Level.INFO,"Parsed " + boxscores.size() + " boxscores.");
        log.log(Level.INFO,"Creating SQL output file...");
        
        writeSQLFile();
        
        log.log(Level.INFO,"Finished creating SQL file + " + getProperty(OUTPUT_FILE));
        
    }
    
    private void parseBoxscoreFiles(File f)
    {
        log.log(Level.INFO, "Parsing boxscore " + f.getName());
        BoxScore box = new BoxScore(f,getProperty(ORG));
        boxscores.add(box);
    }
    
    private void writeSQLFile() throws IOException, Exception
    {
        removeOldFile(new File(getProperty(OUTPUT_FILE)));
        
        BufferedWriter writer = new BufferedWriter(
                new FileWriter(new File(getProperty(OUTPUT_FILE)), false));
        
        for (BoxScore b : boxscores)
        {
            StatFactory f = b.getStatFactory();
            writer.write(f.getNotesAsSQLInserts());
            writer.flush();
            writer.write(f.getRunsScoredAsSQLInserts());
            writer.flush();
            writer.write(f.getStealAttemptsAsSQLInserts());
            writer.flush();
            writer.write(b.getPitchingLinesAsSQLInserts());
            writer.flush();
        }
        writer.close();
    }
    
    private void removeOldFile(File f) {
        if (f.exists())
        {
            f.delete();
        }
    }
    
    
    /**
     * Throws a runtime exception if a required option is not present
     */
    private void checkProperties()
    {
        if (getProperty(BOXSCORE_DIR) == null
                || getProperty(BOXSCORE_DIR).trim().length() == 0
                || getProperty(ORG) == null
                || getProperty(ORG).trim().length() == 0
                || getProperty(OUTPUT_FILE) == null
                || getProperty(OUTPUT_FILE).trim().length() == 0)
        {
            log.log(Level.SEVERE,"Missing either -org or -boxdir parameter");
            throw new RuntimeException("Missing either -org or -boxdir parameter");
        }
    }
}
