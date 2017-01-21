package com.example.administrator.common.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;

import com.example.administrator.common.application.QuickBaseApplication;

/**
 * Created by Administrator on 2017/1/17.
 */

public abstract class QuickBaseActivity extends AppCompatActivity implements View.OnClickListener{

    protected String TAG = getClass().getSimpleName();
    protected QuickBaseApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = QuickBaseApplication.fromContext(this);
        app.pushActivity(this);
//        EventBus.getDefault().register(this); //事件总线程注册
        initData();
        initView();
    }

    protected abstract void initData();
    protected abstract void initView();

    //简化findViewById();
    protected <T extends View> T $(int id) {
        return (T) findViewById(id);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (app != null) {
            app.popActivity(this);
        }
//        EventBus.getDefault().unregister(this); //事件总线程注销
        super.onDestroy();
    }

}
