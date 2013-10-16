package cn.com.zdezclient.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.com.zdezclient.R;
import cn.com.zdezclient.ZdezApplication;
import cn.com.zdezclient.preference.ZdezPreferences;

import com.actionbarsherlock.app.SherlockFragment;

public class SlidingFragment extends SherlockFragment {
	private SharedPreferences prefs;
	private static final boolean DEBUG = ZdezPreferences.getDebug();
	private static String TAG = SlidingFragment.class.getSimpleName();
	// private TextVi zdezUnreadsV;
	// private int schoolMsgUnreads = 0;
	// private int newsUnreads = 0;
	// private int zdezUnreads = 0;
	// private ZdezMsgDao zmd;
	// private NewsDao newsDao;
	// private SchoolMsgDao smd;ew newsUnreadsV;
	// private TextView schoolMsgUnreadsV;
	// private TextView
	TextView userNameV;
	private View slideMenuV;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (DEBUG)
			Log.d(TAG, "VvvvvvvvvvvSlidingFragment onCreadtedView got invoked");

		// zmd = new ZdezMsgDao(getActivity());
		// smd = new SchoolMsgDao(getActivity());
		// newsDao = new NewsDao(getActivity());
		slideMenuV = inflater.inflate(R.layout.fragment_slidemenu, null);
		// newsUnreadsV = (TextView) slideMenuV.findViewById(R.id.badge_news);
		// schoolMsgUnreadsV = (TextView)
		// slideMenuV.findViewById(R.id.badge_msg);
		// zdezUnreadsV = (TextView) slideMenuV.findViewById(R.id.badge_zdez);
		// updateUnreadBadge();

		userNameV = (TextView) slideMenuV.findViewById(R.id.slide_title);

		return slideMenuV;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		if (DEBUG)
			Log.d(TAG,
					"Aaaaaaaaa SlidingFragment onActivityCreated got invoked");
		prefs = ((ZdezApplication) getActivity().getApplication()).getPrefs();
		String userName = ZdezPreferences.getShowName(prefs);
		userNameV.setText(userName);
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		if (DEBUG)
			Log.d(TAG, "onResume start to update unread");
		// updateUnreadBadge();
		super.onResume();
	}

	/**
	 * 更新在sliding-menu中的每一项的未读条数
	 */
	// private void updateUnreadBadge() {
	// if (DEBUG)
	// Log.d(TAG, "NewsUnreadBadge object getted in ZdezMainActivity:"
	// + newsUnreadsV);
	// if (DEBUG)
	// Log.d(TAG, "schoolMsgUnreadsV object getted in ZdezMainActivity:"
	// + schoolMsgUnreadsV);
	// if (DEBUG)
	// Log.d(TAG, "zdezUnreadsV object getted in ZdezMainActivity:"
	// + zdezUnreadsV);
	// schoolMsgUnreads = smd.getUnreadSchoolMsgCount();
	// newsUnreads = newsDao.getUnreadNewsCount();
	// zdezUnreads = zmd.getUnreadZdezCount();
	// // 新闻未读
	// if (newsUnreads > 0) {
	// newsUnreadsV.setText(String.valueOf(newsUnreads));
	// } else {
	// newsUnreadsV.setVisibility(View.GONE);
	// }
	//
	// // 学校通知未读
	// if (schoolMsgUnreads > 0) {
	// schoolMsgUnreadsV.setText(String.valueOf(schoolMsgUnreads));
	// } else {
	// schoolMsgUnreadsV.setVisibility(View.GONE);
	// }
	//
	// // 找得着未读
	// if (zdezUnreads > 0) {
	// zdezUnreadsV.setText(String.valueOf(zdezUnreads));
	// } else {
	// zdezUnreadsV.setVisibility(View.GONE);
	// }
	//
	// }
}
