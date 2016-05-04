package com.example.arial.dgamedetail;

import android.app.Application;

import com.arialyy.frame.core.MVVMFrame;


/**
 * Created by lyy on 2016/5/4.
 */
public class BaseApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        MVVMFrame.init(this);
    }
}
