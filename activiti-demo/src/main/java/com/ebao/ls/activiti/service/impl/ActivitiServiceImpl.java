package com.ebao.ls.activiti.service.impl;

import java.util.List;
import java.util.Map;

import org.activiti.engine.HistoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ebao.ls.activiti.service.ActivitiService;

@Transactional
@Service(ActivitiService.BEAN_DEFAULT)
public class ActivitiServiceImpl implements ActivitiService {

	@Autowired
	private HistoryService historyService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Override
	public void claimTask(String taskId, String userId) {
		synchronized (taskId) {
			taskService.claim(taskId, userId);
		}
	}

	@Override
	public void completeTask(String taskId, Map<String, Object> variables) {
		if (variables == null) {
			taskService.complete(taskId);
		} else {
			taskService.complete(taskId, variables);
		}
	}

	@Override
	public List<Task> getActiveTaskListByTaskName(String taskName) {
		TaskQuery query = taskService.createTaskQuery();
		query.taskName(taskName);
		List<Task> qList = query.active().orderByTaskId().asc().list();
		return qList;
	}

	@Override
	public List<Task> getActiveTaskListByUserId(String userId) {
		TaskQuery query = taskService.createTaskQuery();
		query.taskCandidateUser(userId);
		List<Task> qList = query.active().orderByTaskId().asc().list();
		return qList;
	}

	@Override
	public List<Task> getActiveTaskListByTaskNameAndUserId(String taskName, String userId) {
		TaskQuery query = taskService.createTaskQuery();
		query.taskName(taskName).taskCandidateUser(userId);
		List<Task> qList = query.active().orderByTaskId().asc().list();
		return qList;
	}

	@Override
	public boolean isProcessFinishedByProcDefKey(String processDefinitionKey) {
		boolean isFinished = false;
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processDefinitionKey(processDefinitionKey).singleResult();
		if (null == processInstance) {
			isFinished = true;
		}

		return isFinished;
	}

	@Override
	public boolean isProcessFinishedByProcInstId(String processInstanceId) {
		boolean isFinished = false;
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		if (null == processInstance) {
			isFinished = true;
		}
		return isFinished;
	}

	@Override
	public String getHistoryTaskByKey(String processInstanceId, String taskDefinitionKey) {
		List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId).taskDefinitionKey(taskDefinitionKey).orderByExecutionId().asc()
				.list();
		if (null != list && list.size() > 0) {
			return list.get(0).getAssignee();
		}
		return null;
	}
}
