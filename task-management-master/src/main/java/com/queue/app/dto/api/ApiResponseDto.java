package com.queue.app.dto.api;

import java.util.Map;

/**
 * responseの形式をここで指定する
 * @author nagataryou
 *
 */
public class ApiResponseDto {

	public String status;
	// JSONをここに入れる
	public Map<String, Object> data;
	public long time;
	public String dialog;

}
