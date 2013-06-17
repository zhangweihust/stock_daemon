package com.zhangwei.stock.main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

import com.zhangwei.stock.service.DailyTask;

public class Main {
	static DailyTask dt;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		FileLock lck;
		try {
			lck = new FileOutputStream("D:\\.sobulldog\\_lock").getChannel().tryLock();
			if (lck == null) {
				System.out
						.println("A previous instance is already running....");
				System.exit(1);
			}
			System.out.println("This is the first instance of this program...");
			// Do some work here.....
			if (dt == null || !dt.isAlive()) {
				dt = new DailyTask(args[0]);
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
