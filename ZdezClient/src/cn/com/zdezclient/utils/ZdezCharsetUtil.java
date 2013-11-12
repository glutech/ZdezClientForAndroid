package cn.com.zdezclient.utils;

import java.io.UnsupportedEncodingException;

public class ZdezCharsetUtil {

	public static String toUTF8Str(String str) {
		String result = null;
		try {
			result = new String(str.getBytes("UTF-8"), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		if (null == result)
			return str;

		return result;
	}
}
