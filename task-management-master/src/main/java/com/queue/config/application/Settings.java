package com.queue.config.application;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/*
 * 後々必要なものだけに絞る
 */
@Component
@ConfigurationProperties(prefix = "settings")
public class Settings {
	
	private String aes_key;
	private String cms_key;
	private String mail_aes_key;
	private String csv_aes_key;
	private String hash_seed;
	private Boolean encrypt;
	private String env;
	
	/*
	 * Setter
	 */
	
	/*
	 * *************
	 * ** Setting **
	 * *************
	 */
	public void setEncrypt(Boolean encrypt) {
		this.encrypt = encrypt;
	}
	
	public void setEnv(String env) {
		this.env = env;
	}
	
	
	
	/*
	 * *********
	 * ** key **
	 * *********
	 */
	public void setAesKey(String aes_key) {
		this.aes_key = aes_key;
	}
	
	public void setCmsKey(String cms_key) {
		this.cms_key = cms_key;
	}
	
	public void setMailAesKey(String mail_aes_key) {
		this.mail_aes_key = mail_aes_key;
	}
	
	public void setCsvAesKey(String csv_aes_key) {
		this.csv_aes_key = csv_aes_key;
	}
	
	public void setHashSeed(String hash_seed) {
		this.hash_seed = hash_seed;
	}
	
	/*
	 * Getter
	 */
	
	/*
	 * *************
	 * ** Setting **
	 * *************
	 */
	public Boolean getEncrypt() {
		return this.encrypt;
	}
	
	public String getEnv() {
		return this.env;
	}
	
	/*
	 * *********
	 * ** key **
	 * *********
	 */
	public String getAesKey() {
		return this.aes_key;
	}
	
	public String getCmsKey() {
		return this.cms_key;
	}
	
	public String getHashSeed() {
		return this.hash_seed;
	}
	
	public String getMailAesKey() {
		return this.mail_aes_key;
	}
	
	public String getCsvAesKey() {
		return this.csv_aes_key;
	}
	

}