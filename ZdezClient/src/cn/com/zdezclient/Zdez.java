package cn.com.zdezclient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import android.content.SharedPreferences;
import cn.com.zdezclient.internet.ZdezHTTPClient;
import cn.com.zdezclient.preference.ZdezPreferences;
import cn.com.zdezclient.types.AckType;
import cn.com.zdezclient.types.NewsVo;
import cn.com.zdezclient.types.SchoolMsgVo;
import cn.com.zdezclient.types.User;
import cn.com.zdezclient.types.ZdezMsgVo;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class Zdez {

	private static String mUserName;
	private static String mPassword;

	public String getmUserName() {
		return mUserName;
	}

	public void setmUserName(String mUserName) {
		Zdez.mUserName = mUserName;
	}

	public String getmPassword() {
		return mPassword;
	}

	public void setmPassword(String mPassword) {
		Zdez.mPassword = mPassword;
	}

	public User convertStudentFromJsonString(String result) {
		System.out.println("Zdez result:" + result);
		Gson gson = new Gson();
		User user = gson.fromJson(result, User.class);
		return user;
	}

	public static void acknowledgeSchoolMsg(LinkedList<SchoolMsgVo> list,
			SharedPreferences prefs) {
		Gson gson = new Gson();
		RequestParams params = new RequestParams();
		AckType ack = new AckType();
		ack.setMsgIds(getIdsFromMsgList(list));
		ack.setUserId(Integer.valueOf(ZdezPreferences.getUserId(prefs)));
		params.put("ack", gson.toJson(ack));

		ZdezHTTPClient.post(ZdezHTTPClient.UPDATE_MSG_RECEIVED_SERVLET_NAME,
				params, new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1);
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
					}

					@Override
					public void onSuccess(String arg0) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0);
					}

				});

	}

	/**
	 * 给服务器返回收到消息的确认
	 * 
	 * @param list
	 * @param prefs
	 */
	public static void acknowledgeNews(LinkedList<NewsVo> list,
			SharedPreferences prefs) {
		Gson gson = new Gson();
		RequestParams params = new RequestParams();
		AckType ack = new AckType();
		ack.setMsgIds(getIdsNewsList(list));
		ack.setUserId(Integer.valueOf(ZdezPreferences.getUserId(prefs)));
		params.put("ack", gson.toJson(ack));

		ZdezHTTPClient.post(ZdezHTTPClient.UPDATE_NEWS_RECEIVED_SERVLET_NAME,
				params, new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1);
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
					}

					@Override
					public void onSuccess(String arg0) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0);
					}

				});
	}

	public static void acknowledgeZdezMsg(LinkedList<ZdezMsgVo> list,
			SharedPreferences prefs) {
		Gson gson = new Gson();
		RequestParams params = new RequestParams();
		AckType ack = new AckType();
		ack.setMsgIds(getIdsZdezMsgList(list));
		ack.setUserId(Integer.valueOf(ZdezPreferences.getUserId(prefs)));
		params.put("ack", gson.toJson(ack));

		ZdezHTTPClient.post(
				ZdezHTTPClient.UPDATE_ZDEZ_MSG_RECEIVED_SERVLET_NAME, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						// TODO Auto-generated method stub
						super.onFailure(arg0, arg1);
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						super.onFinish();
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						super.onStart();
					}

					@Override
					public void onSuccess(String arg0) {
						// TODO Auto-generated method stub
						super.onSuccess(arg0);
					}

				});
	}

	private static List<String> getIdsNewsList(LinkedList<NewsVo> list) {
		List<String> ids = new ArrayList<String>();

		Iterator<NewsVo> it = list.iterator();
		while (it.hasNext()) {
			NewsVo news = it.next();
			System.out.println("this msg has id:"
					+ String.valueOf(news.getId()));
			ids.add(String.valueOf(news.getId()));
		}
		return ids;
	}

	private static List<String> getIdsZdezMsgList(LinkedList<ZdezMsgVo> list) {
		List<String> ids = new ArrayList<String>();

		Iterator<ZdezMsgVo> it = list.iterator();
		while (it.hasNext()) {
			ZdezMsgVo zdez = it.next();
			System.out.println("this msg has id:"
					+ String.valueOf(zdez.getZdezMsgId()));
			ids.add(String.valueOf(zdez.getZdezMsgId()));
		}
		return ids;
	}

	private static List<String> getIdsFromMsgList(LinkedList<SchoolMsgVo> list) {
		List<String> ids = new ArrayList<String>();

		Iterator<SchoolMsgVo> it = list.iterator();
		while (it.hasNext()) {
			SchoolMsgVo msg = it.next();
			System.out.println("this msg has id:"
					+ String.valueOf(msg.getSchoolMsgId()));
			ids.add(String.valueOf(msg.getSchoolMsgId()));
		}
		return ids;
	}

}