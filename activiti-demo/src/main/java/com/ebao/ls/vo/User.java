package com.ebao.ls.vo;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = -7805775379975900475L;
	private String userId;
	private String workNo;
	private String userName;
	private String password;
	private String token;
	private String userBirthday;
	private Double userSalary;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId == null ? null : userId.trim();
	}

	public String getWorkNo() {
		return workNo;
	}

	public void setWorkNo(String workNo) {
		this.workNo = workNo;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName == null ? null : userName.trim();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserBirthday() {
		return userBirthday;
	}

	public void setUserBirthday(String userBirthday) {
		this.userBirthday = userBirthday;
	}

	public Double getUserSalary() {
		return userSalary;
	}

	public void setUserSalary(Double userSalary) {
		this.userSalary = userSalary;
	}

	@Override
	public String toString() {
		return "User [userId=" + userId + ", workNo=" + workNo + ", userName=" + userName + ", password=" + password
				+ ", token=" + token + ", userBirthday=" + userBirthday + ", userSalary=" + userSalary + "]";
	}
}
