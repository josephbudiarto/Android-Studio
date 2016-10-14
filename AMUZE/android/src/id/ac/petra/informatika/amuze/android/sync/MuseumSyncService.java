package id.ac.petra.informatika.amuze.android.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by josephnw on 10/7/2015.
 */
public class MuseumSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static MuseumSyncAdapter sMuseumSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("MuseumSyncService", "onCreate - MuseumSyncService");
        synchronized (sSyncAdapterLock) {
            if (sMuseumSyncAdapter == null) {
                sMuseumSyncAdapter = new MuseumSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sMuseumSyncAdapter.getSyncAdapterBinder();
    }
}
