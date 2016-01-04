package com.itemstudio.luen.model;

import android.graphics.drawable.Drawable;

public class AppItem {

    public String appName;
    public String packageName;

    public Drawable appIcon;

    public String getAppName() {
        return appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return appIcon;
    }
}