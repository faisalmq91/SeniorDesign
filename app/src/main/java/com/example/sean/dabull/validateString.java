package com.example.sean.dabull;

/**
 * Created by Sean on 2/15/2016.
 */
public class validateString
{
    static boolean isValid(String str)
    {
        String pattern= "^[a-zA-Z0-9]*$";//Define so only Alphanumeric pass
        if (str.equals(""))//Invalid if blank
            return false;
        return str.matches(pattern);
    }
    static int isValidPassword(String str)
    {
        String pattern = "^[a-zA-Z0-9]*$";
        if (str.equals(""))
            return -1;
        if (str.length()<5 || str.length()>14)
            return 0;
        if(str.matches(pattern))
            return 1;
        return -2;
    }
    static int isValidEmail(String str)
    {
        if (str.equals(""))
            return -1;
        if (!str.contains("@") || !str.contains("."))
            return 0;
        return 1;
    }
}
