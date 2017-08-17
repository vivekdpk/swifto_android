package com.haski.swifto.model.vo;

public class OfflineWalkRequest {


	int _id;
	String walkid ;
	String request;
	String type;
	String status;

	public OfflineWalkRequest(String walkid, String request, String type, String status) {
		this.walkid = walkid;
		this.request = request;
		this.type = type;
		this.status = status;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getWalkid() {
		return walkid;
	}

	public void setWalkid(String walkid) {
		this.walkid = walkid;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
