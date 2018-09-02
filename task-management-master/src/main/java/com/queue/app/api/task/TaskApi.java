package com.queue.app.api.task;

import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.queue.app.action.base.ApiBaseAction;
import com.queue.app.annotation.ApiExecution;
import com.queue.app.service.task.TaskService;
import com.queue.common.exception.ErrorException;

@CrossOrigin
@RestController
public class TaskApi extends ApiBaseAction {
	
	@Autowired
	private TaskService taskService;
	
	@ApiExecution(requiredList= {"title", "content"})
	@RequestMapping(value="/api/task/add", method=RequestMethod.POST)  //postが行われたら
	public void addTask() {  											//addTaskという関数を定義
		super.result = taskService.addNewTask(							//resultにtaskServiceのaddNewTaskメソッドの実行結果が代入される
				super.input.memid,										//第一引数としてApiBaseActionに保存されているmemid
				MapUtils.getString(super.input.data, "title"), 			//super.input.dataとは？superはApiBaseActionではないのか
				MapUtils.getString(super.input.data, "content")			
				);
	}
	
	@ApiExecution(requiredList= {"task_id", "comment"})
	@RequestMapping(value="/api/task/comment/add", method=RequestMethod.POST)  //postが行われたら
	public void addCommentTask() throws ErrorException {  											//addTaskという関数を定義
		super.result = taskService.addCommentTask(							//resultにtaskServiceのaddNewTaskメソッドの実行結果が代入される
				super.input.memid,									//第一引数としてApiBaseActionに保存されているmemid
				MapUtils.getInteger(super.input.data, "task_id"), 			//super.input.dataとは？superはApiBaseActionではないのか
				MapUtils.getString(super.input.data, "comment")			
				);
	}
	
	@ApiExecution(requiredList= {"task_id", "status"})
	@RequestMapping(value="/api/task/update", method=RequestMethod.POST)
	public void updateTaskStatus() throws ErrorException {
		super.result = taskService.updateTaskStatus(
				super.input.memid,
				MapUtils.getInteger(super.input.data, "task_id"), 
				MapUtils.getBoolean(super.input.data, "status")
				);
	}
	
	@ApiExecution(requiredList= {})
	@RequestMapping(value="/api/task", method=RequestMethod.POST)
	public void getTasks() {
		super.result = taskService.getUserTask(
				super.input.memid
				);
	}
}