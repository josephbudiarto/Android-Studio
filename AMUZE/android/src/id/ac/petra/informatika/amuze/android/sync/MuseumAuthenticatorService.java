package id.ac.petra.informatika.amuze.android.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by josephnw on 10/7/2015.
 */
public class MuseumAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private MuseumAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new MuseumAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
