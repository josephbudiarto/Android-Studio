package id.ac.petra.informatika.amuze.android.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import id.ac.petra.informatika.amuze.android.R;

/**
 * Created by josephnw on 10/7/2015.
 */
public class MuseumContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website. A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "id.ac.petra.informatika.amuze.android";
    // content authority ada strings.xml, MuseumContract.java dan AndroidManifest.xml
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.example.android.sunshine.app/weather/ is a valid path for
    // looking at weather data. content://com.example.android.sunshine.app/givemeroot/ will fail,
    // as the ContentProvider hasn't been given any information on what to do with "givemeroot".
    // At least, let's hope not. Don't be that dev, reader. Don't be that dev.
    public static final String PATH_MUSEUM = "museum";
    public static final String PATH_ITEM = "item";
    public static final String PATH_MAP = "map";
    public static final String PATH_PUZZLE = "puzzle";
    public static final String PATH_QUIZ = "quiz";
    public static final String PATH_SILHOUETTE = "silhouette";
    public static final String PATH_PLAYER = "player";

    //public static final String PATH_LOCATION = "location";


    // Inner class that defines the table contents of the location table */
    public static final class PlayerEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PLAYER).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLAYER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PLAYER;
        // Table name
        public static final String TABLE_NAME = "player";
        public static final String COLUMN_GOLD = "gold";
        public static Uri buildUri() {
            return CONTENT_URI.buildUpon().build();
        }

    }

    public static final class MuseumEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MUSEUM).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MUSEUM;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MUSEUM;
        // Table name
        public static final String TABLE_NAME = "museum";
        public static final String COLUMN_MUSEUM_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_FLAG_DOWNLOAD = "flag_download";
        public static final int FLAG_NO = 0;
        public static final int FLAG_YES = 100;
        public static final String QUIZ_EASY_REQUIREMENT = "easy_requirement";
        public static final String QUIZ_EASY_REWARD = "easy_reward";
        public static final String QUIZ_EASY_UNLOCK = "easy_unlock";
        public static final String QUIZ_EASY_FINISH = "easy_finish";
        public static final String QUIZ_MEDIUM_REQUIREMENT = "medium_requirement";
        public static final String QUIZ_MEDIUM_REWARD = "medium_reward";
        public static final String QUIZ_MEDIUM_UNLOCK = "medium_unlock";
        public static final String QUIZ_MEDIUM_FINISH = "medium_finish";
        public static final String QUIZ_HARD_REQUIREMENT = "hard_requirement";
        public static final String QUIZ_HARD_REWARD = "hard_reward";
        public static final String QUIZ_HARD_UNLOCK = "hard_unlock";
        public static final String QUIZ_HARD_FINISH = "hard_finish";

        public static Uri buildUri() {
            return CONTENT_URI.buildUpon().build();
        }

        /*
        public static Uri buildUriById(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        */
        //search berdasarkan nama
        public static Uri buildUriByName(String nameParameter) {
            return CONTENT_URI.buildUpon().appendPath("s").appendPath(nameParameter).build();
        }
        public static Uri buildUriById(String id){
            return CONTENT_URI.buildUpon().appendPath("id").appendPath(id).build();
        }
        public static String getParameterNameFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }
    public static final class ItemEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ITEM).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;
        // Table name
        public static final String TABLE_NAME = "item";
        public static final String COLUMN_MUSEUM_KEY = "id_museum";
        public static final String COLUMN_ITEM_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_AUDIO = "audio";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_SCAN = "flag_scan";
        public static final String COLUMN_FAV = "flag_favorite";
        public static Uri buildUri() {
            return CONTENT_URI.buildUpon().build();
        }
        public static Uri buildUriById(String id){
            return CONTENT_URI.buildUpon().appendPath("id").appendPath(id).build();
        }
        //get data di museum tertentu
        public static Uri buildUriByMuseumId(String id){
            return CONTENT_URI.buildUpon().appendPath("museum").appendPath(id).build();
        }
        public static Uri buildUriFav(String id){
            return CONTENT_URI.buildUpon().appendPath("fav").appendPath(id).build();
        }
        public static String getParameterFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }
    public static final class MapEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MAP).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MAP;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MAP;
        // Table name
        public static final String TABLE_NAME = "map";
        public static final String COLUMN_MUSEUM_KEY = "id_museum";
        public static final String COLUMN_MAP_NAME = "name";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_GRID = "grid";
        public static Uri buildUri() {
            return CONTENT_URI.buildUpon().build();
        }
        public static Uri buildUriById(String id){
            return CONTENT_URI.buildUpon().appendPath("id").appendPath(id).build();
        }
        //get data di museum tertentu
        public static Uri buildUriByMuseumId(String id){
            return CONTENT_URI.buildUpon().appendPath("museum").appendPath(id).build();
        }
        public static String getParameterFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }
    public static final class PuzzleEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PUZZLE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PUZZLE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PUZZLE;
        // Table name
        public static final String TABLE_NAME = "puzzle";
        public static final String COLUMN_MUSEUM_KEY = "id_museum";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_GOLD_REQUIREMENT = "gold_requirement";
        public static final String COLUMN_GOLD_REWARD = "gold_reward";

        public static final String COLUMN_UNLOCK = "unlock";
        public static final String COLUMN_FINISH = "finish";

        public static Uri buildUri() {
            return CONTENT_URI.buildUpon().build();
        }
        public static Uri buildUriById(String id){
            return CONTENT_URI.buildUpon().appendPath("id").appendPath(id).build();
        }
        //get data di museum tertentu
        public static Uri buildUriByMuseumId(String id){
            return CONTENT_URI.buildUpon().appendPath("museum").appendPath(id).build();
        }
        public static String getParameterFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }
    public static final class QuizEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_QUIZ).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUIZ;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_QUIZ;
        // Table name
        public static final String TABLE_NAME = "quiz";
        public static final String COLUMN_MUSEUM_KEY = "id_museum";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_ANSWER1 = "answer1";
        public static final String COLUMN_ANSWER2 = "answer2";
        public static final String COLUMN_ANSWER3 = "answer3";
        public static final String COLUMN_ANSWER4 = "answer4";
        public static final String COLUMN_ANSWER5 = "answer5";
        public static final String COLUMN_ANSWER = "answer";
        public static final String COLUMN_DIFFICULTY = "difficulty";
        //delete req,reward,unlock,finish
        /*
        public static final String COLUMN_GOLD_REQUIREMENT = "gold_requirement";
        public static final String COLUMN_GOLD_REWARD = "gold_reward";
        public static final String COLUMN_UNLOCK = "unlock";
        public static final String COLUMN_FINISH = "finish";
        */
        public static Uri buildUri() {
            return CONTENT_URI.buildUpon().build();
        }
        public static Uri buildUriById(String id){
            return CONTENT_URI.buildUpon().appendPath("id").appendPath(id).build();
        }
        //get data di museum tertentu
        public static Uri buildUriByMuseumId(String id){
            return CONTENT_URI.buildUpon().appendPath("museum").appendPath(id).build();
        }
        public static String getParameterFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }
    public static final class SilhouetteEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SILHOUETTE).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ITEM;
        // Table name
        public static final String TABLE_NAME = "silhouette";
        public static final String COLUMN_MUSEUM_KEY = "id_museum";
        public static final String COLUMN_ITEM_KEY = "id_item";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_GOLD_REQUIREMENT = "gold_requirement";
        public static final String COLUMN_GOLD_REWARD = "gold_reward";

        public static final String COLUMN_UNLOCK = "unlock";
        public static final String COLUMN_FINISH = "finish";

        public static Uri buildUri() {
            return CONTENT_URI.buildUpon().build();
        }
        public static Uri buildUriById(String id){
            return CONTENT_URI.buildUpon().appendPath("id").appendPath(id).build();
        }
        //get data di museum tertentu
        public static Uri buildUriByMuseumId(String id){
            return CONTENT_URI.buildUpon().appendPath("museum").appendPath(id).build();
        }
        public static String getParameterFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }
    }
    // Inner class that defines the table contents of the weather table */
    /*
    public static final class WeatherEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static final String TABLE_NAME = "weather";
        public static final String COLUMN_LOC_KEY = "location_id";
        public static final String COLUMN_DATE = "date";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";
        public static final String COLUMN_HUMIDITY = "humidity";
        public static final String COLUMN_PRESSURE = "pressure";
        public static final String COLUMN_WIND_SPEED = "wind";
        public static final String COLUMN_DEGREES = "degrees";

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }


        //Student: This is the buildWeatherLocation function you filled in.

        public static Uri buildWeatherLocation(String locationSetting) {
           return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, long startDate) {
            long normalizedDate = normalizeDate(startDate);
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATE, Long.toString(normalizedDate)).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, long date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendPath(Long.toString(normalizeDate(date))).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static long getDateFromUri(Uri uri) {
            return Long.parseLong(uri.getPathSegments().get(2));
        }

        public static long getStartDateFromUri(Uri uri) {
            String dateString = uri.getQueryParameter(COLUMN_DATE);
            if (null != dateString && dateString.length() > 0)
                return Long.parseLong(dateString);
            else
                return 0;
        }
    }
    */
}
