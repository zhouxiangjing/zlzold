package com.zxj.zlz;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Blog {
    String title;
    String user;
    String time;
    String content;

    Blog(String title, String user, Long time, String content) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.time = getDateToString(time, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 时间戳转换成字符窜
     * @param milSecond
     * @param pattern
     * @return
     */
    public static String getDateToString(long milSecond, String pattern) {
        Date date = new Date(milSecond);
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    /**
     * 将字符串转为时间戳
     * @param dateString
     * @param pattern
     * @return
     */
    public static long getStringToDate(String dateString, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try{
            date = dateFormat.parse(dateString);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        return date.getTime();
    }
}
