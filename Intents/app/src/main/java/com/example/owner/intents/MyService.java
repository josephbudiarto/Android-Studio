package com.example.owner.intents;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {

    private static final String TAG = "com.example.owner.intents";

    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStart method called");

        Runnable r = new Runnable() {
            @Override
            public void run() {
                for(int i=0; i<5;i++){
                    long futureTIme = System.currentTimeMillis()+5000;
                    while(System.currentTimeMillis() < futureTIme){
                        synchronized (this){
                            try{
                                wait(futureTIme-System.currentTimeMillis());
                                //Tempat buat download code dkk etc
                                Log.i(TAG,"Service is Doing Something");
                            }catch(Exception e){}
                        }
                    }
                }
            }
        };

        Thread BuckysThread = new Thread(r);
        BuckysThread.start();
        return Service.START_STICKY;//Start_Sticky --> di restart if destroyed
    }

    @Override
    public void onDestroy() {
        Log.i(TAG,"onDestroy method called");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
