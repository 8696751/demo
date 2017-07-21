package com.ebao.ls.json;

import java.util.Date;
import java.util.UUID;

import com.alibaba.fastjson.JSONObject;
import com.ebao.ls.pub.trans.DefaultInput;
import com.ebao.ls.pub.trans.InputVO;

public class JsonGenerate {

	public static void main(String[] args) {
		JsonGenerate jg = new JsonGenerate();
		jg.generateInputVO();
	}

	String generateInputVO() {
		DefaultInput di = new DefaultInput();
		InputVO vo = new InputVO();
		di.setPolicy(vo);
		di.setClientRequestId(UUID.randomUUID().toString());
		di.setClientRequestTime(new Date());
		di.setServiceRequestTime(new Date());
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		vo.setContractId(1L);
		vo.setApplyCode("0001");
		vo.setPolicyCode("0001");
		vo.setOrganId(1L);
		vo.setSubmitChannel(5L);
		vo.setMasterProductId(10001L);
		String jsonString = JSONObject.toJSONString(di);
		System.out.println(jsonString);
		return jsonString;
	}

}
