package com.queue.app.service.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.queue.app.dao.task.TaskDao;
import com.queue.app.dto.task.TaskDto;
import com.queue.app.service.base.BaseService;
import com.queue.common.calendar.CalendarBase;
import com.queue.common.exception.ErrorException;
import com.queue.common.exception.WarningException;

@Service
public class TaskService extends BaseService {
	
	@Autowired
	private TaskDao taskDao;
	
	public Map<String, Object> addNewTask(Integer memid, String title, String content) {		//addTaskのメソッド
		// TaskDtoに引数の値を格納する
		TaskDto taskDto = new TaskDto();		//TaskDtoのインスタンスを生成して、taskDtoという変数名に設定
		taskDto.memid = memid;					//taskDto.memidに第一引数を代入（このメソッドはtaskApiで実行される）
		taskDto.title = title;
		taskDto.content = content;
		
		// DAOを用いてDBにinsertする
		taskDao.insertNewTask(taskDto);
		
		// 結果を返す
		Map<String, Object> result = new HashMap<>();
		result.put("message", "ok");
		return result;
	}
	
	public Map<String, Object> addCommentTask(Integer memid, Integer taskId, String comment) throws ErrorException {
		// 指定されたtaskが存在するか確認する
		TaskDto taskDto = taskDao.getTaskByTaskId(taskId);
		if (taskDto == null) {
			throw new WarningException("errors_task_notexist", "指定されたタスクは登録されていません。");
		}
		
		// 指定されたtaskがリクエストをしてきたユーザーのものか確認する
		if (taskDto.memid != memid) {
			throw new ErrorException("errors_task_notyours", 401, "指定されたタスクの編集権限がありません");
		}
		
		// コメントを足す
		taskDao.addCommentTask(taskId, comment);
		
		// 結果を返す
		Map<String, Object> result = new HashMap<>();
		result.put("message", "ok");
		return result;
	}

	
	public Map<String, Object> updateTaskStatus(Integer memid, Integer taskId, Boolean status) throws ErrorException {
		// 指定されたtaskが存在するか確認する
		TaskDto taskDto = taskDao.getTaskByTaskId(taskId);
		if (taskDto == null) {
			throw new WarningException("errors_task_notexist", "指定されたタスクは登録されていません。");
		}
		
		// 指定されたtaskがリクエストをしてきたユーザーのものか確認する
		if (taskDto.memid != memid) {
			throw new ErrorException("errors_task_notyours", 401, "指定されたタスクの編集権限がありません");
		}
		
		// statusを編集する
		taskDao.updateTaskStatus(taskId, status);
		
		// 結果を返す
		Map<String, Object> result = new HashMap<>();
		result.put("message", "ok");
		return result;
	}

	public Map<String, Object> getUserTask(Integer memid) {
		// userのタスクを取得する
		List<TaskDto> taskDtos = taskDao.getTasksByMemid(memid);
		
		// 取得したタスクからレスポンス用のArrayを作る
		List<Map<String, Object>> tasks = new ArrayList<>();
		for (TaskDto dto : taskDtos) {
			Map<String, Object> task = new HashMap<>();
			
			task.put("task_id", dto.taskId);
			task.put("title", dto.title);
			task.put("content", dto.content);
			task.put("status", dto.status);
			task.put("created_at", CalendarBase.getUTCFromDate(dto.createdAt));
			task.put("comment", dto.comment);
			tasks.add(task);
		}
		
		// 結果を返す
		Map<String, Object> result = new HashMap<>();
		result.put("tasks", tasks);
		return result;
	}
	
}