package com.queue.common.exception;

import java.util.List;

public class ErrorException extends Exception {

	private static final long serialVersionUID = 4475953165243798321L;
	String errorCode;
	String key;
	Integer status;
	String dialog;

	public ErrorException() {
		super();
	}

	public ErrorException(String errorCode, Integer status, String dialog) {
		super(errorCode);
		this.errorCode = errorCode;
		this.status = status;
		this.dialog = dialog;
	}

	public ErrorException(String errorCode, String key, Integer status) {
		super(errorCode);
		this.errorCode = errorCode;
		this.key = key;
		this.status = status;
	}

	public ErrorException(String errorCode, Throwable throwable) {
		super(errorCode, throwable);
		this.errorCode = errorCode;
	}

	public ErrorException(String errorCode, String key, Throwable throwable) {
		super(errorCode, throwable);
		this.errorCode = errorCode;
		this.key = key;
	}

	public ErrorException(Throwable throwable) {
		super(throwable);
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public String getKey() {
		return key;
	}
	
	public Integer getStatus() {
		return status;
	}
	
	public String getDialog() {
		return dialog;
	}
	
	/**
	 * RequiredListから項目をコンマ区切りで取得する
	 * @param requiredList
	 * @return
	 */
	public static String getRequiredStatement(List<String> requiredList) {
		String result = "[";
		for (int i = 0; i < requiredList.size(); i++) {
			result += requiredList.get(i);
			if (i != requiredList.size() - 1) {
				result += ", ";
			}
		}
		result += "]";
		return result;
	}
	
}
