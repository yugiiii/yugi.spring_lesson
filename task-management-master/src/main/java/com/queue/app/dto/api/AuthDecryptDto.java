package com.queue.app.dto.api;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import com.queue.common.exception.WarningException;

import lombok.Data;
/**
 * user/workerのauth
 * @author nagataryou
 *
 * session = memid,userId,sessionExpired
 */
@Data
public class AuthDecryptDto {
	
	public Integer memid;
	public Date sessionExpired;
	
	public static AuthDecryptDto decryptSession(String sessionBaseString) {
		String[] split = StringUtils.split(sessionBaseString, ",");
		AuthDecryptDto dto = new AuthDecryptDto();
		if (split.length == 2) {
			dto.memid = Integer.parseInt(split[0]);
			dto.sessionExpired = new Date();
			dto.sessionExpired.setTime(Long.parseLong(split[1]));
		} else {
			throw new WarningException("errors_session_invalid", "セッションの値が正しくありません。再度サインインしてください。");
		}
		return dto;
	}

}