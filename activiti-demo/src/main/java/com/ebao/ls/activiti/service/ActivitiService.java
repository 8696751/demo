package com.ebao.ls.activiti.service;

import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Task;

public interface ActivitiService {

	static final String BEAN_DEFAULT = "activitiService";

	/**
	 * 根据任务taskName，查询taskName下的活动任务
	 * 
	 * @param taskName
	 *            任务名：例如 dataEntry
	 * @return
	 */
	public void claimTask(String taskId, String userId);

	/**
	 * 结束当前taskId的当前节点任务，自动进入下一个节点
	 * 
	 * @param taskId
	 * 
	 * @return
	 */
	public void completeTask(String taskId, Map<String, Object> variables);

	/**
	 * 根据任务taskName，查询taskName下的活动任务
	 * 
	 * @param taskName
	 *            任务名：例如 dataEntry
	 * @return
	 */
	public List<Task> getActiveTaskListByTaskName(String taskName);

	/**
	 * 根据用户userId，查询userId下的活动任务
	 * 
	 * @param userId
	 *            CandidateUser：例如 401
	 * @return
	 */
	public List<Task> getActiveTaskListByUserId(String userId);

	/**
	 * 根据任务taskName、用户userId，查询活动任务
	 * 
	 * @param taskName，userId
	 * 
	 * @return
	 */
	public List<Task> getActiveTaskListByTaskNameAndUserId(String taskName, String userId);

	/**
	 * 根据流程定义key判断流程是否结束
	 * 
	 * @param processDefinitionKey
	 *            流程定义key
	 * @return
	 */
	public boolean isProcessFinishedByProcDefKey(String processDefinitionKey);

	/**
	 * 根据流程实例ID判断流程是否结束
	 * 
	 * @param processDefinitionKey
	 *            流程定义key
	 * @return
	 */
	public boolean isProcessFinishedByProcInstId(String processInstanceId);

	/**
	 * 根据流程实例ID和任务Key，查询历史任务执行人，用于驳回时指定处理人
	 * 
	 * @param processInstanceId
	 *            流程实例ID
	 * @param taskDefinitionKey
	 *            任务key，对应流程图里面的ID
	 * @return String 历史执行任务的处理人
	 */
	public String getHistoryTaskByKey(String processInstanceId, String taskDefinitionKey);

}
