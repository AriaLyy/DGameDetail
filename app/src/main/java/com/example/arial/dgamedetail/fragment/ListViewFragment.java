package com.example.arial.dgamedetail.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.arialyy.frame.temp.ITempView;
import com.arialyy.frame.util.show.L;
import com.example.arial.dgamedetail.MainActivity;
import com.example.arial.dgamedetail.R;
import com.example.arial.dgamedetail.databinding.FragmentScrollviewBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import butterknife.InjectView;

/**
 * Created by lyy on 2016/3/22.
 */
@SuppressLint("ValidFragment")
public class ListViewFragment extends BaseFragment<FragmentScrollviewBinding> {
    @InjectView(R.id.list)
    ListView mList;

    public static ListViewFragment getInstance() {
        return new ListViewFragment();
    }

    private ListViewFragment() {

    }

    @Override
    protected void onDelayLoad() {
        super.onDelayLoad();
        showTempView(ITempView.LOADING);
        SimpleAdapter adapter = new SimpleAdapter(getContext(), getData(), R.layout.item_test, new String[]{"str"}, new int[]{R.id.text});
        mList.setAdapter(adapter);
        mList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    View firstVisibleItemView = mList.getChildAt(0);
                    if (firstVisibleItemView != null && firstVisibleItemView.getTop() == 0) {
                        ((MainActivity) mActivity).setTopState(mList, true);
                    } else {
                        ((MainActivity) mActivity).setTopState(mList, false);
                    }
                } else {
                    ((MainActivity) mActivity).setTopState(mList, false);
                }
            }
        });
        hintTempView(1000);
    }

    private List<Map<String, String>> getData() {
        String[] array = getContext().getResources().getStringArray(R.array.list_data);
        List<Map<String, String>> data = new ArrayList<>();
        for (String str : array) {
            Map<String, String> map = new WeakHashMap<>();
            map.put("str", str);
            data.add(map);
        }
        return data;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_listview;
    }
}
