package cn.com.zdezclient.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.com.zdezclient.ActivityContrl;
import cn.com.zdezclient.R;
import cn.com.zdezclient.internet.ZdezHTTPClient;
import cn.com.zdezclient.preference.ZdezPreferences;

import com.actionbarsherlock.app.SherlockActivity;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class FeedBackActivity extends SherlockActivity {

	private final static boolean DEBUG = ZdezPreferences.getDebug();
	private final static String TAG = FeedBackActivity.class.getSimpleName();

	private String feedbackStr;

	private EditText feedBackV;
	private Button sendBtn;
	private View mSendFormView;
	private View mSendStatusView;
	private TextView mSendStatusMessageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(
				getResources().getDrawable(R.drawable.arrow_back));
		ActivityContrl.add(this);
		setContentView(R.layout.activity_feed_back);

		feedBackV = (EditText) findViewById(R.id.feedback_text);
		sendBtn = (Button) findViewById(R.id.send_feedback);
		mSendFormView = findViewById(R.id.feedback_form);
		mSendStatusView = findViewById(R.id.feed_back_status);
		mSendStatusMessageView = (TextView) findViewById(R.id.feed_back_status_message);

		sendBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				attemptSend();
			}
		});

		setTitle("意见反馈");
	}

	public void attemptSend() {
		feedBackV.setError(null);

		feedbackStr = feedBackV.getText().toString();
		boolean cancel = false;
		View focusView = null;

		if (TextUtils.isEmpty(feedbackStr)) {
			feedBackV.setError("请填写反馈意见再提交");
			focusView = feedBackV;
			cancel = true;
		}

		if (cancel) {
			// 取消提交操作
			focusView.requestFocus();
		} else {
			// 校验完成没问题，可以提交
			mSendStatusMessageView.setText("正在提交反馈");
			showProgress(true);

			sendFeedbackToServer();
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

			mSendStatusView.setVisibility(View.VISIBLE);
			mSendStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mSendStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mSendFormView.setVisibility(View.VISIBLE);
			mSendFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mSendFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mSendStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mSendFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public void sendFeedbackToServer() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(FeedBackActivity.this);
		RequestParams params = new RequestParams();
		params.put("user_id", ZdezPreferences.getUserId(prefs));
		params.put("feedback", feedbackStr);
		ZdezHTTPClient.post(ZdezHTTPClient.SEND_FEEDBACK_SERVLET_NAME, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onStart() {
						if (DEBUG)
							Log.d(TAG, "Start to send the feedback to server");
						super.onStart();
					}

					@Override
					public void onFinish() {
						if (DEBUG)
							Log.d(TAG, "Finish send feedback to server");
						super.onFinish();
					}

					@Override
					@Deprecated
					public void onSuccess(String content) {
						showProgress(false);
						if (DEBUG)
							Log.d(TAG,
									"Success get response send feedback to server");
						if ("false".equals(content)) {
							feedBackV.setError("反馈未能正常发送，请稍后再试");
							feedBackV.requestFocus();
						} else {
							if (DEBUG)
								Log.d(TAG, "Success sending feed back");
							Builder exitDialog = new AlertDialog.Builder(
									FeedBackActivity.this);
							exitDialog
									.setTitle("反馈提示")
									.setMessage("我们已记录你的建议，感谢你的反馈。")
									.setPositiveButton(
											"确定",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface arg0,
														int arg1) {
													arg0.cancel();
													finish();
												}
											}).show();
						}

						super.onSuccess(content);
					}

					@Override
					@Deprecated
					public void onFailure(Throwable error, String content) {
						super.onFailure(error, content);
					}

				});

	}

}
