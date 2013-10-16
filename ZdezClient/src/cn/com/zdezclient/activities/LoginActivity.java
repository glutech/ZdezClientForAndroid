package cn.com.zdezclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import cn.com.zdezclient.ActivityContrl;
import cn.com.zdezclient.R;
import cn.com.zdezclient.Zdez;
import cn.com.zdezclient.ZdezApplication;
import cn.com.zdezclient.internet.ZdezHTTPClient;
import cn.com.zdezclient.preference.ZdezPreferences;
import cn.com.zdezclient.utils.ZdezCharsetUtil;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

	// Values for email and password at the time of the login attempt.
	private String mUsername;
	private String mPassword;

	// UI references.
	private EditText mUsernameView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private static final boolean DEBUG = ZdezPreferences.getDebug();
	private static final String TAG = "LogingActivity";
	private Zdez zdez;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		zdez = ((ZdezApplication) getApplication()).getZdez();
		// 设置没有title的界面
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		ActivityContrl.add(this);

		// 注销用户
		ZdezPreferences.logoutUser(PreferenceManager
				.getDefaultSharedPreferences(this).edit());

		// Set up the login form.
		mUsernameView = (EditText) findViewById(R.id.email);
		mUsernameView.setText(mUsername);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// super.onCreateOptionsMenu(menu);
	// getMenuInflater().inflate(R.menu.login, menu);
	// return true;
	// }

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {

		// Reset errors.
		mUsernameView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUsername = mUsernameView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 6) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mUsername)) {
			mUsernameView.setError(getString(R.string.error_field_required));
			focusView = mUsernameView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			userLogin();
			// mAuthTask = new UserLoginTask();
			// mAuthTask.execute((Void) null);
		}
	}

	/**
	 * 显示登录进度条
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	// 为同一异步请求管理，使用外部async-http-client包处理登录，而不是用自带的async-task
	private void userLogin() {
		RequestParams params = new RequestParams();
		params.put("username", mUsername);
		params.put("password", mPassword);
		ZdezHTTPClient.post(ZdezHTTPClient.LOGIN_SERVLET_NAME, params,
				new AsyncHttpResponseHandler() {
					public void onSuccess(String result) {
						result = ZdezCharsetUtil.toUTF8Str(result);

						// 请求正常返回
						showProgress(false);
						if (DEBUG)
							Log.d(TAG, " 请求成功，从服务器返回的数据为:  " + result);
						if ("fail".equals(result)) {
							// 用户验证失败
							mPasswordView
									.setError(getString(R.string.error_incorrect_password));
							mPasswordView.requestFocus();
						} else if (isIdSetted(result)) {
							// 用户验证成功
							redirectToMainActivity(result);
						} else {
							// 用户验证失败
							mPasswordView
									.setError(getString(R.string.error_incorrect_password));
							mPasswordView.requestFocus();
						}

					}

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						super.onFailure(arg0, arg1);
						// 请求失败
						if (DEBUG)
							Log.d(TAG, "请求失败");
						showProgress(false);
						mPasswordView
								.setError(getString(R.string.error_with_internet));
						mPasswordView.requestFocus();
					}

					@Override
					public void onFinish() {
						super.onFinish();
						if (DEBUG)
							Log.d(TAG, "请求结束");
						showProgress(false);
					}

					@Override
					public void onStart() {
						super.onStart();
						if (DEBUG)
							Log.d(TAG, "请求开始");
					}

				});

	}

	private boolean isIdSetted(String result) {
		boolean flag = false;
		// 首先存储登录返回的用户信息的方法
		ZdezPreferences.setUser(zdez, result, PreferenceManager
				.getDefaultSharedPreferences(this).edit());
		String idStr = ZdezPreferences.getUserId(PreferenceManager
				.getDefaultSharedPreferences(this));
		int id = Integer.valueOf(idStr);
		if (idStr != null && !"".equals(idStr) && id > 0) {
			flag = true;
		}

		return flag;
	}

	private void redirectToMainActivity(String result) {
		setVisible(false);

		// // 启动必要的Service，登陆成功启动
		// // 先启动定时更新的Receiver,这里还不进行数据请求，只是启动定时请求的manager
		// Calendar wakeUpTime = Calendar.getInstance();
		// wakeUpTime.add(Calendar.SECOND,
		// ZdezPreferences.getKeepAliveSeconds());
		//
		// AlarmManager aMgr = (AlarmManager) getSystemService(ALARM_SERVICE);
		// Intent receiverIntent = new
		// Intent(ZdezPreferences.getReceiverName());
		// receiverIntent.setClass(this, RequestOnTimeReceiver.class);
		// PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
		// receiverIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		// aMgr.set(AlarmManager.RTC_WAKEUP, wakeUpTime.getTimeInMillis(),
		// pendingIntent);

		// 然后开始跳转到主界面
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, Whatsnew.class);
		intent.setAction(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
				| Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showExitAlert();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showExitAlert() {
		System.exit(0);
	}
}
