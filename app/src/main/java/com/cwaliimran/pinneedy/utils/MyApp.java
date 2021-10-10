package com.cwaliimran.pinneedy.utils;


import android.app.Application;

import com.cwaliimran.pinneedy.models.ModelUser;
import com.google.gson.Gson;

public class MyApp extends Application {
    public static MyApp myApp;
    public static Shared shared;

    public MyApp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        shared = new Shared(this);

    }

    public static MyApp getInstance() {
        return myApp;
    }

    //get current user
    public static ModelUser getAppUser() {
        Gson gson = new Gson();
        return gson.fromJson(MyApp.shared.getString(AppConstants.CURRENT_USER), ModelUser.class);
    }


}
