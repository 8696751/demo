package com.ebao.ls.pub;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class DefaultClientRequest extends GenericVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3241611736180382306L;

	private String clientRequestId;
	@JSONField(format = "yyyy-MM-dd HH:mm:ss SSS")
	private Date clientRequestTime;
	@JSONField(format = "yyyy-MM-dd HH:mm:ss SSS")
	private Date serviceRequestTime;

	public String getClientRequestId() {
		return clientRequestId;
	}

	public void setClientRequestId(String clientRequestId) {
		this.clientRequestId = clientRequestId;
	}

	public Date getClientRequestTime() {
		return clientRequestTime;
	}

	public void setClientRequestTime(Date clientRequestTime) {
		this.clientRequestTime = clientRequestTime;
	}

	public Date getServiceRequestTime() {
		return serviceRequestTime;
	}

	public void setServiceRequestTime(Date serviceRequestTime) {
		this.serviceRequestTime = serviceRequestTime;
	}

}
