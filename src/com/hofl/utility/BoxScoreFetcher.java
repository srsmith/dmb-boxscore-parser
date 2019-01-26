package com.hofl.utility;

import java.io.*;
import java.net.*;

public class BoxScoreFetcher
{
    public static final String LATEST_BOXSCORE_ID_URL =
            "https://www.hofl.com/admin/parse/majors/insert_plateapperance.php?org=3&year=2008";
    public static final String DEFAULT_BOX_SCORE_DIR = "C:\\Docs\\Personal\\HoFL\\boxscores\\2008";
    
    private String boxScoreURL;
    private String boxScoreDir;
    
    public static final String[] LEAGUE_ARRAY = new String[]{"1","2","3"};
    public static final String[] LEAGUE_NAME_ARRAY = new String[]{"majors","minors","independent"};
    
    public static void main(String[] args)
    {
        BoxScoreFetcher fetcher = new BoxScoreFetcher(LATEST_BOXSCORE_ID_URL, DEFAULT_BOX_SCORE_DIR);
        fetcher.fetchNewestBoxscores();
    }
    
    public BoxScoreFetcher(String newBoxScoresURL, String boxScoreDir)
    {
        this.boxScoreURL = newBoxScoresURL;
        this.boxScoreDir = boxScoreDir;
        for (int i=0; i < LEAGUE_ARRAY.length; i++)
        {
            File testDir = new File(boxScoreDir + "\\" + LEAGUE_ARRAY[i]);
            if (!testDir.exists())
            {
                System.out.println(boxScoreDir + "\\" + LEAGUE_ARRAY[i] + " doesn't exist, creating directory.");
                boolean success = testDir.mkdir();
                if (!success)
                    System.out.println("Failed to create directory " + boxScoreDir + "\\" + LEAGUE_ARRAY[i]);
            }
        }
    }
    
    public int fetchNewestBoxscores()
    {
        BufferedReader reader = null;
        int bsCt = 0;
        try
        {
            URL url = new URL(this.boxScoreURL);
            reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String line = reader.readLine();
            while (line != null)
            {
                try
                {
                    writeBoxScoreToFile(line, boxScoreDir);
                    bsCt++;
                }
                catch (Throwable t)
                {
                    // don't count - didn't fetch
                }
                line = reader.readLine();
            }
        }
        catch (Throwable t)
        {
            System.out.println("Throwable caught in constructor: " + t);
        }
        return bsCt;
    }
    
    private void writeBoxScoreToFile(String filename, String boxScoreDir) throws Throwable
    {
        BufferedReader in = null;
        FileWriter writer = null;
        try
        {
            System.out.println("Parsing: " + filename.trim() + "...");
            URL url = new URL("https://www.hofl.com" + filename.trim());
            in = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuffer b = new StringBuffer();
            String line = in.readLine();
            while (line != null)
            {
                b.append(line + "\n");
                line = in.readLine();
            }
            int i=0;
            while (i < LEAGUE_NAME_ARRAY.length)
            {
                if (filename.indexOf(LEAGUE_NAME_ARRAY[i]) > -1)
                    break;
                else
                    i++;
            }
            writer = new FileWriter(boxScoreDir + "\\" + LEAGUE_ARRAY[i] + "\\" + filename.substring(filename.lastIndexOf("/")+1, filename.length()).trim());
            writer.write(b.toString());
            
        }
        catch (Throwable t)
        {
            System.out.println("Unable to parse boxscore id '" + filename.trim() + "': " + t);
            throw t;
        }
        finally
        {
            try
            { 
                writer.flush();
                writer.close();
            }
            catch (Throwable t2)
            {
                
            }
            finally
            {
                writer = null;
            }
        }
    }
}
