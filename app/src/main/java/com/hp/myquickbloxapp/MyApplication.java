package com.hp.myquickbloxapp;

import android.content.Context;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.hp.myquickbloxapp.vedio_audio_call.QBResRequestExecutor;
import com.quickblox.auth.session.QBSettings;

public class MyApplication extends MultiDexApplication {
    public static final String TAG = MyApplication.class.getSimpleName();
    public static Context mContext;
    private static MyApplication mInstance;
    public static synchronized MyApplication getInstance() {
        return mInstance;
    }
    public static Context getContext() {
        return mContext;
    }
    //App credentials
    private static final String APPLICATION_ID = "78566";
    private static final String AUTH_KEY = "Ovc8B8V8eTU4njb";
    private static final String AUTH_SECRET = "aYZGcEYXdXSPFaB";
    private static final String ACCOUNT_KEY = "yXuUCgEhxgvKVjBbsuPX";
    public static final String USER_DEFAULT_PASSWORD = "singh@@@@singh";
    private QBResRequestExecutor qbResRequestExecutor;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = this;


        checkAppCredentials();
        initCredentials();
    }

    private void checkAppCredentials() {
        if (APPLICATION_ID.isEmpty() || AUTH_KEY.isEmpty() || AUTH_SECRET.isEmpty() || ACCOUNT_KEY.isEmpty()) {
            throw new AssertionError(getString(R.string.app_name));
        }
    }

    private void initCredentials() {
        QBSettings.getInstance().init(getApplicationContext(), APPLICATION_ID, AUTH_KEY, AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);

    }

    public synchronized QBResRequestExecutor getQbResRequestExecutor() {
        return qbResRequestExecutor == null
                ? qbResRequestExecutor = new QBResRequestExecutor()
                : qbResRequestExecutor;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
