package com.tcc.renxl.util;

import java.util.Date;

public class DateUtil {
    public static int differentDays(Date date1, Date date2)
    {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }

    public static int differentHours(Date date1, Date date2)
    {
        int hours = (int) ((date2.getTime() - date1.getTime()) / (1000*3600));
        return hours;
    }


    public static int differentMinute(Date date1, Date date2)
    {
        int minute = (int) ((date2.getTime() - date1.getTime()) / (1000*60));
        return minute;
    }

    public static int differentSecond(Date date1, Date date2)
    {
        int differentSecond = (int) ((date2.getTime() - date1.getTime()) / (1000));
        return differentSecond;
    }

}
