package cn.com.zdezclient.activities;

import java.sql.Timestamp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import cn.com.zdezclient.ActivityContrl;
import cn.com.zdezclient.R;
import cn.com.zdezclient.db.SchoolMsgDao;
import cn.com.zdezclient.preference.ZdezPreferences;
import cn.com.zdezclient.types.SchoolMsgVo;
import cn.com.zdezclient.utils.TimeToWords;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class SchoolMsgWebViewActivity extends SherlockActivity {

	private int id;
	private String content;
	private SchoolMsgDao smd;
	private SchoolMsgVo msg;
	private static String TAG = SchoolMsgWebViewActivity.class.getSimpleName();
	private static boolean DEBUG = ZdezPreferences.getDebug();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		smd = new SchoolMsgDao(this);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);

		// 对ActionBar进行一些定制
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(
				getResources().getDrawable(R.drawable.arrow_back));

		ActivityContrl.add(this);

		// 从跳转前的列表中的item中取到对应的信息内容和相关属性
		Bundle bundle = this.getIntent().getExtras();
		id = Integer.valueOf(bundle.getString("id"));
		msg = smd.getSchoolMsgById(id);

		TimeToWords ttw = new TimeToWords();
		String[] time = ttw.getWordsOfTime(Timestamp.valueOf(msg.getDate()));
		if (msg.getRemarks() != null && !"".equals(msg.getRemarks())) {
			content = "<h2>" + msg.getTitle() + "</h2>" + "<font size=2>"
					+ time[1] + "&nbsp&nbsp&nbsp来自:" + msg.getRemarks()
					+ "</font><br><br>" + msg.getContent();
		} else {
			content = "<h2>" + msg.getTitle() + "</h2>" + "<font size=2>"
					+ time[1] + "</font><br><br>" + msg.getContent();
		}

		// 显示消息
		setTitle(getResources().getString(R.string._column_title_schoolmsg));
		WebView webview = new WebView(this);
		setContentView(webview);
		webview.getSettings().setDefaultTextEncodingName("utf-8");
		// 设置单列显示，不可左右滚动
		webview.setHorizontalScrollBarEnabled(false);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		// 设置无网络是优先取本地缓存的数据，否则优先取网络
		if (!isOnline()) {
			webview.getSettings().setCacheMode(
					WebSettings.LOAD_CACHE_ELSE_NETWORK);
		} else {
			webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
		}

		webview.loadData(content, "text/html; charset=utf-8", "utf-8");

		webview.getSettings().setJavaScriptEnabled(true);

		// 设置附件下载功能
		webview.setDownloadListener(new DownloadListener() {

			public void onDownloadStart(String url, String userAgent,
					String contentDisposition, String mimetype,
					long contentLength) {
				// handle download, here we use brower to download, also you can
				// try other approach.
				Uri uri = Uri.parse(url);
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});

		final Activity activity = this;
		webview.setWebChromeClient(new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				// Activities and WebViews measure progress with different
				// scales.
				// The progress meter will automatically disappear when we reach
				// 100%
				activity.setProgress(progress * 1000);
			}
		});
		webview.setWebViewClient(new WebViewClient() {
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				Toast.makeText(activity, "加载失败，请检查网络连接 " + description,
						Toast.LENGTH_SHORT).show();
			}
		});

		// 最后进行已读设置
		smd.setRead(id);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.abs__up:
			// Intent intent = new Intent();
			// intent.setClass(PureTextContentActivity.this,
			// ZdezMainActivity.class);
			// startActivity(intent);
			finish();
			break;
		case R.id.abs__home:
			// Intent intent2 = new Intent();
			// intent2.setClass(PureTextContentActivity.this,
			// ZdezMainActivity.class);
			// startActivity(intent2);
			// finish();
			break;
		default:
			break;
		}
		finish();
		return super.onOptionsItemSelected(item);
	}

	private boolean isOnline() {
		final ConnectivityManager cm = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		State cellurState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		Log.d("ZdezHttpClient", "State of wifi:" + wifiState + "and mobile:"
				+ cellurState);

		if (wifiState == State.CONNECTED || cellurState == State.CONNECTED) {
			Log.d("ZdezHttpClient", "Got network connected");
			return true;

		}
		return false;

	}
}
