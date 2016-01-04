package com.itemstudio.luen;


import android.app.Application;

import com.bettervectordrawable.Convention;
import com.bettervectordrawable.VectorDrawableCompat;
import com.tumblr.remember.Remember;

public class LuenApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Remember.init(getApplicationContext(), "com.itemstudio.luen");

        int[] ids = VectorDrawableCompat.findVectorResourceIdsByConvention(getResources(), R.drawable.class, Convention.RESOURCE_NAME_HAS_VECTOR_SUFFIX);
        VectorDrawableCompat.enableResourceInterceptionFor(getResources(), ids);
    }
}
