package cn.com.zdezclient.internet;

import android.content.Context;
import android.util.Log;
import cn.com.zdezclient.ZdezApplication;
import cn.com.zdezclient.preference.ZdezPreferences;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class ZdezHTTPClient {

	public static final String HOSTNAME_AND_PORT = "http://www.zdez.com.cn:9080";
	public static final String BASE_URL = HOSTNAME_AND_PORT + "/zdezServer/";
	public static final String RELATIVE_IMAG_PATH_HEAD = "/zdezServer/attached/image/";
	public static final String RELATIVE_FILE_PATH_HEAD = "/zdezServer/attached/file/";
	public static final String GET_UPDATE_MSG_SERVLET_NAME = "AndroidClient_GetUpdateSchoolMsg";
	public static final String GET_UPDATE_NEWS_SERVLET_NAME = "AndroidClient_GetUpdateNews";
	public static final String GET_UPDATE_ZDEZMSG_SERVLET_NAME = "AndroidClient_GetUpdateZdezMsg";
	public static final String UPDATE_NEWS_RECEIVED_SERVLET_NAME = "AndroidClient_UpdateNewsReceived";
	public static final String UPDATE_ZDEZ_MSG_RECEIVED_SERVLET_NAME = "AndroidClient_UpdateZdezMsgReceived";
	public static final String UPDATE_MSG_RECEIVED_SERVLET_NAME = "AndroidClient_UpdateSchoolMsgReceived";
	public static final String CHECK_APK_UPDATE_SERVLET_NAME = "AndroidClient_CheckAPKUpdate";
	public static final String LOGIN_SERVLET_NAME = "AndroidClient_StudentLogiCheck";
	public static final String MODIFY_PASSWORD_SERVLET_NAME = "AndroidClient_ModifyPsw";
	public static final String ABOUT_US_HTML_PATH = "http://www.zdez.com.cn/about.html";
	public static final String CRASH_LOG_PATH_SERVLET_NAME = "AndroidClient_CrashsReport";
	public static final String ZDEZ_WEBSITE_URL = "http://www.zdez.cn";
	public static final String BOKE_WEBSITE_URL = "http://www.zdez.com.cn";
	public static final String ZDEZ_WEBSITE_AD_PIC_UPDATE_URL = "http://www.zdez.cn/check_new_ad_for_client";
	public static final String SEND_FEEDBACK_SERVLET_NAME = "AndroidClient_FeedBack";

	private static AsyncHttpClient client = new AsyncHttpClient();

	public static void shutUnfinishedRequest(Context arg0, boolean arg1) {
		if (ZdezPreferences.getDebug())
			Log.d("ZdezHttpClient",
					"Shut the Unfinished reqesut before leave fragment");
		client.cancelRequests(arg0, arg1);
	}

	public static void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		params.put("version_name", ZdezApplication.versionName);
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void getWithoutVersionName(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public static void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		params.put("version_name", ZdezApplication.versionName);
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}

	private static String getAbsoluteUrl(String relativeUrl) {
		Log.d("HTTPClient request url:", BASE_URL + relativeUrl);
		return BASE_URL + relativeUrl;
	}

}
