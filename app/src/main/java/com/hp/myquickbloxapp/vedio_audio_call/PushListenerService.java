package com.hp.myquickbloxapp.vedio_audio_call;

import android.util.Log;

import androidx.annotation.Keep;

import com.google.firebase.messaging.RemoteMessage;
import com.quickblox.messages.services.fcm.QBFcmPushListenerService;
import com.quickblox.users.model.QBUser;

import java.util.Map;

@Keep
public class PushListenerService extends QBFcmPushListenerService {
    private static final String TAG = PushListenerService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        SharedPrefsHelper sharedPrefsHelper = SharedPrefsHelper.getInstance();
        if (sharedPrefsHelper.hasQbUser()) {
            QBUser qbUser = sharedPrefsHelper.getQbUser();
            Log.d(TAG, "App has logged user" + qbUser.getId());
            LoginService.start(this, qbUser);
        }
    }

    @Override
    protected void sendPushMessage(Map data, String from, String message) {
        super.sendPushMessage(data, from, message);
        Log.v(TAG, "From: " + from);
        Log.v(TAG, "Message: " + message);
    }
}