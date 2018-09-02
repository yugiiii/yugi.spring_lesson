package com.queue.app.dto.task;

import java.util.Date;

import com.queue.app.dto.base.BaseDto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author nagataryou
 *
 */
@Data
@EqualsAndHashCode(callSuper=false)
public class TaskDto extends BaseDto {
	
	/**
	 * ユーザー情報
	 */
	private static final long serialVersionUID = 1846641068082252136L;
	
	public Integer taskId;			// AIされたID
	public Integer memid;			// 紐づけられたユーザーのid
	public String title;				// タスクのタイトル
	public String content;			// タスクの詳細
	public Boolean status;			// タスクのステータス
	public Date createdAt;			// タスクの作成日
	public String comment;			//　コメント
}