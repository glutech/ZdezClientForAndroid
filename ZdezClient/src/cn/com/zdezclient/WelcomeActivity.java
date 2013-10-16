package cn.com.zdezclient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import cn.com.zdezclient.R;
import cn.com.zdezclient.activities.LoginActivity;
import cn.com.zdezclient.internet.ZdezHTTPClient;
import cn.com.zdezclient.preference.ZdezPreferences;
import cn.com.zdezclient.utils.MyZdezClientExceptionHandler;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 用于显示打开应用的第一个欢迎界面，可以进行一些耗时的操作再跳应用的其他功能界面
 * 
 * @author werther
 * 
 */

public class WelcomeActivity extends Activity {

	private static final String TAG = "WelcomeActivity";
	private static final boolean DEBUG = ZdezPreferences.getDebug();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		ActivityContrl.add(this);

		// 添加版本信息
		TextView tv = (TextView) findViewById(R.id.welcome_info);
		tv.append("Version ");
		tv.append(getVersionName());
		if (!((ZdezApplication) getApplication()).isReady()) {
			TextView hint = (TextView) findViewById(R.id.hint);
			hint.setVisibility(View.VISIBLE);
		}

		// 自定义的crash reporter
//		SharedPreferences mPrefs = ((ZdezApplication) getApplication())
//				.getPrefs();
//		String url = ZdezHTTPClient.CRASH_LOG_PATH;
//		Thread.setDefaultUncaughtExceptionHandler(new MyZdezClientExceptionHandler(
//				mPrefs, "/sdcard/", url));

		// 下面的就是延时和耗时的操作
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// 检查是否登录过，登录过就不用再登录，直接跳转到主界面，
				// 若是没有登录过，就跳转到登陆界面
				System.out.println(!((ZdezApplication) getApplication())
						.isReady());
				if (!((ZdezApplication) getApplication()).isReady()) {
					if (DEBUG)
						Log.d(TAG, "没有登录过,强制进行登录操作");
					redirectToLoginActivity();
				} else {
					Intent toMainActivity = new Intent();
					toMainActivity.setClass(WelcomeActivity.this,
							ZdezMainActivity.class);
					startActivity(toMainActivity);
					finish();
				}

			}
		}, 3000);
	}

	public void getResponseResult() {
		// test for the async http client
		RequestParams params = new RequestParams();
		params.put("username", "bokeltd5");
		params.put("password", "bokeltd5");
		ZdezHTTPClient.post("AuthTest", params, new AsyncHttpResponseHandler() {
			public void onSuccess(String result) {
				System.out.println("This is response from server:  " + result);
			}

			@Override
			public void onFailure(Throwable arg0, String arg1) {
				super.onFailure(arg0, arg1);
				System.out.println("Fail :" + arg1);
			}

			@Override
			public void onFinish() {
				super.onFinish();
				System.out.println("On finish;");
			}

			@Override
			public void onStart() {
				super.onStart();
				System.out.println("Start");
			}

		});
	}

	private void redirectToLoginActivity() {
		setVisible(false);
		Intent toLoginActivity = new Intent();
		toLoginActivity.setClass(WelcomeActivity.this, LoginActivity.class);
		toLoginActivity.setAction(Intent.ACTION_MAIN);
		toLoginActivity.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(toLoginActivity);
		finish();
	}

	private String getVersionName() {
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
