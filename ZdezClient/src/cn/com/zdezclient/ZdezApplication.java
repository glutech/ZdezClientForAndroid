package cn.com.zdezclient;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Timestamp;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import cn.com.zdezclient.internet.ZdezHTTPClient;
import cn.com.zdezclient.preference.ZdezPreferences;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ZdezApplication extends Application {

	public static final String PACKAGE_NAME = "cn.com.zdez";
	private static SharedPreferences mPrefs;
	private boolean mIsFirstRun;
	public static String versionName;
	private Zdez mZdez;
	public static ZdezApplication instance;

	// PendingIntent restartIntent;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		mZdez = new Zdez();
		// 检查存储空间中的prefrence文件是否存在已判断这是否是第一次安装
		mIsFirstRun = checkIfIsFirstRun();

		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		// 设置一些默认值
		ZdezPreferences.setupDefaults(mPrefs, getResources());

		versionName = getVersionName();

		// 以下用来捕获程序崩溃异常
		// Intent intent = new Intent();
		// // 参数1：包名，参数2：程序入口的activity
		// intent.setClassName("cn.com.zdezclient",
		// "cn.com.zdezclient.WelcomeActivity");
		// restartIntent = PendingIntent.getActivity(getApplicationContext(), 0,
		// intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		if (!ZdezPreferences.getDebug())
			Thread.setDefaultUncaughtExceptionHandler(stopHandler);
		// 程序崩溃时触发线程
	}

	private boolean checkIfIsFirstRun() {
		File file = new File(
				"/data/data/cn.com.zdezclient/shared_prefs/cn.com.zdezclient_preferences.xml");
		return !file.exists();
	}

	public boolean isReady() {
		System.out.println("Is there something wrong!");
		boolean flag = !TextUtils.isEmpty(getUserId()) && getUserId() != null
				&& !"-1".equals(getUserId());
		return flag;
	}

	public static String getUserId() {
		return ZdezPreferences.getUserId(mPrefs);
	}

	public Zdez getZdez() {
		return mZdez;
	}

	public SharedPreferences getPrefs() {
		return mPrefs;
	}

	public static ZdezApplication getInstance() {
		return instance;
	}

	public UncaughtExceptionHandler stopHandler = new UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			// AlarmManager mgr = (AlarmManager)
			// getSystemService(Context.ALARM_SERVICE);
			// mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,
			// restartIntent); // 1秒钟后重启应用

			String timestamp = new Timestamp(System.currentTimeMillis())
					.toString();
			final Writer result = new StringWriter();
			final PrintWriter printWriter = new PrintWriter(result);
			ex.printStackTrace(printWriter);
			String stacktrace = result.toString();
			printWriter.close();
			String user_id = getUserId();
			String filename = timestamp + "_user_id_" + user_id + "_version_"
					+ versionName + ".log";
			sendToServer(stacktrace, filename);

			ActivityContrl.finishProgram(); // 自定义方法，关闭当前打开的所有avtivity
		}
	};

	private void sendToServer(String stacktrace, String filename) {
		RequestParams params = new RequestParams();
		params.put("filename", filename);
		params.put("stacktrace", stacktrace);
		ZdezHTTPClient.post(ZdezHTTPClient.CRASH_LOG_PATH_SERVLET_NAME, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						Log.d("SendToServer", "failed with:" + arg1);
						super.onFailure(arg0, arg1);
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						Log.d("SendToServer", "finished");
						super.onFinish();
					}

					@Override
					public void onStart() {
						// TODO Auto-generated method stub
						Log.d("SendToServer", "start");
						super.onStart();
					}

					@Override
					public void onSuccess(int arg0, String arg1) {
						Log.d("SendToServer", "got it down");
						super.onSuccess(arg0, arg1);
					}

				});
	}

	public String getVersionName() {
		String versionName = null;
		try {
			// 获取软件版本号，对应AndroidManifest.xml下android:versionCode
			versionName = getPackageManager().getPackageInfo(
					"cn.com.zdezclient", 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

}
