package com.queue.common.exception;

/**
 * statusCodeとerrorCodeをメインで扱う
 * @author shibatanaoto
 *
 */
public class WarningException extends RuntimeException {
	
	private static final long serialVersionUID = 4475953165243798321L;
	String errorCode;
	String dialog;
	String key;

	public WarningException() {
		super();
	}

	public WarningException(String errorCode, String dialog) {
		super(errorCode);
		this.errorCode = errorCode;
		this.dialog = dialog;
	}

//	public WarningException(String errorCode, String key) {
//		super(errorCode);
//		this.errorCode = errorCode;
//		this.key = key;
//	}

	public WarningException(String errorCode, Throwable throwable) {
		super(errorCode, throwable);
		this.errorCode = errorCode;
	}

	public WarningException(String errorCode, String key, Throwable throwable) {
		super(errorCode, throwable);
		this.errorCode = errorCode;
		this.key = key;
	}

	public WarningException(Throwable throwable) {
		super(throwable);
	}

	public String getErrorCode() {
		return this.errorCode;
	}
	
	public String getDialog() {
		return this.dialog;
	}

	public String getKey() {
		return key;
	}

}
