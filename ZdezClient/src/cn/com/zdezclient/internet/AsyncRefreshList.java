package cn.com.zdezclient.internet;

import java.lang.reflect.Type;
import java.util.LinkedList;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Adapter;

public class AsyncRefreshList<T> extends AsyncTask<Void, Void, String> {

	private String requestUrl;
	private Context context;
	private Adapter adapter;
	private LinkedList list;

	public AsyncRefreshList(String requestUrl, Context context,
			Adapter adapter, Type T) {
		super();
		this.requestUrl = requestUrl;
		this.context = context;
		this.adapter = adapter;
		this.list = new LinkedList<T>();
	}

	@Override
	protected String doInBackground(Void... params) {
		// 后台耗时操作
		
		return null;
	
		
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}
	
	

}
