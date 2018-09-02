package com.queue.app.interceptor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.queue.app.action.base.ApiBaseAction;
import com.queue.app.dto.api.ApiResponseDto;
import com.queue.common.constant.Singleton;
import com.queue.common.exception.ErrorException;
import com.queue.common.exception.WarningException;
import com.queue.common.http.ResponseUtils;
import com.queue.config.ConfigReader;
import com.squareup.okhttp.OkHttpClient;

@Aspect
@Component
public class ApiResponseAdvice {
	
	private Logger logger = Logger.getLogger(this.getClass());
	private static final Gson gson = new GsonBuilder().serializeNulls().create();
	private static OkHttpClient client = new OkHttpClient();
	
	@Autowired
	private ConfigReader configReader;
	
	/**
	 * @ApiExecutionのアノテーションが付与されているメソッドには実行後に処理を行う。
	 */
	@Around("@annotation(com.queue.app.annotation.ApiExecution)")
	public Object invoke(final ProceedingJoinPoint joinPoint) throws Throwable {
		// ApiBaseActionを継承していない場合
		if (!(joinPoint.getThis() instanceof ApiBaseAction)) {
			return joinPoint.proceed();
		}
		int status = 200;
		
		// 結果を取得する
		ApiResponseDto response = new ApiResponseDto();
		// エラー処理、クライアントにはエラーの文言を返さない
		try {
			// methodを実行
			joinPoint.proceed();
			
			// responseを取得する
			ApiBaseAction action = (ApiBaseAction) joinPoint.getTarget(); // インスタンス取得
			if (action.result != null) {
				response.data = action.result; // actionのスーパークラスのクラス変数resultをresponseに代入
			}
			response.status = "0";
		} catch (WarningException we) {
			// WarningExceptionはリクエストは正しいが内容が不正な場合
			status = 200;
			// エラーメッセージを出力
			Map<String, Object> data = new HashMap<String, Object>();
			response.data = data;
			response.status = we.getErrorCode();
			response.dialog = we.getDialog();
		} catch (ErrorException ee) {
			// ErrorExceptionはリクエストに問題がある時
			status = ee.getStatus();
			// エラーメッセージを出力
			Map<String, Object> data = new HashMap<String, Object>();
			response.data = data;
			response.dialog = ee.getDialog();
			response.status = ee.getErrorCode();
		} catch (Exception e) {
			status = 500;
			Map<String, Object> data = new HashMap<String, Object>();
			response.data = data;
			response.status = "internal server error";								//このエラー
			e.printStackTrace();
		}
		
		// 結果を取得する
		response.time = Singleton.getServerTime();
		String resultJson =gson.toJson(response);
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest(); // アプリケーションのどこにいても現在のリクエストオブジェクトを取得できる
		logger.debug("【Request URL】" + request.getRequestURL());
		logger.debug("【レスポンスデータ】" + resultJson);
		
		// ステータスコードの取得
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		HttpServletResponse res = ResponseUtils.getResponse(); // レスポンスオブジェクトを取得
		res.setStatus(status);
		
		// 暗号化をせずに全て返す場合
		ResponseUtils.write(resultJson, "application/json");
		
		return null;
	}
}