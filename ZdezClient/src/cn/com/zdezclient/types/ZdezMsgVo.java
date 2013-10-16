package cn.com.zdezclient.types;

public class ZdezMsgVo {

	private int zdezMsgId;
	private String coverPath;
	private String title;
	private String content;
	private String date;
	private int receivedNum;
	private int receiverNum;
	private int readStatus;

	public int getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(int readStatus) {
		this.readStatus = readStatus;
	}

	public String getCoverPath() {
		return coverPath;
	}

	public int getZdezMsgId() {
		return zdezMsgId;
	}

	public void setZdezMsgId(int zdezMsgId) {
		this.zdezMsgId = zdezMsgId;
	}

	public String getCoverPath(String hostname) {
		coverPath = hostname + coverPath;
		return coverPath;
	}

	public void setCoverPath(String coverPath) {
		this.coverPath = coverPath;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getReceivedNum() {
		return receivedNum;
	}

	public void setReceivedNum(int receivedNum) {
		this.receivedNum = receivedNum;
	}

	public int getReceiverNum() {
		return receiverNum;
	}

	public void setReceiverNum(int receiverNum) {
		this.receiverNum = receiverNum;
	}

}
