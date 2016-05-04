package com.example.arial.dgamedetail.fragment;

import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arialyy.frame.core.AbsFragment;

import butterknife.ButterKnife;

/**
 * Created by AriaL on 2016/3/16.
 */
public abstract class BaseFragment<VB extends ViewDataBinding> extends AbsFragment<VB> {

    @Override
    protected void dataCallback(int result, Object obj) {

    }

    @Override
    protected void onDelayLoad() {

    }

    @Override
    protected void init(Bundle savedInstanceState) {

    }
}
