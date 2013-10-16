package cn.com.zdezclient.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 专门用于将数据库表述的时间格式转化成语义化的表述
 * @author werther
 *
 */

public class TimeToWords {
	//一天之内的几个阶段
	private static String PERIOD_BEFORE_DAWN = "凌晨";
	private static String PERIOD_MORNING = "早上";
	private static String PERIOD_NOON = "中午";
	private static String PERIOD_AFTER_NOON = "下午";
	private static String PERIOD_EVENING = "晚上";
	
	//用于一天之前的前缀
	private static String 	YESTERDAY = "昨天";
	 
	//用于日期显示
	private static String year = "年";
	private static String month = "月";
	private static String day = "日";
	
	//当前时间
	private static Timestamp nowTs = null;
	private static Timestamp yesterdayTs = null;
	private static Timestamp theDayBeforeYesterdayTs = null;
	private static Timestamp dayBeforeOneWeekTs = null;
	
	
	@SuppressWarnings({ "static-access", "deprecation" })
	public TimeToWords(){
		//set current timestamp
		Date dt = new Date();
		this.nowTs = new Timestamp(dt.getTime());
		
		//set yesterday timestamp
		Long temp = (long) ((nowTs.getHours()*60*60 + nowTs.getMinutes()*60 + nowTs.getSeconds())*1000 + 1000); 
		this.yesterdayTs = new Timestamp(nowTs.getTime() - temp);
		
		//set the before yesterday timestamp
		this.theDayBeforeYesterdayTs = new Timestamp(yesterdayTs.getTime() - 24*60*60*1000);
		
		//set the day of week ago timestamp
		Date dateNow = new Date();
		Calendar calendar = new GregorianCalendar();
	    calendar.setTime(dateNow);
		Long temp2 = (long) ((calendar.get(Calendar.DAY_OF_WEEK)-2)*24*60*60*1000 - 1000);
	    this.dayBeforeOneWeekTs = new Timestamp(this.yesterdayTs.getTime() - temp2);
	}
	
	/*
	 * 参数：Timestamp
	 * 计算传入时间位于一天的哪个时段
	 */
	@SuppressWarnings("deprecation")
	private String periodInOneDay(Timestamp ts){
		String pStr = "出错了！！";
		int hour = ts.getHours();
		if(hour>=0 && hour<5){
			//[00:00:00.0, 04:59:59.0]
			pStr = PERIOD_BEFORE_DAWN;
		}else if(hour>=5 && hour<12){
			pStr = PERIOD_MORNING;
		}else if(hour>=12 && hour<13){
			pStr = PERIOD_NOON;
		}else if(hour>=13 && hour<19){
			pStr = PERIOD_AFTER_NOON;
		}else{
			pStr = PERIOD_EVENING;
		}
		
		return pStr;
	}
	
	/*
	 * 传入参数：timstamp
	 * 计算（并返回）是“今天”，还是“昨天”，还是“”(昨天之前，返回空字符串)
	 */
	private String yesterdayPrefixOfDay(Timestamp ts){
		String prefix = "今天";
		if(ts.before(TimeToWords.yesterdayTs) && ts.after(TimeToWords.theDayBeforeYesterdayTs)){
			prefix = YESTERDAY;
		}else if(ts.before(theDayBeforeYesterdayTs)){
			prefix = "";
		}
		
		return prefix;
	}
	
	/**
	 * 开始计算是星期几
	 * 若在一个星期之前，返回空字符串
	 * @param testTs
	 * @return string
	 */
	private String dayOfWeek(Timestamp ts) {
		String dayOfWeek = "";
		if(ts.after(dayBeforeOneWeekTs)){
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("E");
		    dayOfWeek = simpleDateFormat.format(ts);
		}
		return dayOfWeek;
	}
	
//	@SuppressWarnings("deprecation")
//	public static void main(String[] args) throws ParseException {
//		Date dt = new Date(2012-1900, 3-1, 3, 00, 00);
//		Timestamp testTs = new Timestamp(dt.getTime());
//		TimeToWords ttw = new TimeToWords();
//		
//		String period = ttw.periodInOneDay(testTs);
//		
//		System.out.println("Now time                      :    " +nowTs);
//		System.out.println("Test time is                  :    " +testTs);
//		System.out.println("Test for period method        :    " +period);
//		
//		//test for prefix
//		String prefix = ttw.yesterdayPrefixOfDay(testTs);
//		System.out.println("Test for calculate the prefix :    " +prefix);
//		
//		//Test for week display
//		String dayOfWeek = "";
//		if(prefix.equals("")){
//			dayOfWeek = ttw.dayOfWeek(testTs);
//		}
//		
//		
//		//Test for date display
//		if(dayOfWeek.equals("") && testTs.getYear() == nowTs.getYear()){
//			System.out.println("在一个星期之前，而且在同一年。");
//			DateFormat df = new SimpleDateFormat("MM月dd日");
//			System.out.println(df.format(testTs));
//			
//		}else{
//			System.out.println("在今年之前，需要显示年月日。");
//			DateFormat df = new SimpleDateFormat("yyyy年MM月dd日");
//			System.out.println(df.format(testTs));
//		}
//	     
//	}
	
	@SuppressWarnings("deprecation")
	public String[] getWordsOfTime(Timestamp ts){
		String[] results = {"",""};
		DateFormat df = new SimpleDateFormat("HH:mm");
		DateFormat df2 = new SimpleDateFormat("MM月dd日");
		DateFormat df3 = new SimpleDateFormat("yyyy年MM月dd日");
		String date = df3.format(ts);
		String time = df.format(ts);
		String dayOfMounth = df2.format(ts);
		String period = this.periodInOneDay(ts);
		String prefix = this.yesterdayPrefixOfDay(ts);
		String dayOfWeek = this.dayOfWeek(ts);
		if(!prefix.equals("")){
			if(prefix.equals("今天")){
				results[0] = period + " " + time;
				results[1] = results[0];
			}else{
				results[0] = prefix + " " +period;
				results[1] = prefix + " " +period + " " + time;
			}
		}else if(!dayOfWeek.equals("")){
			results[0] = dayOfWeek + " " + period;
			results[1] = dayOfWeek + " " + period + " " + time;
		}else if(dayOfWeek.equals("") && ts.getYear() == nowTs.getYear()){
			//在一个星期之前，而且在同一年。
			results[0] = dayOfMounth;
			results[1] = dayOfMounth + " " + period + " " + time;
		}else{
			//在今年之前，需要显示年月日。
			results[0] = date;
			results[1] = date + " " + period + " " + time;
		}
		
		return results;
	}

	
}
