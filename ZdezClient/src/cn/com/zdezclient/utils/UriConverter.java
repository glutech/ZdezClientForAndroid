package cn.com.zdezclient.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.util.Log;
import cn.com.zdezclient.db.SchoolMsgDao;
import cn.com.zdezclient.internet.ZdezHTTPClient;
import cn.com.zdezclient.preference.ZdezPreferences;

public class UriConverter {

	private static String TAG = UriConverter.class.getSimpleName();
	private static boolean DEBUG = ZdezPreferences.getDebug();

	/**
	 * 因为服务器发过来的消息内容中的图片使用的是Tomcat服务器的相对地址，所以不会有 Host ip 和 port number
	 * 所以需要手动遍历添加hostip+number+相对地址
	 * 
	 * @param content
	 * @return
	 */
	public static String replaceSrc(String content) {
		Document document = Jsoup.parse(content);

		Elements imgs = document.getElementsByTag("img");
		// 处理content中引用服务器本地图片的地址
		for (Element img : imgs) {
			// 处理img元素中影响排版的属性
			if (img.parent().hasAttr("style")) {
				if (DEBUG)
					Log.d(TAG, "图片经过一键排版，会造成显示错误，在此去掉这个属性");
				img.parent().removeAttr("style");
			} else {
				if (DEBUG)
					Log.d(TAG, "图片没有经过一键排版，或者识别不出这个属性");
			}

			if (DEBUG)
				Log.d(TAG, "替换内容中的服务器图片地址,替换之前:" + img.attr("src"));
			if (img.attr("src").startsWith(
					ZdezHTTPClient.RELATIVE_IMAG_PATH_HEAD)) {
				img.attr("src",
						ZdezHTTPClient.HOSTNAME_AND_PORT + img.attr("src"));
				if (DEBUG)
					Log.d(TAG, "替换内容中的服务器图片地址,替换之后:" + img.attr("src"));
			}
		}

		Elements as = document.getElementsByTag("a");
		// 处理content中引用服务器本地的文件地址
		for (Element a : as) {
			if (DEBUG)
				Log.d(TAG, "替换内容中的服务器链接地址,替换之前:" + a.attr("href"));
			if (a.attr("href").startsWith(
					ZdezHTTPClient.RELATIVE_FILE_PATH_HEAD)) {
				a.attr("href",
						ZdezHTTPClient.HOSTNAME_AND_PORT + a.attr("href"));
				if (DEBUG)
					Log.d(TAG, "替换内容中的服务器链接地址,替换之后:" + a.attr("href"));
			}
		}

		// System.out.println("After replace:" + document.html());

		String head = "<!DOCTYPE html><html><head><meta charset=\"utf-8\"><meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge,chrome=1\"><style type=\"text/css\">p{word-break:break-all;}</style></head><body>";
		StringBuilder sb = new StringBuilder(head);
		String html = document.html();
		sb.append(html);
		String tail = "</body></html>";
		sb.append(tail);
		if (DEBUG)
			Log.d(TAG, "The converted html is:" + html);
		String result = sb.toString();
		if (DEBUG)
			Log.d(TAG, "After add css : " + result);
		return result;
	}

	/**
	 * 与内容中的图片，超链接一样，封面图片的地址也需要进行ip+portNumber处理
	 * 
	 * @param path
	 * @return
	 */
	public static String replaceCoverPath(String path) {
		if (DEBUG)
			Log.d(TAG, "替换封面图片链接地址,替换之前:" + path);
		if (path.startsWith(ZdezHTTPClient.RELATIVE_IMAG_PATH_HEAD)) {
			// 如果是服务器本地图片，而不是远程引用的图片，则添加服务器请求地址
			path = ZdezHTTPClient.HOSTNAME_AND_PORT + path;
		}

		if (DEBUG)
			Log.d(TAG, "替换封面图片链接地址,替换之后:" + path);
		return path;
	}

}
