package id.ac.petra.informatika.amuze.android.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import id.ac.petra.informatika.amuze.android.R;
import id.ac.petra.informatika.amuze.android.data.MuseumContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

/**
 * Created by josephnw on 10/7/2015.
 */
public class MuseumSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MuseumSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    public MuseumSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        //String locationQuery = Utility.getPreferredLocation(getContext());

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String museumJsonStr = null;



        try {
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            final String MUSEUM_BASE_URL =
                    "http://opensource.petra.ac.id/~rina/Amuze/query.php?";
            final String TABLE_PARAM = "table";
            String table = "Museums";
            //final String UNITS_PARAM = "units";

            Uri builtUri = Uri.parse(MUSEUM_BASE_URL).buildUpon()
                    .appendQueryParameter(TABLE_PARAM, table)
                    .appendQueryParameter("MuseumDeleted", "0")
                    .appendQueryParameter("MuseumTour", "1")
                    .build();

            URL url = new URL(builtUri.toString());

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            museumJsonStr = buffer.toString();
            getMuseumDataFromJson(museumJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
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
        return;
    }
    private void getMuseumDataFromJson(String museumJsonStr)
            throws JSONException {

        //Museum Information
        final String OWM_MUSEUM_ID = "idMuseum";
        final String OWM_MUSEUM_NAME = "MuseumName";
        final String OWM_DESCRIPTION = "MuseumDescription";
        final String OWM_PHOTO = "MuseumPhoto";
        final String OWM_GOLD_REQ_EASY = "GoldRequirementEasy";
        final String OWM_GOLD_REQ_MEDIUM = "GoldRequirementMedium";
        final String OWM_GOLD_REQ_HARD = "GoldRequirementHard";
        final String OWM_GOLD_REWARD_EASY = "GoldRewardEasy";
        final String OWM_GOLD_REWARD_MEDIUM = "GoldRewardMedium";
        final String OWM_GOLD_REWARD_HARD = "GoldRewardHard";

        try {
            JSONArray museumArray = new JSONArray(museumJsonStr);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(museumArray.length());

            for(int i = 0; i < museumArray.length(); i++) {
                String museumName;
                String description;
                String photo ="";
                int museumId;
                int quizUnlock[] = new int [3];
                int quizReward[] = new int [3];
                int quizRequirement[] = new int [3];

                JSONObject museumObject = museumArray.getJSONObject(i);

                museumId = museumObject.getInt(OWM_MUSEUM_ID);
                museumName = museumObject.getString(OWM_MUSEUM_NAME);
                description = museumObject.getString(OWM_DESCRIPTION);
                photo = museumObject.getString(OWM_PHOTO);
                quizRequirement[0] = museumObject.getInt(OWM_GOLD_REQ_EASY);
                quizRequirement[1] = museumObject.getInt(OWM_GOLD_REQ_MEDIUM);
                quizRequirement[2] = museumObject.getInt(OWM_GOLD_REQ_HARD);
                quizReward[0] = museumObject.getInt(OWM_GOLD_REWARD_EASY);
                quizReward[1] = museumObject.getInt(OWM_GOLD_REWARD_MEDIUM);
                quizReward[2] = museumObject.getInt(OWM_GOLD_REWARD_HARD);
                for(int j = 0; j < 3; j++){
                    if(quizRequirement[j] == 0)
                        quizUnlock[j] = 1;
                    else
                        quizUnlock[j] = 0;
                }

                ContentValues values = new ContentValues();

                values.put(MuseumContract.MuseumEntry._ID, museumId);
                values.put(MuseumContract.MuseumEntry.COLUMN_MUSEUM_NAME, museumName);
                values.put(MuseumContract.MuseumEntry.COLUMN_DESCRIPTION, description);
                if(photo != "") {
                    if(downloadImage(photo, getContext()))
                        values.put(MuseumContract.ItemEntry.COLUMN_PHOTO, photo.replaceAll("/", "_") );
                }
                values.put(MuseumContract.MuseumEntry.COLUMN_FLAG_DOWNLOAD, MuseumContract.MuseumEntry.FLAG_NO);
                values.put(MuseumContract.MuseumEntry.QUIZ_EASY_FINISH, 0);
                values.put(MuseumContract.MuseumEntry.QUIZ_MEDIUM_FINISH, 0);
                values.put(MuseumContract.MuseumEntry.QUIZ_HARD_FINISH, 0);
                values.put(MuseumContract.MuseumEntry.QUIZ_EASY_UNLOCK, quizUnlock[0]);
                values.put(MuseumContract.MuseumEntry.QUIZ_MEDIUM_UNLOCK, quizUnlock[1]);
                values.put(MuseumContract.MuseumEntry.QUIZ_HARD_UNLOCK, quizUnlock[2]);
                values.put(MuseumContract.MuseumEntry.QUIZ_EASY_REQUIREMENT, quizRequirement[0]);
                values.put(MuseumContract.MuseumEntry.QUIZ_MEDIUM_REQUIREMENT, quizRequirement[1]);
                values.put(MuseumContract.MuseumEntry.QUIZ_HARD_REQUIREMENT, quizRequirement[2]);
                values.put(MuseumContract.MuseumEntry.QUIZ_EASY_REWARD, quizReward[0]);
                values.put(MuseumContract.MuseumEntry.QUIZ_MEDIUM_REWARD, quizReward[1]);
                values.put(MuseumContract.MuseumEntry.QUIZ_HARD_REWARD, quizReward[2]);

                cVVector.add(values);
            }

            //int inserted = 0;
            // add to database
            if ( cVVector.size() > 0 ) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                // delete old data so we don't build up an endless history
                //getContext().getContentResolver().delete(MuseumContract.MuseumEntry.CONTENT_URI, null, null);
                getContext().getContentResolver().bulkInsert(MuseumContract.MuseumEntry.CONTENT_URI, cvArray);
                //notifyWeather();
            }

            Log.e(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }
    private boolean downloadImage(String path, Context context){
        try {
            URL url = new URL("http://opensource.petra.ac.id/~rina/" + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            Bitmap bm = BitmapFactory.decodeStream(is);
            if(bm == null)
                return false;
            String new_path = path.replaceAll("/", "_");//internal storage tidak boleh ada substring "/"

            FileOutputStream fos = context.openFileOutput(new_path, Context.MODE_PRIVATE);

            ByteArrayOutputStream outstream = new ByteArrayOutputStream();

            bm.compress(Bitmap.CompressFormat.JPEG, 100, outstream);

            byte[] byteArray = outstream.toByteArray();

            fos.write(byteArray);
            fos.close();
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
            return false;
        } catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }



    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }
    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        MuseumSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
