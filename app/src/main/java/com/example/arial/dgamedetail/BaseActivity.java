package com.example.arial.dgamedetail;

import android.databinding.ViewDataBinding;

import com.arialyy.frame.core.AbsActivity;

/**
 * Created by lyy on 2016/5/4.
 */
public abstract class BaseActivity<VB extends ViewDataBinding> extends AbsActivity<VB> {
    @Override
    protected void dataCallback(int result, Object data) {

    }
}
