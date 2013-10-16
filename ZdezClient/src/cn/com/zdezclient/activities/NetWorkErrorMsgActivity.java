package cn.com.zdezclient.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import cn.com.zdezclient.ActivityContrl;
import cn.com.zdezclient.R;
import cn.com.zdezclient.ZdezMainActivity;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class NetWorkErrorMsgActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIcon(
				getResources().getDrawable(R.drawable.arrow_back));
		setContentView(R.layout.activity_net_work_error_msg);
		setTitle("网络连接障碍");
		ActivityContrl.add(this);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.abs__up:
			Intent intent = new Intent();
			intent.setClass(NetWorkErrorMsgActivity.this,
					ZdezMainActivity.class);
			startActivity(intent);
			finish();
			break;
		case R.id.abs__home:
			Intent intent2 = new Intent();
			intent2.setClass(NetWorkErrorMsgActivity.this,
					ZdezMainActivity.class);
			startActivity(intent2);
			finish();
			break;
		default:
			break;
		}
		finish();
		return super.onOptionsItemSelected(item);
	}
}
