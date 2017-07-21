package com.ebao.ls.pub;

import com.ebao.ls.pub.trans.DefaultOutput;

public class PolicyVO extends DefaultOutput {

	private static final long serialVersionUID = 8061340793781063368L;
	private Long contractId;
	private String applyCode;
	private String policyCode;
	private Long organId;
	private Long submitChannel;

	public Long getContractId() {
		return contractId;
	}

	public void setContractId(Long contractId) {
		this.contractId = contractId;
	}

	public String getApplyCode() {
		return applyCode;
	}

	public void setApplyCode(String applyCode) {
		this.applyCode = applyCode;
	}

	public String getPolicyCode() {
		return policyCode;
	}

	public void setPolicyCode(String policyCode) {
		this.policyCode = policyCode;
	}

	public Long getOrganId() {
		return organId;
	}

	public void setOrganId(Long organId) {
		this.organId = organId;
	}

	public Long getSubmitChannel() {
		return submitChannel;
	}

	public void setSubmitChannel(Long submitChannel) {
		this.submitChannel = submitChannel;
	}
}
