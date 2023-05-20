package com.hcmus.project_21424074_21424092_21424094.activities.mainActivities;

import android.app.Application;

import com.hcmus.project_21424074_21424092_21424094.activities.mainActivities.data_favor.DataLocalManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DataLocalManager.init(getApplicationContext());
    }
}
