package com.zhangwei.stocklist;

public class AssetManager {
	private static AssetManager ins;
	
	private AssetManager(){
		
	}
	
	public static AssetManager getInstance(){
		if(ins==null){
			ins = new AssetManager();
		}
		
		return ins;
	}
	
	

}
