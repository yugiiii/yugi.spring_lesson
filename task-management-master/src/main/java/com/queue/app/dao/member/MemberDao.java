package com.queue.app.dao.member;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import com.queue.app.dao.base.BaseDao;
import com.queue.app.dto.member.MemberDto;

@Component
public class MemberDao extends BaseDao {
	
	public MemberDao() {
		super.resultObject = MemberDto.class;
	}
	
	public MemberDto getMemberByMemid(Integer memid) {
		MapSqlParameterSource param = super.createParamMap();
		param.addValue("memid", memid);
		return (MemberDto) super.select(param);
	}

	public MemberDto getMemberByEmail(String email) {
		MapSqlParameterSource param = super.createParamMap();
		param.addValue("email", email);
		return (MemberDto) super.select(param);
	}

	public int insertDataForSignUp(MemberDto memberDto) {
		return super.insert(memberDto);
	}

	@SuppressWarnings("unchecked")
	public List<MemberDto> getUserList(Integer limit, Integer offset) {
		MapSqlParameterSource param = super.createParamMap();
		param.addValue("limit", limit);
		param.addValue("offset", offset);
		return (List<MemberDto>) super.selectAll(param);
	}
	
}