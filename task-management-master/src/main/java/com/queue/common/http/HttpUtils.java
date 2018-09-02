package com.queue.common.http;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.queue.common.encrypt.Encryptor;
import com.queue.common.exception.ErrorException;
import com.queue.config.ConfigReader;

@Component
public class HttpUtils {
	
	static ConfigReader config;
	
	@Autowired
	private ConfigReader configReader;
	
	@PostConstruct
	public void start() {
		HttpUtils.config = configReader;
	}

	
	// Gsonのインスタンス作成
	private static final Gson gson = new Gson();
	
	/*
	 * 	結果として連想配列からResponseの形式を作成する
	 */
	public static String getResponseContents(Map<String, Object> result) {
		try {
			// サーバーで計算をした結果をZIP化した後に、AESで暗号化して返す
			String encryptedData = Encryptor.zlibCompressAndAesEncrypt(gson.toJson(result), config.getAesKey());
			return encryptedData;
		} catch (IOException e) {
			// errorの時は、暗号化せずにそのまま返す
			e.printStackTrace();
			Map<String, String> error = new HashMap<String, String>();
			error.put("message", "internal server error");
			return gson.toJson(error);
		}
	}
	
	/*
	 * 　クライアントから送られてきた暗号文から、連想配列に復号化する
	 */
	@SuppressWarnings("unchecked")
	public static Map<Object, Object> getRequestContents(String encryptedString) throws ErrorException {
		// クライアントから取得した暗号を複合して、unzipして返す
		Map<Object, Object> map = new HashMap<>();
		try {
			String data = Encryptor.aesDecryptAndZlibDecompress(encryptedString, config.getAesKey());
			map = gson.fromJson(data, Map.class);
			if (map == null){
				return null;
			} else {
				return map;
			}
		} catch (Exception e) {
			// 例外が発生するときは、暗号化データではない時
			throw new ErrorException("暗号はされていません。", 200, "暗号化されていません。");
		}
	}
}
