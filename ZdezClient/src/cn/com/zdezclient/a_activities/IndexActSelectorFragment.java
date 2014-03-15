package cn.com.zdezclient.a_activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import cn.com.zdezclient.R;

/**
 * 首页上的校园活动Fragment
 */
public class IndexActSelectorFragment extends Fragment {

    public static IndexActSelectorFragment newInstance() {
        IndexActSelectorFragment fragment = new IndexActSelectorFragment();
        Bundle args = new Bundle();
        // args.putXXX
        fragment.setArguments(args);
        return fragment;
    }

    public IndexActSelectorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.a_fragment_activity_query, container, false);
        ListView lv = (ListView) rootView.findViewById(R.id.a_activity_list_query);
        List<Object> items = new ArrayList<Object>();
        for (int i = 0; i < 5; i++) {
            items.add(new Object());
        }
        lv.setAdapter(new ActListAdapter(getActivity(), R.layout.a_list_item_activity_list, items));
        lv.setItemsCanFocus(false);
        lv.setClickable(true);
        return rootView;
    }

    private static class ActListAdapter extends ArrayAdapter<Object> {

        private Context context;

        public ActListAdapter(Context context, int resource, List<Object> objects) {
            super(context, resource, objects);
            this.context = context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                return View.inflate(context, R.layout.a_list_item_activity_list, null);
            } else {
                return convertView;
            }
        }
    }
}