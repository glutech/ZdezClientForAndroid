package cn.com.zdezclient.utils;
/**
 * 用于将时间戳格式化成"几天前","几秒前"的字符串
 * @author werther
 *
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimestampToSemanticFomat {

    public static Date getDateByString(String time) {
        Date date = null;
        if (time == null)
            return date;
        String date_format = "yyyy-mm-dd hh:mm:ss";
        SimpleDateFormat format = new SimpleDateFormat(date_format);
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String getShortTime(String dateline) {
        String shortstring = null;
        Date date = getDateByString(dateline);
        if(date == null) return shortstring;
        
        long now = Calendar.getInstance().getTimeInMillis();
        long deltime = (now - date.getTime())/1000;
        if(deltime > 365*24*60*60) {
            shortstring = (int)(deltime/(365*24*60*60)) + "年前";
        } else if(deltime > 24*60*60) {
            shortstring = (int)(deltime/(24*60*60)) + "天前";
        } else if(deltime > 60*60) {
            shortstring = (int)(deltime/(60*60)) + "小时前";
        } else if(deltime > 60) {
            shortstring = (int)(deltime/(60)) + "分前";
        } else if(deltime > 1) {
            shortstring = deltime + "秒前";
        } else {
            shortstring = "1秒前";
        }
        return shortstring;
    }

//    //Timestamp转化为String:
//    public static String timestampToStr(long dateline){
//        Timestamp timestamp = new Timestamp(dateline*1000);
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm :ss");//定义格式，不显示毫秒
//       return df.format(timestamp);
//    }
    
//    
//    public static void main(String[] args) {
////        long dateline = 1335189486;
////        System.out.println(getShortTime(dateline));
////        String time = "2012-04-20 10:40:55";
////        System.out.println(getShortTime(time));
//    }

}
