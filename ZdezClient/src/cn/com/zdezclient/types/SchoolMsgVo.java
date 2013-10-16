package cn.com.zdezclient.types;

import java.util.List;

public class SchoolMsgVo {

	private int schoolMsgId;
	private String coverPath;
	private String title;
	private String content;
	private String date;
	private String schoolName;
	private String senderName;
	private List<String> destGrade;
	private List<String> destDepartment;
	private List<String> destMajor;
	private int readStatus;
	private String remarks;

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public int getSchoolMsgId() {
		return schoolMsgId;
	}

	public void setSchoolMsgId(int schoolMsgId) {
		this.schoolMsgId = schoolMsgId;
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

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public List<String> getDestGrade() {
		return destGrade;
	}

	public void setDestGrade(List<String> destGrade) {
		this.destGrade = destGrade;
	}

	public List<String> getDestDepartment() {
		return destDepartment;
	}

	public void setDestDepartment(List<String> destDepartment) {
		this.destDepartment = destDepartment;
	}

	public List<String> getDestMajor() {
		return destMajor;
	}

	public void setDestMajor(List<String> destMajor) {
		this.destMajor = destMajor;
	}

	public int getReadStatus() {
		return readStatus;
	}

	public void setReadStatus(int readStatus) {
		this.readStatus = readStatus;
	}

	public String getCoverPath() {
		return coverPath;
	}

	public void setCoverPath(String coverPath) {
		this.coverPath = coverPath;
	}

}
