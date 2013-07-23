package com.zhangwei.stock.main;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.zhangwei.stock.net.SinaStockHelper;
import com.zhangwei.stock.service.TencentDailyTask;

public class Main {
	static TencentDailyTask dt;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

/*		FileLock lck;
		try {
			lck = new FileOutputStream("D:\\.sobulldog_test\\_lock").getChannel().tryLock();
			if (lck == null) {
				System.err
						.println("A previous instance is already running....");
				System.exit(1);
			}
			System.err.println("This is the first instance of this program...");
			// Do some work here.....
			if (dt == null || !dt.isAlive()) {
				dt = new TencentDailyTask(args[0]);
				dt.start();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		SinaStockHelper ssh = SinaStockHelper.getInstance();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//小写的mm表示的是分钟  
		String b_str="2013-12-08";
		String e_str="2011-12-09";  
		try {
			java.util.Date start_date=sdf.parse(b_str);
			java.util.Date end_date=sdf.parse(e_str);
			ssh.get_HistoryRecords_from_sina("300031", start_date, end_date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		

	}

}
