package com.github.thunder413.netrequestsample;

import android.app.Application;

import com.github.thunder413.netrequest.NetRequest;
import com.github.thunder413.netrequest.NetRequestManager;


public class App extends Application {
    private static App _instance;

    @Override
    public void onCreate() {
        super.onCreate();
        _instance = this;
        NetRequestManager.getInstance().setDebug(true);
    }
    public static App instance(){
        return _instance;
    }
}
