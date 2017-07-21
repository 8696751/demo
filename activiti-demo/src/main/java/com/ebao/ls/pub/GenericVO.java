package com.ebao.ls.pub;

import java.io.Serializable;

public abstract class GenericVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5891631736169323878L;
	private String processInstanceId;
	private String taskId;
	private String userId;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	public String getTaskId() {
		return taskId;
	}

	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
}
