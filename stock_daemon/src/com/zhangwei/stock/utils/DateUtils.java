/**
 * 
 */
package com.zhangwei.stock.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author wuxiang
 *
 * @create 2013-4-10
 */
public class DateUtils {
	
	public static final String DATE_FORMAT_NORMAL="yyyy-MM-dd HH:mm";
	public static final String DATE_FORMAT_TODAY="HH:mm";

	public static Date stringToDate(String date,String format){
		try {
			SimpleDateFormat dateFormat=new SimpleDateFormat(format);
			Date dDate=dateFormat.parse(date);
			return dDate;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//根据给定日期返回制定
	public static String dateFormat(String date,String inFormat,String outFormat){
		SimpleDateFormat dateFormat=new SimpleDateFormat(outFormat);
		return dateFormat.format(stringToDate(date,inFormat));
	}
	
	public static String dateToString(Date date,String format){
		SimpleDateFormat dateFormat=new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	public static boolean isToday(String date,String format){
		Date dDate=stringToDate(date,format);
		Date today=new Date();
		Date start=new Date(today.getYear(), today.getMonth(), today.getDate(), 0, 0, 0);
		Date end=new Date(today.getYear(), today.getMonth(), today.getDate(), 23, 59, 59);
		if(dDate.getTime()>=start.getTime()&&dDate.getTime()<=end.getTime())
			return true;
		return false;
	}
	
	public static int  compareDay(Date d1,Date d2){
        Calendar c1 = Calendar.getInstance();  
        Calendar c2 = Calendar.getInstance();  
        c1.setTime(d1);  
        c2.setTime(d2);  
        
        if((c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR))!=0){
        	return (c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR));
        }
        
        if((c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH)) !=0){
        	return (c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH));
        }
        
        if((c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH)) !=0){
        	return (c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH));
        }
        

        return 0;
	}
	
}
