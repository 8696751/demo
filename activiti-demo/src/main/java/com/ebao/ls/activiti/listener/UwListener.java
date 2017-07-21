package com.ebao.ls.activiti.listener;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
* @ClassName: UwListener 
* @Description: 在diagrams/*bpmn文件内定义了监听类
* @author angus.xu
* @date Jul 18, 2017 2:11:41 PM 
*  
*/
public class UwListener implements TaskListener {
	private static final Logger logger = LoggerFactory.getLogger(UwListener.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -6940184175542761337L;

	@Override
	public void notify(DelegateTask delegateTask) {
		logger.info("进入核保监听");
		/**
		 * 本事件中可以做些业务处理，例如添加任务所允许的操作组；
		 * 操作人等 addCandidateUser为公有任务，确定哪些人可以认领此任务
		 * 后面可以通过taskService.claim来讲公用任务转换为私有任务
		 */
		delegateTask.addCandidateUser("400");
		delegateTask.addCandidateUser("401");
	}

}
