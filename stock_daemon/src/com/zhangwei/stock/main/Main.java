package com.zhangwei.stock.main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

import com.zhangwei.stock.service.TencentDailyTask;

public class Main {
	static TencentDailyTask dt;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		FileLock lck;
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
		}

	}

}
