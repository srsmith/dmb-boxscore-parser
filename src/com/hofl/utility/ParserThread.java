package com.hofl.utility;

import com.hofl.parser.BoxScore;
import java.io.*;
import java.util.Calendar;


public class ParserThread extends Thread
{

    public ParserThread()
    {
        fetcher = new BoxScoreFetcher("https://www.hofl.com/admin/parse/majors/insert_plateapperance.php?org=3&year=2008", "C:\\Docs\\Personal\\HoFL\\boxscores\\2008");
    }

    public static void main(String args[])
    {
        int sleepTime;
        try
        {
            sleepTime = (new Integer(args[0])).intValue();
        }
        catch(Throwable t)
        {
            sleepTime = 0x1b7740;
        }
        ParserThread pThread = new ParserThread();
        try
        {
            pThread.run();
            do
            {
                log("Attempting to get new boxscores...");
                int bsCt = pThread.fetchBoxScores();
                if(bsCt == 0)
                {
                    log("No new boxscores.");
                    log((new StringBuilder()).append("Sleeping ").append(sleepTime).append("ms...").toString());
                    ParserThread _tmp = pThread;
                    sleep(sleepTime);
                    log("... done sleeping");
                } else
                {
                    log((new StringBuilder()).append("Found ").append(bsCt).append(" boxscores to parse.").toString());
                    File tmpFile = new File("C:\\Docs\\Personal\\HoFL\\boxscores\\2008\\sqlinserts.sql");
                    if(tmpFile.exists())
                        tmpFile.delete();
                    for(int lCt = 0; lCt < BoxScoreFetcher.LEAGUE_ARRAY.length; lCt++)
                    {
                        String currentLeagueDir = (new StringBuilder()).append("C:\\Docs\\Personal\\HoFL\\boxscores\\2008\\").append(BoxScoreFetcher.LEAGUE_ARRAY[lCt]).toString();
                        log((new StringBuilder()).append("Converting extended line characters in ").append(currentLeagueDir).toString());
                        EOLConverter.convertDirectory(currentLeagueDir, false);
                        File boxScoreDir = new File(currentLeagueDir);
                        File boxScoreFiles[] = boxScoreDir.listFiles();
                        log("Creating backup directory for parsed files, if not created.");
                        File boxScoreBackups = new File((new StringBuilder()).append(currentLeagueDir).append("\\").append("parsed").toString());
                        if(!boxScoreBackups.exists())
                            boxScoreBackups.mkdir();
                        for(int i = 0; i < boxScoreFiles.length; i++)
                        {
                            if(boxScoreFiles[i].getName().indexOf(".box") <= -1)
                                continue;
                            System.out.println((new StringBuilder()).append("parsing ").append(boxScoreFiles[i].getName()).toString());
                            BoxScore parser = new BoxScore(boxScoreFiles[i], lCt + 1, 0);
                            StatFactory statFactory = parser.getStatFactory();
                            File sqlFile = new File("C:\\Docs\\Personal\\HoFL\\boxscores\\2008\\sqlinserts.sql");
                            BufferedWriter writer = new BufferedWriter(new FileWriter(sqlFile, true));
                            writer.write(statFactory.getNotesAsSQLInserts());
                            writer.write(statFactory.getRunsScoredAsSQLInserts());
                            writer.write(statFactory.getStealAttemptsAsSQLInserts());
                            writer.write(parser.getPitchingLinesAsSQLInserts());
                            writer.flush();
                            writer.close();
                            boolean moved = boxScoreFiles[i].renameTo(new File((new StringBuilder()).append(boxScoreBackups.getAbsolutePath()).append("\\").append(boxScoreFiles[i].getName()).toString()));
                            if(!moved)
                                System.out.println((new StringBuilder()).append("Unable to move ").append(boxScoreFiles[i].getAbsolutePath()).append(" to ").append(boxScoreBackups.getAbsolutePath()).append("\\").append(boxScoreFiles[i].getName()).toString());
                        }

                    }

                    log((new StringBuilder()).append("Finished with current batch of ").append(bsCt).append(" boxscores.").toString());
                    log("Sleeping for 18 hours before trying again...");
                    ParserThread _tmp1 = pThread;
                    sleep(0x3dcc500L);
                    log("Slept for 18 hours - lets try to get some more boxscores.");
                }
            } while(true);
        }
        catch(Throwable e)
        {
            log((new StringBuilder()).append("Exception caught in ParserThread: ").append(e).toString());
            e.printStackTrace();
            return;
        }
    }

    public int fetchBoxScores()
    {
        return fetcher.fetchNewestBoxscores();
    }

    private static void log(String msg)
    {
        Calendar c = Calendar.getInstance();
        String dStr = (new StringBuilder()).append(c.get(2) + 1).append("-").append(c.get(5)).append("-").append(c.get(1)).append(" ").append(c.get(11)).append(":").append(c.get(12)).append(":").append(c.get(13)).append(" ").append(c.get(14)).append("ms").toString();
        System.out.println((new StringBuilder()).append(dStr).append(" ").append("ParserThread: ").append(msg).toString());
    }

    public static final String PARSER_SYSOUT = "ParserThread: ";
    public static final int DEFAULT_SLEEP_TIME_MS = 0x1b7740;
    public static final int SLEEP_18HOURS_MS = 0x3dcc500;
    public static final String BACKUP_DIR_NAME = "parsed";
    BoxScoreFetcher fetcher;
}
