package cn.com.zdezclient.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import cn.com.zdezclient.R;
import cn.com.zdezclient.layouts.ZdezMsgItem;
import cn.com.zdezclient.types.ZdezMsgVo;

public class ZdezMsgListAdapter extends ArrayAdapter<ZdezMsgVo> {
	private Context context;

	public ZdezMsgListAdapter(Context context, int textViewResourceId,
			List<ZdezMsgVo> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ZdezMsgItem v;
		// if (null == convertView) {
		// v = (ZdezMsgItem) View.inflate(context,
		// R.layout.list_item_for_zdezmsg, null);
		// } else {
		// v = (ZdezMsgItem) convertView;
		// }
		
		v = (ZdezMsgItem) View.inflate(context,
				R.layout.list_item_for_zdezmsg, null);

		v.setZdezMsgItem(getItem(position));

		return v;
	}

}
