package com.queue.app.dao.task;

import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;

import com.queue.app.dao.base.BaseDao;
import com.queue.app.dto.task.TaskDto;

@Component
public class TaskDao extends BaseDao {
	
	public TaskDao() {
		super.resultObject = TaskDto.class;
	}
	
	public int insertNewTask (TaskDto dto) {
		return super.insert(dto);
	}
	
	public int addCommentTask (Integer taskId, String comment) {			//made
		MapSqlParameterSource param = super.createParamMap();
		param.addValue("taskId", taskId);
		param.addValue("comment", comment);
		return super.update(param);
	}
	
	public int updateTaskStatus (Integer taskId, Boolean status) {
		MapSqlParameterSource param = super.createParamMap();
		param.addValue("taskId", taskId);
		param.addValue("status", status);
		return super.update(param);
	}
	
	public TaskDto getTaskByTaskId (Integer taskId) {
		MapSqlParameterSource param = super.createParamMap();
		param.addValue("taskId", taskId);
		return (TaskDto) super.select(param);
	}
	
	@SuppressWarnings("unchecked")
	public List<TaskDto> getTasksByMemid (Integer memid) {
		MapSqlParameterSource param = super.createParamMap();
		param.addValue("memid", memid);
		return (List<TaskDto>) super.selectAll(param);
	}
	
	
}