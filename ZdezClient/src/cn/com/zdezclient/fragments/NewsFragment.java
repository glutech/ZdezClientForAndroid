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
import cn.com.zdezclient.activities.NewsWebViewActivity;
import cn.com.zdezclient.adapter.NewsListAdapter;
import cn.com.zdezclient.db.NewsDao;
import cn.com.zdezclient.internet.ZdezHTTPClient;
import cn.com.zdezclient.preference.ZdezPreferences;
import cn.com.zdezclient.types.NewsVo;
import cn.com.zdezclient.utils.ToastUtil;
import cn.com.zdezclient.utils.UriConverter;
<<<<<<< HEAD
import cn.com.zdezclient.utils.ZdezCharsetUtil;
=======
>>>>>>> 03b4d0d7f0af036d634c6948ad027e3ca367c378

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 主界面显示的第一个界面，新闻资讯栏目
 * 
 * @author werther
 * 
 */
public class NewsFragment extends
		PullToRefreshBaseListFragment<PullToRefreshListView> {

	private static SharedPreferences prefs;
	private LinkedList<NewsVo> newsList;
	private PullToRefreshListView mPTRNewsListView;
	private NewsDao newsDao;
	private TextView countV;
	private int unreadCount = 0;
	private final static boolean DEBUG = ZdezPreferences.getDebug();
	private static String TAG = NewsFragment.class.getSimpleName();
	// 使用SchoolMsg的Adapter来充装NewsAdapter
	private NewsListAdapter adapter;
	private int retainPosition = 0;
	private int tailPositionNewsId = -1;
	private boolean isResumeFlag = true;
<<<<<<< HEAD
=======
	private View header;
>>>>>>> 03b4d0d7f0af036d634c6948ad027e3ca367c378

	protected PullToRefreshListView onCreatePullToRefreshListView(
			LayoutInflater inflater, Bundle savedInstanceState) {
		prefs = ((ZdezApplication) getActivity().getApplication()).getPrefs();
		if (DEBUG)
			Log.d(TAG, "In NewsFragment, Start onCreateView() method");
		mPTRNewsListView = (PullToRefreshListView) inflater.inflate(
				R.layout.fragment_news_list, null);
<<<<<<< HEAD
=======
		header = inflater.inflate(R.layout.header_of_outline, null);
>>>>>>> 03b4d0d7f0af036d634c6948ad027e3ca367c378
		mPTRNewsListView.setMode(Mode.BOTH);

		return mPTRNewsListView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// 在Activity创建之初初始化smd
		super.onActivityCreated(savedInstanceState);
		if (DEBUG)
			Log.d(TAG, "start the onActivityCreated method in NewsFragment");
		newsDao = new NewsDao(getActivity());
		setListenerOnRefresh();
		new LoadListFromDBAndRefresh().execute();
		isResumeFlag = false;
	}

	private void updateNewsList() {
		RequestParams params = new RequestParams();
		if (DEBUG)
			Log.d(TAG,
					"getted user_id from preference: "
							+ ZdezPreferences.getUserId(prefs));
		params.put("user_id", ZdezPreferences.getUserId(prefs));
		ZdezHTTPClient.get(ZdezHTTPClient.GET_UPDATE_NEWS_SERVLET_NAME, params,
				new AsyncHttpResponseHandler() {

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
						mPTRNewsListView.onRefreshComplete();
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
<<<<<<< HEAD

						String resultUTF8 = ZdezCharsetUtil.toUTF8Str(arg0);
						if (DEBUG) {
							Log.d(TAG, "使用String构造转码（utf-8）之后的结果:" + resultUTF8);
						}

						// 开始处理跟新得到的数据
						final Gson gson = new Gson();
						NewsVo[] latestNews = gson.fromJson(resultUTF8,
=======
						// 开始处理跟新得到的数据
						final Gson gson = new Gson();
						NewsVo[] latestNews = gson.fromJson(arg0,
>>>>>>> 03b4d0d7f0af036d634c6948ad027e3ca367c378
								NewsVo[].class);
						if (DEBUG)
							Log.d(TAG,
									"下拉刷新取得的新闻资讯的数组：" + latestNews.toString());

						if (latestNews != null && latestNews.length > 0) {
							for (int i = latestNews.length - 1; i >= 0; i--) {
								NewsVo news = latestNews[i];
								news.setCoverPath(UriConverter
										.replaceCoverPath(news.getCoverPath()));
								news.setContent(UriConverter.replaceSrc(news
										.getContent()));
								newsList.addFirst(news);
							}
							// 通知列表数据有更新，需要刷新
							adapter.notifyDataSetChanged();

							LinkedList<NewsVo> latestNewsList = new LinkedList<NewsVo>();
							latestNewsList.addAll(Arrays.asList(latestNews));

							// 存入
							newsDao.createNewsList(latestNewsList);

							// 收到确认
							Zdez.acknowledgeNews(newsList, prefs);

							// 提示刷新了几条新的信息
							ToastUtil.showShortToast(getActivity(), "加载了"
									+ latestNewsList.size() + "条新信息");
							// Toast.makeText(
							// (ZdezApplication) getActivity()
							// .getApplication(),
							// "加载了" + latestNewsList.size() + "条新信息",
							// Toast.LENGTH_SHORT).show();
							setUnreadCountBadge();
							// Toast toast = Toast.makeText(getActivity(), "加载了"
							// + latestNewsList.size() + "条新信息",
							// Toast.LENGTH_SHORT);
							// toast.setGravity(Gravity.FILL_HORIZONTAL, 0, 0);
							// toast.show();
						} else if ("[]".equals(arg0)) {
							// Toast.makeText(
							// (ZdezApplication) getActivity()
							// .getApplication(), "没有新信息",
							// Toast.LENGTH_SHORT).show();
							ToastUtil.showShortToast(getActivity(), "没有新信息");
							// Toast toast = Toast.makeText(
							// (ZdezApplication) getActivity()
							// .getApplication(), "没有新信息",
							// Toast.LENGTH_SHORT);
							// toast.setGravity(Gravity.FILL_HORIZONTAL, 0, 0);
							// toast.show();
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
			AsyncTask<Void, Void, LinkedList<NewsVo>> {

		@Override
		protected LinkedList<NewsVo> doInBackground(Void... params) {
			LinkedList<NewsVo> list = newsDao.getPagedNewsList(
					tailPositionNewsId,
					ZdezPreferences.NUMBER_OF_EACH_LOAD_IN_LIST);
			if (list != null && list.size() > 0) {
				tailPositionNewsId = list.getLast().getId();
				if (DEBUG) {
					Log.d(TAG, "上拉加载更多信息， 取到的20条信息中，最新的是：id:"
							+ list.getFirst().getId() + ", date:"
							+ list.getFirst().getDate() + ", MsgTitle:"
							+ list.getFirst().getTitle());
					Log.d(TAG, "最后的信息是：id:" + list.getLast().getId()
							+ ", Date: " + list.getLast().getDate() + ",Title:"
							+ list.getLast().getTitle());
				}

				return list;
			}
			return null;
		}

		@Override
		protected void onPostExecute(LinkedList<NewsVo> result) {
			if (null == result) {
				// 数据库里没有更多信息，应该就是调用这个了
				mPTRNewsListView.onRefreshComplete();
				// Toast.makeText(
				// (ZdezApplication) getActivity().getApplication(),
				// "没有更多信息", Toast.LENGTH_SHORT).show();
				ToastUtil.showShortToast(getActivity(), "没有更多信息");
			} else if (result.size() == 0) {
				// 而不是这个
				mPTRNewsListView.onRefreshComplete();
				// Toast.makeText(
				// (ZdezApplication) getActivity().getApplication(),
				// "没有更多信息", Toast.LENGTH_SHORT).show();
				ToastUtil.showShortToast(getActivity(), "没有更多信息");
			} else {
				newsList.addAll(result);
				adapter.notifyDataSetChanged();
				mPTRNewsListView.onRefreshComplete();
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

	private class LoadListFromDB extends
			AsyncTask<Void, Void, LinkedList<NewsVo>> {

		@Override
		protected LinkedList<NewsVo> doInBackground(Void... params) {
			if (DEBUG)
				Log.d(TAG,
						"Do this in background and ------------------------------------------------------------ this is no refresh");

			if (newsList == null || newsList.size() == 0) {
				// 第一次加载的时候，只加载20条
				newsList = newsDao.getPagedNewsList(-1,
						ZdezPreferences.NUMBER_OF_EACH_LOAD_IN_LIST);
			} else {
				// 为了重新加载未读的状态，所以必须从数据库中再加载一次，这次的数量是在阅读内容之前的列表已有的数目
				if (DEBUG)
					Log.d(TAG, "Current MsgList size:" + newsList.size());
				newsList = newsDao.getPagedNewsList(-1, newsList.size());
			}

			if (newsList != null && newsList.size() > 0) {
				tailPositionNewsId = newsList.getLast().getId();
				if (DEBUG) {
					Log.d(TAG, "最初加载信息，取到的20条信息中，最新的是：id:"
							+ newsList.getFirst().getId() + ", date:"
							+ newsList.getFirst().getDate() + ", MsgTitle:"
							+ newsList.getFirst().getTitle());
					Log.d(TAG, "最后的信息是：id:" + newsList.getLast().getId()
							+ ", Date: " + newsList.getLast().getDate()
							+ ",Title:" + newsList.getLast().getTitle());
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(LinkedList<NewsVo> result) {
			if (DEBUG)
				Log.d(TAG,
						"And now I can show the list------------------------------------------------------------");

			adapter = new NewsListAdapter(getActivity(),
					R.layout.list_item_for_news, newsList);
			setListAdapter(adapter);

			if (DEBUG)
				Log.d(TAG, "Current retainPosition:" + retainPosition);
			getListView().setSelectionFromTop(retainPosition, 0);

			// 完成列表刷新之后，设置未读条数
			// setUnreadCountBadge();
			super.onPostExecute(result);
		}

	}

	private class LoadListFromDBAndRefresh extends
			AsyncTask<Void, Void, LinkedList<NewsVo>> {

		@Override
		protected LinkedList<NewsVo> doInBackground(Void... params) {
			if (DEBUG)
				Log.d(TAG,
						"Do this in background and ------------------------------------------------------------ and refresh later");

			if (newsList == null || newsList.size() == 0) {
				// 第一次加载的时候，只加载20条
				newsList = newsDao.getPagedNewsList(-1,
						ZdezPreferences.NUMBER_OF_EACH_LOAD_IN_LIST);
			} else {
				// 为了重新加载未读的状态，所以必须从数据库中再加载一次，这次的数量是在阅读内容之前的列表已有的数目
				if (DEBUG)
					Log.d(TAG, "Current MsgList size:" + newsList.size());
				newsList = newsDao.getPagedNewsList(-1, newsList.size());
			}

			if (newsList != null && newsList.size() > 0) {
				tailPositionNewsId = newsList.getLast().getId();
				if (DEBUG) {
					Log.d(TAG, "最初加载信息，取到的20条信息中，最新的是：id:"
							+ newsList.getFirst().getId() + ", date:"
							+ newsList.getFirst().getDate() + ", MsgTitle:"
							+ newsList.getFirst().getTitle());
					Log.d(TAG, "最后的信息是：id:" + newsList.getLast().getId()
							+ ", Date: " + newsList.getLast().getDate()
							+ ",Title:" + newsList.getLast().getTitle());
				}
			}

			return null;
		}

		@Override
		protected void onPostExecute(LinkedList<NewsVo> result) {
			if (DEBUG)
				Log.d(TAG,
						"And now I can show the list------------------------------------------------------------");

			adapter = new NewsListAdapter(getActivity(),
					R.layout.list_item_for_news, newsList);
			setListAdapter(adapter);
			if (newsList == null || newsList.isEmpty()) {
				updateNewsList();
			}

			if (DEBUG)
				Log.d(TAG, "Current retainPosition:" + retainPosition);
			getListView().setSelectionFromTop(retainPosition, 0);
			// 执行自动下拉刷新操作
			mPTRNewsListView.setRefreshing();

			// 完成列表刷新之后，设置未读条数
			// setUnreadCountBadge();
			super.onPostExecute(result);
		}

	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent toShowNewsContent = new Intent();
		Bundle bundle = new Bundle();
		NewsVo news = (NewsVo) l.getItemAtPosition(position);
		bundle.putString("id", String.valueOf(news.getId()));
		bundle.putString("title", news.getTitle());
		bundle.putString("content", news.getContent());
		bundle.putString("date", news.getDate());
		toShowNewsContent.putExtras(bundle);
		isResumeFlag = true;

		toShowNewsContent.setClass(getActivity(), NewsWebViewActivity.class);
		startActivity(toShowNewsContent);
	}

	@Override
	public void onPause() {
		if (DEBUG)
			Log.d(TAG, "onPause");
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
		countV = (TextView) getActivity().findViewById(R.id.badge_news);
		if (DEBUG)
			Log.d(TAG,
					"I am the Setter of unreads in the NewsFragment NewsUnreadBadge object:"
							+ countV);
		unreadCount = newsDao.getUnreadNewsCount();
		if (unreadCount > 0) {
			countV.setText(String.valueOf(unreadCount));
		} else {
			countV.setVisibility(View.GONE);
		}
	}

	private void setListenerOnRefresh() {
		// 设置刷新监听
		mPTRNewsListView
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
						if (DEBUG)
							Log.d(TAG, "start to set a pulllllllllllllllll");

						// 执行下拉刷新操作
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								updateNewsList();
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

						// 执行上拉刷新操作
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
