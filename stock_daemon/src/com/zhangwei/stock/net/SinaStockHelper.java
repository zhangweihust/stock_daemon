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
	//String doctor_url = "http://stockapp.finance.qq.com/doctor/sz002605.html";
	String doctor_url_prefix = "http://stockapp.finance.qq.com/doctor/";
	String ycgz_url_prefix = "http://img.gtimg.cn/copage/ycgz/htm/";//600315.htm";
	
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
								Element head = trs.get(0);
								
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
	
	/**
	 * 
	 *  从腾讯doctor获取信息，再从腾讯英策估值中完善信息
	 * */
	public Stock get_stock_from_tencent(String stockID){

		try {
			String result_stockname = null;
			String result_rank = null;
			String result_info = null;
			String result_descrp = null;
			String result_trend = null;
			String result_trend_detail = null;
			String result_quality = null;
			String result_quality_detail = null;
			
			Document doc = null;
			String connect_url = doctor_url_prefix + stockID + ".html";
			doc = Jsoup.connect(connect_url).timeout(30000).get();
			JsoupHelper jh = new JsoupHelper();
			
			//rank
			Element lefttd = doc.body().getElementById("lefttd");
			if(lefttd!=null){
				//Element rank = lefttd.select("div").first();
				Element rank =  jh.search(lefttd, "div" , "0");
				if(rank!=null){
					result_rank = rank.attr("class");
				}

			}

			//Log.i("ok", "rank:" + rank.attr("class"));
			
			Element qt_ctn1 = doc.body().getElementById("qt-ctn1");
			if(qt_ctn1!=null){
				//Element stockname = qt_ctn1.select("span.dh_name").first();
				Element stockname =  jh.search(qt_ctn1, "span.dh_name" , "0");
				if(stockname!=null){
					result_stockname = stockname.text();
				}

			}

			//Log.i("ok", "stockname : " + stockname.text()); //

			Element righttd = doc.body().getElementById("righttd");
			if(righttd!=null){
				//Element info = righttd.select("div.text-head").first();
				Element info =  jh.search(righttd, "div.text-head" , "0");
				if(info!=null){
					result_info = info.text();
				}
				
				Element descrp = righttd.getElementById("doctor_h2desp");
				if(descrp!=null){
					result_descrp = descrp.text();
				}
			}



			
			Element boxb = doc.body().getElementById("boxb");
			if(boxb!=null){
				Element trend =  jh.search(boxb, "div.icon01/span" , "0/0");
				if(trend!=null){
					result_trend = trend.text();
				}
				
				Element trend_detail = jh.search(boxb, "div.data1", "0");
				if(trend_detail!=null){
					result_trend_detail = trend_detail.text();
				}
				
				
				Element quality = jh.search(boxb, "div.icon02/span", "0/0");
				if(quality!=null){
					result_quality = quality.text();
				}
				
				Element quality_detail =  jh.search(boxb, "div.data2", "0");
				if(quality_detail!=null){
					result_quality_detail = quality_detail.text();
				}

			}

			
			Stock stock = new Stock(stockID, result_stockname, result_rank, 
					result_info, result_descrp, result_trend,  result_trend_detail,
					result_quality, result_quality_detail);
			
			if(fetch_ycgz(stock)){
				Date now = new Date();
				DateFormat df = DateFormat.getDateInstance();
				stock.scan_date = df.format(now);
				Log.i("test", "stock.scan_date:" + stock.scan_date);
			}else{
				return null;
			}
			

			return stock;

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			return null;
		}

	}
	
	public boolean fetch_ycgz(Stock  stock){
		//http://stockhtm.finance.qq.com/sstock/quotpage/q/600315.htm#ycgz
		//http://img.gtimg.cn/copage/ycgz/htm/600315.htm
		
		Document doc = null;
		
		if(stock==null || stock.id==null){
			return false;
		}
		
		String id = Pattern.compile("[a-zA-Z]").matcher(stock.id).replaceAll("");
		String connect_url = ycgz_url_prefix + id + ".htm";
		Log.i("test", "fetch_ycgz connect_url:" + connect_url);
		
		JsoupHelper jh = new JsoupHelper();
		try {
			doc = Jsoup.connect(connect_url).timeout(30000).get();

			Element values =  jh.search(doc.body(), "div.zdig/div" , "0/1");
			if(values!=null){
				//Element values = doc.body().select("div.zdig").first().select("div").first().child(0);//("div.title");
				//jh.dump(doc.body(), "div.zdig_div" );
				
				Double max = 0.0;
				Double min = 999999999.0;
				for(TextNode tn : values.textNodes()){
					if(InputCheck.checkNum(tn.text().trim())){
						//Log.i("stock qujian", tn.text());
						Double temp = -1.0;
						try{
							temp = Double.valueOf(tn.text().trim());
						}catch(NumberFormatException ex){
							
						}
						
						if(temp>0 && max<temp){
							max = temp;
						}
						if(temp>0 && min>temp){
							min = temp;
						}
					}
					
				}
				DecimalFormat df = new DecimalFormat(".##");
				if(max>0.0){
					stock.stockEstimateHighValue = df.format(max);
					Log.i("stockEstimateHighValue", stock.stockEstimateHighValue );
				}
				
				if(min<999999999.0){
					stock.stockEstimateLowValue = df.format(min);;
					Log.i("stockEstimateLowValue", stock.stockEstimateLowValue );
				}
			}

			
			//ArrayList<Element> result_descrp = jh.dump(doc.body(), "td.gray_bottom_solid/tbody/td/strong" );
			//ArrayList<Element> result_descrp = jh.dump(doc.body(), "td.gray_bottom_solid/tbody/td");
			
			//公司品质
			Element stockQualityElement = jh.search(doc.body(), "td.gray_bottom_solid/tbody/td", "0/0/1");
			String[] stockQualityTexts = jh.getTextFromElement(stockQualityElement);
			if(stockQualityTexts!=null && stockQualityTexts.length>0){
				stock.stockQuality = stockQualityTexts[0].replaceAll("[:： ]", "");
				Log.i(TAG, Constants.stockQuality + " :" + stock.stockQuality);
				
				
				Element stockSafeDetailElement = jh.search(doc.body(), "td.gray_bottom_solid/tbody/td", "0/0/2");
				if(stockSafeDetailElement!=null){
					stock.stockQuality_detail = stockSafeDetailElement.text();
					Log.i(TAG, Constants.stockQuality + "细节 :" + stock.stockQuality_detail);
				}
				
			}
			
			//增长速度
			Element stockIncrementElement = jh.search(doc.body(), "td.gray_bottom_solid/tbody/td", "0/0/3");
			String[] stockIncrementTexts = jh.getTextFromElement(stockIncrementElement);
			if(stockIncrementTexts!=null && stockIncrementTexts.length>0){
				stock.stockIncrement = stockIncrementTexts[0].replaceAll("[:： ]", "");
				Log.i(TAG, Constants.stockIncrement + " :" + stock.stockIncrement);
				
				Element stockIncrementDetailElement = jh.search(doc.body(), "td.gray_bottom_solid/tbody/td", "0/0/4");
				if(stockIncrementDetailElement!=null){
					stock.stockIncrement_detail = stockIncrementDetailElement.text();
					Log.i(TAG, Constants.stockIncrement + "细节 :" + stock.stockIncrement_detail);
				}
			}
			
			//资本安全
			Element stockSafeElement = jh.search(doc.body(), "td.gray_bottom_solid/tbody/td", "0/0/5");
			String[] stockSafeTexts = jh.getTextFromElement(stockSafeElement);
			if(stockSafeTexts!=null && stockSafeTexts.length>0){
				stock.stockSafe = stockSafeTexts[0].replaceAll("[:： ]", "");
				Log.i(TAG, Constants.stockSafe + " :" + stock.stockSafe);

				Element stockSafeDetailElement = jh.search(doc.body(), "td.gray_bottom_solid/tbody/td", "0/0/6");
				if(stockSafeDetailElement!=null){
					stock.stockSafe_detail = stockSafeDetailElement.text();
					Log.i(TAG, Constants.stockSafe + "细节 :" + stock.stockSafe_detail);
				}
			}
			
			//市场价值
			Element stockMarketValueElement = jh.search(doc.body(), "td.gray_bottom_solid/tbody/td", "1/0/1");
			String[] stockMarketValueTexts = jh.getTextFromElement(stockMarketValueElement);
			if(stockQualityTexts!=null && stockMarketValueTexts.length>0){
				stock.stockMarketValue = stockMarketValueTexts[0].replaceAll("[:： ]", "");
				Log.i(TAG, Constants.stockMarketValue + " :" + stock.stockMarketValue);
				
				Element stockMarketValueDetailElement = jh.search(doc.body(), "td.gray_bottom_solid/tbody/td", "1/0/2");
				if(stockMarketValueDetailElement!=null){
					stock.stockMarketValue_detail = stockMarketValueDetailElement.text();
					Log.i(TAG, Constants.stockMarketValue + "细节 :" + stock.stockMarketValue_detail);
				}
			}
			
			//资产价值
			Element stockAssetValueTextsElement = jh.search(doc.body(), "td.gray_bottom_solid/tbody/td", "1/0/3");
			String[] stockAssetValueTexts = jh.getTextFromElement(stockAssetValueTextsElement);
			if(stockAssetValueTexts!=null && stockAssetValueTexts.length>0){
				stock.stockAssetValue = stockAssetValueTexts[0].replaceAll("[:： ]", "");
				Log.i(TAG, Constants.stockAssetValue + " :" + stock.stockAssetValue);
				
				Element stockAssetValueDetailElement = jh.search(doc.body(), "td.gray_bottom_solid/tbody/td", "1/0/4");
				if(stockAssetValueDetailElement!=null){
					stock.stockAssetValue_detail = stockAssetValueDetailElement.text();
					Log.i(TAG, Constants.stockAssetValue + "细节 :" + stock.stockAssetValue_detail);
				}
			}
			
			
			//收益价值
			Element stockReturnValueTextsElement = jh.search(doc.body(), "td.gray_bottom_solid/tbody/td", "1/0/5");
			String[] stockReturnValueTexts = jh.getTextFromElement(stockReturnValueTextsElement);
			if(stockReturnValueTexts!=null && stockReturnValueTexts.length>0){
				stock.stockReturnValue = stockReturnValueTexts[0].replaceAll("[:： ]", "");
				Log.i(TAG, Constants.stockReturnValue + " :" + stock.stockReturnValue);
				
				Element stockReturnValueElement = jh.search(doc.body(), "td.gray_bottom_solid/tbody/td", "1/0/6");
				if(stockReturnValueElement!=null){
					stock.stockReturnValue_detail = stockReturnValueElement.text();
					Log.i(TAG, Constants.stockReturnValue + "细节 :" + stock.stockReturnValue_detail);
				}
			}
		    
		    Elements es2 = jh.match(doc.body(), "table.table1/tbody/tr/td/table/tbody/tr/td/span", "0/0/8/0/0/0/0/2");
		    if(es2!=null){
			    for(Element e:es2){
					if(e.text()!=null && e.text().contains("股")){
						stock.Size = e.text();
					}else if(e.text()!=null && e.text().contains("元")){
						stock.MarketValue = e.text();
					}else if(e.text()!=null && e.text().contains("行业")){
						stock.Category = e.text();
					}else {
						//stock.Category = e.text();
					}
			    }
		    }

		    
		    Elements es3 = jh.match(doc.body(), "span.logo", "0");
		    if(es3!=null){
				for(Element elem :es3){
					
					if(elem.text()!=null && elem.text().contains("更新")){
						Log.i("logo", elem.text());
						stock.YC_Time = elem.text();
					}
				}	
		    }



			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
		return true;
		
	}
	
}
