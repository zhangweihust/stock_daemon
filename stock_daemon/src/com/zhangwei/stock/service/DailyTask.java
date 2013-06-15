package com.zhangwei.stock.service;


import java.util.Date;

import com.zhangwei.stock.androidconvert.Log;
import com.zhangwei.stock.gson.Stock;
import com.zhangwei.stock.gson.StockList;
import com.zhangwei.stock.net.TencentStockHelper;
import com.zhangwei.stock.utils.DateUtils;
import com.zhangwei.stocklist.StockListHelper;


public class DailyTask extends Thread {
	private static final String TAG = "DailyTask";
	private StockList stocklist;
	private boolean update;
	private boolean isAbort;
	private String completeID;
	
	public void run() {
		update = false;
		isAbort = false;
		completeID = null;
		stocklist = StockListHelper.getInstance().getStockList();
		

		// TODO Auto-generated method stub

		StockList stocklist = StockListHelper.getInstance().getStockList();
		String curScanStockID = null;
		
		int errCount = 0; //连续出错计数
		int retry = 0; //重试计数
		
		//stocklist.setlastScanID("sh600001");
		
		do{
			Log.e(TAG, " curScanStockID:" + curScanStockID + " errCount:" + errCount + " retry:" + retry);
			if(errCount<1){
				curScanStockID = stocklist.getCurStockID();
				Date lastscan_day = new Date(stocklist.getlastScanTime());
				Date now_day = new Date();
				
				if(curScanStockID==null){
					break;
				}
				
				if(curScanStockID.equals(StockList.TAIL)){
					if(DateUtils.compareDay(lastscan_day, now_day)==0){
						Log.e(TAG,"last scan time is the same day of the today, ingore");
						completeID = StockList.TAIL;
						break;
					}else{
						//new day
						stocklist.rewind();
						errCount = 0;
						retry = 0;
						continue;
					}

				}
				
			}else{
				if(TencentStockHelper.getInstance().judgeNetwork()){
					Log.e(TAG, "www.baidu.com is ok");
					//网络可用情况下，如果重试超过3次，则说明目的端有问题，取下一个
					if(retry>3){
						retry=0;
						errCount = 0;
						stocklist.next();
						continue;
					}

					retry++;

				}else{
					Log.e(TAG, "www.baidu.com not connected");
					//没有网络就一直连接百度看是否能连上
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			Log.e(TAG, "curScanStockID:" + curScanStockID);
			
/*			if(isCancelled()){
				Log.e(TAG, "isCancelled, curScanStockID:" + curScanStockID);
				isAbort = true;
				break;
			}*/
			
			/*stocklist.next();
			stocklist.setlastScanTime(System.currentTimeMillis());*/
			
			Stock stock = TencentStockHelper.getInstance().get_stock_from_tencent(curScanStockID);
			if(stock!=null){
				Log.e(TAG, "a stock done,  stock.id:" + stock.id);
				//lastStockID = stock.id;
				//实时记录扫描的id到dailyList中
				//stocklist.setlastScanID(lastStockID);
				update = true;
				completeID = stock.id;
				stocklist.next();
				stocklist.setlastScanTime(System.currentTimeMillis());
				errCount = 0;
				
				//对比laststock和这个stock是否有变化
				Stock lastStock = StockListHelper.getInstance().getLastStock(stock.id);
				
				if(StockListHelper.isChangeStock(lastStock, stock)){
					//save stock into history stocks					
					StockListHelper.getInstance().persistHistoryStock(stock);
					
					//save stock into internal storage
					StockListHelper.getInstance().persistLastStock(stock);
				}
				
				StockListHelper.getInstance().persistStockList(stocklist);

			}else{
				errCount++;
			}
			

			
		}while(curScanStockID!=null);


		
		Log.e(TAG, "loop over, update:" + update + " isAbort:" + isAbort + " completeID:" + completeID);

		if(update){
			if(!isAbort){
				//完成这次扫描(中途被终止的不算)，记录时间
				stocklist.setlastScanTime(System.currentTimeMillis());
			}
			Log.e(TAG, "persistStockList!");
			StockListHelper.getInstance().persistStockList(stocklist);
		}

		
		return;
	
	}

}
