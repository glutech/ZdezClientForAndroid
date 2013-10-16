package cn.com.zdezclient.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.util.Log;

public class ConnectivityChangeReceiver extends BroadcastReceiver {

	private static final String TAG = ConnectivityChangeReceiver.class
			.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		State cellurState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		Log.d(TAG, "State of wifi:" + wifiState + "and mobile:" + cellurState);

		if (wifiState == State.CONNECTED || cellurState == State.CONNECTED) {
			Log.d(TAG, "Got network connected");
			Intent toService = new Intent();
			toService.setClass(context, ZdezService.class);
			context.startService(toService);

		} else if (wifiState != State.CONNECTED
				&& cellurState != State.CONNECTED) {
			Log.d(TAG, "Network Disconnected, 先把服务ZdezService干掉");
			Intent toService = new Intent();
			toService.setClass(context, ZdezService.class);
			context.stopService(toService);
		}
	}

}
