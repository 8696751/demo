package com.ebao.ls;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiTaskAlreadyClaimedException;
import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ebao.ls.activiti.variables.ContractInfo;

/**
 * @ClassName: WorkflowTest
 * @Description: 测试整个新契约流程
 *               如下几个Autowired的service已经在Spring配置文件中定义了bean，是activiti最核心的service
 * @author angus.xu
 * @date Jul 17, 2017 5:46:18 PM
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:applicationContext-spring-test.xml")
public class WorkflowTest {
	private static final Logger logger = LoggerFactory.getLogger(WorkflowTest.class);

	/**
	 * @Fields repositoryService : TODO
	 */
	@Autowired
	private RepositoryService repositoryService;

	/**
	 * @Fields historyService :
	 *         查看历史轨迹，因每个实例结束后ProcessInstance都是空，如果要查询只能通过history
	 */
	@Autowired
	private HistoryService historyService;

	/**
	 * @Fields runtimeService : TODO
	 */
	@Autowired
	private RuntimeService runtimeService;

	/**
	 * @Fields taskService : 客户化开发中使用最多 例如：任务查询，任务分配，任务完成
	 */
	@Autowired
	private TaskService taskService;

	/**
	 * @Fields formService : 表单，本demo暂时未使用
	 */
	@Autowired
	private FormService formService;

	@Test
	public void testWorkflow() {
		/**
		 * 启动一个流程实例，“myProcess”是流程实例xml里规定的名字
		 * 由于在Spring配置文件中已经添加了自动部署功能，所以这里直接启动即可
		 */
		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myProcess");
		// 唯一标识processInstanceId
		String processInstanceId = processInstance.getId();
		logger.info("processInstance ID:{}", processInstance.getId());

		Task task = null;
		// 查询出当前流程实例的任务，当前已经进入DataEntry环节
		task = taskService.createTaskQuery().processInstanceId(processInstanceId).singleResult();
		logger.info("task ID:{},task Name:{}", task.getId(), task.getName());
		// 任务开始后直接进入DataEntry，要对本次任务添加一些参数，参数类型有serializable的也有基础数据类型的
		Map<String, Object> variables = new HashMap<String, Object>();
		ContractInfo cInfo = new ContractInfo();
		cInfo.setContractId(1L);
		cInfo.setApplyCode("AA00001");
		cInfo.setOrganId(101L);
		cInfo.setPolicyCode("AA00001");
		cInfo.setSubmitChannel(5L);
		variables.put("policyInfo", cInfo);
		variables.put("policyId", RandomUtils.nextInt());
		// setVariable 是全局的
		taskService.setVariable(task.getId(), "policy", variables);

		// 查询400用户可以操作的任务
		TaskQuery userQuery = taskService.createTaskQuery();
		userQuery.taskCandidateUser("400");
		List<Task> qList = null;
		qList = userQuery.active().orderByTaskId().asc().list();
		logger.info("BF CandidateUserQuery Size{}", qList.size());
		for (Task t : qList) {
			logger.info("task list-pID{}-tID{}", t.getProcessInstanceId(), t.getId());
		}

		// 认领这个任务,讲公有任务转换为私有任务
		taskService.claim(task.getId(), "401");
		// 只可以认领一次，多次认领会抛出ActivitiTaskAlreadyClaimedException异常
		try {
			taskService.claim(task.getId(), "400");
		} catch (ActivitiTaskAlreadyClaimedException e) {
			logger.info("多次认领，抛出异常{}", e.toString());
		}
		// 分配给401之后再查一次
		qList = userQuery.active().orderByTaskId().asc().list();
		logger.info("AF CandidateUserQuery Size{}", qList.size());
		for (Task t : qList) {
			logger.info("task list-pID{}-tID{}", t.getProcessInstanceId(), t.getId());
		}

		// DataEntry Complete,结束当前任务，自动进入下一个任务节点
		taskService.complete(task.getId());
		task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
		logger.info("task ID:{},task Name:{}", task.getId(), task.getName());
		// Verification Complete 进入排他网关，传入参数result <0→驳回 ；>0 → 核保；默认accept
		// 参数名和0，1的判断都是在**bpmn文件内配置的
		Map<String, Object> vv = new HashMap<String, Object>();
		vv.put("result", -1);
		taskService.complete(task.getId(), vv);// -1 驳回到dataEntry
		task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
		logger.info("task ID:{},task Name:{}", task.getId(), task.getName());

		// result=1，再次进入Verification
		vv.put("result", 1);
		taskService.complete(task.getId(), vv);
		task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
		logger.info("task ID:{},task Name:{}", task.getId(), task.getName());

		// 进入uw
		taskService.complete(task.getId());
		task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
		logger.info("task ID:{},task Name:{}", task.getId(), task.getName());

		// 输出variables
		@SuppressWarnings("unchecked")
		Map<String, Object> taskVariables = (HashMap<String, Object>) taskService.getVariable(task.getId(), "policy");
		for (String key : taskVariables.keySet()) {
			Object obj = taskVariables.get(key);
			if (obj instanceof ContractInfo) {
				ContractInfo c = (ContractInfo) obj;
				logger.info("Process Variables policyCode:{}", c.getPolicyCode());
			} else {
				logger.info("Process Variables policyId:{}", (int) obj);
			}
		}
		// EndEvent
		taskService.complete(task.getId());
		processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
				.singleResult();
		// 流程结束，实例被删除了，用null来判断流程是否结束
		if (processInstance == null) {
			logger.info("流程结束了");
			/** 查询历史，获取流程的相关信息 */
			HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
					.processInstanceId(processInstanceId).singleResult();
			logger.info("processInstanceId:{}#startTime:{}#endtime:{}#durationMilles:{}",
					historicProcessInstance.getId(), historicProcessInstance.getStartTime(),
					historicProcessInstance.getEndTime(), historicProcessInstance.getDurationInMillis());
		}
		// 查询所有处在“dataEntry”节点的任务
		TaskQuery taskQuery = taskService.createTaskQuery();
		taskQuery.taskName("dataEntry");
		List<Task> list = taskQuery.active().orderByTaskId().asc().list();
		logger.info("DataEntry Size{}", list.size());
		for (Task t : list) {
			logger.info("task list-pID{}-tID{}", t.getProcessInstanceId(), t.getId());
		}
		// 查询所有未结束的流程实例
		int size = runtimeService.createExecutionQuery().list().size();
		logger.info("runtimeActive:{}", size);

	}

}
