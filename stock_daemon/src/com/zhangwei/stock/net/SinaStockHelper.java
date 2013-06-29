package com.zhangwei.stock.net;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;


import com.zhangwei.stock.androidconvert.Log;
import com.zhangwei.stock.gson.HistoryRecord;
import com.zhangwei.stock.gson.Stock;
import com.zhangwei.stock.utils.Constants;
import com.zhangwei.stock.utils.InputCheck;

public class SinaStockHelper {
	private static SinaStockHelper ins;
	private final String TAG = "SinaStockHelper";

	//http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/601398.phtml?year=2007&jidu=2
	String sina_history_stock_prefix = "http://vip.stock.finance.sina.com.cn/corp/go.php/vMS_MarketHistory/stockid/";
	
	
	private SinaStockHelper(){
		
	}
	
	public static SinaStockHelper getInstance(){
		if(ins==null){
			ins = new SinaStockHelper();
		}
		
		return ins;
	}
	
	public boolean judgeNetwork(){
		boolean result = false;
		try{
			Document doc = null;
			doc = Jsoup.connect("http://www.baidu.com").timeout(30000).get();
			if(doc!=null){
				result = true;
			}

		}catch(Exception e){
			e.printStackTrace();
		}
		
		return result;
	}
	
	/**
	 * 
	 *  从新浪获取历史记录，从较近到较远
	 *  从begindate（包含） 到 enddate（包含）, begindate is after enddate 逆序
	 * */
	public ArrayList<HistoryRecord> get_HistoryRecords_from_sina(String stockID, Date start_date, Date end_date){
		if(start_date.before(end_date)){
			Date tmp = end_date;
			end_date = start_date;
			start_date = tmp;
		}
		
		ArrayList<HistoryRecord> result = new ArrayList<HistoryRecord>();
		
		int start_year = start_date.getYear()+1900;
		int start_jidu = (start_date.getMonth())/3+1;
		int end_year = end_date.getYear()+1900;
		int end_jidu = (end_date.getMonth())/3+1;

		int year=start_year; //year
		int jidu=start_jidu; //jidu
		for( ; year>=end_year; year--){
			while(jidu>=end_jidu || year!=end_year){
				Log.e(TAG, "processing year:" + year + " jidu:" + jidu);
				//do..
				try {
					Document doc = null;
					//601398.phtml?year=2007&jidu=2
					String connect_url = sina_history_stock_prefix  + stockID + ".phtml?year=" + year + "&jidu=" + jidu;
					doc = Jsoup.connect(connect_url).timeout(30000).get();
					JsoupHelper jh = new JsoupHelper();
					
					//rank
					Element FundHoldSharesTable = doc.body().getElementById("FundHoldSharesTable");

					
					if(FundHoldSharesTable!=null){
						//Log.e(TAG, FundHoldSharesTable.text());
						Element FundHoldSharesTable_content = jh.search(FundHoldSharesTable, "tbody");
						if(FundHoldSharesTable_content!=null){
							ArrayList<Element> trs = jh.dump(FundHoldSharesTable_content, "tr");
							if(trs.size()>1){
								for(int index=1; index<trs.size() ; index++){
									Log.e(TAG, trs.get(index).text());
									
									//
									
									ArrayList<Element> tds = jh.dump(trs.get(index), "td");
									if(tds!=null && tds.size()==7){
										HistoryRecord hRecord = new HistoryRecord();
										
										hRecord.date = tds.get(0).text();
										hRecord.op = tds.get(1).text();
										hRecord.hp = tds.get(2).text();
										hRecord.cp = tds.get(3).text();
										hRecord.lp = tds.get(4).text();
										hRecord.tv = tds.get(5).text();
										hRecord.ta = tds.get(6).text();
										result.add(hRecord);
									}
									
								}
							}
							

						}

					}

				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					return null;
				}
				
				//往后一个季度
				if(jidu<=1){
					jidu=4; 
					break;
				}else{
					jidu--;
					continue;
				}
			}
		}

		
		return result;
		
	}

	

	
}
