package com.queue.app.dto.member;

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
public class MemberDto extends BaseDto {
	
	/**
	 * ユーザー情報
	 */
	private static final long serialVersionUID = 1846641068082252136L;
	
	public Integer memid; 			// AIされたid
	public String email;			// メールアドレス
	public String password;		// hashされたpassword
	public String name;			// 名前
	
}