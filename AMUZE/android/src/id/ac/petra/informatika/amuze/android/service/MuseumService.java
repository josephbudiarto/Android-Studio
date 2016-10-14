package id.ac.petra.informatika.amuze.android.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.NotificationCompat;
import android.text.format.Time;
import android.util.Log;

import id.ac.petra.informatika.amuze.android.MainActivity;
import id.ac.petra.informatika.amuze.android.R;
import id.ac.petra.informatika.amuze.android.data.MuseumContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

/**
 * Created by josephnw on 10/16/2015.
 */
public class MuseumService extends IntentService {
    public static final int UPDATE_PROGRESS = 8344;
    public static final String ID_EXTRA = "ie";
    public static final String NAME_EXTRA = "ne";
    public static final String RECEIVER_EXTRA = "re";
    private final String LOG_TAG = MuseumService.class.getSimpleName();
    private ResultReceiver mReceiver;
    private NotificationManager mNotifyManager;
    private NotificationCompat.Builder mBuilder;
    private int mSelected;
    private String mSelectedName;
    private static final int NOTIFICATION_ID = 12345;

    public MuseumService() {
        super("MuseumService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String museum = intent.getStringExtra(ID_EXTRA);
        mSelectedName = intent.getStringExtra(NAME_EXTRA);
        mSelected = Integer.parseInt(museum);

        mNotifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle(mSelectedName)
                .setContentText("Download in progress")
                .setSmallIcon(R.mipmap.ic_launcher);

        mReceiver = (ResultReceiver) intent.getParcelableExtra(RECEIVER_EXTRA);

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String[] MUSEUM_COLUMNS = {
                MuseumContract.MuseumEntry.TABLE_NAME + "." + MuseumContract.MuseumEntry._ID,
                MuseumContract.MuseumEntry.COLUMN_MUSEUM_NAME,
                MuseumContract.MuseumEntry.COLUMN_FLAG_DOWNLOAD
        };

        Uri museumUri = MuseumContract.MuseumEntry.buildUriById(museum);
        Cursor cursor = getContentResolver().query(museumUri, MUSEUM_COLUMNS, MuseumContract.MuseumEntry._ID + "=?", new String[]{museum}, null);
        int progress = 0;
        if (cursor != null && cursor.moveToFirst()) {
            progress = cursor.getInt(2);
            cursor.close();
        }


        final String MUSEUM_BASE_URL =
                "http://opensource.petra.ac.id/~rina/Amuze/query.php?";
        final String TABLE_PARAM = "table";
        String table;
        final String MUSEUM_PARAM = "idMuseum";
        final String EXIST_PARAM = "exist";
        final String exist = "1";

        //coba update progress
        setUpdateProgress(progress);
        if (progress < 20) {
            //item
            String itemJsonStr = null;
            table = "Museum_items";
            try {

                Uri builtUri = Uri.parse(MUSEUM_BASE_URL).buildUpon()
                        .appendQueryParameter(TABLE_PARAM, table)
                        .appendQueryParameter(MUSEUM_PARAM, museum)
                        .appendQueryParameter("ItemDeleted", "0")
                        .build();
                URL url = new URL(builtUri.toString());
                Log.e(LOG_TAG, builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed buffer for debugging.
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() != 0) {
                        itemJsonStr = buffer.toString();
                    }
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                getItemDataFromJson(itemJsonStr, Integer.parseInt(museum));
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the item.
            progress = 20;
            setUpdateProgress(20);
        }
        if (progress < 40) {
            //map
            String mapJsonStr = null;
            table = "maps";
            try {
                Uri builtUri = Uri.parse(MUSEUM_BASE_URL).buildUpon()
                        .appendQueryParameter(TABLE_PARAM, table)
                        .appendQueryParameter(MUSEUM_PARAM, museum)
                        .appendQueryParameter(EXIST_PARAM, exist)
                        .build();
                URL url = new URL(builtUri.toString());
                Log.e(LOG_TAG, builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() != 0) {
                        mapJsonStr = buffer.toString();
                    }
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                getMapDataFromJson(mapJsonStr, Integer.parseInt(museum));
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }  // This will only happen if there was an error getting or parsing the item.
            progress = 40;
            setUpdateProgress(40);
        }
        if (progress < 60) {
            //silhouette
            String silhouetteJsonStr = null;
            table = "game_silhouette";
            try {
                Uri builtUri = Uri.parse(MUSEUM_BASE_URL).buildUpon()
                        .appendQueryParameter(TABLE_PARAM, table)
                        .appendQueryParameter(MUSEUM_PARAM, museum)
                        .appendQueryParameter(EXIST_PARAM, exist)
                        .build();
                URL url = new URL(builtUri.toString());
                Log.e(LOG_TAG, builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() != 0) {
                        silhouetteJsonStr = buffer.toString();
                    }
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                getSilhouetteDataFromJson(silhouetteJsonStr, Integer.parseInt(museum));
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the item.
            progress = 60;
            setUpdateProgress(60);
        }
        if (progress < 80) {
            //puzzle
            String puzzleJsonStr = null;
            table = "game_puzzle";
            try {
                Uri builtUri = Uri.parse(MUSEUM_BASE_URL).buildUpon()
                        .appendQueryParameter(TABLE_PARAM, table)
                        .appendQueryParameter(MUSEUM_PARAM, museum)
                        .appendQueryParameter(EXIST_PARAM, exist)
                        .build();
                URL url = new URL(builtUri.toString());
                Log.e(LOG_TAG, builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() != 0) {
                        puzzleJsonStr = buffer.toString();
                    }
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                getPuzzleDataFromJson(puzzleJsonStr, Integer.parseInt(museum));
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the item.
            progress = 80;
            setUpdateProgress(80);
        }
        if (progress < 100) {
            //quiz
            String quizJsonStr = null;
            table = "game_quiz";
            try {
                Uri builtUri = Uri.parse(MUSEUM_BASE_URL).buildUpon()
                        .appendQueryParameter(TABLE_PARAM, table)
                        .appendQueryParameter(MUSEUM_PARAM, museum)
                        .appendQueryParameter(EXIST_PARAM, exist)
                        .build();
                URL url = new URL(builtUri.toString());
                Log.e(LOG_TAG, builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream != null) {
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() != 0) {
                        quizJsonStr = buffer.toString();
                    }
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                getQuizDataFromJson(quizJsonStr, Integer.parseInt(museum));
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the item.
            progress = 100;
            setUpdateProgress(100);
        }
        finish();
    }

    private void setUpdateProgress(int progress) {
        mBuilder.setProgress(100, progress, false);
        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());

        Bundle resultData = new Bundle();
        resultData.putInt("progress", progress);
        resultData.putInt("selected", mSelected);
        mReceiver.send(UPDATE_PROGRESS, resultData);

        ContentValues values = new ContentValues();
        values.put(MuseumContract.MuseumEntry._ID, mSelected);
        values.put(MuseumContract.MuseumEntry.COLUMN_FLAG_DOWNLOAD, progress);
        //update
        String selection = MuseumContract.MuseumEntry._ID + "=?";
        String[] args = {String.valueOf(mSelected)};
        this.getContentResolver().update(MuseumContract.MuseumEntry.CONTENT_URI, values, selection, args);
    }

    private void finish() {
        Log.e(LOG_TAG, "enter finish()");
        Bundle resultData = new Bundle();
        resultData.putInt("progress", 100);
        mReceiver.send(UPDATE_PROGRESS, resultData);
        //update table museum (flag_download jadi 100)
        ContentValues values = new ContentValues();
        values.put(MuseumContract.MuseumEntry._ID, mSelected);
        values.put(MuseumContract.MuseumEntry.COLUMN_FLAG_DOWNLOAD, 100);
        String selection = MuseumContract.MuseumEntry._ID + "=?";
        String[] args = {String.valueOf(mSelected)};
        this.getContentResolver().update(MuseumContract.MuseumEntry.CONTENT_URI, values, selection, args);
        //notification
        mBuilder.setContentText("Done");
        mBuilder.setProgress(100, 100, false);
        mBuilder.setAutoCancel(true);
        Intent targetIntent = new Intent(this, MainActivity.class);
        //trial to not create new task if already exists
        targetIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        //targetIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        //targetIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //targetIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //end trial
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, targetIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(contentIntent);
        mNotifyManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private boolean downloadAudio(String path, Context context) {
        try {
            URL url = new URL("http://opensource.petra.ac.id/~rina/" + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();

            String new_path = path.replaceAll("/", "_");//internal storage tidak boleh ada substring "/"
            FileOutputStream fos = context.openFileOutput(new_path, Context.MODE_PRIVATE);

            int bytesRead = -1;
            final int BUFFER_SIZE = 4096;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = is.read(buffer)) != -1)
                fos.write(buffer, 0, bytesRead);

            fos.close();
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean downloadImage(String path, Context context) {
        try {
            URL url = new URL("http://opensource.petra.ac.id/~rina/" + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            Bitmap bm = BitmapFactory.decodeStream(is);
            if (bm == null)
                return false;
            String new_path = path.replaceAll("/", "_");//internal storage tidak boleh ada substring "/"

            FileOutputStream fos = context.openFileOutput(new_path, Context.MODE_PRIVATE);

            ByteArrayOutputStream outstream = new ByteArrayOutputStream();

            bm.compress(Bitmap.CompressFormat.JPEG, 100, outstream);

            byte[] byteArray = outstream.toByteArray();

            fos.write(byteArray);
            fos.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
        /*
        String filepath=null;
        try
        {
            URL url = new URL("http://opensource.petra.ac.id/~rina/" + path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.connect();
            File storagePath = new File(Environment.getExternalStorageDirectory(),"Wallpaper");
            storagePath.mkdirs();

            File SDCardRoot = Environment.getExternalStorageDirectory().getAbsoluteFile();
            String filename="downloadedFile.png";
            Log.i("Local filename:",""+filename);
            File file = new File(SDCardRoot,filename);
            if(file.createNewFile())
            {
                file.createNewFile();
            }
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ( (bufferLength = inputStream.read(buffer)) > 0 )
            {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                Log.i("Progress:","downloadedSize:"+downloadedSize+"totalSize:"+ totalSize) ;
            }
            fileOutput.close();
            if(downloadedSize==totalSize) filepath=file.getPath();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            filepath=null;
            e.printStackTrace();
        }
        Log.i("filepath:"," "+filepath) ;
        return filepath;
         */
    }

    private void getItemDataFromJson(String itemJsonStr, int museumId)
            throws JSONException {
        /*        `idItem` int(4) NOT NULL AUTO_INCREMENT,          `idMuseum` int(4) DEFAULT NULL,
          `ItemName` varchar(255) DEFAULT NULL,          `ItemQRcode` varchar(255) DEFAULT NULL,
          `ItemDescription` varchar(255) DEFAULT NULL,          `ItemPhotoPath` varchar(255) DEFAULT NULL,
          `ItemDeleted` int(3) NOT NULL,          `video` varchar(50) NOT NULL,          `audio` varchar(50) NOT NULL,        */
        final String OWM_ITEM_ID = "idItem";
        final String OWM_ITEM_NAME = "ItemName";
        final String OWM_ITEM_DESCRIPTION = "ItemDescription";
        final String OWM_ITEM_PHOTO_PATH = "ItemPhotoPath";
        final String OWM_ITEM_VIDEO = "video";
        final String OWM_ITEM_AUDIO = "audio";
        try {
            JSONArray dataArray = new JSONArray(itemJsonStr);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(dataArray.length());

            for (int i = 0; i < dataArray.length(); i++) {
                int itemId;
                String itemName, itemDescription, itemPhoto, itemVideo, itemAudio;
                JSONObject dataObject = dataArray.getJSONObject(i);

                itemId = dataObject.getInt(OWM_ITEM_ID);
                itemName = dataObject.getString(OWM_ITEM_NAME);
                itemDescription = dataObject.getString(OWM_ITEM_DESCRIPTION);
                itemPhoto = dataObject.getString(OWM_ITEM_PHOTO_PATH);
                itemVideo = dataObject.getString(OWM_ITEM_VIDEO);
                itemAudio = dataObject.getString(OWM_ITEM_AUDIO);

                ContentValues values = new ContentValues();
                values.put(MuseumContract.ItemEntry._ID, itemId);
                values.put(MuseumContract.ItemEntry.COLUMN_MUSEUM_KEY, museumId);
                values.put(MuseumContract.ItemEntry.COLUMN_ITEM_NAME, itemName);
                values.put(MuseumContract.ItemEntry.COLUMN_DESCRIPTION, itemDescription);
                if (!itemPhoto.isEmpty()) {
                    if (downloadImage(itemPhoto, this))
                        values.put(MuseumContract.ItemEntry.COLUMN_PHOTO, itemPhoto.replaceAll("/", "_"));
                }
                values.put(MuseumContract.ItemEntry.COLUMN_VIDEO, itemVideo);
                if (!itemAudio.isEmpty()) {
                    if(downloadAudio(itemAudio, this))
                        values.put(MuseumContract.ItemEntry.COLUMN_AUDIO, itemAudio.replaceAll("/", "_"));
                }
                //values.put(MuseumContract.ItemEntry.COLUMN_AUDIO, itemAudio);
                values.put(MuseumContract.ItemEntry.COLUMN_SCAN, 0);
                values.put(MuseumContract.ItemEntry.COLUMN_FAV, 0);
                cVVector.add(values);
            }
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                this.getContentResolver().bulkInsert(MuseumContract.ItemEntry.CONTENT_URI, cvArray);
            }
            Log.e(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void getMapDataFromJson(String jsonStr, int museumId)
            throws JSONException {
        /*  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_museum` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `photo` varchar(100) NOT NULL,
  `grid` varchar(4000) NOT NULL,
  `exist` int(1) NOT NULL,          */
        final String OWM_ID = "id";
        final String OWM_NAME = "name";
        final String OWM_PHOTO = "photo";
        final String OWM_GRID = "grid";
        try {
            JSONArray dataArray = new JSONArray(jsonStr);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(dataArray.length());
            for (int i = 0; i < dataArray.length(); i++) {
                int id;
                String name, photo, grid;

                JSONObject dataObject = dataArray.getJSONObject(i);
                id = dataObject.getInt(OWM_ID);
                name = dataObject.getString(OWM_NAME);
                photo = dataObject.getString(OWM_PHOTO);
                grid = dataObject.getString(OWM_GRID);

                ContentValues values = new ContentValues();
                values.put(MuseumContract.MapEntry._ID, id);
                values.put(MuseumContract.MapEntry.COLUMN_MUSEUM_KEY, museumId);
                values.put(MuseumContract.MapEntry.COLUMN_MAP_NAME, name);
                values.put(MuseumContract.MapEntry.COLUMN_GRID, grid);

                if (photo != "") {
                    if (downloadImage(photo, this))
                        values.put(MuseumContract.MapEntry.COLUMN_PHOTO, photo.replaceAll("/", "_"));
                }
                cVVector.add(values);
            }
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                this.getContentResolver().bulkInsert(MuseumContract.MapEntry.CONTENT_URI, cvArray);
            }
            Log.e(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void getPuzzleDataFromJson(String jsonStr, int museumId)
            throws JSONException {
        /*     `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_museum` int(11) NOT NULL,
  `photo` varchar(100) NOT NULL,
  `gold_requirement` int(11) NOT NULL,
  `gold_reward` int(11) NOT NULL,
  `unlocked` int(11) NOT NULL,
     + finish*/
        final String OWM_ID = "id";
        final String OWM_GOLD_REQ = "gold_requirement";
        final String OWM_GOLD_REWARD = "gold_reward";
        final String OWM_PHOTO = "photo";
        final String OWM_UNLOCK = "unlocked";

        try {
            JSONArray dataArray = new JSONArray(jsonStr);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(dataArray.length());
            for (int i = 0; i < dataArray.length(); i++) {
                int id, gold_req, gold_reward, unlock;
                String photo;

                JSONObject dataObject = dataArray.getJSONObject(i);
                id = dataObject.getInt(OWM_ID);
                gold_req = dataObject.getInt(OWM_GOLD_REQ);
                gold_reward = dataObject.getInt(OWM_GOLD_REWARD);
                unlock = dataObject.getInt(OWM_UNLOCK);
                photo = dataObject.getString(OWM_PHOTO);

                ContentValues values = new ContentValues();
                values.put(MuseumContract.PuzzleEntry._ID, id);
                values.put(MuseumContract.PuzzleEntry.COLUMN_MUSEUM_KEY, museumId);
                values.put(MuseumContract.PuzzleEntry.COLUMN_GOLD_REQUIREMENT, gold_req);
                values.put(MuseumContract.PuzzleEntry.COLUMN_GOLD_REWARD, gold_reward);
                values.put(MuseumContract.PuzzleEntry.COLUMN_UNLOCK, unlock);
                values.put(MuseumContract.PuzzleEntry.COLUMN_FINISH, 0);

                if (photo != "") {
                    if (downloadImage(photo, this))
                        values.put(MuseumContract.PuzzleEntry.COLUMN_PHOTO, photo.replaceAll("/", "_"));
                }
                cVVector.add(values);
            }
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                this.getContentResolver().bulkInsert(MuseumContract.PuzzleEntry.CONTENT_URI, cvArray);
            }
            Log.e(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void getSilhouetteDataFromJson(String jsonStr, int museumId)
            throws JSONException {
        /*     `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_museum` int(11) NOT NULL,
  `id_item` int(11) NOT NULL,
  `photo` varchar(100) NOT NULL,
  `gold_requirement` int(11) NOT NULL,
  `gold_reward` int(11) NOT NULL,
  `unlocked` int(11) NOT NULL,
     + finish*/
        final String OWM_ID = "id";
        final String OWM_ITEM = "id_item";
        final String OWM_GOLD_REQ = "gold_requirement";
        final String OWM_GOLD_REWARD = "gold_reward";
        final String OWM_PHOTO = "photo";
        final String OWM_UNLOCK = "unlocked";

        try {
            JSONArray dataArray = new JSONArray(jsonStr);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(dataArray.length());
            for (int i = 0; i < dataArray.length(); i++) {
                int id, id_item, gold_req, gold_reward, unlock;
                String photo;

                JSONObject dataObject = dataArray.getJSONObject(i);
                id = dataObject.getInt(OWM_ID);
                id_item = dataObject.getInt(OWM_ITEM);
                gold_req = dataObject.getInt(OWM_GOLD_REQ);
                gold_reward = dataObject.getInt(OWM_GOLD_REWARD);
                unlock = dataObject.getInt(OWM_UNLOCK);
                photo = dataObject.getString(OWM_PHOTO);


                ContentValues values = new ContentValues();
                values.put(MuseumContract.SilhouetteEntry._ID, id);
                values.put(MuseumContract.SilhouetteEntry.COLUMN_ITEM_KEY, id_item);
                values.put(MuseumContract.SilhouetteEntry.COLUMN_MUSEUM_KEY, museumId);
                values.put(MuseumContract.SilhouetteEntry.COLUMN_GOLD_REQUIREMENT, gold_req);
                values.put(MuseumContract.SilhouetteEntry.COLUMN_GOLD_REWARD, gold_reward);
                values.put(MuseumContract.SilhouetteEntry.COLUMN_UNLOCK, unlock);
                values.put(MuseumContract.SilhouetteEntry.COLUMN_FINISH, 0);

                if (photo != "") {
                    if (downloadImage(photo, this))
                        values.put(MuseumContract.SilhouetteEntry.COLUMN_PHOTO, photo.replaceAll("/", "_"));
                }
                cVVector.add(values);
            }
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                this.getContentResolver().bulkInsert(MuseumContract.SilhouetteEntry.CONTENT_URI, cvArray);
            }
            Log.e(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void getQuizDataFromJson(String jsonStr, int museumId)
            throws JSONException {
        /*    `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_museum` int(11) NOT NULL,
  `question` varchar(100) NOT NULL,
  `answer1` - `answer5` varchar(30) NOT NULL,
  `answer` int(1) NOT NULL,
  `difficulty` int(3) NOT NULL,
  `gold_requirement` int(11) NOT NULL,
  `gold_reward` int(11) NOT NULL,
  `unlocked` int(11) NOT NULL,
     + finish*/
        final String OWM_ID = "id";
        final String OWM_GOLD_REQ = "gold_requirement";
        final String OWM_GOLD_REWARD = "gold_reward";
        final String OWM_QUESTION = "question";
        final String OWM_ANSWER = "answer";
        final String OWM_ANSWER1 = "answer1";
        final String OWM_ANSWER2 = "answer2";
        final String OWM_ANSWER3 = "answer3";
        final String OWM_ANSWER4 = "answer4";
        final String OWM_ANSWER5 = "answer5";
        final String OWM_UNLOCK = "unlocked";
        final String OWM_DIFFICULTY = "difficulty";
        try {
            JSONArray dataArray = new JSONArray(jsonStr);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(dataArray.length());
            for (int i = 0; i < dataArray.length(); i++) {
                int id, gold_req, gold_reward, unlock, answer, difficulty;
                String question, answer1, answer2, answer3, answer4, answer5;

                JSONObject dataObject = dataArray.getJSONObject(i);
                id = dataObject.getInt(OWM_ID);
                /*
                gold_req = dataObject.getInt(OWM_GOLD_REQ);
                gold_reward = dataObject.getInt(OWM_GOLD_REWARD);
                unlock = dataObject.getInt(OWM_UNLOCK);
                */
                question = dataObject.getString(OWM_QUESTION);
                answer = dataObject.getInt(OWM_ANSWER);
                answer1 = dataObject.getString(OWM_ANSWER1);
                answer2 = dataObject.getString(OWM_ANSWER2);
                answer3 = dataObject.getString(OWM_ANSWER3);
                answer4 = dataObject.getString(OWM_ANSWER4);
                answer5 = dataObject.getString(OWM_ANSWER5);
                difficulty = dataObject.getInt(OWM_DIFFICULTY);

                ContentValues values = new ContentValues();
                values.put(MuseumContract.QuizEntry._ID, id);
                values.put(MuseumContract.QuizEntry.COLUMN_MUSEUM_KEY, museumId);
                /*
                values.put(MuseumContract.QuizEntry.COLUMN_GOLD_REQUIREMENT, gold_req);
                values.put(MuseumContract.QuizEntry.COLUMN_GOLD_REWARD, gold_reward);
                values.put(MuseumContract.QuizEntry.COLUMN_UNLOCK, unlock);
                values.put(MuseumContract.QuizEntry.COLUMN_FINISH, 0);
                */
                values.put(MuseumContract.QuizEntry.COLUMN_QUESTION, question);
                values.put(MuseumContract.QuizEntry.COLUMN_ANSWER, answer);
                values.put(MuseumContract.QuizEntry.COLUMN_ANSWER1, answer1);
                values.put(MuseumContract.QuizEntry.COLUMN_ANSWER2, answer2);
                values.put(MuseumContract.QuizEntry.COLUMN_ANSWER3, answer3);
                values.put(MuseumContract.QuizEntry.COLUMN_ANSWER4, answer4);
                values.put(MuseumContract.QuizEntry.COLUMN_ANSWER5, answer5);
                values.put(MuseumContract.QuizEntry.COLUMN_DIFFICULTY, difficulty);
                cVVector.add(values);
            }
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                this.getContentResolver().bulkInsert(MuseumContract.QuizEntry.CONTENT_URI, cvArray);
            }
            Log.e(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
}
