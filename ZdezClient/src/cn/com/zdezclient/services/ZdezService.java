package cn.com.zdezclient.services;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import cn.com.zdezclient.preference.ZdezPreferences;

public class ZdezService extends Service {

	private static final String TAG = ZdezService.class.getSimpleName();

	@Override
	public void onCreate() {
		// 在该Service创建时，由系统调用该方法，一次性初始化操作
		// 在onStartCommand和onBind之前
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// 被activity等component启动是调用,
		// 1.登录之后第一次启用
		// 2.开机启动，若用户一登录则启动
		// 3.网络重新恢复，检查后启动

		new Thread(new Runnable() {

			@Override
			public void run() {
				doSomething();
			}
		}).start();

		Log.d(TAG, "StartCommand called in service, Got the startId:" + startId);
		return START_STICKY;
	}

	protected void doSomething() {
		Log.d(TAG, "Start do some trick things");
		// 启动Receiver
		Calendar wakeUpTime = Calendar.getInstance();
		wakeUpTime.add(Calendar.SECOND, 20);
		AlarmManager aMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		Intent intent = new Intent(ZdezPreferences.getReceiverName());
		intent.setClass(this, RequestOnTimeReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		aMgr.set(AlarmManager.RTC_WAKEUP, wakeUpTime.getTimeInMillis(),
				pendingIntent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onDestroy() {
		Log.d(TAG, "I got know when and why I am destroy");
		super.onDestroy();
	}

}
