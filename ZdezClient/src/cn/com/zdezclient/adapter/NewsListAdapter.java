package cn.com.zdezclient.adapter;

import java.util.List;

import cn.com.zdezclient.R;
import cn.com.zdezclient.layouts.NewsListItem;
import cn.com.zdezclient.types.NewsVo;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class NewsListAdapter extends ArrayAdapter<NewsVo> {

	private Context context;

	public NewsListAdapter(Context context, int textViewResourceId,
			List<NewsVo> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		NewsListItem v;
		v = (NewsListItem) View.inflate(context, R.layout.list_item_for_news,
				null);

		v.setNewsItem(getItem(position));

		return v;
	}

}
