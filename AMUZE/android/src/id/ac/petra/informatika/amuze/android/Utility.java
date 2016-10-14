package id.ac.petra.informatika.amuze.android;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by josephnw on 10/27/2015.
 */
public class Utility {
    private static final String LOG_TAG = Utility.class.getSimpleName();
    public static Bitmap loadBitmap(Context context, String picName){
        Bitmap b = null;
        FileInputStream fis;
        try {
            fis = context.openFileInput(picName);
            b = BitmapFactory.decodeStream(fis);
            fis.close();
        }
        catch (FileNotFoundException e) {
            Log.d(LOG_TAG, "file not found");
            e.printStackTrace();
        }
        catch (IOException e) {
            Log.d(LOG_TAG, "io exception");
            e.printStackTrace();
        }
        return b;
    }

}
