package cn.com.zdezclient.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import cn.com.zdezclient.ActivityContrl;
import cn.com.zdezclient.R;
import cn.com.zdezclient.internet.ZdezHTTPClient;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class AboutActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// getWindow().requestFeature(Window.FEATURE_PROGRESS);
		// getSupportActionBar().setHomeButtonEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(
				getResources().getDrawable(R.drawable.arrow_back));
		ActivityContrl.add(this);

		// 显示消息
		setTitle("关于我们");
		WebView webview = new WebView(this);
		setContentView(webview);
		webview.getSettings().setDefaultTextEncodingName("utf-8");
		// 设置单列显示，不可左右滚动
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		webview.loadUrl(ZdezHTTPClient.ABOUT_US_HTML_PATH);

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
				Toast.makeText(activity, description, Toast.LENGTH_SHORT)
						.show();
			}
		});
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

}
