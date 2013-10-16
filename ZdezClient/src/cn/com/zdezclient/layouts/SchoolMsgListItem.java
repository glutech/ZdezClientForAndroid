/**
 * 
 */
package cn.com.zdezclient.layouts;

import java.sql.Timestamp;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.zdezclient.R;
import cn.com.zdezclient.types.SchoolMsgVo;
import cn.com.zdezclient.utils.TimeToWords;

import com.loopj.android.image.SmartImageView;

/**
 * @author werther 校园通知列表的条目自定义
 */
public class SchoolMsgListItem extends RelativeLayout {

	private Context context;
	private TextView title;
	private TextView createdTime;
	private TextView sender;
	private SmartImageView cover;
	private static String TAG = SchoolMsgListItem.class.getSimpleName();

	/**
	 * @param context
	 * @param attrs
	 */
	public SchoolMsgListItem(Context arg, AttributeSet attrs) {
		super(arg, attrs);
		context = arg;
	}

	public void setSchoolMsg(SchoolMsgVo msg) {
		findView();
		title.setText(msg.getTitle());
		// 根据消息的未读状态设置不同的字体颜色
		if (msg.getReadStatus() == 0) {
			title.setTextColor(Color.BLUE);
		}

		// 转换时间，设置时间
		TimeToWords ttw = new TimeToWords();
		String[] dateStrs = ttw
				.getWordsOfTime(Timestamp.valueOf(msg.getDate()));
		createdTime.setText(dateStrs[0]);

		// 设置图片，在没有封面时，去掉图片view
		Log.d(TAG, "getted msg coverPath:" + msg.getCoverPath());
		if (null != msg.getCoverPath() && !"".equals(msg.getCoverPath())) {
			// 有封面图片
			Log.d(TAG, "太好了，有图片可以用来做封面;coverPath:" + msg.getCoverPath());
			cover.setImageUrl(msg.getCoverPath());
		} else {
			// 没有封面图片
			// Log.d(TAG, "没有封面图片，开始重新布局");
			// deleteCover();
			cover.setImageResource(R.drawable.ic_launcher);
		}
	}

	private void deleteCover() {
		// removeView(cover);
		cover.setVisibility(GONE);
		// title.setWidth(LayoutParams.MATCH_PARENT);
	}

	private void findView() {
		title = (TextView) findViewById(R.id.title);
		createdTime = (TextView) findViewById(R.id.time);
		cover = (SmartImageView) this.findViewById(R.id.cover);
	}

}
