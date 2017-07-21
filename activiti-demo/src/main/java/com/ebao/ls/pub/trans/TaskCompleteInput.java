package com.ebao.ls.pub.trans;

import java.util.Map;

public class TaskCompleteInput extends DefaultInput {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = 3922320353054965092L;

	private Map<String, Object> variables;

	public Map<String, Object> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

}
