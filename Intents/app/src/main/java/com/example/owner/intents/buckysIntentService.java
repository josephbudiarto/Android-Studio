package com.example.owner.intents;

import android.app.IntentService;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;

public class buckysIntentService extends IntentService {

    private static final  String TAG = "com.example.owner.intents";

    public buckysIntentService(){
        super("buckysIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //This is what the service does
        Log.i(TAG,"Service has started");
    }
}
