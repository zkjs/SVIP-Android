package com.zkjinshi.ritz.utils;

import java.util.UUID;

public class UUIDBuilder {
	
	private UUIDBuilder(){}
	
	private static UUIDBuilder instance;
	
	public synchronized static UUIDBuilder getInstance(){
		if(null == instance){
			instance = new UUIDBuilder();
		}
		return instance;
	}
	
	public String getRandomUUID(){
		return UUID.randomUUID().toString();
	}
}
