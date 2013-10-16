package cn.com.zdezclient.preference;

import android.content.SharedPreferences;

public class ZdezSettingPreferences {

	public static int getSyncFrequency(SharedPreferences prefs, String key) {
		return prefs.getInt(key, 0);
	}
	
	
}
