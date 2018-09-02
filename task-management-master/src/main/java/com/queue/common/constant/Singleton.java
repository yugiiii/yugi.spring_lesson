package com.queue.common.constant;

import org.springframework.beans.factory.annotation.Autowired;

import com.queue.common.servicetype.ServiceTypeUtils;

public class Singleton {

	@Autowired
	private ServiceTypeUtils serviceTypeUtils;
	
	public static Long serverTimeDiff;
	
	static {
		serverTimeDiff = 0L;
	}
	
	public static void setServerTime(long newServerTime) {
		// TODO: 本番環境ではできないようにする
		if (true) {
			serverTimeDiff = newServerTime - System.currentTimeMillis();
		}
	}
	
	public static void resetServerTime() {
		// TODO: 本番環境ではできないようにする
		if (true) {
			serverTimeDiff = 0L;
		}
	}
	
	public static long getServerTime() {
		return System.currentTimeMillis() + serverTimeDiff;
	}
}
