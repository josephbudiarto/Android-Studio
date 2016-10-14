package id.ac.petra.informatika.amuze.android;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import id.ac.petra.informatika.amuze.android.data.MuseumContract;

public class QRCodeFragment extends Fragment {
    private final String LOG_TAG = QRCodeFragment.class.getSimpleName();
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    TextView scanText;
    //Button scanButton;
    FrameLayout mPreviewFrame;

    ImageScanner scanner;

    private boolean mScanned = false;
    private boolean mActive = false;
    private boolean mPreviewing = true;

    static {
        System.loadLibrary("iconv");
    }

    public QRCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //Log.e(LOG_TAG, "Create");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_qr_code, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //findview
        mPreviewFrame = (FrameLayout) rootView.findViewById(R.id.cameraPreview);
        scanText = (TextView)rootView.findViewById(R.id.scanText);
        //scanButton = (Button)rootView.findViewById(R.id.ScanButton);

/*

        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mScanned) {
                    mScanned = false;
                    scanText.setText("Scanning...");
                    mCamera.setPreviewCallback(previewCb);
                    mCamera.startPreview();
                    mPreviewing = true;
                    mCamera.autoFocus(autoFocusCB);
                }
            }
        });
        */
        return rootView;
    }
    private void tryStart(){
        if(mActive == false) {
            /* Instance barcode scanner */
            scanner = new ImageScanner();
            scanner.setConfig(0, Config.X_DENSITY, 3);
            scanner.setConfig(0, Config.Y_DENSITY, 3);

            autoFocusHandler = new Handler();

            mCamera = getCameraInstance();
            mPreview = new CameraPreview(getActivity(), mCamera, previewCb, autoFocusCB);
            mPreviewFrame.addView(mPreview);

            mActive = true;
        }
    }

    public void onPause() {
        //Log.e(LOG_TAG, "Pause");
        super.onPause();
        releaseCamera();
        mPreviewFrame.removeView(mPreview);
        mActive = false;
    }
    @Override
    public void onResume(){
        //Log.e(LOG_TAG, "Resume");
        super.onResume();

        tryStart();
    }
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e){
        }
        return c;
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mPreviewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (mPreviewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();

            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);

            int result = scanner.scanImage(barcode);

            if (result != 0) {


                SymbolSet syms = scanner.getResults();
                for (Symbol sym : syms) {
                    //scanText.setText("barcode result " + sym.getData());
                    //cek dulu apakah ada data tersebut dan di museum ini
                    //kalau ada update flag scan di database
                    Uri itemUri = MuseumContract.ItemEntry.buildUriById(sym.getData());
                    Cursor cursor = getActivity().getContentResolver().query(itemUri,
                            new String[] {MuseumContract.ItemEntry.TABLE_NAME + "." + MuseumContract.ItemEntry._ID}, MuseumContract.ItemEntry._ID + "=?",
                            new String[]{sym.getData()}, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        //update flag database
                        ContentValues values = new ContentValues();
                        values.put(MuseumContract.ItemEntry._ID, sym.getData());
                        //values.put(MuseumContract.MuseumEntry.COLUMN_MUSEUM_NAME, cursor.getString(COL_MUSEUM_NAME));
                        values.put(MuseumContract.ItemEntry.COLUMN_SCAN, 1);
                        String selection = MuseumContract.ItemEntry._ID + "=?";
                        String[] args = {sym.getData()};
                        getContext().getContentResolver().update(MuseumContract.ItemEntry.CONTENT_URI, values, selection, args);
                        //end update
                        Intent intent = new Intent(getActivity(), ItemActivity.class)
                                .putExtra("id", sym.getData());
                        startActivity(intent);
                        scanText.setText("Scanning...");
                        mPreviewing = false;
                        mCamera.setPreviewCallback(null);
                        mCamera.stopPreview();
                        mScanned = true;
                    }
                    else
                        scanText.setText("Unknown object...");
                }
            }
        }
    };

    // Mimic continuous auto-focusing
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (this.isVisible())
        {
            if (!isVisibleToUser)   // If we are becoming invisible, then...
            {
                Log.e(LOG_TAG, "Invisible");
                mPreviewFrame.setVisibility(View.INVISIBLE);
                //releaseCamera();
                //mPreviewFrame.removeView(mPreview);
                //mActive = false;
                scanText.setText("Scanning...");
            }

            if (isVisibleToUser)
            {
                Log.e(LOG_TAG, "Visible");
                mPreviewFrame.setVisibility(View.VISIBLE);
                //tryStart();
            }

        }
    }


}