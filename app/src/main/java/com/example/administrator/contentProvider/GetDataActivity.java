package com.example.administrator.contentProvider;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.administrator.contentProvider.core.ProviderCall;

/**
 * Created by yxb on 2018/2/12.
 */

public class GetDataActivity extends Activity{

    public static final String AUTH = "administrator.contentProvider.AProvider";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle result = new ProviderCall.Builder(this, getPackageName() + "."+AUTH).methodName("@").call();
        String data = result.getString("test");
        Log.e("aaa", data);
    }
}
