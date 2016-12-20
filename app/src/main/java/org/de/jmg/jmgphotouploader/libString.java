package org.de.jmg.jmgphotouploader;

/**
 * Created by hmnatalie on 20.12.16.
 */
public class libString
{
    public static boolean IsNullOrEmpty(String s)
    {
        if (s == null || s == "" || s.length() == 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public static int InStr(String s, String Search)
    {
        int Start = 1;
        return InStr(Start, s, Search);
    }

    public static int InStr(int Start, String s, String Search)
    {
        return s.indexOf(Search, Start - 1) + 1;
    }

    public static String Chr(int Code)
    {
        char c[] = {(char) Code};
        return new String(c);
    }

    public static String Left(String s, int length)
    {
        return s.substring(0, length);
    }

    public static int Len(String s)
    {
        return s.length();
    }

    public static String Right(String wort, int i)
    {

        return wort.substring(wort.length() - i);
    }

}
