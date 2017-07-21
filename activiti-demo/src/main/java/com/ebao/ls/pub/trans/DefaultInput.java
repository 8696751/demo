package com.ebao.ls.pub.trans;

import com.ebao.ls.pub.DefaultClientRequest;

public class DefaultInput extends DefaultClientRequest {

	/**
	 * @Fields serialVersionUID : TODO
	 */
	private static final long serialVersionUID = -2853229719259363414L;

	private InputVO policy;

	public InputVO getPolicy() {
		return policy;
	}

	public void setPolicy(InputVO policy) {
		this.policy = policy;
	}

}
