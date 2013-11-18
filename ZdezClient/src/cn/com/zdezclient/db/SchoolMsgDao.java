package cn.com.zdezclient.db;

import java.util.Iterator;
import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cn.com.zdezclient.ZdezApplication;
import cn.com.zdezclient.preference.ZdezPreferences;
import cn.com.zdezclient.types.SchoolMsgVo;
import cn.com.zdezclient.utils.UriConverter;

public class SchoolMsgDao {

	private SQLiteDatabase db;
	private ZdezDataBaseHelper zdezDBHelper;
	private String TAG = SchoolMsgDao.class.getSimpleName();
	private final static String userId = ZdezApplication.getUserId();
	private boolean DEBUG = ZdezPreferences.getDebug();

	public SchoolMsgDao(Context context) {
		this.zdezDBHelper = ZdezDataBaseHelper.getInstance(context, userId);
		this.db = zdezDBHelper.getWritableDatabase();
	}

	/**
	 * 关闭数据库连接
	 */
	public void close() {
		db.close();
	}

	/**
	 * 插入新的学校通知信息数据,有两个默认值： 类型默认为‘school_msg’ 未读状态默认为‘0’
	 * 
	 * @param msg
	 */
	public void createSchoolMsg(SchoolMsgVo msg) {
		ContentValues cv = new ContentValues();
		cv.put("schoolMsgId", msg.getSchoolMsgId());
		cv.put("schoolMsgTitle", msg.getTitle());
		// 插入
		cv.put("schoolMsgContent", msg.getContent());
		cv.put("schoolMsgDate", msg.getDate());
		cv.put("schoolMsgSenderName", msg.getSenderName());
		cv.put("schoolMsgSchoolName", msg.getSchoolName());
		cv.put("schoolMsgReadStatus", msg.getReadStatus());
		cv.put("schoolMsgRemarks", msg.getRemarks());
		if (msg.getCoverPath() != null && !msg.getCoverPath().equals(""))
			cv.put("schoolMsgCover", msg.getCoverPath());

		db.insert("SchoolMsg", null, cv);
	}

	/**
	 * 插入一组linkedList的msg
	 * 
	 * @param msgs
	 */
	public void createSchoolMsgs(LinkedList<SchoolMsgVo> msgs) {
		Iterator<SchoolMsgVo> it = msgs.iterator();
		while (it.hasNext()) {
			createSchoolMsg((SchoolMsgVo) it.next());
		}

	}

	/**
	 * 取得所有信息的cursor
	 * 
	 * @return
	 */
	public Cursor getSchoolMsgCursor() {
		String sql = "select schoolMsgId as _id," + " schoolMsgTitle, "
				+ "schoolMsgContent, " + "schoolMsgDate, "
				+ "schoolMsgSchoolName, " + "schoolMsgSenderName, "
				+ "schoolMsgReadStatus, "
				+ "schoolMsgCover, schoolMsgRemarks from SchoolMsg";
		return db.rawQuery(sql, null);

	}

	/**
	 * 返回所有的msg，以LinkedList的格式,按照创建时间排序
	 * 从SchoolMsg表中取出，依据type字段中的数据是school_msg即时学校信息，
	 * 
	 * @return
	 */
	public LinkedList<SchoolMsgVo> getSchoolMsgList() {
		LinkedList<SchoolMsgVo> list = new LinkedList<SchoolMsgVo>();
		String sql = "select schoolMsgId as _id," + " schoolMsgTitle, "
				+ "schoolMsgContent, " + "schoolMsgDate, "
				+ "schoolMsgSchoolName, " + "schoolMsgSenderName, "
				+ "schoolMsgReadStatus, " + "schoolMsgCover, schoolMsgRemarks "
				+ "from SchoolMsg order by schoolMsgDate desc";
		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			SchoolMsgVo msg = new SchoolMsgVo();
			if (DEBUG)
				Log.d("SchoolMsgDao", "first id:" + cursor.getInt(0));
			msg.setSchoolMsgId(cursor.getInt(0));
			msg.setTitle(cursor.getString(1));
			msg.setContent(cursor.getString(2));
			msg.setDate(cursor.getString(3));
			msg.setSchoolName(cursor.getString(4));
			msg.setSenderName(cursor.getString(5));
			msg.setReadStatus(cursor.getInt(6));
			msg.setCoverPath(cursor.getString(7));
			msg.setRemarks(cursor.getString(8));
			list.add(msg);
			if (DEBUG)
				Log.d("SchoolMsgDao", "Now size:" + list.size());
			while (cursor.moveToNext()) {
				SchoolMsgVo msg2 = new SchoolMsgVo();
				if (DEBUG)
					Log.d("SchoolMsgDao", "next id:" + cursor.getInt(0));
				msg2.setSchoolMsgId(cursor.getInt(0));
				msg2.setTitle(cursor.getString(1));
				msg2.setContent(cursor.getString(2));
				msg2.setDate(cursor.getString(3));
				msg2.setSchoolName(cursor.getString(4));
				msg2.setSenderName(cursor.getString(5));
				msg2.setReadStatus(cursor.getInt(6));
				msg2.setCoverPath(cursor.getString(7));
				msg2.setRemarks(cursor.getString(8));
				list.add(msg2);
				if (DEBUG)
					Log.d("SchoolMsgDao", "Now size:" + list.size());
			}
		}

