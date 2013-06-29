package com.zhangwei.stocklist;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.apache.http.util.ByteArrayBuffer;

/*import android.content.Context;
import android.content.res.AssetManager;
import cn.zipper.framwork.core.ZApplication;*/

import com.google.gson.Gson;
import com.zhangwei.stock.gson.DailyList;
import com.zhangwei.stock.gson.Stock;
import com.zhangwei.stock.gson.StockList;
import com.zhangwei.stock.storage.SDCardStorageManager;
import com.zhangwei.stock.utils.DateUtils;

public class StockListHelper {

	private static  StockListHelper ins;
	private static String TAG = "StockListHelper";
	
	//private Context context;
	private DailyList dailylist;
	private StockList stocklist;
	
	private StockListHelper(){
		//context = ZApplication.getInstance();
		dailylist = null;
		stocklist = null;
	}
	
	public synchronized static StockListHelper getInstance(){
		if(ins==null){
			ins = new StockListHelper();
		}
		
		return ins;
	}
	
	public synchronized StockList getStockList(){
		//1. from memory
		if(stocklist!=null){
			return stocklist;
		}
		
		//2. from internal storage
		stocklist = (StockList) SDCardStorageManager.getInstance()
				                              .getItem(null, StockList.ID, StockList.class);
		if(stocklist!=null){
			return stocklist;
		}
	
		//3. from assets last
		//use default stock list in assets
		//AssetManager manager = context.getAssets();
		try {
			//InputStream mInput = manager.open("stock_list");
			File dataFile = new File("stock_list");
			if(!dataFile.exists()){
				return null;
			}
			
			//InputStream mInput = manager.open("daily_list");
			FileInputStream mInput =  new FileInputStream(dataFile);
			
			// myData.txt can't be more than 2 gigs.
            //int size = mInput.available();
            //byte[] buffer = new byte[size];
            //mInput.read(buffer);

			byte[] buffer = new byte[1024];
			ByteArrayBuffer ba = new ByteArrayBuffer(1024);
			int len = 0;
			while ((len = mInput.read(buffer)) != -1) {
				ba.append(buffer, 0, len);
			}
 
            // byte buffer into a string
            String text = new String(ba.toByteArray());
            
            
            Gson gson = new Gson();
            stocklist = gson.fromJson(text, StockList.class);
			
            mInput.close();
            
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return stocklist;
	}
	
	public synchronized void persistStockList(StockList stocklist){

		//save to internal storage
		if(stocklist!=null){
			SDCardStorageManager.getInstance()
			              .putItem(null, StockList.ID, stocklist, StockList.class);
		}

	}
	
	public synchronized DailyList getDailyList(){

		//1. from memory
		if(dailylist!=null){
			return dailylist;
		}
		
		//2. from internal storage
		dailylist = (DailyList) SDCardStorageManager.getInstance()
				                              .getItem(null, DailyList.ID, DailyList.class);
		if(dailylist!=null){
			return dailylist;
		}
		
		//3. from assets last
		//use default stock list in assets
		//AssetManager manager = context.getAssets();
		try {
			File dataFile = new File("daily_list");
			if(!dataFile.exists()){
				return null;
			}
			
			//InputStream mInput = manager.open("daily_list");
			FileInputStream mInput =  new FileInputStream(dataFile);
			// myData.txt can't be more than 2 gigs.
            int size = mInput.available();
            byte[] buffer = new byte[size];
            mInput.read(buffer);
            mInput.close();
 
            // byte buffer into a string
            String text = new String(buffer);
            Gson gson = new Gson();
            dailylist = gson.fromJson(text, DailyList.class);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return dailylist;
	}
	
	/*public void persistDailyList(DailyList dlist){

		//save to internal storage
		if(dlist!=null){
			StorageManager.getInstance(null).putItem(DailyList.ID, dlist, DailyList.class);
		}

	}*/
	
	
	public synchronized void persistLastStock(Stock stock){

		//save to internal storage
		if(stock!=null){
			SDCardStorageManager.getInstance()
			              .putItem("last", stock.id, stock, Stock.class);
		}

	}
	
	public synchronized Stock getLastStock(String stockid){
		return (Stock) SDCardStorageManager.getInstance()
				                     .getItem("last", stockid, Stock.class);
	}
	
	public synchronized void persistHistoryStock(Stock stock){
		String date = DateUtils.dateToString(new Date(), "yyyyMMdd");
		//save to internal storage
		if(stock!=null){
			SDCardStorageManager.getInstance()
			              .putItem("history/" + stock.id, date, stock, Stock.class);
		}

	}
	
	/**
	 *  比较两个stock是否改变
	 *  true 改变
	 *  false 未改变
	 * */
	public synchronized static boolean isChangeStock(Stock left, Stock right){
		if(left==null && right!=null){
			return true;
		}else if(left!=null && right==null){
			return true;
		}else if(left==null && right==null){
			return false;
		}
		
		
		
		boolean change = false;
		if(isChange(left.rank, right.rank)){
			change = true;
		}
		
		if(isChange(left.info, right.info)){
			change = true;
		}
		
		if(isChange(left.trend, right.trend)){
			change = true;
		}
		
		if(isChange(left.quality, right.quality)){
			change = true;
		}
		
		if(isChange(left.YC_Time, right.YC_Time)){
			change = true;
		}
				
		return change;
	}
	
	private static boolean isChange(String a, String b){
		boolean change = false;
		if(a==null && b !=null){
			change = true;
		}else if(a!=null && b==null){
			change = true;
		}else if(a!=null && b!=null && !a.equals(b)){
			change = true;
		}
		
		return change;
	} 
}
