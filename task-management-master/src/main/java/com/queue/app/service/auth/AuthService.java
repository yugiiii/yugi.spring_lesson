package com.queue.app.service.auth;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.EmailException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.queue.app.dao.member.MemberDao;
import com.queue.app.dto.member.MemberDto;
import com.queue.app.service.base.BaseService;
import com.queue.common.constant.Singleton;
import com.queue.common.encrypt.Encryptor;
import com.queue.common.exception.ErrorException;
import com.queue.common.exception.WarningException;
import com.queue.common.mail.GmailSender;
import com.queue.config.ConfigReader;

@Service
public class AuthService extends BaseService {
	
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private ConfigReader configReader;
	
	@Autowired
	private GmailSender gmailSender;
	
	@Autowired
	private MemberDao memberDao;
	
	// tokenの有効期限(minutes) -> 1日にしている
	private static final int TOKEN_EXPIRE_MIN = 1440;
	
	// sessionの有効期限(minutes) -> 1日にしている
	private static final int SESSION_EXPIRE_MIN = 1440;
	
	/**
	 * Signup用のService
	 * @param email
	 * @param name
	 * @return
	 */
	public Map<String, Object> userAuthSignup(String email, String name) {
		// emailアドレスが登録されていないことを確認する。
		MemberDto memberDtoForCheck = memberDao.getMemberByEmail(email);
		if (memberDtoForCheck != null) {
			throw new WarningException("errors_email_alreadyexist", "このメールアドレスはすでに登録されています。");
		}
		
		// tokenを発行(email, expire)
		long expire = Singleton.getServerTime() + (TOKEN_EXPIRE_MIN * 60 * 1000);
		String token = generateToken(email, name, Long.toString(expire));
		
		// メールを送信
		String title = "新規登録";
		String content = "以下のTokenを用いて登録を完了してください。\n"
				+ "token=" + token;
		try {
			gmailSender.send(email, title, content);
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 結果を返す
		Map<String, Object> result = new HashMap<>();
		result.put("message", "ok");
		return result;
	}

	/**
	 * DBに登録する
	 * @param token
	 * @param newPassword
	 * @return
	 * @throws ErrorException 
	 */
	public Map<String, Object> userAuthRegisterByToken(String token, String newPassword) throws ErrorException {
		MemberDto memberDto = new MemberDto();
		
		Long expire;
		// tokenをdecodeする
		try {
			String[] split = StringUtils.split(Encryptor.aesDecrypt(token, configReader.getAesKey()), ",");
			memberDto.email = split[0];
			memberDto.name = split[1];
			expire = Long.parseLong(split[2]);
		} catch (ErrorException e) {
			// TODO Auto-generated catch block
			// 本来入らない
			throw new ErrorException("errors_token_invalid", 401, "tokenの値が不正です。");
		}
		
		// tokenが有効か確認する
		if (Singleton.getServerTime() > expire) {
			throw new WarningException("errors_token_expire", "tokenの有効期限が過ぎています。");
		}
		
		// userが登録済みでないか確認する
		MemberDto memberDtoForCheck = memberDao.getMemberByEmail(memberDto.email);
		if (memberDtoForCheck != null) {
			throw new ErrorException("errors_user_alreadyregistered", 401, "すでに登録済みのUSERです。");
		}
		
		// データをinsertする
		memberDto.password = Encryptor.hash256(newPassword);
		memberDao.insertDataForSignUp(memberDto);
		
		Map<String, Object> result = new HashMap<>();
		result.put("message", "ok");
		return result;
	}

	/**
	 * signin用のmethod
	 * @param email
	 * @param password
	 * @return
	 * @throws ErrorException
	 */
	public Map<String, Object> userAuthSignin(String email, String password) throws ErrorException {
		// userがいるか確認
		MemberDto memberDto = memberDao.getMemberByEmail(email);
		if (memberDto == null) {
			throw new ErrorException("errors_user_notexist", 401, "USERが存在しません。");
		}
		
		// passwordがあっているか確認
		if (!StringUtils.equals(memberDto.password, Encryptor.hash256(password))) {
			throw new ErrorException("errors_password_notcorrect", 401, "PASSWORDが間違っています。");
		}
		
		// sessionの生成　session = AES(memid, expire)
		Long expire = Singleton.getServerTime() + (SESSION_EXPIRE_MIN * 60 * 1000);
		String session = this.generateToken(Integer.toString(memberDto.memid), Long.toString(expire));
		
		Map<String, Object> result = new HashMap<>();
		result.put("session", session);
		result.put("session_expire", expire);
		return result;
	}
	
	/**
	 * token作成用のmethod
	 * @param args
	 * @return
	 */
	private String generateToken(String... args) {
		String token = "";
		try {
			token = Encryptor.aesEncrypt(String.join(",", args), configReader.getAesKey());
		} catch (UnsupportedEncodingException e) {
			// 本来入らない
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return token;
	}
	
}