		return list;
	}

	public SchoolMsgVo getSchoolMsgById(int id) {
		SchoolMsgVo msg = new SchoolMsgVo();
		String sql = "select schoolMsgId as _id," + " schoolMsgTitle, "
				+ "schoolMsgContent, " + "schoolMsgDate, "
				+ "schoolMsgSchoolName, " + "schoolMsgSenderName, "
				+ "schoolMsgReadStatus, " + "schoolMsgCover, schoolMsgRemarks "
				+ "from SchoolMsg where schoolMsgId=" + id
				+ " order by schoolMsgDate desc";
		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			if (DEBUG)
				Log.d("SchoolMsgDao", "first id:" + cursor.getInt(0));
			msg.setSchoolMsgId(cursor.getInt(0));
			msg.setTitle(cursor.getString(1));
			msg.setContent(cursor.getString(2));
			msg.setDate(cursor.getString(3));
			msg.setSchoolName(cursor.getString(4));
			msg.setSenderName(cursor.getString(5));
			msg.setReadStatus(cursor.getInt(6));
			msg.setCoverPath(cursor.getString(7));
			msg.setRemarks(cursor.getString(8));
		}

		return msg;
	}

	/**
	 * 按照给定的起始id和要求的数目取出最近的n条信息，用于分段加载信息
	 * 
	 * @param start
	 * @param count
	 * @return
	 */
	public LinkedList<SchoolMsgVo> getPagedSchoolMsgList(int start, int count) {
		LinkedList<SchoolMsgVo> list = new LinkedList<SchoolMsgVo>();
		String sql;
		if (start == -1) {
			sql = "select schoolMsgId as _id," + " schoolMsgTitle, "
					+ "schoolMsgContent, " + "schoolMsgDate, "
					+ "schoolMsgSchoolName, " + "schoolMsgSenderName, "
					+ "schoolMsgReadStatus, " + "schoolMsgCover "
					+ "from SchoolMsg order by schoolMsgDate desc limit "
					+ count;

		} else {
			sql = "select schoolMsgId as _id," + " schoolMsgTitle, "
					+ "schoolMsgContent, " + "schoolMsgDate, "
					+ "schoolMsgSchoolName, " + "schoolMsgSenderName, "
					+ "schoolMsgReadStatus, " + "schoolMsgCover "
					+ "from SchoolMsg where schoolMsgId<" + start
					+ " order by schoolMsgDate desc limit " + count;
		}
		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			SchoolMsgVo msg = new SchoolMsgVo();
			if (DEBUG)
				Log.d("SchoolMsgDao", "first id:" + cursor.getInt(0));
			msg.setSchoolMsgId(cursor.getInt(0));
			msg.setTitle(cursor.getString(1));
			msg.setContent(cursor.getString(2));
			msg.setDate(cursor.getString(3));
			msg.setSchoolName(cursor.getString(4));
			msg.setSenderName(cursor.getString(5));
			msg.setReadStatus(cursor.getInt(6));
			msg.setCoverPath(cursor.getString(7));
			list.add(msg);
			if (DEBUG)
				Log.d("SchoolMsgDao", "Now size:" + list.size());
			while (cursor.moveToNext()) {
				SchoolMsgVo msg2 = new SchoolMsgVo();
				if (DEBUG)
					Log.d("SchoolMsgDao", "next id:" + cursor.getInt(0));
				msg2.setSchoolMsgId(cursor.getInt(0));
				msg2.setTitle(cursor.getString(1));
				msg2.setContent(cursor.getString(2));
				msg2.setDate(cursor.getString(3));
				msg2.setSchoolName(cursor.getString(4));
				msg2.setSenderName(cursor.getString(5));
				msg2.setReadStatus(cursor.getInt(6));
				msg2.setCoverPath(cursor.getString(7));
				list.add(msg2);
				if (DEBUG)
					Log.d("SchoolMsgDao", "Now size:" + list.size());
			}
		}

		return list;
	}

	/**
	 * 计算数据库中学校通知未读条数
	 * 
	 * @return
	 */
	public int getUnreadSchoolMsgCount() {
		int count = 0;

		String query = "select count(schoolMsgId) from SchoolMsg where schoolMsgReadStatus=0";

		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}

		return count;
	}

	/**
	 * 计算数据库中新闻资讯未读条数
	 * 
	 * @return
	 */
	public int getUnreadNewsCount() {
		int count = 0;

		String query = "select count(schoolMsgId) from SchoolMsg where schoolMsgReadStatus=0 and type='news'";

		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}

		return count;
	}

	/**
	 * 将某条信息的阅读状态schoolMsgReadStatus设置为1，默认未读状态是0. 即将消息设置为已读，应该在进行阅读时调用
	 * 
	 * @param id
	 */
	public void setRead(int id) {
		if (DEBUG)
			Log.d(TAG, "在数据库中设置id为：" + id + "的消息为已读");
		String updateSql = "update SchoolMsg set schoolMsgReadStatus=1 where schoolMsgId="
				+ id;
		db.execSQL(updateSql);

	}

}
