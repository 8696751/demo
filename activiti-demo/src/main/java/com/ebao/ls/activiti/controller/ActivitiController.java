package com.ebao.ls.activiti.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.activiti.engine.IdentityService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.ebao.ls.activiti.constant.Constant;
import com.ebao.ls.activiti.service.ActivitiService;
import com.ebao.ls.activiti.variables.ContractInfo;
import com.ebao.ls.pub.DefaultClientResponse;
import com.ebao.ls.pub.PolicyVO;
import com.ebao.ls.pub.ServiceResultCode;
import com.ebao.ls.pub.trans.DefaultInput;
import com.ebao.ls.pub.trans.DefaultOutput;
import com.ebao.ls.pub.trans.TaskClaimInput;
import com.ebao.ls.pub.trans.TaskCompleteInput;
import com.ebao.ls.pub.trans.TaskListOutput;

@RestController
@RequestMapping("/rest/activiti")
public class ActivitiController {
	private static final Logger logger = LoggerFactory.getLogger(ActivitiController.class);

	@Autowired
	private IdentityService identityService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Resource(name = ActivitiService.BEAN_DEFAULT)
	private ActivitiService activitiService;

	/**
	 * @Title: dataEntry
	 * @Description: 首次进入工作流，到dataEntry节点
	 * @param
	 * @return String
	 */
	@Transactional
	@RequestMapping(method = RequestMethod.POST, value = "/start")
	public String start(@RequestBody DefaultInput di) {
		/**
		 * 启动一个流程实例，“myProcess”是流程实例xml里规定的名字
		 * 由于在Spring配置文件中已经添加了自动部署功能，所以这里直接启动即可
		 */
		di.setServiceRequestTime(new Date());
		String processInstanceId = null;
		String taskId = null;
		ServiceResultCode result = ServiceResultCode.SUCCEEDED;
		String responseJson = "";
		try {
			// 用来设置启动流程的人员ID，引擎会自动把用户ID保存到activiti:initiator中
			identityService.setAuthenticatedUserId(di.getUserId());
			ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(Constant.PROCESS_KEY_NB);
			processInstanceId = processInstance.getId();
			// 查询出当前流程实例的任务，当前已经进入DataEntry环节
			Task task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
			taskId = task.getId();
			logger.info("task ID:{},task Name:{}", taskId, task.getName());
			// 任务开始后直接进入DataEntry，要对本次任务添加一些参数，参数类型有serializable的也有基础数据类型的
			Map<String, Object> variables = new HashMap<String, Object>();
			ContractInfo cInfo = new ContractInfo();
			BeanUtils.copyProperties(di.getPolicy(), cInfo);
			variables.put("policyInfo", cInfo);
			variables.put("policyId", RandomUtils.nextInt());
			// setVariable 是全局的
			taskService.setVariable(taskId, Constant.NB_TASK_VARIABLE_NAME, variables);
		} catch (Exception e) {
			result = ServiceResultCode.FAILED;
			logger.error("Catch Exception: {}", e.toString());
		} finally {
			DefaultOutput response = initResponse(di, processInstanceId, taskId, result);
			responseJson = JSONObject.toJSONString(response);
			logger.info("finally init response json: {}", responseJson);
		}
		return responseJson;
	}

	/**
	 * @Title: getTaskList
	 * @Description: 通过taskName和userID 查询出来活动的task
	 * @param taskName
	 * @param userId
	 * @return String
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/taskList/{taskName}/{userId}")
	public String getTaskList(@PathVariable("taskName") String taskName, @PathVariable("userId") String userId) {
		String responseJson = "";
		// 查询用户可以操作的某个节点的任务
		List<Task> qList = activitiService.getActiveTaskListByTaskNameAndUserId(taskName, userId);
		TaskListOutput o = new TaskListOutput(new ArrayList<PolicyVO>());
		for (Task t : qList) {
			@SuppressWarnings("unchecked")
			Map<String, Object> taskVariables = (HashMap<String, Object>) taskService.getVariable(t.getId(),
					Constant.NB_TASK_VARIABLE_NAME);
			PolicyVO policy = new PolicyVO();
			o.getPolicys().add(policy);
			for (String key : taskVariables.keySet()) {
				Object obj = taskVariables.get(key);
				if (obj instanceof ContractInfo) {
					ContractInfo c = (ContractInfo) obj;
					BeanUtils.copyProperties(c, policy);
					policy.setProcessInstanceId(t.getProcessInstanceId());
					policy.setTaskId(t.getId());
					logger.info("Process Variables policyCode:{}", c.getPolicyCode());
				} else {
					logger.info("Process Variables policyId:{}", (int) obj);
				}
			}
		}
		o.setResult(ServiceResultCode.SUCCEEDED.code());
		responseJson = JSONObject.toJSONString(o);
		logger.info("response json: {}", responseJson);
		return responseJson;
	}

	/**
	 * @Title: taskClaim
	 * @Description:认领任务（task有公用转换为私有）
	 * @param taskId
	 *            + userId
	 * @return String
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/taskClaim")
	public String taskClaim(@RequestBody TaskClaimInput input) {
		String responseJson = "";
		DefaultClientResponse resp = new DefaultClientResponse();
		activitiService.claimTask(input.getTaskId(), input.getUserId());
		resp.setResult(ServiceResultCode.SUCCEEDED.code());
		responseJson = JSONObject.toJSONString(resp);
		logger.info("response json: {}", responseJson);
		return responseJson;
	}

	/**
	 * @Title: taskComplete
	 * @Description: 完成本次任务
	 * @param TaskCompleteInput
	 * @return String @throws
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/taskComplete")
	public String taskComplete(@RequestBody TaskCompleteInput input) {
		String responseJson = "";
		DefaultClientResponse resp = new DefaultClientResponse();
		activitiService.completeTask(input.getTaskId(), input.getVariables());
		resp.setResult(ServiceResultCode.SUCCEEDED.code());
		responseJson = JSONObject.toJSONString(resp);
		logger.info("response json: {}", responseJson);
		return responseJson;
	}

	private DefaultOutput initResponse(DefaultInput di, String processInstanceId, String taskId,
			ServiceResultCode result) {
		DefaultOutput o = new DefaultOutput();
		o.setClientRequestId(di.getClientRequestId());
		o.setClientRequestTime(o.getClientRequestTime());
		o.setServiceResponseTime(new Date());
		o.setServiceRequestTime(di.getServiceRequestTime());
		o.setProcessInstanceId(processInstanceId);
		o.setTaskId(taskId);
		o.setResult(result.code());
		return o;
	}

}
