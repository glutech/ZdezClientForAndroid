package cn.com.zdezclient.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import cn.com.zdezclient.R;
import cn.com.zdezclient.SettingsActivity;
import cn.com.zdezclient.ZdezMainActivity;

public class SlideMenuFragment extends ListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_left_menu, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		TextView tv = new TextView(getActivity());
//		tv.setMinHeight(R.dimen.abs__action_bar_default_height);
//		tv.setBackgroundColor(getResources().getColor(R.color.divider_blue));
//		tv.setText("Hello");
//        getListView().addHeaderView(tv);
		String[] colors = getResources().getStringArray(R.array.menus);
		ArrayAdapter<String> colorAdapter = new ArrayAdapter<String>(
				getActivity(), R.layout.list_item_for_slide_menu,
				android.R.id.text1, colors);
		setListAdapter(colorAdapter);
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Fragment newContent = null;
		Bundle bundle = new Bundle();
		switch (position) {
		case 0:
//			newContent = new NewsFragment();
//			bundle.putString("title", "����");
//			newContent.setArguments(bundle);
			break;
		case 1:
			newContent = new NewsFragment();
			bundle.putString("title", "新闻资讯");
			newContent.setArguments(bundle);
			break;
		case 2:
			newContent = new SchoolMsgFragment();
			bundle.putString("title", "校园通知");
			newContent.setArguments(bundle);
			break;
		case 3:
			newContent = new ColorFragment();
			bundle.putString("title", "找得着");
			newContent.setArguments(bundle);
			break;
		case 4:
			bundle.putString("title", "设置");
			Intent intent = new Intent();
			intent.setClass(getActivity(), SettingsActivity.class);
			startActivity(intent);
			break;
		}
		if (newContent != null)
			switchFragment(newContent);
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof ZdezMainActivity) {
			ZdezMainActivity fca = (ZdezMainActivity) getActivity();
			fca.switchContent(fragment);
		}
		// else if (getActivity() instanceof ResponsiveUIActivity) {
		// ResponsiveUIActivity ra = (ResponsiveUIActivity) getActivity();
		// ra.switchContent(fragment);
		// }
	}
}
