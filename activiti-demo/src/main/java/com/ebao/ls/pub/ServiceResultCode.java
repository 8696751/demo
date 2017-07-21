package com.ebao.ls.pub;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ServiceResultCode {
	SUCCEEDED(1), FAILED(0), EXCEPTION(-1);
	private int code;

	private ServiceResultCode(int code) {
		this.code = code;
	}

	public int code() {
		return this.code;
	}

	@JsonValue
	public int toValue() {
		return this.code;
	}

	@JsonCreator
	public static ServiceResultCode forValue(int code) {
		ServiceResultCode[] values = ServiceResultCode.values();
		for (ServiceResultCode value : values) {
			if (value.code == code) {
				return value;
			}
		}
		return EXCEPTION;
	}
}
