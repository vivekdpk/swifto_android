package com.haski.swifto.model.vo;

public class FeedMedicine {

	String msg;
	boolean check;
	
	
	public FeedMedicine(String msg, boolean check) {
		this.msg = msg;
		this.check = check;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
	
}
