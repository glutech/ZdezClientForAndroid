package cn.com.zdezclient.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {

	private static final String TAG = BootCompleteReceiver.class
			.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "Got you, i will start my service from here");
		Intent toService = new Intent();
		toService.setClass(context, ZdezService.class);
		context.startService(toService);
	}

}
