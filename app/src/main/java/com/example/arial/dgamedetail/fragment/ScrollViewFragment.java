package com.example.arial.dgamedetail.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

import com.example.arial.dgamedetail.MainActivity;
import com.example.arial.dgamedetail.R;
import com.example.arial.dgamedetail.databinding.FragmentScrollviewBinding;

import butterknife.InjectView;

/**
 * Created by lyy on 2016/3/22.
 */
@SuppressLint("ValidFragment")
public class ScrollViewFragment extends BaseFragment<FragmentScrollviewBinding> {
    @InjectView(R.id.sv)
    ScrollView mSv;

    public static ScrollViewFragment getInstance() {
        return new ScrollViewFragment();
    }

    private ScrollViewFragment() {

    }

    @Override
    protected void init(Bundle savedInstanceState) {
        mSv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float y = mSv.getScrollY();
                ((MainActivity) mActivity).setTopState(mSv, -0.999 <= y && y <= 0.001);
                return false;
            }
        });
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_scrollview;
    }
}
