package cn.com.zdezclient;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ActivityContrl {
	private static List<Activity> activityList = new ArrayList<Activity>();

	public static void remove(Activity activity) {
		activityList.remove(activity);
	}

	public static void add(Activity activity) {
		activityList.add(activity);
	}

	public static void finishProgram() {
		for (Activity activity : activityList) {
			if (null != activity) {
				activity.finish();
			}
		}
		android.os.Process.killProcess(android.os.Process.myPid());
	}
}
