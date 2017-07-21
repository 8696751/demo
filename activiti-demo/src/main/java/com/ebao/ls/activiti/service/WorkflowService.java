package com.ebao.ls.activiti.service;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkflowService {

	// 日志对象
	private static final Logger logger = LoggerFactory.getLogger(WorkflowService.class);

	@Autowired
	private RepositoryService repositoryService;

	@Autowired
	private HistoryService historyService;

	@Autowired
	private RuntimeService runtimeService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private FormService formService;

	/**
	 * 部署流程定义
	 * 
	 * @param in
	 *            包含*.bpmn、*.png的文件流
	 * @param deployName
	 *            流程部署名称
	 * @throws FileNotFoundException
	 *             异常
	 */
	public Deployment deploy(InputStream in, String deployName) throws FileNotFoundException {
		// 2：将File类型的文件转化成ZipInputStream流
		ZipInputStream zipInputStream = new ZipInputStream(in);
		Deployment deploy = repositoryService.createDeployment()// 创建部署对象
				.name(deployName)// 添加部署名称
				.addZipInputStream(zipInputStream)//
				.deploy();// 完成部署
		logger.info("############部署ID：{},部署名称：{},部署时间：{}", deploy.getId(), deploy.getName(),
				deploy.getDeploymentTime());
		return deploy;
	}

	/**
	 * 查询所有部署流程信息列表
	 * 
	 * @return List<Deployment> 部署流程列表
	 */
	public List<Deployment> getDeploymentList(Deployment deployment) {
		List<Deployment> list = new ArrayList<Deployment>();
		String deployName = deployment.getName();
		if (StringUtils.isNotEmpty(deployName)) {
			list = repositoryService.createDeploymentQuery()// 创建部署对象查询
					.deploymentNameLike("hello").orderByDeploymenTime().desc().list();
		} else {
			list = repositoryService.createDeploymentQuery()// 创建部署对象查询
					.orderByDeploymenTime().desc().list();
		}
		return list;
	}

	/**
	 * 分页查询流程定义信息列表
	 * 
	 * @return List<ProcessDefinition> 流程定义列表
	 */
	public List<ProcessDefinition> getProcessDefinitionPageList(String processDefinitionName, int firstResult,
			int maxResults) {
		List<ProcessDefinition> list = new ArrayList<ProcessDefinition>();
		if (StringUtils.isNotEmpty(processDefinitionName)) {
			list = repositoryService.createProcessDefinitionQuery()// 创建流程定义查询
					.processDefinitionNameLike(processDefinitionName).orderByProcessDefinitionVersion().desc()//
					.listPage(firstResult, maxResults);
		} else {
			list = repositoryService.createProcessDefinitionQuery()// 创建流程定义查询
					.orderByProcessDefinitionVersion().desc()//
					.listPage(firstResult, maxResults);
		}
		return list;
	}

	/**
	 * 查询流程定义信息列表
	 * 
	 * @return List<ProcessDefinition> 流程定义列表
	 */
	public long getProcessDefinitionPageCount(String processDefinitionName, int firstResult, int maxResults) {
		long count = 0;
		if (StringUtils.isNotEmpty(processDefinitionName)) {
			count = repositoryService.createProcessDefinitionQuery()// 创建流程定义查询
					.processDefinitionNameLike(processDefinitionName).orderByProcessDefinitionVersion().desc()//
					.count();
		} else {
			count = repositoryService.createProcessDefinitionQuery()// 创建流程定义查询
					.orderByProcessDefinitionVersion().desc()//
					.count();
		}
		return count;
	}

	/**
	 * 使用部署对象ID和资源图片名称，获取图片的输入流
	 * 
	 * @param deploymentId
	 *            流程部署ID
	 * @param imageName
	 *            图片资源名称，例：helloworld.png
	 * @return InputStream 图片输入流
	 */
	public InputStream getImageInputStream(String deploymentId, String imageName) {
		return repositoryService.getResourceAsStream(deploymentId, imageName);
	}

	/**
	 * 使用部署对象ID，删除流程定义
	 * 
	 * @param deploymentId
	 *            流程部署ID
	 */
	public void deleteProcDefByDeploymentId(String deploymentId) {
		repositoryService.deleteDeployment(deploymentId, true);
	}

	/**
	 * 根据用户查询个人待处理的任务列表，获取当前任务的集合List<Task>
	 * 
	 * @param userID
	 *            用户ID
	 * @return List<Task> 任务列表
	 */
	public List<Task> getTaskListByUserID(String userID) {
		List<Task> list = taskService.createTaskQuery()//
				.taskAssignee(userID)// 指定个人任务查询
				.orderByTaskCreateTime().desc()//
				.list();
		return list;
	}

	/**
	 * 获取任务节点中对应的Form key
	 * 
	 * @param taskId
	 *            任务ID
	 * @return String Form key取值
	 */
	public String getTaskFormKeyByTaskId(String taskId) {
		TaskFormData formData = formService.getTaskFormData(taskId);
		// 获取Form key的值
		String value = formData.getFormKey();
		return value;
	}

	/**
	 * 根据任务ID，查询ProcessDefinitionEntiy对象，从而获取当前任务完成之后的连线名称，并放置到List<String>集合中
	 * 
	 * @param taskId
	 * @return
	 */
	public List<String> getOutGoingListByTaskId(String taskId) {
		// 返回存放连线的名称集合
		List<String> list = new ArrayList<String>();
		List<PvmTransition> pvmList = getOutGoingSeqFlows(taskId);
		if (pvmList != null && pvmList.size() > 0) {
			for (PvmTransition pvm : pvmList) {
				String name = (String) pvm.getProperty("name");
				if (StringUtils.isNotBlank(name)) {
					list.add(name);
				} else {
					list.add("提交");
				}
			}
		}
		return list;
	}

	/**
	 * 根据出连线名称，获取下一个节点ID
	 * 
	 * @param taskId
	 *            任务ID
	 * @param sequenceFlowName
	 *            连线名称
	 * @return 连线目标任务ID
	 */
	public String getTargetTaskId(String taskId, String sequenceFlowName) {
		String targetNodeId = "";
		List<PvmTransition> pvmList = getOutGoingSeqFlows(taskId);
		if (pvmList != null && pvmList.size() > 0) {
			for (PvmTransition pvm : pvmList) {
				String name = (String) pvm.getProperty("name"); // 连线名称
				String destination = pvm.getDestination().getId();
				if (StringUtils.isNotEmpty(name) && name.equalsIgnoreCase(sequenceFlowName)) {
					targetNodeId = destination;
					break;
				}
			}
		}
		return targetNodeId;
	}

	/**
	 * 根据任务ID，查询ProcessDefinitionEntiy对象，获取当前任务完成之后的连线集合
	 */
	private List<PvmTransition> getOutGoingSeqFlows(String taskId) {
		// 1:使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 2：获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		// 3：查询ProcessDefinitionEntiy对象
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processDefinitionId);
		// 使用任务对象Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// 使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();
		// 获取当前活动的id
		String activityId = pi.getActivityId();
		// 4：获取当前的活动
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
		// 5：获取当前活动完成之后连线的名称
		List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
		return pvmList;
	}

	/**
	 * 根据任务ID获取批注信息列表
	 * 
	 * @param taskId
	 *            任务ID
	 * @return List<Comment> 批注
	 */
	public List<Comment> getCommentByTaskId(String taskId) {
		List<Comment> list = new ArrayList<Comment>();
		// 使用当前任务ID，获取当前任务对象
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// //使用流程实例ID，查询历史任务，获取历史任务对应的每个任务ID
		// List<HistoricTaskInstance> htiList =
		// historyService.createHistoricTaskInstanceQuery()//历史任务表查询
		// .processInstanceId(processInstanceId)//使用流程实例ID查询
		// .list();
		// //遍历集合，获取每个任务ID
		// if(htiList!=null && htiList.size()>0){
		// for(HistoricTaskInstance hti:htiList){
		// //任务ID
		// String htaskId = hti.getId();
		// //获取批注信息
		// List<Comment> taskList =
		// taskService.getTaskComments(htaskId);//对用历史完成后的任务ID
		// list.addAll(taskList);
		// }
		// }
		list = taskService.getProcessInstanceComments(processInstanceId);
		return list;
	}

	/**
	 * 根据任务ID查询流程定义对象
	 * 
	 * @param taskId
	 *            任务ID
	 * @return ProcessDefinition 流程定义对象
	 */
	public ProcessDefinition getProcessDefinitionByTaskId(String taskId) {
		// 使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		// 查询流程定义的对象
		ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()// 创建流程定义查询对象，对应表act_re_procdef
				.processDefinitionId(processDefinitionId)// 使用流程定义ID查询
				.singleResult();
		return pd;
	}

	/**
	 * 二：查看当前活动，获取当期活动对应的坐标x,y,width,height，将4个值存放到Map<String,Object>中
	 * map集合的key：表示坐标x,y,width,height map集合的value：表示坐标对应的值
	 * 
	 * @param taskId
	 *            当前正在执行的任务ID
	 * @return Map<String,Object> 当前任务节点的坐标系
	 */
	public Map<String, Object> getCoordinateByTaskID(String taskId) {
		// 存放坐标
		Map<String, Object> map = new HashMap<String, Object>();
		// 使用任务ID，查询任务对象
		Task task = taskService.createTaskQuery()//
				.taskId(taskId)// 使用任务ID查询
				.singleResult();
		// 获取流程定义的ID
		String processDefinitionId = task.getProcessDefinitionId();
		// 获取流程定义的实体对象（对应.bpmn文件中的数据）
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processDefinitionId);
		// 流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// 使用流程实例ID，查询正在执行的执行对象表，获取当前活动对应的流程实例对象
		ProcessInstance pi = runtimeService.createProcessInstanceQuery()// 创建流程实例查询
				.processInstanceId(processInstanceId)// 使用流程实例ID查询
				.singleResult();
		// 获取当前活动的ID
		String activityId = pi.getActivityId();
		// 获取当前活动对象
		ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);// 活动ID
		// 获取坐标
		map.put("x", activityImpl.getX());
		map.put("y", activityImpl.getY());
		map.put("width", activityImpl.getWidth());
		map.put("height", activityImpl.getHeight());
		return map;
	}

	/**
	 * 根据流程实例ID和任务Key，查询历史任务执行人，用于驳回时指定处理人
	 * 
	 * @param processInstanceId
	 *            流程实例ID
	 * @param taskDefinitionKey
	 *            任务key，对应流程图里面的ID
	 * @return String 历史执行任务的处理人
	 */
	public String getHistoryTaskByKey(String processInstanceId, String taskDefinitionKey) {
		List<HistoricTaskInstance> list = historyService.createHistoricTaskInstanceQuery()
				.processInstanceId(processInstanceId).taskDefinitionKey(taskDefinitionKey).orderByExecutionId().asc()
				.list();
		if (null != list && list.size() > 0) {
			return list.get(0).getAssignee();
		}
		return null;
	}

	/**
	 * 根据流程定义key判断流程是否结束
	 * 
	 * @param processDefinitionKey
	 *            流程定义key
	 * @return
	 */
	public boolean isProcessFinishedByProcDefKey(String processDefinitionKey) {
		boolean isFinished = false;
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processDefinitionKey(processDefinitionKey).singleResult();
		if (null == processInstance) {
			isFinished = true;
		}

		return isFinished;
	}

	/**
	 * 根据流程实例ID判断流程是否结束
	 * 
	 * @param processDefinitionKey
	 *            流程定义key
	 * @return
	 */
	public boolean isProcessFinishedByProcInstId(String processInstanceId) {
		boolean isFinished = false;
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		if (null == processInstance) {
			isFinished = true;
		}
		return isFinished;
	}

}
