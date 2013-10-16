package cn.com.zdezclient;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cn.com.zdezclient.activities.BaseActivity;
import cn.com.zdezclient.activities.NetWorkErrorMsgActivity;
import cn.com.zdezclient.db.NewsDao;
import cn.com.zdezclient.db.SchoolMsgDao;
import cn.com.zdezclient.db.ZdezMsgDao;
import cn.com.zdezclient.fragments.ColorFragment;
import cn.com.zdezclient.fragments.NewsFragment;
import cn.com.zdezclient.fragments.SchoolMsgFragment;
import cn.com.zdezclient.fragments.SlidingFragment;
import cn.com.zdezclient.fragments.ZdezMsgFragment;
import cn.com.zdezclient.internet.ZdezHTTPClient;
import cn.com.zdezclient.preference.ZdezPreferences;
import cn.com.zdezclient.services.ZdezService;
import cn.com.zdezclient.utils.UpdateManager;

import com.actionbarsherlock.view.MenuItem;

/**
 * 程序主界面
 * 
 * @author werther
 * 
 */

public class ZdezMainActivity extends BaseActivity {

	private Fragment mContent;
	// private SharedPreferences sp;
	private int newsUnreads = 0;
	private int schoolMsgUnreads = 0;
	private int zdezUnreads = 0;

	private SchoolMsgDao smd;
	private NewsDao newsDao;
	private ZdezMsgDao zmd;

	private String TAG = ZdezMainActivity.class.getSimpleName();
	private boolean DEBUG = ZdezPreferences.getDebug();
	private SharedPreferences sp;

	public ZdezMainActivity() {
		super(R.string._column_title_news);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		// 防止注销登录后回到主界面
		// boolean finish = getIntent().getBooleanExtra("finish", false);
		// if (finish) {
		// startActivity(new Intent(ZdezMainActivity.this,
		// LoginActivity.class));
		// System.exit(0);
		// return;
		// }

		ActivityContrl.add(this);
		if (!((ZdezApplication) getApplication()).isReady()) {
			System.exit(0);
		}

		// 初始化dao
		newsDao = new NewsDao(ZdezMainActivity.this);
		smd = new SchoolMsgDao(ZdezMainActivity.this);
		zmd = new ZdezMsgDao(ZdezMainActivity.this);
		sp = ((ZdezApplication) getApplication()).getPrefs();

		// 如果acitivity是由用户从通知栏点击过来的时候，显示对应的fragment
		boolean isFromNoti = sp.getBoolean("from_noti", false);
		Log.d(TAG, "Getted string from notification : " + isFromNoti);
		if (isFromNoti) {
			setTitle(getResources().getString(R.string._column_title_schoolmsg));
			mContent = new SchoolMsgFragment();

			// 之后又重置
			ZdezPreferences.setLaunchFromNotificationTag(sp.edit(), false);
		}

		// 开始设置滑动抽屉
		// getSlidingMenu().setMode(SlidingMenu.LEFT);
		// getSlidingMenu().setBehindOffsetRes(R.dimen.behindWidth);
		// getSlidingMenu().setShadowWidth(20);
		// getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		// 设置主界面的第一个显示的界面
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null) {
			mContent = new NewsFragment();
		}

		// 设置上层界面
		// getSupportActionBar().setDisplayShowHomeEnabled(false);
		setContentView(R.layout.content_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent).commit();

