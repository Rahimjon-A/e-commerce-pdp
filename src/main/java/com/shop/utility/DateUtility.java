package com.shop.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtility {

    public static String formatMyDate(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy  HH:mm");
        return dateFormat.format(date);
    }
}
