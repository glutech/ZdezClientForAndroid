package cn.com.zdezclient.a_activities;

import cn.com.zdezclient.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class IndexMyActFragment extends Fragment {

    public static IndexMyActFragment newInstance() {
        IndexMyActFragment fragment = new IndexMyActFragment();
        Bundle args = new Bundle();
        // args.putXXX
        fragment.setArguments(args);
        return fragment;
    }

    public IndexMyActFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.a_fragment_activity_empty, container, false);
        return rootView;
    }

}
