package id.ac.petra.informatika.amuze.android.game;

import android.content.ContentValues;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import id.ac.petra.informatika.amuze.android.CameraPreview;
import id.ac.petra.informatika.amuze.android.R;
import id.ac.petra.informatika.amuze.android.Utility;
import id.ac.petra.informatika.amuze.android.data.MuseumContract;

/**
 * Created by josephnw on 11/2/2015.
 */
public class SilhouetteGame extends AppCompatActivity {

    AlertDialog dialog;
    boolean dialog_active;
    private final String LOG_TAG = SilhouetteGame.class.getSimpleName();
    private Camera mCamera;
    private CameraPreview mPreview;
    private Handler autoFocusHandler;

    TextView scanText;
    //Button scanButton;
    FrameLayout mPreviewFrame;

    ImageScanner scanner;

    private String mItemId, mId;
    private int mGold, mReward;
    private boolean mScanned = false;
    private boolean mActive = false;
    private boolean mPreviewing = true;


    private static final String[] SILHOUETTE_COLUMNS = {
            MuseumContract.SilhouetteEntry.TABLE_NAME + "." + MuseumContract.SilhouetteEntry._ID,
            MuseumContract.SilhouetteEntry.COLUMN_PHOTO,
            MuseumContract.SilhouetteEntry.COLUMN_GOLD_REWARD
    };
    static final int COL_ID = 0;
    static final int COL_PHOTO = 1;
    static final int COL_GOLD_REWARD = 2;

    static {
        System.loadLibrary("iconv");
    }

    public SilhouetteGame() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_silhouette);
        dialog_active = false;
        mPreviewFrame = (FrameLayout) findViewById(R.id.cameraPreview);
        scanText = (TextView) findViewById(R.id.scanText);

        Bundle extras = getIntent().getExtras();
        /*   if (extras != null) {
            Toast.makeText(this, "ID ITEM: " + extras.get("id"), Toast.LENGTH_SHORT).show();
        }       */
        mItemId = extras.getString("itemId");
        mId = extras.getString("id");
        Uri silhouetteUri = MuseumContract.SilhouetteEntry.buildUriById(mId);
        Cursor cursor = getContentResolver().query(silhouetteUri, SILHOUETTE_COLUMNS, MuseumContract.SilhouetteEntry._ID + "=?", new String[]{mId}, null);


        if (cursor != null && cursor.moveToFirst()) {
            ImageView image = (ImageView) findViewById(R.id.silhouette_image);
            image.setImageBitmap(Utility.loadBitmap(this, cursor.getString(COL_PHOTO)));
            mReward = cursor.getInt(COL_GOLD_REWARD);
            cursor.close();
        }


        Uri itemUri = MuseumContract.ItemEntry.buildUriById(mItemId);
        Cursor cursor2 = getContentResolver().query(itemUri, new String[] {MuseumContract.ItemEntry.COLUMN_ITEM_NAME}, null, null, null);
        if(cursor2 != null & cursor2.moveToFirst()){
            TextView target = (TextView) findViewById(R.id.target_textview);
            target.setText("Target: " + cursor2.getString(0));
            cursor2.close();
        }
    }

    private void tryStart() {
        if (!mActive) {
            /* Instance barcode scanner */
            scanner = new ImageScanner();
            scanner.setConfig(0, Config.X_DENSITY, 3);
            scanner.setConfig(0, Config.Y_DENSITY, 3);

            autoFocusHandler = new Handler();

            mCamera = getCameraInstance();
            mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);
            mPreviewFrame.addView(mPreview);

            mActive = true;
        }
    }

    public void onPause() {
        Log.e(LOG_TAG, "Pause");
        super.onPause();
        releaseCamera();
        mPreviewFrame.removeView(mPreview);
        mActive = false;
    }

    @Override
    public void onResume() {
        Log.e(LOG_TAG, "Resume");
        super.onResume();

        tryStart();
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
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
                    //cek apakah ditemukan
                    if (sym.getData().equals(mItemId)) {
                        scanText.setText("Correct");
                        //update database, gold dan finish di silhouette

                        Uri playerUri = MuseumContract.PlayerEntry.buildUri();
                        Cursor cursor = getContentResolver().query(playerUri, new String[]{MuseumContract.PlayerEntry.COLUMN_GOLD}, MuseumContract.PlayerEntry._ID + "=?", new String[]{"1"}, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            mGold = cursor.getInt(0);
                            cursor.close();
                        }

                        ContentValues values = new ContentValues();
                        values.put(MuseumContract.PlayerEntry.COLUMN_GOLD, (mGold + mReward));
                        String selection = MuseumContract.PlayerEntry._ID + "=?";
                        String[] args = {"1"};
                        getContentResolver().update(MuseumContract.PlayerEntry.CONTENT_URI, values, selection, args);

                        values.clear();
                        values.put(MuseumContract.SilhouetteEntry._ID, mId);
                        values.put(MuseumContract.SilhouetteEntry.COLUMN_FINISH, 1);
                        String selection2 = MuseumContract.SilhouetteEntry._ID + "=?";
                        String[] args2 = { String.valueOf(mId) };
                        getContentResolver().update(MuseumContract.SilhouetteEntry.CONTENT_URI, values, selection2, args2);


                        //end
                        mPreviewing = false;
                        mCamera.setPreviewCallback(null);
                        mCamera.stopPreview();
                        mScanned = true;

                        //Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT);

                        dialog = new AlertDialog.Builder(SilhouetteGame.this).
                                setMessage("Correct!").create();
                        dialog.show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                dialog.dismiss();
                                finish();
                            }
                        }).start();
                    } else {
                        //scanText.setText("Wrong object");
                        if(dialog_active == false) {
                            dialog = new AlertDialog.Builder(SilhouetteGame.this).
                                    setMessage("Wrong object").create();
                            dialog.show();

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog_active = true;
                                    try {
                                        Thread.sleep(1500);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    dialog_active = false;
                                    dialog.dismiss();
                                }
                            }).start();
                        }
                    }


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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// Respond to the action bar's Up/Home button
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
