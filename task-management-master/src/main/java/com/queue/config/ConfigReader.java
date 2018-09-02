package com.queue.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.queue.config.application.MailConfig;
// import com.queue.config.application.MailConfig;
// import com.queue.config.application.RedisConfig;
import com.queue.config.application.Settings;


@Component
public class ConfigReader {
	
	@Autowired
    private Settings settings;
	@Autowired
	private MailConfig mailConfig;
	
	/*
	 * *************
	 * ** Setting **
	 * *************
	 */
	public Boolean isEncrypt() {
		return settings.getEncrypt();
	}
	
	public String getEnv() {
		return settings.getEnv();
	}
	
	/*
	 * *********
	 * ** key **
	 * *********
	 */
	public String getAesKey() {
		return settings.getAesKey();
	}
	
	public String getCmsKey() {
		return settings.getCmsKey();
	}
	
	public String getMailAesKey() {
		return settings.getMailAesKey();
	}
	
	public String getCsvAesKey() {
		return settings.getCsvAesKey();
	}
	
	public String getHashSeed() {
		return settings.getHashSeed();
	}
	
	
	/*
	 * **********
	 * ** Mail **
	 * **********
	 */
	public String getHost() {
		return mailConfig.getHost();
	}
	
	public Integer getPort() {
		return mailConfig.getPort();
	}
	
	public String getUsername() {
		return mailConfig.getUsername();
	}
	
	public String getPassword() {
		return mailConfig.getPassword();
	}
	
}