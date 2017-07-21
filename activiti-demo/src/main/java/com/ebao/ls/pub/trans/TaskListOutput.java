package com.ebao.ls.pub.trans;

import java.util.List;

import com.ebao.ls.pub.PolicyVO;

public class TaskListOutput extends DefaultOutput {

	private static final long serialVersionUID = -8279310031663442919L;
	
	

	public TaskListOutput(List<PolicyVO> policys) {
		super();
		this.policys = policys;
	}

	private List<PolicyVO> policys;

	public List<PolicyVO> getPolicys() {
		return policys;
	}

	public void setPolicys(List<PolicyVO> policys) {
		this.policys = policys;
	}

	

}