		// 设置下层滑动抽屉界面
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.menu_frame, new SlidingFragment()).commit();

		// 设置右边的滑动界面
		// getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_two);
		// getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);
		// getSupportFragmentManager().beginTransaction()
		// .replace(R.id.menu_frame_two, new SlidingFragment()).commit();
		//
		// getSupportActionBar().setIcon(android.R.color.transparent);

		// 启动Service,从preference中取得是否开启推送通知，第一次没有值，则默认为true
		boolean isGetPushNotification = sp.getBoolean(
				"notifications_new_message", true);
		if (isGetPushNotification && isOnline()) {
			Intent service = new Intent();
			service.setClass(ZdezMainActivity.this, ZdezService.class);
			startService(service);
		}

		// 检查软件版本更新
		UpdateManager manager = new UpdateManager(ZdezMainActivity.this);
		manager.checkUpdate();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void switchContent(Fragment fragment) {
		mContent = fragment;
		String title = "";
		title = mContent.getArguments().getString("title");
		System.out.println("The title passed" + title);
		setTitle(title);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
		getSlidingMenu().showContent();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.bar_setting:
			Intent intent = new Intent();
			intent.setClass(this, SettingsActivity.class);
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			showExitAlert();
		}
		return super.onKeyDown(keyCode, event);
	}

	private void showExitAlert() {
		// TODO Auto-generated method stub
		Builder exitDialog = new AlertDialog.Builder(ZdezMainActivity.this);
		exitDialog.setMessage("确定要退出系统吗？").setTitle("退出系统确认")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						System.exit(0);
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				}).show();

	}

	public void toNewsFragment(View view) {
		Fragment newContent = new NewsFragment();
		Bundle bundle = new Bundle();
		bundle.putString("title",
				getResources().getString(R.string._column_title_news));
		newContent.setArguments(bundle);
		switchFragment(newContent);
	}

	public void toSchoolMsgFragment(View view) {
		Fragment newContent = new SchoolMsgFragment();
		Bundle bundle = new Bundle();
		bundle.putString("title",
				getResources().getString(R.string._column_title_schoolmsg));
		newContent.setArguments(bundle);
		switchFragment(newContent);
	}

	public void toZdezFragment(View view) {
		Fragment newContent = new ZdezMsgFragment();
		Bundle bundle = new Bundle();
		bundle.putString("title",
				getResources().getString(R.string._column_title_zdezmsg));
		newContent.setArguments(bundle);
		switchFragment(newContent);
	}

	public void toSettingActivity(View view) {
		Bundle bundle = new Bundle();
		bundle.putString("title",
				getResources().getString(R.string._column_title_setting));
		Intent intent = new Intent();
		intent.setClass(this, SettingsActivity.class);
		startActivity(intent);
	}

	public void toTestFragment(View view) {
		Fragment newContent = new ColorFragment();
		Bundle bundle = new Bundle();
		bundle.putString("title", "测试条件");
		newContent.setArguments(bundle);
		switchContent(newContent);
	}

	public void toNetErrorMsg(View view) {
		Intent intent = new Intent();
		intent.setClass(this, NetWorkErrorMsgActivity.class);
		startActivity(intent);
	}

	public void towebsite(View view) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse(ZdezHTTPClient.ZDEZ_WEBSITE_URL));
		startActivity(browserIntent);
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (this instanceof ZdezMainActivity) {
			switchContent(fragment);
		}

	}

	@Override
	protected void onResume() {
		updateUnreadBadge();
		super.onResume();
	}

	/**
	 * 更新在sliding-menu中的每一项的未读条数
	 */
	private void updateUnreadBadge() {
		TextView newsUnreadsV = (TextView) findViewById(R.id.badge_news);
		TextView schoolMsgUnreadsV = (TextView) findViewById(R.id.badge_msg);
		TextView zdezUnreadsV = (TextView) findViewById(R.id.badge_zdez);
		if (DEBUG)
			Log.d(TAG, "NewsUnreadBadge object getted in ZdezMainActivity:"
					+ newsUnreadsV);
		if (DEBUG)
			Log.d(TAG, "schoolMsgUnreadsV object getted in ZdezMainActivity:"
					+ schoolMsgUnreadsV);
		if (DEBUG)
			Log.d(TAG, "zdezUnreadsV object getted in ZdezMainActivity:"
					+ zdezUnreadsV);
		schoolMsgUnreads = smd.getUnreadSchoolMsgCount();
		newsUnreads = newsDao.getUnreadNewsCount();
		zdezUnreads = zmd.getUnreadZdezCount();
		// 新闻未读
		if (newsUnreads > 0) {
			newsUnreadsV.setText(String.valueOf(newsUnreads));
		} else {
			newsUnreadsV.setVisibility(View.GONE);
		}

		// 学校通知未读
		if (schoolMsgUnreads > 0) {
			schoolMsgUnreadsV.setText(String.valueOf(schoolMsgUnreads));
		} else {
			schoolMsgUnreadsV.setVisibility(View.GONE);
		}

		// 找得着未读
		if (zdezUnreads > 0) {
			zdezUnreadsV.setText(String.valueOf(zdezUnreads));
		} else {
			zdezUnreadsV.setVisibility(View.GONE);
		}

		if (!isOnline()) {
			Button btn = (Button) findViewById(R.id.net_error_info);
			btn.setVisibility(View.VISIBLE);
		}
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
