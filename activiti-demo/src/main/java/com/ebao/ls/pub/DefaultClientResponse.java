package com.ebao.ls.pub;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @ClassName: DefaultClientResponse
 * @Description:
 * @author angus.xu
 * @date Jul 17, 2017 3:04:47 PM
 * 
 */
public class DefaultClientResponse extends GenericVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1037381362241394951L;
	private String clientRequestId;
	@JSONField(format = "yyyy-MM-dd HH:mm:ss SSS")
	private Date clientRequestTime;
	/**
	 * @Fields result : 交易结果：成功、失败、异常
	 */
	private Integer result;
	@JSONField(format = "yyyy-MM-dd HH:mm:ss SSS")
	private Date serviceRequestTime;
	@JSONField(format = "yyyy-MM-dd HH:mm:ss SSS")
	private Date serviceResponseTime;

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

	public Integer getResult() {
		return result;
	}

	public void setResult(Integer result) {
		this.result = result;
	}

	public Date getServiceRequestTime() {
		return serviceRequestTime;
	}

	public void setServiceRequestTime(Date serviceRequestTime) {
		this.serviceRequestTime = serviceRequestTime;
	}

	public Date getServiceResponseTime() {
		return serviceResponseTime;
	}

	public void setServiceResponseTime(Date serviceResponseTime) {
		this.serviceResponseTime = serviceResponseTime;
	}

}
