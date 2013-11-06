package cn.com.zdezclient.utils;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import cn.com.zdezclient.internet.ZdezHTTPClient;
import cn.com.zdezclient.preference.ZdezPreferences;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 用于侧滑菜单顶部的广告位图片及其URL信息维护更新
 * 
 * @version 1.0
 * @author werther
 */

public class SlideAdUtil {

	private String picSavePath;
	private int picVersion;
	private static String TAG = SlideAdUtil.class.getSimpleName();
	private static boolean DEBUG = ZdezPreferences.getDebug();
	private Context mContext;

	public SlideAdUtil(Context context) {
		this.mContext = context;
	}

	/**
	 * 使用目前客户端上得图片的版本数去跟服务器上得数值对比，查看是否有更新，如果有的话直接取数据回来，如果没有则返回最新提示
	 */
	public void checkNewAdPicFromZdezSite() {
		final int nowAdPicNumber = getNowAdPicNumber(mContext);
		if (DEBUG)
			Log.d(TAG,
					"开始检查侧滑广告图片的更新，现在的number为："
							+ String.valueOf(nowAdPicNumber));
		RequestParams p = new RequestParams();
		p.put("number", nowAdPicNumber);
		ZdezHTTPClient.get(ZdezHTTPClient.ZDEZ_WEBSITE_AD_PIC_UPDATE_URL, p,
				new AsyncHttpResponseHandler() {

					@Override
					public void onStart() {
						if (DEBUG)
							Log.d(TAG, "Start to request the new slide ad pic");
						super.onStart();
					}

					@Override
					public void onFinish() {
						if (DEBUG)
							Log.d(TAG,
									"Finished requesting the new slide ad pic");
						super.onFinish();
					}

					@Override
					public void onSuccess(String content) {
						if (DEBUG)
							Log.d(TAG,
									"Succesed get the new slide ad pic, and the result is:"
											+ content);
						super.onSuccess(content);
					}

					@Override
					public void onFailure(Throwable error, String content) {
						if (DEBUG)
							Log.d(TAG,
									"Failed while request the new slide ad pic, and the reason is :"
											+ content);
						super.onFailure(error, content);
					}

				});
	}

	/**
	 * 从设置文件中取出客户端目前广告图片的版本，若是没有这个设置（之前没有写入过，首次使用等）则默认为0，使用默认的图片
	 * 
	 * @param context
	 * @return
	 */
	private int getNowAdPicNumber(Context context) {
		int number = 0;
		try {
			number = ZdezPreferences.getNowAdPicNumber(PreferenceManager
					.getDefaultSharedPreferences(context));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return number;
	}
}
