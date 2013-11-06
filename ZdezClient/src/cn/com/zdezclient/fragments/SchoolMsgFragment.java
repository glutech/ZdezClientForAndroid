package cn.com.zdezclient.fragments;

import java.util.Arrays;
import java.util.LinkedList;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import cn.com.zdezclient.R;
import cn.com.zdezclient.Zdez;
import cn.com.zdezclient.ZdezApplication;
import cn.com.zdezclient.activities.SchoolMsgWebViewActivity;
import cn.com.zdezclient.adapter.SchoolMsgListAdapter;
import cn.com.zdezclient.db.SchoolMsgDao;
import cn.com.zdezclient.internet.ZdezHTTPClient;
import cn.com.zdezclient.preference.ZdezPreferences;
import cn.com.zdezclient.types.SchoolMsgVo;
import cn.com.zdezclient.utils.ToastUtil;
import cn.com.zdezclient.utils.UriConverter;
import cn.com.zdezclient.utils.ZdezCharsetUtil;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class SchoolMsgFragment extends
		PullToRefreshBaseListFragment<PullToRefreshListView> {

	private LinkedList<SchoolMsgVo> mMsgList;
	private SchoolMsgListAdapter adapter;
	private PullToRefreshListView mPTRSchoolMsgList;
	private SharedPreferences prefs;
	private SchoolMsgDao smd;
	private int unreadCount = 0;
	private TextView countV;
	private final static boolean DEBUG = ZdezPreferences.getDebug();
	private static String TAG = SchoolMsgFragment.class.getSimpleName();
	private int retainPosition = 0;
	private int tailPositionMsgId = -1;
	private boolean isResumeFlag = true;

	@Override
	protected PullToRefreshListView onCreatePullToRefreshListView(
			LayoutInflater inflater, Bundle savedInstanceState) {
		prefs = ((ZdezApplication) getActivity().getApplication()).getPrefs();
		mPTRSchoolMsgList = (PullToRefreshListView) inflater.inflate(
				R.layout.fragment_schoolmsg_list, null);
		// 设置下拉刷新的模式为“支持他头部下拉刷新和尾部上拉加载更多”
		mPTRSchoolMsgList.setMode(Mode.BOTH);
		return mPTRSchoolMsgList;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		smd = new SchoolMsgDao(getActivity());

		setListenerForRefresh();
		new LoadListFromDBAndRefresh().execute();
		isResumeFlag = false;
	}

	private void updateMsgList() {
		RequestParams params = new RequestParams();
		params.put("user_id", ZdezPreferences.getUserId(prefs));
		ZdezHTTPClient.get(ZdezHTTPClient.GET_UPDATE_MSG_SERVLET_NAME, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						System.out.println("~~~~~failure");
						// Toast.makeText((ZdezApplication) getActivity()
						// .getApplication(), "网络错误，请检查网络连接",
						// Toast.LENGTH_SHORT).show();
						try {
							ToastUtil.showShortToast(getActivity(),
									"网络错误，请检查网络连接");
						} catch (Exception e) {
							e.printStackTrace();
						}
						Log.d("SchoolMsgFragment", "Get latest error");

						super.onFailure(arg0, arg1);
					}

					@Override
					public void onFinish() {
						// TODO Auto-generated method stub
						System.out.println("~~~~~finish");
						mPTRSchoolMsgList.onRefreshComplete();
						super.onFinish();
					}

					@Override
					public void onStart() {
						System.out.println("~~~~~start");
						super.onStart();
					}

					@Override
					public void onSuccess(String arg0) {
						String resultUTF8 = ZdezCharsetUtil.toUTF8Str(arg0);
						if (DEBUG) {
							Log.d(TAG, "使用String构造转码（utf-8）之后的结果:" + resultUTF8);
						}

						final Gson gson = new Gson();
						SchoolMsgVo[] latestMsgs = gson.fromJson(resultUTF8,
								SchoolMsgVo[].class);
						Log.d(TAG, "下拉更新得到的新的数组：" + latestMsgs.toString());
						if (latestMsgs != null && latestMsgs.length > 0) {
							for (int i = latestMsgs.length - 1; i >= 0; i--) {
								SchoolMsgVo msg = latestMsgs[i];
								msg.setCoverPath(UriConverter
										.replaceCoverPath(msg.getCoverPath()));
								msg.setContent(UriConverter.replaceSrc(msg
										.getContent()));
								mMsgList.addFirst(msg);
							}
							adapter.notifyDataSetChanged();

							LinkedList<SchoolMsgVo> latestList = new LinkedList<SchoolMsgVo>();
							latestList.addAll(Arrays.asList(latestMsgs));

							// 存入
							smd.createSchoolMsgs(latestList);

							// 返回消息确认
							Zdez.acknowledgeSchoolMsg(latestList, prefs);

							// 提示刷新了几条新的消息
							// Toast.makeText((ZdezApplication) getActivity()
							// .getApplication(),
							// "加载了" + latestMsgs.length + "条新信息",
							// Toast.LENGTH_SHORT).show();
							ToastUtil.showShortToast(getActivity(), "加载了"
									+ latestMsgs.length + "条新信息");
							setUnreadCountBadge();

						} else if ("[]".equals(arg0)) {
							// Toast.makeText((ZdezApplication) getActivity()
							// .getApplication(), "没有新信息",
							// Toast.LENGTH_SHORT).show();
							ToastUtil.showShortToast(getActivity(), "没有新信息");
							Log.d(TAG, "Get no Update from server");
						} else {
							// Toast.makeText(
							// (ZdezApplication) getActivity()
							// .getApplication(), "网络错误，请检查网络连接",
							// Toast.LENGTH_SHORT).show();
							ToastUtil.showShortToast(getActivity(),
									"网络错误，请检查网络连接");
							Log.d(TAG, "Get latest error");
						}
					}

				});
	}

	private class LoadMoreData extends
			AsyncTask<Void, Void, LinkedList<SchoolMsgVo>> {

		@Override
		protected LinkedList<SchoolMsgVo> doInBackground(Void... params) {
			LinkedList<SchoolMsgVo> list = smd.getPagedSchoolMsgList(
					tailPositionMsgId,
					ZdezPreferences.NUMBER_OF_EACH_LOAD_IN_LIST);
			if (list != null && list.size() > 0) {
				tailPositionMsgId = list.getLast().getSchoolMsgId();
				if (DEBUG) {
					Log.d(TAG, "上拉加载更多信息， 取到的20条信息中，最新的是：id:"
							+ list.getFirst().getSchoolMsgId() + ", date:"
							+ list.getFirst().getDate() + ", MsgTitle:"
							+ list.getFirst().getTitle());
					Log.d(TAG, "最后的信息是：id:" + list.getLast().getSchoolMsgId()
							+ ", Date: " + list.getLast().getDate() + ",Title:"
							+ list.getLast().getTitle());
				}

				return list;
			}
			return null;
		}

		@Override
		protected void onPostExecute(LinkedList<SchoolMsgVo> result) {
			if (null == result) {
				// 数据库里没有更多信息，应该就是调用这个了
				mPTRSchoolMsgList.onRefreshComplete();
				// Toast.makeText(
				// (ZdezApplication) getActivity().getApplication(),
				// "没有更多信息", Toast.LENGTH_SHORT).show();
				ToastUtil.showShortToast(getActivity(), "没有更多信息");
			} else if (result.size() == 0) {
				// 而不是这个
				mPTRSchoolMsgList.onRefreshComplete();
				// Toast.makeText(
				// (ZdezApplication) getActivity().getApplication(),
				// "没有更多信息", Toast.LENGTH_SHORT).show();
				ToastUtil.showShortToast(getActivity(), "没有更多信息");
			} else {
				mMsgList.addAll(result);
				adapter.notifyDataSetChanged();
				mPTRSchoolMsgList.onRefreshComplete();
				// Toast.makeText(
				// (ZdezApplication) getActivity().getApplication(),
				// "加载了" + result.size() + "条信息", Toast.LENGTH_SHORT)
				// .show();
				ToastUtil.showShortToast(getActivity(), "加载了" + result.size()
						+ "条信息");
			}

			super.onPostExecute(result);
		}
	}

	private class LoadListFromDBAndRefresh extends
			AsyncTask<Void, Void, LinkedList<SchoolMsgVo>> {

		@Override
		protected LinkedList<SchoolMsgVo> doInBackground(Void... params) {
			if (DEBUG)
				Log.d(TAG,
						"Do this in background and ------------------------------------------------------------");

			if (mMsgList == null || mMsgList.size() == 0) {
				// 第一次加载的时候，只加载20条
				mMsgList = smd.getPagedSchoolMsgList(-1,
						ZdezPreferences.NUMBER_OF_EACH_LOAD_IN_LIST);
			} else {
				// 为了重新加载未读的状态，所以必须从数据库中再加载一次，这次的数量是在阅读内容之前的列表已有的数目
				if (DEBUG)
					Log.d(TAG, "Current MsgList size:" + mMsgList.size());
				mMsgList = smd.getPagedSchoolMsgList(-1, mMsgList.size());
			}

			if (mMsgList != null && mMsgList.size() > 0) {
				tailPositionMsgId = mMsgList.getLast().getSchoolMsgId();
				if (DEBUG) {
					Log.d(TAG, "最初加载信息，取到的20条信息中，最新的是：id:"
							+ mMsgList.getFirst().getSchoolMsgId() + ", date:"
							+ mMsgList.getFirst().getDate() + ", MsgTitle:"
							+ mMsgList.getFirst().getTitle());
					Log.d(TAG, "最后的信息是：id:"
							+ mMsgList.getLast().getSchoolMsgId() + ", Date: "
							+ mMsgList.getLast().getDate() + ",Title:"
							+ mMsgList.getLast().getTitle());
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(LinkedList<SchoolMsgVo> result) {
			if (DEBUG)
				Log.d(TAG,
						"And now I can show the list------------------------------------------------------------");
			adapter = new SchoolMsgListAdapter(getActivity(),
					R.layout.list_item, mMsgList);
			setListAdapter(adapter);
			if (mMsgList == null || mMsgList.isEmpty()) {
				updateMsgList();
			}

			if (DEBUG)
				Log.d(TAG, "Current retainPosition:" + retainPosition);
			getListView().setSelectionFromTop(retainPosition, 0);
			// performPullDownAction();
			mPTRSchoolMsgList.setRefreshing();

			// // 完成列表刷新之后，设置未读条数
			// setUnreadCountBadge();
			super.onPostExecute(result);
		}

	}

	private class LoadListFromDB extends
			AsyncTask<Void, Void, LinkedList<SchoolMsgVo>> {

		@Override
		protected LinkedList<SchoolMsgVo> doInBackground(Void... params) {
			if (DEBUG)
				Log.d(TAG,
						"Do this in background and ------------------------------------------------------------");

			if (mMsgList == null || mMsgList.size() == 0) {
				// 第一次加载的时候，只加载20条
				mMsgList = smd.getPagedSchoolMsgList(-1,
						ZdezPreferences.NUMBER_OF_EACH_LOAD_IN_LIST);
			} else {
				// 为了重新加载未读的状态，所以必须从数据库中再加载一次，这次的数量是在阅读内容之前的列表已有的数目
				if (DEBUG)
					Log.d(TAG, "Current MsgList size:" + mMsgList.size());
				mMsgList = smd.getPagedSchoolMsgList(-1, mMsgList.size());
			}

			if (mMsgList != null && mMsgList.size() > 0) {
				tailPositionMsgId = mMsgList.getLast().getSchoolMsgId();
				if (DEBUG) {
					Log.d(TAG, "最初加载信息，取到的20条信息中，最新的是：id:"
							+ mMsgList.getFirst().getSchoolMsgId() + ", date:"
							+ mMsgList.getFirst().getDate() + ", MsgTitle:"
							+ mMsgList.getFirst().getTitle());
					Log.d(TAG, "最后的信息是：id:"
							+ mMsgList.getLast().getSchoolMsgId() + ", Date: "
							+ mMsgList.getLast().getDate() + ",Title:"
							+ mMsgList.getLast().getTitle());
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(LinkedList<SchoolMsgVo> result) {
			if (DEBUG)
				Log.d(TAG,
						"And now I can show the list------------------------------------------------------------");
			adapter = new SchoolMsgListAdapter(getActivity(),
					R.layout.list_item, mMsgList);
			setListAdapter(adapter);

			if (DEBUG)
				Log.d(TAG, "Current retainPosition:" + retainPosition);
			getListView().setSelectionFromTop(retainPosition, 0);

			// // 完成列表刷新之后，设置未读条数
			// setUnreadCountBadge();
			super.onPostExecute(result);
		}

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent toShowContent = new Intent();
		Bundle bundle = new Bundle();
		SchoolMsgVo msg = (SchoolMsgVo) l.getItemAtPosition(position);
		bundle.putString("id", String.valueOf(msg.getSchoolMsgId()));
		toShowContent.putExtras(bundle);
		isResumeFlag = true;

		toShowContent.setClass(getActivity(), SchoolMsgWebViewActivity.class);
		startActivity(toShowContent);
	}

	@Override
	public void onPause() {
		ZdezHTTPClient.shutUnfinishedRequest(getActivity(), true);
		retainPosition = getListView().getFirstVisiblePosition();
		if (DEBUG)
			Log.d(TAG, "Setted now retain position:" + retainPosition);
		super.onPause();
	}

	@Override
	public void onResume() {
		if (DEBUG)
			Log.d(TAG, "on resume and isresume is :" + isResumeFlag);
		if (isResumeFlag)
			new LoadListFromDB().execute();
		super.onResume();
	}

	public void setUnreadCountBadge() {
		countV = (TextView) getActivity().findViewById(R.id.badge_msg);
		if (DEBUG)
			Log.d(TAG,
					"I am the Setter of unreads in the SchoolMsgFragment MsgUnreadBadge object:"
							+ countV);
		unreadCount = smd.getUnreadSchoolMsgCount();
		if (unreadCount > 0) {
			countV.setText(String.valueOf(unreadCount));
		} else {
			countV.setVisibility(View.GONE);
		}
	}

	private void setListenerForRefresh() {
		// 设置刷新监听
		mPTRSchoolMsgList
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(getActivity()
								.getApplicationContext(), System
								.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						// 执行更新操作
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								updateMsgList();
							}
						}, 500);

					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(getActivity()
								.getApplicationContext(), System
								.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						// Update the LastUpdatedLabel
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						// 执行上拉加载更多地操作
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								new LoadMoreData().execute();
							}
						}, 800);

					}

				});
	}

}
