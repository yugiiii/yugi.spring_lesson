package com.queue.common.http;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public final class RequestUtils {
	
	private RequestUtils() {
    }

    /**
     * リクエストを返します。
     * 
     * @return リクエスト
     */
    public static HttpServletRequest getRequest() {
    		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    		return request;
    }
}
