package com.example.administrator.demo;

import android.widget.ImageView;

import com.example.administrator.common.activity.QuickBaseActivity;
import com.example.administrator.imageLoad.ImageLoader;
import com.example.administrator.newappforutils.R;

public class MainActivity extends QuickBaseActivity {

    private String url;
    private ImageView ivTest;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        url = "https://gss1.bdstatic.com/9vo3dSag_xI4khGkpoWK1HF6hhy/baike/w%3D268%3Bg%3D0/sign=92e00c9b8f5494ee8722081f15ce87c3/29381f30e924b899c83ff41c6d061d950a7bf697.jpg";
    }

    @Override
    protected void initView() {
        ivTest = $(R.id.ivTest);
        ivTest.setTag(url);
        ImageLoader.getInstance().bindToView(url, ivTest);
    }

}
