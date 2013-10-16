package cn.com.zdezclient.preference;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.Log;
import cn.com.zdezclient.Zdez;
import cn.com.zdezclient.types.User;

public class ZdezPreferences {

	private static final String TAG = "ZdezPreferences";
	private static final String STARTUP_SET = "startup_set";
	private static final String PREFERENCE_ID = "id";
	private static final String PREFERENCE_USERNAME = "username";
	private static final String PREFERENCE_PASSWORD = "password";
	private static final String PREFERENCE_NAME = "name";
	private static final String PREFERENCE_DEPARTMENT = "department";
	private static final String PREFERENCE_MAJOR = "major";
	private static final String PREFERENCE_GRADE = "grade";
	private static final String PREFERENCE_LATEST_SCHOOL_MSG_ID = "latest_school_msg_id";
	// private static final String PREFERENCE_BIRTHDATE = "birthdate";
	private static final String PREFERENCE_GENDER = "gender";
	private static final boolean DEBUG = true;
	private static final int KEEP_ALIVE_SECONDS = 20;
	public static final int NUMBER_OF_EACH_LOAD_IN_LIST = 20;
	private static final String RECEIVER_NAME = "cn.com.zdez.pull";

	public static void setupDefaults(SharedPreferences preferences,
			Resources resources) {
		Editor editor = preferences.edit();
		if (!preferences.contains(STARTUP_SET)) {
			editor.putString(STARTUP_SET, "zdez");
		}
		editor.commit();
	}

	public static String getUserId(SharedPreferences prefs) {
		return prefs.getString(PREFERENCE_ID, null);
	}

	public static boolean setUser(Zdez zdez, String result, Editor editor) {
		if (DEBUG)
			Log.d(TAG, "存储用户数据:" + result);

		User user = zdez.convertStudentFromJsonString(result);
		storeUser(editor, user);
		if (!editor.commit()) {
			if (DEBUG)
				Log.d(TAG, "存储用户数据时commit失败");
			return false;
		}
		
		return true;
	}

	public static boolean logoutUser(Editor editor) {
		if (DEBUG)
			Log.d(TAG, "尝试注销登录");
		// 注销登录的一些操作，待完成
		editor.clear();
		editor.commit();
		return true;
	}

	// 存储用户数据到preference中
	private static void storeUser(final Editor editor, User user) {
		if (user != null) {
			editor.putString(PREFERENCE_ID, String.valueOf(user.getId()));
			editor.putString(PREFERENCE_NAME, user.getName());
			editor.putString(PREFERENCE_DEPARTMENT, user.getDepartment());
			editor.putString(PREFERENCE_MAJOR, user.getMajor());
			editor.putString(PREFERENCE_GRADE, user.getGrade());
			editor.putString(PREFERENCE_GENDER, user.getGender());
		} else {
			if (DEBUG)
				Log.d(TAG, "取不到用户的数据");
		}
	}

	// 存储用户的登录信息到preference中
	public static void storeLoginAndPassword(Editor editor, String userName,
			String password) {
		editor.putString(PREFERENCE_USERNAME, userName);
		editor.putString(PREFERENCE_PASSWORD, password);
	}

	public static boolean getDebug() {
		return DEBUG;
	}

	/**
	 * 每次取到新的数据之后要设置这个值，用于记录取到的最新的学校通知的id
	 * 
	 * @param editor
	 * @param id
	 */
	public static void setLatestSchoolMsgId(final Editor editor, int id) {
		editor.putInt(PREFERENCE_LATEST_SCHOOL_MSG_ID, id);
		editor.commit();
	}

	/**
	 * 取得上次请求的最新的校园通知的id
	 * 
	 * @param sp
	 * @return
	 */
	public static int getLatestSchoolMsgId(SharedPreferences sp) {
		return sp.getInt(PREFERENCE_LATEST_SCHOOL_MSG_ID, 0);
	}

	public static int getKeepAliveSeconds(SharedPreferences sp, String key) {
		// 这里的key要尤其注意，需要跟在设置界面xml中使用的key一致，所以统一放在string文件中
		System.out.println("Geted defualt keep alive seconds from xml:"
				+ sp.getString(key, String.valueOf(KEEP_ALIVE_SECONDS)));
		return Integer.valueOf(sp.getString(key,
				String.valueOf(KEEP_ALIVE_SECONDS)));
	}

	public static String getReceiverName() {
		return RECEIVER_NAME;
	}

	// 从用户信息中取出用于显示的用户名，如果有专门用于显示的昵称（而不是登录帐号名）
	// 的话，就用昵称，否则使用账户名称
	public static String getShowName(SharedPreferences prefs) {
		return prefs.getString(PREFERENCE_NAME,
				prefs.getString(PREFERENCE_USERNAME, "没有用户名"));
	}

	public static void setLaunchFromNotificationTag(Editor e, boolean b) {
		e.putBoolean("from_noti", b);
		e.commit();
	}

}
