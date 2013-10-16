package cn.com.zdezclient.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import cn.com.zdezclient.ZdezApplication;
import cn.com.zdezclient.preference.ZdezPreferences;

/**
 * Created by werther on 13-5-31.
 */

public class MyZdezClientExceptionHandler implements UncaughtExceptionHandler {

	private UncaughtExceptionHandler defaultUEH;

	private SharedPreferences sp;

	private String localPath;

	private String url;

	/*
	 * if any of the parameters is null, the respective functionality will not
	 * be used
	 */
	public MyZdezClientExceptionHandler(SharedPreferences sp, String localPath,
			String url) {
		if (ZdezPreferences.getDebug())
			Log.d("In Exception handler", "Create a Exception handler");
		this.localPath = localPath;
		this.url = url;
		this.sp = sp;
		this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
	}

	public void uncaughtException(Thread t, Throwable e) {
		String timestamp = new Timestamp(System.currentTimeMillis()).toString();
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		String stacktrace = result.toString();
		printWriter.close();
		String user_id = ZdezPreferences.getUserId(sp);
		String filename = timestamp + "UserID:" + user_id + ".log";

		if (localPath != null) {
			writeToFile(stacktrace, filename);
		}
		if (url != null) {
			sendToServer(stacktrace, filename);
		}
		
		defaultUEH.uncaughtException(t, e);
	}

	private void writeToFile(String stacktrace, String filename) {
		filename = ZdezApplication.getInstance().getFilesDir().getPath()
				.toString()
				+ filename;
		if (ZdezPreferences.getDebug())
			Log.d("In Exception handler", "Write to Local sdcard");
		try {
			BufferedWriter bos = new BufferedWriter(new FileWriter(filename));
			bos.write(stacktrace);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void sendToServer(String stacktrace, String filename) {
		if (ZdezPreferences.getDebug())
			Log.d("In Exception handler", "Send to Server and URL:" + url
					+ ", and filename: " + filename);
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("filename", filename));
		nvps.add(new BasicNameValuePair("stacktrace", stacktrace));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			httpClient.execute(httpPost);
		} catch (IOException e) {
			if (ZdezPreferences.getDebug())
				Log.d("In Exception handler",
						"Send to Server and exception while sending");
			e.printStackTrace();
		}

	}
}
