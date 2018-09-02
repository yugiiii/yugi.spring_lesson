package com.queue.common.aws;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.simpleemail.AWSJavaMailTransport;

/**
 * メールをSESを使用して送信をする
 * @author shibatanaoto
 *
 */
@Service
public class SesUtils {
	
	@Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;
	
	private static final Logger logger = Logger.getLogger(SesUtils.class);
	
	public void sendMail(String from, String to, String subject, String body) {
		
		// メールを飛ばす部分のテスト
		AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
		Properties props = new Properties();
		props.setProperty("mail.transport.protocol", "aws");
		props.setProperty("mail.aws.user", credentials.getAWSAccessKeyId());
		props.setProperty("mail.aws.password", credentials.getAWSSecretKey());
		
		Session session = Session.getInstance(props);
		
		logger.warn("【メールを送信します】 from: " + from + ", to: " + to);
		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(from));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject(subject);
			message.setText(body);
			message.saveChanges();
			
			Transport transport = new AWSJavaMailTransport(session, null);
			try {
				transport.connect();
				transport.sendMessage(message, message.getAllRecipients());
			} finally {
				transport.close();
			}
		} catch (MessagingException e) {
			logger.error(e, e);
		}
	}
}
