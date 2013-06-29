package com.zhangwei.stock.gson;

import java.util.ArrayList;

public class StockList {
	//////////////////////////////////////////
	public int status; //1 when ok
	public long last_modify;  //列表上次更新时间
	
	/**
	 *  {@literal 上次扫描的stockid， sh600031}
	 * */
	String lastScanID;
	
	/**
	 *  {@literal 上次完成列表扫描的时间}
	 * */
	long lastScanTime;
	
	/**
	 *  {@literal 上次完成sina列表扫描的时间}
	 * */
	long lastHistoryRecordScanTime;
	
	/**
	 *  {@literal 上次扫描的stockid， sh600031}
	 * */
	String lastHistoryRecordScanID;
	
	/**
	 *  {@literal 上次完成发出通知的时间}
	 * */
	long lastNotifyTime;
	
	public ArrayList<String> shangzheng_list; //"sh600000", 上证
	public ArrayList<String>  shenzheng_list;  //"sz000858", 深证
	public ArrayList<String>  chuangye_list;   //"sz300005", 创业板
	

	////////////////////////////////////////////

	public transient static String ID = "_StockList_";
	private transient static int index = -1;
	public transient static String TAIL = "tail";
	public transient static String HISTORY_TAIL = "history_tail";
/*	public StockList(){
		shangzheng_list = new ArrayList<String>();
		shenzheng_list = new ArrayList<String>();
		chuangye_list = new ArrayList<String>();
		index = -1;
		lastScanTime = 0;
		lastScanID = null;
	}*/
	
	
	/**
	 *  type: 0 daily(default), 1 history 
	 *  
	 * */
	private boolean seekTo(String stockID, int type){
		boolean found = false;
		String cmp_tail = null;
		
		if(type==0){
			cmp_tail = TAIL;
		}else{
			cmp_tail = HISTORY_TAIL;
		}
		
		if(stockID.equals(cmp_tail)){
			found = true;
			index = shangzheng_list.size() + shenzheng_list.size() + chuangye_list.size();
		}else{
			if(shangzheng_list.contains(stockID)){
				found = true;
				index = shangzheng_list.indexOf(stockID);
			}else if(shenzheng_list.contains(stockID)){
				found = true;
				index = shenzheng_list.indexOf(stockID) + shangzheng_list.size();
			}else if(chuangye_list.contains(stockID)){
				found = true;
				index = chuangye_list.indexOf(stockID) + shangzheng_list.size() + shenzheng_list.size();
			}
		}
		
		return found;
	}
	
	/**
	 *  type: 0 daily(default), 1 history 
	 *  
	 * */
	private void seekTo(int index, int type){
		
		int sh_size = shangzheng_list.size();
		int sz_size = shenzheng_list.size();
		int cyb_size = chuangye_list.size();
		if(type==0){
			if(index<sh_size){
				lastScanID =  shangzheng_list.get(index);
				//index++;
			}else if(index<sh_size+sz_size){			
				lastScanID =  shenzheng_list.get(index-sh_size);
				//index++;
			}else if(index<sh_size+sz_size+cyb_size){
				lastScanID =  chuangye_list.get(index-sh_size-sz_size);
				//index++;
			}else{
				//no-op
				lastScanID = TAIL;
			}
		}else{
			if(index<sh_size){
				lastHistoryRecordScanID =  shangzheng_list.get(index);
				//index++;
			}else if(index<sh_size+sz_size){			
				lastHistoryRecordScanID =  shenzheng_list.get(index-sh_size);
				//index++;
			}else if(index<sh_size+sz_size+cyb_size){
				lastHistoryRecordScanID =  chuangye_list.get(index-sh_size-sz_size);
				//index++;
			}else{
				//no-op
				lastHistoryRecordScanID = HISTORY_TAIL;
			}
		}

	}
	
	/**
	 *  type: 0 daily(default), 1 history 
	 *  
	 * */
	public void rewind(int type){
		index=0;
		seekTo(index, type);
	}
	
	/**
	 *  type: 0 daily(default), 1 history 
	 *  
	 * */
	public void next(int type){
		seekTo(lastScanID, type);
		index++;
		seekTo(index, type);
		

	}
	
/*	public String getCurStockID(){		
		return lastScanID;
	}*/
	
	
	public String getlastScanID(){
		return lastScanID;
	}
	
	public void setlastScanID(String id){
		lastScanID = id;
	}
	
	public long getlastScanTime(){
		return lastScanTime;
	}
	
	
	public void setlastScanTime(long time){
		lastScanTime = time;
	}
	
	//sina
	public String getHistoryRecordScanID(){
		return lastHistoryRecordScanID;
	}
	
	public void setHistoryRecordScanID(String id){
		lastHistoryRecordScanID = id;
	}
	
	public long getHistoryRecordScanTime(){
		return lastHistoryRecordScanTime;
	}
	
	
	public void setHistoryRecordScanTime(long time){
		lastHistoryRecordScanTime = time;
	}
	
	public long getlastNotifyTime(){
		return lastNotifyTime;
	}
	
	public void setlastNotifyTime(long time){
		lastNotifyTime = time;
	}

}
