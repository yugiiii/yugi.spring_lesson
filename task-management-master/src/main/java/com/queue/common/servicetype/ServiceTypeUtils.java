package com.queue.common.servicetype;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.google.api.client.util.Value;

/**
 * サービスタイプに関するクラス
 * @author shibatanaoto
 *
 */
@Component
public class ServiceTypeUtils {

	@Value("settings.service_type")
	private String serviceType;
	@Value("settings.env")
	private String env;

	public String getServiceType() {
		return serviceType;
	}
	
	/**
	 * CMSタイプの時を判定する
	 * @return
	 */
	public Boolean isCms() {
		if (StringUtils.equals(serviceType, "cms")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Serviceの時(Web, API...etc)
	 * @return
	 */
	public Boolean isService() {
		if (StringUtils.equals(serviceType, "service")) {
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean isRelease() {
		if (StringUtils.equals(env, "release")) {
			return true;
		} else {
			return false;
		}
	}

}
