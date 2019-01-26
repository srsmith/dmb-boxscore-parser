// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EOLConverter.java

package com.hofl.utility;

import java.io.*;

public class EOLConverter
    implements FilenameFilter
{

    public EOLConverter()
    {
    }

    public static void main(String arguments[])
    {
        convertDirectory(".", true);
    }

    public boolean accept(File file, String string)
    {
        String path = (new String((new StringBuilder()).append(file.toString()).append("?").append(string).toString())).replace('?', File.separatorChar);
        boolean b_1 = (new File(path)).isDirectory();
        boolean b_2 = string.endsWith(".htm");
        boolean b_3 = string.endsWith(".html");
        boolean b_4 = string.endsWith(".java");
        boolean b_5 = string.endsWith(".box");
        return b_1 || b_2 || b_3 || b_4 || b_5;
    }

    public static void convertDirectory(String string, boolean convertSubDirs)
    {
        File directory = new File(string);
        String list[] = directory.list(new EOLConverter());
        for(int i = 0; i < list.length; i++)
        {
            String path = (new String((new StringBuilder()).append(string).append("?").append(list[i]).toString())).replace('?', File.separatorChar);
            if(!(new File(path)).isDirectory())
            {
                convertFile(path);
                continue;
            }
            if(convertSubDirs)
                convertDirectory(path, convertSubDirs);
        }

    }

    public static void convertFile(String string)
    {
        File file_txt = new File(string);
        File file_tmp = new File((new StringBuilder()).append(string).append(".tmp").toString());
        try
        {
            BufferedReader bufferedreader = new BufferedReader(new FileReader(file_txt));
            BufferedWriter bufferedwriter = new BufferedWriter(new FileWriter(file_tmp));
            String line;
            while((line = bufferedreader.readLine()) != null) 
            {
                bufferedwriter.write(line.replaceAll("\f", ""));
                bufferedwriter.newLine();
            }
            bufferedreader.close();
            bufferedwriter.close();
            if(file_txt.delete())
                file_tmp.renameTo(file_txt);
        }
        catch(FileNotFoundException filenotfoundexception) { }
        catch(IOException e) { }
    }
}
