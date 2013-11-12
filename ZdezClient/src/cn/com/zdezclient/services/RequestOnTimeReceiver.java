package cn.com.zdezclient.services;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.regex.Pattern;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import cn.com.zdezclient.R;
import cn.com.zdezclient.Zdez;
import cn.com.zdezclient.ZdezMainActivity;
import cn.com.zdezclient.db.SchoolMsgDao;
import cn.com.zdezclient.internet.ZdezHTTPClient;
import cn.com.zdezclient.preference.ZdezPreferences;
import cn.com.zdezclient.types.SchoolMsgVo;
import cn.com.zdezclient.utils.ZdezCharsetUtil;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class RequestOnTimeReceiver extends BroadcastReceiver {
	private static SharedPreferences sp;
	private static String TAG = "RequestOnTimeReceiver";
	private static boolean DEBUG = ZdezPreferences.getDebug();
	private static Context context;
	private static SchoolMsgDao smd;
	private static LinkedList<SchoolMsgVo> msgList;

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		sp = PreferenceManager.getDefaultSharedPreferences(arg0);

		RequestOnTimeReceiver.context = arg0;
		if (DEBUG)
			Log.d(TAG, "收到消息，开始处理");

		// 每次收到首先要做的就是检查当前网络状况
		boolean isOnline = isOnline();

		boolean isNotice = sp.getBoolean("notifications_new_message", true);
		if (DEBUG)
			Log.d(TAG, "Getted is notice from preference : " + isNotice);

		if (DEBUG)
			Log.d(TAG,
					"Getted userid from preference : "
							+ ZdezPreferences.getUserId(sp));
		// 在用户数据没有id的情况下返回“-1”
		if ((isOnline && isNotice)
				&& (ZdezPreferences.getUserId(sp) != null && !"-1"
						.equals(ZdezPreferences.getUserId(sp)))) {
			// 在确认userId正常之后才可以实例化数据库dao
			smd = new SchoolMsgDao(arg0);

			// 进入这个方法需要确定user_id必须存在有效，因为请求消息必须使用它
			// 收到之后先启动下一个定时器
			Calendar wakeUpTime = Calendar.getInstance();
			wakeUpTime.add(Calendar.SECOND, ZdezPreferences
					.getKeepAliveSeconds(
							sp,
							arg0.getResources().getString(
									R.string.pref_sync_frequency_name)));
			AlarmManager aMgr = (AlarmManager) arg0
					.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(ZdezPreferences.getReceiverName());
			intent.setClass(arg0, getClass());
			PendingIntent pendingIntent = PendingIntent.getBroadcast(arg0, 0,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
			aMgr.set(AlarmManager.RTC_WAKEUP, wakeUpTime.getTimeInMillis(),
					pendingIntent);

			// 之后开始向服务器发送请求，更新消息
			checkUpdate(arg0);
		} else {
			Log.d(TAG,
					"网络中断或者用户不想接收推送通知，不进行先一次请求的安排，当网络恢复时再由ConnectivityChangeReceiver启动");
		}

	}

	private static void checkUpdate(Context context) {
		RequestParams params = new RequestParams();
		params.put("user_id", ZdezPreferences.getUserId(sp));
		if (DEBUG)
			Log.d(TAG, "The params are:" + params.toString());
		ZdezHTTPClient.get(ZdezHTTPClient.GET_UPDATE_MSG_SERVLET_NAME, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						if (DEBUG)
							Log.d(TAG, "Update news get failure, msg is +"
									+ arg1);
						super.onFailure(arg0, arg1);

					}

					@Override
					public void onFinish() {
						if (DEBUG)
							Log.d(TAG, "Update news get finished");
						super.onFinish();

					}

					@Override
					public void onStart() {
						if (DEBUG)
							Log.d(TAG, "Start to update news");
						super.onStart();
					}

					@Override
					public void onSuccess(String arg0) {
						if (DEBUG)
							Log.d(TAG,
									"Finally update news success with result:"
											+ arg0);

						arg0 = ZdezCharsetUtil.toUTF8Str(arg0);

						// 处理请求得到的数据
						handleResult(arg0);
						super.onSuccess(arg0);
					}

				});

	}

	private static void handleResult(String arg0) {
		if ("[]".equals(arg0)) {
			// 没有新的消息
			// Intent intent = new Intent();
			// intent.setClass(RequestOnTimeReceiver.context,
			// ZdezMainActivity.class);
			// notifyUser(RequestOnTimeReceiver.context, "没有新的消息", "没有新的消息",
			// "哈哈，你上当了！这里没有你想要的消息。", intent);
		} else {
			msgList = new LinkedList<SchoolMsgVo>();
			final Gson gson = new Gson();
			SchoolMsgVo[] msgArray = {};
			msgArray = gson.fromJson(arg0, SchoolMsgVo[].class);
			msgList.addAll(Arrays.asList(msgArray));

			// 先存储到数据库中
			smd.createSchoolMsgs(msgList);

			// post消息确认
			Zdez.acknowledgeSchoolMsg(msgList, sp);

			Intent intent = new Intent();
			intent.setClass(RequestOnTimeReceiver.context,
					ZdezMainActivity.class);
			notifyUser(RequestOnTimeReceiver.context, "找得着", "你有未读消息",
					"点击查看新消息", intent);
			System.out.println("result:");
		}
	}

	private static void notifyUser(Context context, String alert, String title,
			String body, Intent intent) {
		NotificationManager nm = (NotificationManager) context
				.getSystemService(context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher,
				alert, System.currentTimeMillis());
		// 从用户设置中取得选取的铃声，振动与否等设置

		String strRingtonePreference = sp.getString(
				"notifications_new_message_ringtone", "DEFAULT_SOUND");
		if (DEBUG)
			Log.d(TAG, "Getted ringtone from preference:"
					+ strRingtonePreference);
		notification.sound = Uri.parse(strRingtonePreference);
		boolean isVibrate = sp.getBoolean("notifications_new_message_vibrate",
				true);
		if (DEBUG)
			Log.d(TAG, "Getted is vibrate from preference:" + isVibrate);
		if (isVibrate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		notification.defaults |= Notification.DEFAULT_LIGHTS;
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.ledARGB = Color.MAGENTA;
		Intent notificationIntent = new Intent();
		notificationIntent.setClass(context, ZdezMainActivity.class);
		// 为了定向跳转，在preference中记录个值，说明是来自notification的启动
		ZdezPreferences.setLaunchFromNotificationTag(sp.edit(), true);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(context, title, body, contentIntent);
		nm.notify(2, notification);
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	private static boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		State cellurState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		Log.d(TAG, "State of wifi:" + wifiState + "and mobile:" + cellurState);

		if (wifiState == State.CONNECTED || cellurState == State.CONNECTED) {
			Log.d(TAG, "Got network connected");
			return true;

		}
		return false;

	}

}
