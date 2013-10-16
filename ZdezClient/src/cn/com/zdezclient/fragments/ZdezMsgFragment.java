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
import cn.com.zdezclient.activities.ZdezMsgWebViewActivity;
import cn.com.zdezclient.adapter.ZdezMsgListAdapter;
import cn.com.zdezclient.db.ZdezMsgDao;
import cn.com.zdezclient.internet.ZdezHTTPClient;
import cn.com.zdezclient.preference.ZdezPreferences;
import cn.com.zdezclient.types.ZdezMsgVo;
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

public class ZdezMsgFragment extends
		PullToRefreshBaseListFragment<PullToRefreshListView> {

	private static SharedPreferences prefs;
	private LinkedList<ZdezMsgVo> zdezList;
	private PullToRefreshListView mPTRZdezListView;
	private ZdezMsgDao zmd;
	private int unreadCount = 0;
	private TextView countV;
	private ZdezMsgListAdapter adapter;
	private final static boolean DEBUG = ZdezPreferences.getDebug();
	private static String TAG = ZdezMsgFragment.class.getSimpleName();
	private int retainPosition = 0;
	private int tailPositionZdezId = -1;
	private boolean isResumeFlag = true;

	@Override
	protected PullToRefreshListView onCreatePullToRefreshListView(
			LayoutInflater inflater, Bundle savedInstanceState) {
		prefs = ((ZdezApplication) getActivity().getApplication()).getPrefs();
		mPTRZdezListView = (PullToRefreshListView) inflater.inflate(
				R.layout.fragment_zdezmsg_list, null);
		mPTRZdezListView.setMode(Mode.BOTH);
		return mPTRZdezListView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		zmd = new ZdezMsgDao(getActivity());

		setListenerForRefresh();
		new LoadListFromDBAnRefresh().execute();
		isResumeFlag = false;
	}

	public void updateZdezMsgList() {
		RequestParams params = new RequestParams();
		if (DEBUG)
			Log.d(TAG,
					"getted user_id from preference: "
							+ ZdezPreferences.getUserId(prefs));
		params.put("user_id", ZdezPreferences.getUserId(prefs));
		ZdezHTTPClient.get(ZdezHTTPClient.GET_UPDATE_ZDEZMSG_SERVLET_NAME,
				params, new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(Throwable arg0, String arg1) {
						// Toast.makeText(
						// (ZdezApplication) getActivity()
						// .getApplication(), "网络错误，请检查网络连接",
						// Toast.LENGTH_SHORT).show();
						try {
							ToastUtil.showShortToast(getActivity(),
									"网络错误，请检查网络连接");
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (DEBUG)
							Log.d(TAG, "Failure at update news request: "
									+ arg1);
						super.onFailure(arg0, arg1);
					}

					@Override
					public void onFinish() {
						if (DEBUG)
							Log.d(TAG, "Finish on News update request");
						// 请求完成，完成刷新操作
						mPTRZdezListView.onRefreshComplete();
						super.onFinish();
					}

					@Override
					public void onStart() {
						if (DEBUG)
							Log.d(TAG, "Start update news request");
						super.onStart();
					}

					@Override
					public void onSuccess(String arg0) {
						if (DEBUG)
							Log.d(TAG,
									"Success on update news request, results is : "
											+ arg0);

						String resultUTF8 = ZdezCharsetUtil.toUTF8Str(arg0);
						if (DEBUG) {
							Log.d(TAG, "使用String构造转码（utf-8）之后的结果:" + resultUTF8);
						}

						// 开始处理跟新得到的数据
						final Gson gson = new Gson();
						ZdezMsgVo[] latestZdezs = gson.fromJson(resultUTF8,
								ZdezMsgVo[].class);
						if (DEBUG)
							Log.d(TAG,
									"下拉刷新取得的新闻资讯的数组：" + latestZdezs.toString());

						if (latestZdezs != null && latestZdezs.length > 0) {
							for (int i = latestZdezs.length - 1; i >= 0; i--) {
								ZdezMsgVo zdez = latestZdezs[i];
								zdez.setCoverPath(UriConverter
										.replaceCoverPath(zdez.getCoverPath()));
								zdez.setContent(UriConverter.replaceSrc(zdez
										.getContent()));
								zdezList.addFirst(zdez);
							}
							// 通知列表数据有更新，需要刷新
							adapter.notifyDataSetChanged();

							LinkedList<ZdezMsgVo> latestZdezList = new LinkedList<ZdezMsgVo>();
							latestZdezList.addAll(Arrays.asList(latestZdezs));

							// 存入
							zmd.createZdezMsgList(latestZdezList);

							// 收到确认
							Zdez.acknowledgeZdezMsg(zdezList, prefs);

							// 提示刷新了几条新的信息
							// Toast.makeText(
							// (ZdezApplication) getActivity()
							// .getApplication(),
							// "加载了" + latestZdezList.size() + "条新信息",
							// Toast.LENGTH_SHORT).show();
							ToastUtil.showShortToast(getActivity(), "加载了"
									+ latestZdezList.size() + "条新信息");
							setUnreadCountBadge();
						} else if ("[]".equals(arg0)) {
							// Toast.makeText(
							// (ZdezApplication) getActivity()
							// .getApplication(), "没有新信息",
							// Toast.LENGTH_SHORT).show();
							ToastUtil.showShortToast(getActivity(), "没有新信息");
							if (DEBUG)
								Log.d("SchoolMsgFragment", "Get latest error");
						} else {
							// Toast.makeText(
							// (ZdezApplication) getActivity()
							// .getApplication(), "网络错误，请检查网络连接",
							// Toast.LENGTH_SHORT).show();
							ToastUtil.showShortToast(getActivity(),
									"网络错误，请检查网络连接");
							Log.d(TAG, "Get latest error");
						}

						super.onSuccess(arg0);
					}

				});
	}

	private class LoadMoreData extends
			AsyncTask<Void, Void, LinkedList<ZdezMsgVo>> {

		@Override
		protected LinkedList<ZdezMsgVo> doInBackground(Void... params) {
			LinkedList<ZdezMsgVo> list = zmd.getPagedZdezMsgList(
					tailPositionZdezId,
					ZdezPreferences.NUMBER_OF_EACH_LOAD_IN_LIST);
			if (list != null && list.size() > 0) {
				tailPositionZdezId = list.getLast().getZdezMsgId();
				if (DEBUG) {
					Log.d(TAG, "上拉加载更多信息， 取到的20条信息中，最新的是：id:"
							+ list.getFirst().getZdezMsgId() + ", date:"
							+ list.getFirst().getDate() + ", MsgTitle:"
							+ list.getFirst().getTitle());
					Log.d(TAG, "最后的信息是：id:" + list.getLast().getZdezMsgId()
							+ ", Date: " + list.getLast().getDate() + ",Title:"
							+ list.getLast().getTitle());
				}

				return list;
			}
			return null;
		}

		@Override
		protected void onPostExecute(LinkedList<ZdezMsgVo> result) {
			if (null == result) {
				// 数据库里没有更多信息，应该就是调用这个了
				mPTRZdezListView.onRefreshComplete();
				// Toast.makeText(
				// (ZdezApplication) getActivity().getApplication(),
				// "没有更多信息", Toast.LENGTH_SHORT).show();
				ToastUtil.showShortToast(getActivity(), "没有更多信息");
			} else if (result.size() == 0) {
				// 而不是这个
				mPTRZdezListView.onRefreshComplete();
				// Toast.makeText(
				// (ZdezApplication) getActivity().getApplication(),
				// "没有更多信息", Toast.LENGTH_SHORT).show();
				ToastUtil.showShortToast(getActivity(), "没有更多信息");
			} else {
				zdezList.addAll(result);
				adapter.notifyDataSetChanged();
				mPTRZdezListView.onRefreshComplete();
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

	private class LoadListFromDBAnRefresh extends
			AsyncTask<Void, Void, LinkedList<ZdezMsgVo>> {

		@Override
		protected LinkedList<ZdezMsgVo> doInBackground(Void... params) {
			if (DEBUG)
				Log.d(TAG,
						"Do this in background and ------------------------------------------------------------");

			if (zdezList == null || zdezList.size() == 0) {
				// 第一次加载的时候，只加载20条
				zdezList = zmd.getPagedZdezMsgList(-1,
						ZdezPreferences.NUMBER_OF_EACH_LOAD_IN_LIST);
			} else {
				// 为了重新加载未读的状态，所以必须从数据库中再加载一次，这次的数量是在阅读内容之前的列表已有的数目
				if (DEBUG)
					Log.d(TAG, "Current MsgList size:" + zdezList.size());
				zdezList = zmd.getPagedZdezMsgList(-1, zdezList.size());
			}

			if (zdezList != null && zdezList.size() > 0) {
				tailPositionZdezId = zdezList.getLast().getZdezMsgId();
				if (DEBUG) {
					Log.d(TAG, "最初加载信息，取到的20条信息中，最新的是：id:"
							+ zdezList.getFirst().getZdezMsgId() + ", date:"
							+ zdezList.getFirst().getDate() + ", MsgTitle:"
							+ zdezList.getFirst().getTitle());
					Log.d(TAG, "最后的信息是：id:" + zdezList.getLast().getZdezMsgId()
							+ ", Date: " + zdezList.getLast().getDate()
							+ ",Title:" + zdezList.getLast().getTitle());
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(LinkedList<ZdezMsgVo> result) {
			if (DEBUG)
				Log.d(TAG,
						"And now I can show the list------------------------------------------------------------");
			adapter = new ZdezMsgListAdapter(getActivity(),
					R.layout.list_item_for_zdezmsg, zdezList);
			setListAdapter(adapter);
			if (zdezList == null || zdezList.isEmpty()) {
				updateZdezMsgList();
			}

			if (DEBUG)
				Log.d(TAG, "Current retainPosition:" + retainPosition);
			getListView().setSelectionFromTop(retainPosition, 0);
			// 执行自动下拉刷新
			mPTRZdezListView.setRefreshing();

			// 完成列表刷新之后，设置未读条数
			// setUnreadCountBadge();
			super.onPostExecute(result);
		}

	}

	private class LoadListFromDB extends
			AsyncTask<Void, Void, LinkedList<ZdezMsgVo>> {

		@Override
		protected LinkedList<ZdezMsgVo> doInBackground(Void... params) {
			if (DEBUG)
				Log.d(TAG,
						"Do this in background and ------------------------------------------------------------");

			if (zdezList == null || zdezList.size() == 0) {
				// 第一次加载的时候，只加载20条
				zdezList = zmd.getPagedZdezMsgList(-1,
						ZdezPreferences.NUMBER_OF_EACH_LOAD_IN_LIST);
			} else {
				// 为了重新加载未读的状态，所以必须从数据库中再加载一次，这次的数量是在阅读内容之前的列表已有的数目
				if (DEBUG)
					Log.d(TAG, "Current MsgList size:" + zdezList.size());
				zdezList = zmd.getPagedZdezMsgList(-1, zdezList.size());
			}

			if (zdezList != null && zdezList.size() > 0) {
				tailPositionZdezId = zdezList.getLast().getZdezMsgId();
				if (DEBUG) {
					Log.d(TAG, "最初加载信息，取到的20条信息中，最新的是：id:"
							+ zdezList.getFirst().getZdezMsgId() + ", date:"
							+ zdezList.getFirst().getDate() + ", MsgTitle:"
							+ zdezList.getFirst().getTitle());
					Log.d(TAG, "最后的信息是：id:" + zdezList.getLast().getZdezMsgId()
							+ ", Date: " + zdezList.getLast().getDate()
							+ ",Title:" + zdezList.getLast().getTitle());
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(LinkedList<ZdezMsgVo> result) {
			if (DEBUG)
				Log.d(TAG,
						"And now I can show the list------------------------------------------------------------");
			adapter = new ZdezMsgListAdapter(getActivity(),
					R.layout.list_item_for_zdezmsg, zdezList);
			setListAdapter(adapter);

			if (DEBUG)
				Log.d(TAG, "Current retainPosition:" + retainPosition);
			getListView().setSelectionFromTop(retainPosition, 0);

			// 完成列表刷新之后，设置未读条数
			// setUnreadCountBadge();
			super.onPostExecute(result);
		}

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent toShowNewsContent = new Intent();
		Bundle bundle = new Bundle();
		ZdezMsgVo zdez = (ZdezMsgVo) l.getItemAtPosition(position);
		bundle.putString("id", String.valueOf(zdez.getZdezMsgId()));
		bundle.putString("title", zdez.getTitle());
		bundle.putString("content", zdez.getContent());
		bundle.putString("date", zdez.getDate());
		toShowNewsContent.putExtras(bundle);
		isResumeFlag = true;

		toShowNewsContent.setClass(getActivity(), ZdezMsgWebViewActivity.class);
		startActivity(toShowNewsContent);
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
		countV = (TextView) getActivity().findViewById(R.id.badge_zdez);
		if (DEBUG)
			Log.d(TAG,
					"I am the Setter of unreads in the ZdezMsgFragment ZdezUnreadBadge object:"
							+ countV);
		unreadCount = zmd.getUnreadZdezCount();
		if (unreadCount > 0) {
			countV.setText(String.valueOf(unreadCount));
		} else {
			countV.setVisibility(View.GONE);
		}
	}

	private void setListenerForRefresh() {
		// 设置刷新监听
		mPTRZdezListView
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
								updateZdezMsgList();
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
