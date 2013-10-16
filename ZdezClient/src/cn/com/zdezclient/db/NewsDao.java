package cn.com.zdezclient.db;

import java.util.Iterator;
import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import cn.com.zdezclient.preference.ZdezPreferences;
import cn.com.zdezclient.types.NewsVo;
import cn.com.zdezclient.utils.UriConverter;

public class NewsDao {

	private SQLiteDatabase db;
	private ZdezDataBaseHelper zdezDBHelper;
	private String TAG = NewsDao.class.getSimpleName();
	private boolean DEBUG = ZdezPreferences.getDebug();

	public NewsDao(Context context) {
		this.zdezDBHelper = ZdezDataBaseHelper.getInstance(context);
		this.db = zdezDBHelper.getReadableDatabase();
	}

	/**
	 * 关闭数据库
	 */
	public void close() {
		db.close();
	}

	/**
	 * 插入单条的新闻资讯
	 * 
	 * @param news
	 */
	public void createNewsItem(NewsVo news) {
		ContentValues cv = new ContentValues();
		cv.put("newsId", news.getId());
		cv.put("newsTitle", news.getTitle());
		cv.put("newsContent", UriConverter.replaceSrc(news.getContent()));
		cv.put("newsDate", news.getDate());
		cv.put("newsReadStatus", news.getReadStatus());
		if (news.getCoverPath() != null && !news.getCoverPath().equals(""))
			cv.put("newsCover",
					UriConverter.replaceCoverPath(news.getCoverPath()));

		db.insert("News", null, cv);
	}

	/**
	 * 插入一组LinkedList的news
	 * 
	 * @param newsList
	 */
	public void createNewsList(LinkedList<NewsVo> newsList) {
		Iterator<NewsVo> it = newsList.iterator();
		while (it.hasNext()) {
			createNewsItem(it.next());
		}
	}

	/**
	 * 从数据库中返回所有的newsList，按照创建时间排序 从news表中取出
	 * 
	 * @return
	 */
	public LinkedList<NewsVo> getNewsList() {
		LinkedList<NewsVo> newsList = new LinkedList<NewsVo>();
		String sql = "select newsId as _id, "
				+ "newsTitle, newsContent, newsDate, "
				+ "newsReadStatus, newsCover"
				+ " from News order by newsDate desc";
		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			NewsVo newsItem = new NewsVo();
			if (DEBUG)
				Log.d(TAG, "Get news From db, first id:" + cursor.getInt(0));
			newsItem.setId(cursor.getInt(0));
			newsItem.setTitle(cursor.getString(1));
			newsItem.setContent(cursor.getString(2));
			newsItem.setDate(cursor.getString(3));
			newsItem.setReadStatus(cursor.getInt(4));
			newsItem.setCoverPath(cursor.getString(5));
			newsList.add(newsItem);
			while (cursor.moveToNext()) {
				NewsVo newsItem2 = new NewsVo();
				newsItem2.setId(cursor.getInt(0));
				newsItem2.setTitle(cursor.getString(1));
				newsItem2.setContent(cursor.getString(2));
				newsItem2.setDate(cursor.getString(3));
				newsItem2.setReadStatus(cursor.getInt(4));
				newsItem2.setCoverPath(cursor.getString(5));
				newsList.add(newsItem2);
			}
		}

		return newsList;
	}

	/**
	 * 按照给定的起始id和要求的数目取出最近的n条信息，用于分段加载信息
	 * 
	 * @param start
	 * @param count
	 * @return
	 */
	public LinkedList<NewsVo> getPagedNewsList(int start, int count) {
		LinkedList<NewsVo> newsList = new LinkedList<NewsVo>();
		String sql;
		if (start == -1) {
			sql = "select newsId as _id, "
					+ "newsTitle, newsContent, newsDate, "
					+ "newsReadStatus, newsCover"
					+ " from News order by newsDate desc limit " + count;
		} else {
			sql = "select newsId as _id, "
					+ "newsTitle, newsContent, newsDate, "
					+ "newsReadStatus, newsCover" + " from News where newsId<"
					+ start + " order by newsDate desc limit " + count;
		}

		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToFirst()) {
			NewsVo newsItem = new NewsVo();
			if (DEBUG)
				Log.d(TAG, "Get news From db, first id:" + cursor.getInt(0));
			newsItem.setId(cursor.getInt(0));
			newsItem.setTitle(cursor.getString(1));
			newsItem.setContent(cursor.getString(2));
			newsItem.setDate(cursor.getString(3));
			newsItem.setReadStatus(cursor.getInt(4));
			newsItem.setCoverPath(cursor.getString(5));
			newsList.add(newsItem);
			while (cursor.moveToNext()) {
				NewsVo newsItem2 = new NewsVo();
				newsItem2.setId(cursor.getInt(0));
				newsItem2.setTitle(cursor.getString(1));
				newsItem2.setContent(cursor.getString(2));
				newsItem2.setDate(cursor.getString(3));
				newsItem2.setReadStatus(cursor.getInt(4));
				newsItem2.setCoverPath(cursor.getString(5));
				newsList.add(newsItem2);
			}
		}

		return newsList;
	}

	/**
	 * 返回News中未读的条数统计
	 * 
	 * @return
	 */
	public int getUnreadNewsCount() {
		int count = 0;

		String query = "select count(newsId) from News "
				+ "where newsReadStatus=0";

		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
		}

		if (DEBUG)
			Log.d(TAG, "Get Unread News Count in NewsDao:" + count);

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
		String updateSql = "update News set newsReadStatus=1 where newsId="
				+ id;
		db.execSQL(updateSql);
	}
}
