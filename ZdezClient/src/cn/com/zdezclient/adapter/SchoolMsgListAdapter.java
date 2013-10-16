/**
 * 
 */
package cn.com.zdezclient.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import cn.com.zdezclient.R;
import cn.com.zdezclient.layouts.SchoolMsgListItem;
import cn.com.zdezclient.types.SchoolMsgVo;

/**
 * @author werther
 * 
 */
public class SchoolMsgListAdapter extends ArrayAdapter<SchoolMsgVo> {

	private Context context;

	public SchoolMsgListAdapter(Context context, int resource,
			List<SchoolMsgVo> objects) {
		super(context, resource, objects);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SchoolMsgListItem v;

		// if (null == convertView) {
		// v = (SchoolMsgListItem) View.inflate(context,
		// R.layout.list_item, null);
		// } else {
		// v = (SchoolMsgListItem) convertView;
		// }
		// 使用这个方法会造成列表混乱，但按理说可以提高列表性能，算了，有时间再研究

		v = (SchoolMsgListItem) View.inflate(context, R.layout.list_item, null);
		v.setSchoolMsg((SchoolMsgVo) getItem(position));

		return v;
	}

}
