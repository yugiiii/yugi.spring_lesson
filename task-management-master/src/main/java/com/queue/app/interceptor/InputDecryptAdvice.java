package com.queue.app.interceptor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.MapUtils;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.queue.app.annotation.ApiExecution;
import com.queue.app.annotation.NoAuth;
import com.queue.app.dao.member.MemberDao;
import com.queue.app.action.base.ApiBaseAction;
import com.queue.app.dto.api.ApiRequestDto;
import com.queue.app.dto.api.AuthDecryptDto;
import com.queue.app.dto.member.MemberDto;
import com.queue.common.constant.Singleton;
import com.queue.common.encrypt.Encryptor;
import com.queue.common.exception.ErrorException;
import com.queue.common.exception.WarningException;
import com.queue.config.ConfigReader;

@Aspect
@Component
public class InputDecryptAdvice {
	private Logger logger = Logger.getLogger(this.getClass());
	
	@Autowired
	private ConfigReader configReader;
	
	@Autowired
	private MemberDao memberDao;
	
	private static final Gson gson = new GsonBuilder().serializeNulls().create();
	
	/**
	 * @ApiExecutionの付与されているメソッドには以下の処理を実行後に処理を行う
	 * @param joinPoint
	 * @throws Throwable
	 */
	@Around("@annotation(com.queue.app.annotation.ApiExecution)")
	public void invoke(final ProceedingJoinPoint joinPoint) throws Throwable {
		// requestの処理をする
		// 変数前の()はcast
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest(); // アプリケーションのどこにいても現在のリクエストオブジェクトを取得できる
		request.setCharacterEncoding("UTF-8");
		
		logger.debug("【Request URL】" + request.getRequestURL());
		logger.debug("【Request API】request data: " + request.getParameter("data"));
		

		Map<Object, Object> resultJson = new HashMap<Object, Object>();
		
		// clientから送られてきたparameterを取得する
		resultJson.put("application_key", request.getParameter("application_key"));
		resultJson.put("session", request.getParameter("session"));
		resultJson.put("method", request.getParameter("method"));
		try {
			resultJson.put("data",  gson.fromJson(request.getParameter("data"), Map.class)); // Mapクラスにキャストしている
			if (MapUtils.getObject(resultJson, "data") == null) {
				throw new WarningException("errors_data_unset", "リクエストの方法が違います。");
			}
		} catch (Exception e) {
			// dataがない場合
			throw new WarningException("errors_data_unset", "リクエストの方法が違います。");   
		}
		
		// Requestのパラメーターを準備する
		Integer memid = null;
		Long userId = null;
		
		// front側で保証してもらうためunchecked
		@SuppressWarnings("unchecked")
		Map<Object, Object> data = (Map<Object, Object>) resultJson.get("data");
		logger.debug("【リクエストデータ】" + resultJson);
		
		
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		
		// Sessionによる認証
		// Authが必要あるものに関してのみ実行している。
		if (method.getAnnotation(NoAuth.class) == null) {
			// 認証が必要な場合
			if (resultJson.get("session") == null || resultJson.get("session").toString().equals("") || resultJson.get("session").toString().equals("undefined") || resultJson.get("session").toString().equals("null")) {
				// sessionがない、またはsessionが明らかに不正な場合
				throw new ErrorException("errors_session_unset", 401, "セッションがありません。再度ログインしてください。");
			}
			// sessionをdecodeしてdtoをインスタンス化
			AuthDecryptDto authDecrypt = new AuthDecryptDto();
			try {
				authDecrypt = AuthDecryptDto.decryptSession(Encryptor.aesDecrypt(resultJson.get("session").toString(), configReader.getAesKey()));
			} catch(IllegalArgumentException e) {
				logger.debug(e);
				throw new ErrorException("errors_session_notcorrect", 401, "セッションが間違っています。再度ログインしてください。");
			}
			
			// 存在するユーザーなのか確認
			MemberDto memberDto = memberDao.getMemberByMemid(authDecrypt.memid);
			if (memberDto == null) {
				throw new ErrorException("errors_session_invalid", 401, "ユーザーが存在しません。");
			}
			memid = memberDto.memid;
			
			// sessionが有効か確認
			if (Singleton.getServerTime() > authDecrypt.sessionExpired.getTime()) {
				throw new ErrorException("errors_session_expired", 401, "セッションの有効期限が切れています。再度ログインしてください。");
			}
			
		}
		
		String[] requiredList = method.getAnnotation(ApiExecution.class).requiredList();
		// 400番系のエラーはここで処理をする
		if (requiredList != null) {
			List<String> requiredColumns = new ArrayList<String>();
			
			// INPUT.dataからkeyを取得
			for (String requiredColumn : requiredList) {
				if (data.get(requiredColumn) == null) {
					requiredColumns.add(requiredColumn);
				}
			}
			if (requiredColumns.size() != 0) {
				throw new ErrorException("errors_" + ErrorException.getRequiredStatement(requiredColumns) + "_required", 400, "必要な情報が入力されていません。");
			}
		}
		
		 // リクエストをまとめる
	    ApiRequestDto requestDto = new ApiRequestDto();
		requestDto.data = data;
		requestDto.memid = memid;
		
		// 実行するインスタンスを取得
		ApiBaseAction action = (ApiBaseAction) joinPoint.getTarget(); // インスタンスを取得
		action.input = requestDto; // インスタンスのsuperクラスのクラス変数(ApiRequestDto input)にこのannotation内で定義した値を代入
		
		// メソッドを実行する
		joinPoint.proceed();
	}
}