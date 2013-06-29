package com.zhangwei.stock.service;


import java.util.ArrayList;
import java.util.Date;

import com.zhangwei.stock.androidconvert.Environment;
import com.zhangwei.stock.androidconvert.Log;
import com.zhangwei.stock.gson.HistoryRecord;
import com.zhangwei.stock.gson.Stock;
import com.zhangwei.stock.gson.StockList;
import com.zhangwei.stock.net.SinaStockHelper;
import com.zhangwei.stock.net.TencentStockHelper;
import com.zhangwei.stock.utils.DateUtils;
import com.zhangwei.stocklist.StockListHelper;


public class SinaDailyTask extends Thread {
	private static final String TAG = "SinaDailyTask";
	private boolean update;
	private boolean isAbort;
	private String completeID;
	
	public SinaDailyTask(String path){
		Environment.parent_path = path;
		Log.e(TAG, "SinaDailyTask() - path:" + path);
	}
	
	public void run() {
		update = false;
		isAbort = false;
		completeID = null;		

		// TODO Auto-generated method stub

		StockList stocklist = StockListHelper.getInstance().getStockList();
		String curScanStockID = null;
		
		int errCount = 0; //连续出错计数
		int retry = 0; //重试计数
		
		//stocklist.setHistoryRecordScanID("sh600001");
		
		do{
			Log.i(TAG, " curScanStockID:" + curScanStockID + " errCount:" + errCount + " retry:" + retry);
			if(errCount<1){
				curScanStockID = stocklist.getHistoryRecordScanID();
				Date lastscan_day = new Date(stocklist.getHistoryRecordScanTime());
				Date now_day = new Date();
				
				if(curScanStockID==null){
					break;
				}
				
				if(curScanStockID.equals(StockList.HISTORY_TAIL)){
					if(DateUtils.compareDay(lastscan_day, now_day)==0){
						Log.e(TAG,"last scan time is the same day of the today, ingore");
						completeID = StockList.HISTORY_TAIL;
						break;
					}else{
						//new day
						stocklist.rewind(1);
						errCount = 0;
						retry = 0;
						continue;
					}

				}
				
			}else{
				if(SinaStockHelper.getInstance().judgeNetwork()){
					Log.i(TAG, "www.baidu.com is ok");
					//网络可用情况下，如果重试超过3次，则说明目的端有问题，取下一个
					if(retry>3){
						retry=0;
						errCount = 0;
						stocklist.next(1);
						continue;
					}

					retry++;

				}else{
					Log.i(TAG, "www.baidu.com not connected");
					//没有网络就一直连接百度看是否能连上
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			Log.i(TAG, "curScanStockID:" + curScanStockID);
			Date start_date = new Date();
			Date end_date = new Date();
			 
			ArrayList<HistoryRecord> records = SinaStockHelper.getInstance().get_HistoryRecords_from_sina(curScanStockID, start_date, end_date);
			if(records!=null){
				Log.i(TAG, "a stock's records done,  curScanStockID:" + curScanStockID);
				//lastStockID = stock.id;
				//实时记录扫描的id到dailyList中
				//stocklist.setHistoryRecordScanID(lastStockID);
				update = true;
				completeID = curScanStockID;
				stocklist.next(1);
				stocklist.setHistoryRecordScanTime(System.currentTimeMillis());
				errCount = 0;
				
				//对比laststock和这个stock是否有变化
				
				StockListHelper.getInstance().persistStockList(stocklist);

			}else{
				errCount++;
			}
			

			
		}while(curScanStockID!=null);


		
		Log.i(TAG, "loop over, update:" + update + " isAbort:" + isAbort + " completeID:" + completeID);

		if(update){
			if(!isAbort){
				//完成这次扫描(中途被终止的不算)，记录时间
				stocklist.setHistoryRecordScanTime(System.currentTimeMillis());
			}
			Log.i(TAG, "persistStockList!");
			StockListHelper.getInstance().persistStockList(stocklist);
		}

		
		return;
	
	}

}

