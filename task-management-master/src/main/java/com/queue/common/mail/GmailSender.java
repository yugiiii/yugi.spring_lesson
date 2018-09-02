package com.queue.common.mail;

import java.util.HashMap;

import java.util.Map;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.queue.config.ConfigReader;


/**
 * Gmailを送信するためのクラス
 * @author shibatanaoto
 *
 */
@Component
public class GmailSender {
	
	@Autowired
	private ConfigReader configReader;
	
	// エンコードを設定
	private String charset = "UTF-8";
	private String encoding = "base64";

	// Gmail用の設定をする
	private boolean starttls = true;

	
	// headerを登録する
	private Map<String, String> headers = new HashMap<String, String>(){
	    private static final long serialVersionUID = 1L;
	    {
	      put("Content-Transfer-Encoding", encoding);
	    }
	};
	
	
	/**
	 * タイトルと本文を設定して、メールを送信する
	 * @param subject
	 * @param content
	 */
	public void send(String to, String subject, String content) throws EmailException {

		Email email = new SimpleEmail();
		
		email.setHostName(configReader.getHost());
		email.setSmtpPort(configReader.getPort());
		email.setCharset(charset);
		email.setHeaders(headers);
		email.setAuthenticator(new DefaultAuthenticator(configReader.getUsername(), configReader.getPassword()));
		email.setStartTLSEnabled(starttls);
		email.setFrom(configReader.getUsername());
		email.addTo(to);
		email.setSubject(subject);
		email.setMsg(content);
		email.setDebug(true);

	    email.send();
	  }

	
}
