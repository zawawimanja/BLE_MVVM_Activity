package no.nordicsemi.android.bluetooth;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import androidx.appcompat.app.AppCompatDelegate;

public class BlinkyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        BlinkyApplication.context = getApplicationContext();
        //Added to support vector drawables for devices below android 21
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
    }


    private static Context context;

    public static Context getAppContext() {
        return BlinkyApplication.context;
    }
}