package cn.com.zdezclient;

import java.util.List;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import cn.com.zdezclient.activities.AboutActivity;
import cn.com.zdezclient.activities.ModifyPswActivity;
import cn.com.zdezclient.activities.Whatsnew;
import cn.com.zdezclient.db.ZdezDataBaseHelper;
import cn.com.zdezclient.preference.ZdezPreferences;
import cn.com.zdezclient.services.ZdezService;
import cn.com.zdezclient.utils.UpdateManager;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends SherlockPreferenceActivity {
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	private static final boolean DEBUG = ZdezPreferences.getDebug();
	private static final String TAG = SettingsActivity.class.getSimpleName();
	private static final boolean ALWAYS_SIMPLE_PREFS = false;
	private SharedPreferences sp;

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(
				getResources().getDrawable(R.drawable.arrow_back));
		sp = ((ZdezApplication) getApplication()).getPrefs();

		setupSimplePreferencesScreen();
		ActivityContrl.add(this);
		// getSupportActionBar().setDisplayShowHomeEnabled(true);
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}

		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.

		// Add 'general' preferences.
		addPreferencesFromResource(R.xml.pref_general);

		// Add 'notifications' preferences, and a corresponding header.
		PreferenceCategory fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_notifications);
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_notification);

		// Add 'data and sync' preferences, and a corresponding header.
		fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_data_sync);
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_data_sync);

		// Bind the summaries of EditText/List/Dialog/Ringtone preferences to
		// their values. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.
		// bindPreferenceSummaryToValue(findPreference("example_text"));
		// bindPreferenceSummaryToValue(findPreference("example_list"));
		bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
		bindPreferenceSummaryToValue(findPreference("sync_frequency"));

		// 设置更新监听
		setListenerForUpdate();

		// 设置消息通知开关的监听
		setListenerForMessageNotice();

		// 设置查看教程按钮的监听器
		setListenerForTutorial();

		// 设置注销btn的监听
		setListenerForLogOut();

		// 设置修改密码点击的监听
		setListenerForModifyPsw();

		setListenerForAbout();

	}

	// 消息通知开关状态改变时，相应的开关推送服务
	private void setListenerForMessageNotice() {
		Preference isNotificationMessage = findPreference("notifications_new_message");

		isNotificationMessage
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						if (DEBUG)
							Log.d(TAG,
									"On the change of message notification, the new value is: "
											+ newValue);
						if ((Boolean) newValue) {
							Intent service = new Intent();
							service.setClass(SettingsActivity.this,
									ZdezService.class);
							startService(service);
						}
						if (!(Boolean) newValue) {
							Intent toService = new Intent();
							toService.setClass(SettingsActivity.this,
									ZdezService.class);
							stopService(toService);
						}
						return true;
					}
				});

	}

	private void setListenerForTutorial() {
		Preference tutorialP = findPreference("app_tutorial");

		tutorialP.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent();
				intent.setClass(SettingsActivity.this, Whatsnew.class);
				startActivity(intent);
				return false;
			}
		});

	}

	private void setListenerForAbout() {
		Preference aboutP = findPreference("about");

		aboutP.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent();
				intent.setClass(SettingsActivity.this, AboutActivity.class);
				startActivity(intent);
				return false;
			}
		});
	}

	private void setListenerForModifyPsw() {
		Preference modifyPsw = findPreference("modify_psw");

		modifyPsw.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent();
				intent.setClass(SettingsActivity.this, ModifyPswActivity.class);
				startActivity(intent);
				return false;
			}
		});
	}

	private void setListenerForLogOut() {
		Preference logout = findPreference("log_out");
		logout.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {

				Builder exitDialog = new AlertDialog.Builder(
						SettingsActivity.this);
				exitDialog
						.setMessage("注销登录将无法接收消息")
						.setTitle("确定要注销登录吗？")
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										dialog.cancel();
									}
								})
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										finish();
										// 删除账户数据包括数据库
										ZdezPreferences.logoutUser(sp.edit());

										// ZdezDataBaseHelper
										// .deleteAllTableWhileLogout(getApplication());
										ZdezDataBaseHelper.getInstance(
												getApplication()).close();

										// 停止轮询服务
										Intent toService = new Intent();
										toService.setClass(
												SettingsActivity.this,
												ZdezService.class);
										stopService(toService);

										PendingIntent restartIntent;

										// 重启应用，因为用户已经注销，所以会直接跳转到登陆界面
										Intent intent = new Intent();
										// 参数1：包名，参数2：程序入口的activity
										intent.setClassName(
												"cn.com.zdezclient",
												"cn.com.zdezclient.WelcomeActivity");
										restartIntent = PendingIntent
												.getActivity(
														getApplicationContext(),
														0,
														intent,
														Intent.FLAG_ACTIVITY_NEW_TASK);
										AlarmManager mgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
										mgr.set(AlarmManager.RTC,
												System.currentTimeMillis() + 100,
												restartIntent);

										// 关闭所有的Activity，模拟关闭应用
										ActivityContrl.finishProgram();

										// Intent intent = new Intent(
										// SettingsActivity.this,
										// ZdezMainActivity.class);
										// intent.putExtra("finish", true);
										// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
										// | Intent.FLAG_ACTIVITY_CLEAR_TASK
										// | Intent.FLAG_ACTIVITY_NEW_TASK);
										// startActivity(intent);
										// finish();
										// System.exit(0);
									}
								}).show();
				return false;
			}
		});
	}

	// 为更新时间创建监听器
	private void setListenerForUpdate() {
		if (DEBUG)
			Log.d(TAG, "Start set Listener for update click");
		Preference updateP = (Preference) findPreference("update_client");

		// 先显示一下当前版本名称
		String title = getResources().getString(
				R.string.pref_title_update_client)
				+ " -----  V" + getVersionName();
		updateP.setTitle(title);
		updateP.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				if (DEBUG)
					Log.d(TAG, "Start to Check Update client");
				UpdateManager updateManager = new UpdateManager(
						SettingsActivity.this);
				updateManager.isUpdate();
				return false;
			}
		});
	}

	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS
				|| Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				|| !isXLargeTablet(context);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				// For list preferences, look up the correct display value in
				// the preference's 'entries' list.
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);

				// Set the summary to reflect the new value.
				preference
						.setSummary(index >= 0 ? listPreference.getEntries()[index]
								: null);

			} else if (preference instanceof RingtonePreference) {
				// For ringtone preferences, look up the correct display value
				// using RingtoneManager.
				if (TextUtils.isEmpty(stringValue)) {
					// Empty values correspond to 'silent' (no ringtone).
					preference.setSummary(R.string.pref_ringtone_silent);

				} else {
					Ringtone ringtone = RingtoneManager.getRingtone(
							preference.getContext(), Uri.parse(stringValue));

					if (ringtone == null) {
						// Clear the summary if there was a lookup error.
						preference.setSummary(null);
					} else {
						// Set the summary to reflect the new ringtone display
						// name.
						String name = ringtone
								.getTitle(preference.getContext());
						preference.setSummary(name);
					}
				}

			} else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 * 
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference
				.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				preference,
				PreferenceManager.getDefaultSharedPreferences(
						preference.getContext()).getString(preference.getKey(),
						""));
	}

	/**
	 * This fragment shows general preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			// bindPreferenceSummaryToValue(findPreference("example_text"));
			// bindPreferenceSummaryToValue(findPreference("example_list"));
		}
	}

	/**
	 * This fragment shows notification preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class NotificationPreferenceFragment extends
			PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_notification);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
		}
	}

	/**
	 * This fragment shows data and sync preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class DataSyncPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_data_sync);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("sync_frequency"));
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.abs__up:
			Intent intent = new Intent();
			intent.setClass(SettingsActivity.this, ZdezMainActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.abs__home:
			Intent intent2 = new Intent();
			intent2.setClass(SettingsActivity.this, ZdezMainActivity.class);
			startActivity(intent2);
			finish();
			break;
		default:
			break;
		}
		finish();
		return super.onOptionsItemSelected(item);
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
