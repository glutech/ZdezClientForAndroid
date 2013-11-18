package cn.com.zdezclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import cn.com.zdezclient.ActivityContrl;
import cn.com.zdezclient.R;
import cn.com.zdezclient.SettingsActivity;
import cn.com.zdezclient.ZdezMainActivity;
import cn.com.zdezclient.internet.ZdezHTTPClient;
import cn.com.zdezclient.preference.ZdezPreferences;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class ModifyPswActivity extends SherlockActivity {

	private final static boolean DEBUG = ZdezPreferences.getDebug();
	private final static String TAG = ModifyPswActivity.class.getSimpleName();

	// Values for email and password at the time of the login attempt.
	private String oldPsw;
	private String newPsw;
	private String confirmNewPsw;

	// UI references.
	private EditText oldPswV;
	private EditText newPswV;
	private EditText confirmNewPswV;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(
				getResources().getDrawable(R.drawable.arrow_back));
		setContentView(R.layout.activity_modify_psw);
		ActivityContrl.add(this);

		// Set up the login form.
		oldPswV = (EditText) findViewById(R.id.old_psw);
		newPswV = (EditText) findViewById(R.id.new_psw);
		confirmNewPswV = (EditText) findViewById(R.id.confirm_new_psw);

		oldPswV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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


	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {

		// Reset errors.
		oldPswV.setError(null);
		newPswV.setError(null);
		confirmNewPswV.setError(null);

		// Store values at the time of the login attempt.
		oldPsw = oldPswV.getText().toString();
		newPsw = newPswV.getText().toString();
		confirmNewPsw = confirmNewPswV.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(oldPsw)) {
			oldPswV.setError(getString(R.string.error_field_required));
			focusView = oldPswV;
			cancel = true;
		} else if (oldPsw.length() < 6) {
			oldPswV.setError(getString(R.string.error_invalid_password));
			focusView = oldPswV;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(newPsw)) {
			newPswV.setError(getString(R.string.error_field_required));
			focusView = newPswV;
			cancel = true;
		} else if (newPsw.length() < 6) {
			newPswV.setError(getString(R.string.error_invalid_password));
			focusView = newPswV;
			cancel = true;
		}

		// 检查新密码确认
		if (TextUtils.isEmpty(confirmNewPsw)) {
			confirmNewPswV.setError(getString(R.string.error_field_required));
			focusView = confirmNewPswV;
			cancel = true;
		} else if (!newPsw.equals(confirmNewPsw)) {
			confirmNewPswV.setError(getString(R.string.error_confirm_newpsw));
			focusView = confirmNewPswV;
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
			// 开始到服务器修改密码，做一点点延时
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					modifyPsw();
				}
			}, 500);

			// mAuthTask = new UserLoginTask();
			// mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
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

	private void modifyPsw() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(ModifyPswActivity.this);
		RequestParams params = new RequestParams();
		params.put("id", ZdezPreferences.getUserId(prefs));
		params.put("oldpsw", oldPsw);
		params.put("newpsw", newPsw);
		ZdezHTTPClient.post(ZdezHTTPClient.MODIFY_PASSWORD_SERVLET_NAME,
				params, new AsyncHttpResponseHandler() {
					public void onSuccess(String result) {
						// 请求正常返回
						showProgress(false);
						if (DEBUG)
							Log.d(TAG, " 请求成功，从服务器返回的数据为:  " + result);
						if ("false".equals(result)) {
							// 修改密码失败
							oldPswV.setError(getString(R.string.fail_at_modify_psw));
							oldPswV.requestFocus();
						} else {
							// 成功修改密码
							// Toast.makeText(getApplication(), "修改成功",
							// Toast.LENGTH_SHORT).show();

							Builder exitDialog = new AlertDialog.Builder(
									ModifyPswActivity.this);
							exitDialog
									.setTitle("密码修改成功")
									.setNegativeButton(
											"返回",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// TODO Auto-generated
													// method stub
													dialog.cancel();
													finish();
												}
											}).show();

							if (DEBUG)
								Log.d(TAG, "成功修改用户密码");
						}

					}

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						super.onFailure(arg0, arg1);
						// 请求失败
						if (DEBUG)
							Log.d(TAG, "请求失败");
						showProgress(false);
						confirmNewPswV
								.setError(getString(R.string.error_with_internet));
						confirmNewPswV.requestFocus();
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.abs__up:
			Intent intent = new Intent();
			intent.setClass(ModifyPswActivity.this, ZdezMainActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.abs__home:
			Intent intent2 = new Intent();
			intent2.setClass(ModifyPswActivity.this, ZdezMainActivity.class);
			startActivity(intent2);
			finish();
			break;
		default:
			break;
		}
		finish();
		return super.onOptionsItemSelected(item);
	}
	// /**
	// * Represents an asynchronous login/registration task used to authenticate
	// * the user.
	// */
	// public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
	// @Override
	// protected Boolean doInBackground(Void... params) {
	// // TODO: attempt authentication against a network service.
	//
	// try {
	// // Simulate network access.
	// Thread.sleep(2000);
	// } catch (InterruptedException e) {
	// return false;
	// }
	//
	// for (String credential : DUMMY_CREDENTIALS) {
	// String[] pieces = credential.split(":");
	// if (pieces[0].equals(mEmail)) {
	// // Account exists, return true if the password matches.
	// return pieces[1].equals(mPassword);
	// }
	// }
	//
	// // TODO: register the new account here.
	// return true;
	// }
	//
	// @Override
	// protected void onPostExecute(final Boolean success) {
	// mAuthTask = null;
	// showProgress(false);
	//
	// if (success) {
	// finish();
	// } else {
	// mPasswordView
	// .setError(getString(R.string.error_incorrect_password));
	// mPasswordView.requestFocus();
	// }
	// }
	//
	// @Override
	// protected void onCancelled() {
	// mAuthTask = null;
	// showProgress(false);
	// }
	// }
}
