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
import cn.com.zdezclient.types.ZdezMsgVo;
import cn.com.zdezclient.utils.UriConverter;

public class ZdezMsgDao {

	private SQLiteDatabase db;
	private ZdezDataBaseHelper zdezDBHelper;
	private String TAG = ZdezMsgDao.class.getSimpleName();
	private boolean DEBUG = ZdezPreferences.getDebug();
	private final static String userId = ZdezApplication.getUserId();

	public ZdezMsgDao(Context context) {
		this.zdezDBHelper = ZdezDataBaseHelper.getInstance(context, userId);
		this.db = zdezDBHelper.getReadableDatabase();
	}

	public void close() {
		db.close();
	}

	public void createZdezMsg(ZdezMsgVo zdez) {
		if (DEBUG)
			Log.d(TAG, "Intert zdezmsg id:" + zdez.getZdezMsgId());
		ContentValues cv = new ContentValues();
		cv.put("zdezId", zdez.getZdezMsgId());
		cv.put("zdezTitle", zdez.getTitle());
		cv.put("zdezContent", UriConverter.replaceSrc(zdez.getContent()));
		cv.put("zdezDate", zdez.getDate());
		cv.put("zdezReadStatus", zdez.getReadStatus());
		if (zdez.getCoverPath() != null && !zdez.getCoverPath().equals(""))
			cv.put("zdezCover",
					UriConverter.replaceCoverPath(zdez.getCoverPath()));

		db.insert("ZdezMsg", null, cv);
	}

	public void createZdezMsgList(LinkedList<ZdezMsgVo> zdezList) {
		Iterator<ZdezMsgVo> it = zdezList.iterator();
		while (it.hasNext()) {
			createZdezMsg(it.next());
		}

	}

	public LinkedList<ZdezMsgVo> getZdezMsgList() {
		LinkedList<ZdezMsgVo> list = new LinkedList<ZdezMsgVo>();
		String sql = "select zdezId as _id, " + "zdezTitle, zdezContent, "
				+ "zdezDate, zdezReadStatus, "
				+ "zdezCover from ZdezMsg order by zdezDate desc";
		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			ZdezMsgVo zdez = new ZdezMsgVo();
			zdez.setZdezMsgId(cursor.getInt(0));
			zdez.setTitle(cursor.getString(1));
			zdez.setContent(cursor.getString(2));
			zdez.setDate(cursor.getString(3));
			zdez.setReadStatus(cursor.getInt(4));
			zdez.setCoverPath(cursor.getString(5));
			list.add(zdez);
			if (DEBUG)
				Log.d(TAG, "Get Unread status for zdez:" + zdez.getTitle()
						+ "status: " + zdez.getReadStatus());
			while (cursor.moveToNext()) {
				ZdezMsgVo zdez2 = new ZdezMsgVo();
				zdez2.setZdezMsgId(cursor.getInt(0));
				zdez2.setTitle(cursor.getString(1));
				zdez2.setContent(cursor.getString(2));
				zdez2.setDate(cursor.getString(3));
				zdez2.setReadStatus(cursor.getInt(4));
				zdez2.setCoverPath(cursor.getString(5));
				list.add(zdez2);
				Log.d(TAG, "Get Unread status for zdez:" + zdez2.getTitle()
						+ "status: " + zdez2.getReadStatus());
			}
		}

		return list;
	}

	public LinkedList<ZdezMsgVo> getPagedZdezMsgList(int start, int count) {
		LinkedList<ZdezMsgVo> list = new LinkedList<ZdezMsgVo>();
		String sql;
		if (start == -1) {
			sql = "select zdezId as _id, " + "zdezTitle, zdezContent, "
					+ "zdezDate, zdezReadStatus, "
					+ "zdezCover from ZdezMsg order by zdezDate desc limit "
					+ count;
		} else {
			sql = "select zdezId as _id, " + "zdezTitle, zdezContent, "
					+ "zdezDate, zdezReadStatus, "
					+ "zdezCover from ZdezMsg where zdezId<" + start
					+ " order by zdezDate desc limit " + count;
		}

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			ZdezMsgVo zdez = new ZdezMsgVo();
			zdez.setZdezMsgId(cursor.getInt(0));
			zdez.setTitle(cursor.getString(1));
			zdez.setContent(cursor.getString(2));
			zdez.setDate(cursor.getString(3));
			zdez.setReadStatus(cursor.getInt(4));
			zdez.setCoverPath(cursor.getString(5));
			list.add(zdez);
			if (DEBUG)
				Log.d(TAG, "Get Unread status for zdez:" + zdez.getTitle()
						+ "status: " + zdez.getReadStatus());
			while (cursor.moveToNext()) {
				ZdezMsgVo zdez2 = new ZdezMsgVo();
				zdez2.setZdezMsgId(cursor.getInt(0));
				zdez2.setTitle(cursor.getString(1));
				zdez2.setContent(cursor.getString(2));
				zdez2.setDate(cursor.getString(3));
				zdez2.setReadStatus(cursor.getInt(4));
				zdez2.setCoverPath(cursor.getString(5));
				list.add(zdez2);
				Log.d(TAG, "Get Unread status for zdez:" + zdez2.getTitle()
						+ "status: " + zdez2.getReadStatus());
			}
		}

		return list;
	}

	/**
	 * 返回Zdez中未读的条数统计
	 * 
	 * @return
	 */
	public int getUnreadZdezCount() {
		int count = 0;

		String query = "select count(zdezId) from ZdezMsg where zdezReadStatus=0";

		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}

		if (DEBUG)
			Log.d(TAG, "Get Unread Zdez Count in ZdezMsgDao:" + count);

		return count;
	}

	/**
	 * 将某条信息的阅读状态schoolMsgReadStatus设置为1，默认未读状态是0. 即将消息设置为已读，应该在进行阅读时调用
	 * 
	 * @param id
	 */
	public void setRead(int id) {
		if (DEBUG)
			Log.d(TAG, "在数据库中间id为：" + id + "的消息设置为已读");
		String updateSql = "update ZdezMsg set zdezReadStatus=1 where zdezId="
				+ id;
		db.execSQL(updateSql);
	}
}
