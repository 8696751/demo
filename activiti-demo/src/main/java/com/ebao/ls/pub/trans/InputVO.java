package com.ebao.ls.pub.trans;

import com.ebao.ls.pub.GenericVO;

public class InputVO extends GenericVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1178570061433222777L;

	private Long contractId;
	private String applyCode;
	private String policyCode;
	private Long organId;
	private Long submitChannel;
	private Long masterProductId;

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

	public Long getMasterProductId() {
		return masterProductId;
	}

	public void setMasterProductId(Long masterProductId) {
		this.masterProductId = masterProductId;
	}
}
