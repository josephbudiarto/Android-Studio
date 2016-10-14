package id.ac.petra.informatika.amuze.android.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import id.ac.petra.informatika.amuze.android.service.MuseumService;

/**
 * Created by josephnw on 10/22/2015.
 */
public class DownloadReceiver extends ResultReceiver {
    private Receiver mReceiver;
    public DownloadReceiver(Handler handler) {
        super(handler);
    }
    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);

    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        /*
        super.onReceiveResult(resultCode, resultData);
        if (resultCode == MuseumService.UPDATE_PROGRESS) {
            int progress = resultData.getInt("progress");
            mProgressDialog.setProgress(progress);
            if (progress == 100) {
                mProgressDialog.dismiss();
            }
        }
        */
        if (mReceiver != null) {
            mReceiver.onReceiveResult(resultCode, resultData);
        }
    }
}
