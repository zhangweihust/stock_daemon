package com.zhangwei.stock.main;

import com.zhangwei.stock.service.DailyTask;

public class Main {
	static DailyTask dt;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		if(dt==null || !dt.isAlive()){
			dt = new DailyTask(args[0]);
			dt.start();
		}

	}

}
