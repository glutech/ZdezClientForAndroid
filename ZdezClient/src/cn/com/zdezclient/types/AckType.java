package cn.com.zdezclient.types;

import java.util.List;

public class AckType {
	private int userId;
	private List<String> msgIds;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public List<String> getMsgIds() {
		return msgIds;
	}

	public void setMsgIds(List<String> msgIds) {
		this.msgIds = msgIds;
	}

}
