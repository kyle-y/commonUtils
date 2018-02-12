package com.example.administrator.sample;

import com.example.administrator.common.application.QuickBaseApplication;
import com.example.administrator.imageLoad.ImageLoader;

/**
 * Created by Administrator on 2017/7/21.
 */

public class App extends QuickBaseApplication{
    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoader.getInstance().init(this);
    }
}
