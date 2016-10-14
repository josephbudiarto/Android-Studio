package id.ac.petra.informatika.amuze.android.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by josephnw on 10/7/2015.
 */
public class MuseumProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MuseumDbHelper mOpenHelper;

    static final int MUSEUM = 100;
    static final int MUSEUM_BY_ID = 101;
    static final int MUSEUM_WITH_PARAMETER = 102;
    static final int ITEM = 110;
    static final int ITEM_BY_ID = 111;
    static final int ITEM_BY_MUSEUM_ID = 112;
    static final int ITEM_FAVORITE_BY_MUSEUM_ID = 113;
    static final int MAP = 120;
    static final int MAP_BY_ID = 121;
    static final int MAP_BY_MUSEUM_ID = 122;
    static final int PUZZLE = 130;
    static final int PUZZLE_BY_ID = 131;
    static final int PUZZLE_BY_MUSEUM_ID = 132;
    static final int QUIZ = 140;
    static final int QUIZ_BY_ID = 141;
    static final int QUIZ_BY_MUSEUM_ID = 142;
    static final int SILHOUETTE = 150;
    static final int SILHOUETTE_BY_ID = 151;
    static final int SILHOUETTE_BY_MUSEUM_ID = 152;
    static final int PLAYER = 160;
    static final int PLAYER_BY_ID = 161;

    private static final SQLiteQueryBuilder sMuseumQueryBuilder, sItemQueryBuilder, sMapQueryBuilder, sPuzzleQueryBuilder,
        sQuizQueryBuilder, sSilhouetteQueryBuilder;
    //initialize
    static{
        sMuseumQueryBuilder = new SQLiteQueryBuilder();
        sMuseumQueryBuilder.setTables(MuseumContract.MuseumEntry.TABLE_NAME);
        //This is an inner join which looks like     //weather INNER JOIN location ON weather.location_id = location._id
        /*
        sWeatherByLocationSettingQueryBuilder.setTables(
                WeatherContract.WeatherEntry.TABLE_NAME + " INNER JOIN " +
                        WeatherContract.LocationEntry.TABLE_NAME +
                        " ON " + WeatherContract.WeatherEntry.TABLE_NAME +
                        "." + WeatherContract.WeatherEntry.COLUMN_LOC_KEY +
                        " = " + WeatherContract.LocationEntry.TABLE_NAME +
                        "." + WeatherContract.LocationEntry._ID);
                        */
    }
    static {
        sItemQueryBuilder = new SQLiteQueryBuilder();
        sItemQueryBuilder.setTables(MuseumContract.ItemEntry.TABLE_NAME);
    }
    static {
        sMapQueryBuilder = new SQLiteQueryBuilder();
        sMapQueryBuilder.setTables(MuseumContract.MapEntry.TABLE_NAME);
    }
    static {
        sPuzzleQueryBuilder = new SQLiteQueryBuilder();
        sPuzzleQueryBuilder.setTables(MuseumContract.PuzzleEntry.TABLE_NAME);
    }
    static {
        sQuizQueryBuilder = new SQLiteQueryBuilder();
        sQuizQueryBuilder.setTables(MuseumContract.QuizEntry.TABLE_NAME);
    }
    static {
        sSilhouetteQueryBuilder = new SQLiteQueryBuilder();
        sSilhouetteQueryBuilder.setTables(MuseumContract.SilhouetteEntry.TABLE_NAME);
    }

    //get by name
    private Cursor getMuseumByParameter(Uri uri, String[] projection, String sortOrder) {
        String parameter = MuseumContract.MuseumEntry.getParameterNameFromUri(uri);
        String[] selectionArgs = new String[]{"%" + parameter + "%"};
        String selection = MuseumContract.MuseumEntry.TABLE_NAME+"." + MuseumContract.MuseumEntry.COLUMN_MUSEUM_NAME + " LIKE ? ";
        return sMuseumQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    //get by id
    private Cursor getMuseumById(Uri uri, String[] projection, String sortOrder) {
        String selection = MuseumContract.MuseumEntry.TABLE_NAME+"." + MuseumContract.MuseumEntry._ID + " = ? ";
        String parameter = MuseumContract.MuseumEntry.getIdFromUri(uri);
        String[] selectionArgs = new String[]{parameter};
        return sMuseumQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getItemById(Uri uri, String[] projection, String sortOrder) {
        String parameter = MuseumContract.ItemEntry.getParameterFromUri(uri);
        String[] selectionArgs = new String[]{ parameter };
        String selection = MuseumContract.ItemEntry.TABLE_NAME+"." + MuseumContract.ItemEntry._ID + " = ? ";
        return sItemQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    //
    private Cursor getItemFavByMuseumId(Uri uri, String[] projection, String sortOrder) {
        String selection = MuseumContract.ItemEntry.TABLE_NAME+"." + MuseumContract.ItemEntry.COLUMN_FAV + " = ? AND "
                + MuseumContract.ItemEntry.COLUMN_MUSEUM_KEY + " = ?";
        String parameter = String.valueOf(1);
        String parameter2 = MuseumContract.ItemEntry.getParameterFromUri(uri);
        String[] selectionArgs = new String[]{ parameter, parameter2 };
        return sItemQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getMapById(Uri uri, String[] projection, String sortOrder) {
        String parameter = MuseumContract.MapEntry.getParameterFromUri(uri);
        String[] selectionArgs = new String[]{ parameter };
        String selection = MuseumContract.MapEntry.TABLE_NAME+"." + MuseumContract.MapEntry._ID + " = ? ";
        return sMapQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getMapByMuseumId(Uri uri, String[] projection, String sortOrder) {
        String parameter = MuseumContract.MapEntry.getParameterFromUri(uri);
        String[] selectionArgs = new String[]{ parameter };
        String selection = MuseumContract.MapEntry.TABLE_NAME+"." + MuseumContract.MapEntry.COLUMN_MUSEUM_KEY + " = ? ";;
        return sMapQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getPuzzleById(Uri uri, String[] projection, String sortOrder) {
        String parameter = MuseumContract.PuzzleEntry.getParameterFromUri(uri);
        String[] selectionArgs = new String[]{ parameter };
        String selection = MuseumContract.PuzzleEntry.TABLE_NAME+"." + MuseumContract.PuzzleEntry._ID + " = ? ";;
        return sPuzzleQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getPuzzleByMuseumId(Uri uri, String[] projection, String sortOrder) {
        String parameter = MuseumContract.PuzzleEntry.getParameterFromUri(uri);
        String[] selectionArgs = new String[]{ parameter };
        String selection = MuseumContract.PuzzleEntry.TABLE_NAME+"." + MuseumContract.PuzzleEntry.COLUMN_MUSEUM_KEY + " = ? ";;
        return sPuzzleQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getQuizById(Uri uri, String[] projection, String sortOrder) {
        String parameter = MuseumContract.QuizEntry.getParameterFromUri(uri);
        String[] selectionArgs = new String[]{ parameter };
        String selection = MuseumContract.QuizEntry.TABLE_NAME+"." + MuseumContract.QuizEntry._ID + " = ? ";;
        return sQuizQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getQuizByMuseumId(Uri uri, String[] projection, String sortOrder) {
        String parameter = MuseumContract.QuizEntry.getParameterFromUri(uri);
        String[] selectionArgs = new String[]{ parameter };
        String selection = MuseumContract.QuizEntry.TABLE_NAME+"." + MuseumContract.QuizEntry.COLUMN_MUSEUM_KEY + " = ? ";;
        return sQuizQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getSilhouetteById(Uri uri, String[] projection, String sortOrder) {
        String parameter = MuseumContract.SilhouetteEntry.getParameterFromUri(uri);
        String[] selectionArgs = new String[]{ parameter };
        String selection = MuseumContract.SilhouetteEntry.TABLE_NAME+"." + MuseumContract.SilhouetteEntry._ID + " = ? ";;
        return sSilhouetteQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    private Cursor getSilhouetteByMuseumId(Uri uri, String[] projection, String sortOrder) {
        String parameter = MuseumContract.SilhouetteEntry.getParameterFromUri(uri);
        String[] selectionArgs = new String[]{ parameter };
        String selection = MuseumContract.SilhouetteEntry.TABLE_NAME+"." + MuseumContract.SilhouetteEntry.COLUMN_MUSEUM_KEY + " = ? ";;
        return sSilhouetteQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }
    /* Students: Here is where you need to create the UriMatcher. This UriMatcher will
        match each URI to the integer constants defined above. */
    static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MuseumContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, MuseumContract.PATH_MUSEUM, MUSEUM);//content://com..../museum/
        matcher.addURI(authority, MuseumContract.PATH_MUSEUM + "/id/*", MUSEUM_BY_ID);
        matcher.addURI(authority, MuseumContract.PATH_MUSEUM + "/s/*", MUSEUM_WITH_PARAMETER);//ini kan MUSEUM_WITH_PARAMETER, terus lihat di fungsi query
        matcher.addURI(authority, MuseumContract.PATH_ITEM, ITEM);
        matcher.addURI(authority, MuseumContract.PATH_ITEM + "/id/*", ITEM_BY_ID);
        matcher.addURI(authority, MuseumContract.PATH_ITEM + "/museum/*", ITEM_BY_MUSEUM_ID);
        matcher.addURI(authority, MuseumContract.PATH_ITEM + "/fav/*", ITEM_FAVORITE_BY_MUSEUM_ID);
        matcher.addURI(authority, MuseumContract.PATH_MAP, MAP);
        matcher.addURI(authority, MuseumContract.PATH_MAP + "/id/*", MAP_BY_ID);
        matcher.addURI(authority, MuseumContract.PATH_MAP + "/museum/*", MAP_BY_MUSEUM_ID);
        matcher.addURI(authority, MuseumContract.PATH_PUZZLE, PUZZLE);
        matcher.addURI(authority, MuseumContract.PATH_PUZZLE + "/id/*", PUZZLE_BY_ID);
        matcher.addURI(authority, MuseumContract.PATH_PUZZLE + "/museum/*", PUZZLE_BY_MUSEUM_ID);
        matcher.addURI(authority, MuseumContract.PATH_QUIZ, QUIZ);
        matcher.addURI(authority, MuseumContract.PATH_QUIZ + "/id/*", QUIZ_BY_ID);
        matcher.addURI(authority, MuseumContract.PATH_QUIZ + "/museum/*", QUIZ_BY_MUSEUM_ID);
        matcher.addURI(authority, MuseumContract.PATH_SILHOUETTE, SILHOUETTE);
        matcher.addURI(authority, MuseumContract.PATH_SILHOUETTE + "/id/*", SILHOUETTE_BY_ID);
        matcher.addURI(authority, MuseumContract.PATH_SILHOUETTE + "/museum/*", SILHOUETTE_BY_MUSEUM_ID);
        matcher.addURI(authority, MuseumContract.PATH_PLAYER, PLAYER);
        matcher.addURI(authority, MuseumContract.PATH_PLAYER + "/id/*", PLAYER_BY_ID);
        //matcher.addURI(authority, WeatherContract.PATH_WEATHER, WEATHER);
        //matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*", WEATHER_WITH_LOCATION);
        //matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/*/#", WEATHER_WITH_LOCATION_AND_DATE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MuseumDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            /*
            case WEATHER_WITH_LOCATION_AND_DATE:{
                retCursor = getWeatherByLocationSettingAndDate(uri, projection, sortOrder);
                break;
            }
            // "weather/*"
            case WEATHER_WITH_LOCATION: {
                retCursor = getWeatherByLocationSetting(uri, projection, sortOrder);break;
            }
            // "weather"
            case WEATHER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder );
                break;
            }
             */
            // "museum"
            case MUSEUM: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MuseumContract.MuseumEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "museum/s/*"
            case MUSEUM_WITH_PARAMETER: {
                retCursor = getMuseumByParameter(uri, projection, sortOrder);
                break;
            }
            // "museum/id/*"
            case MUSEUM_BY_ID:{
                retCursor = getMuseumById(uri, projection, sortOrder);
                break;
            }
            // "item"
            case ITEM: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MuseumContract.ItemEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case ITEM_BY_ID: {
                retCursor = getItemById(uri, projection, sortOrder);
                break;
            }
            case ITEM_FAVORITE_BY_MUSEUM_ID: {
                retCursor = getItemFavByMuseumId(uri, projection, sortOrder);
                break;
            }
            case MAP: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MuseumContract.MapEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MAP_BY_ID: {
                retCursor = getMapById(uri, projection, sortOrder);
                break;
            }
            case MAP_BY_MUSEUM_ID: {
                retCursor = getMapByMuseumId(uri, projection, sortOrder);
                break;
            }
            case PUZZLE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MuseumContract.PuzzleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case PUZZLE_BY_ID: {
                retCursor = getPuzzleById(uri, projection, sortOrder);
                break;
            }
            case PUZZLE_BY_MUSEUM_ID: {
                retCursor = getPuzzleByMuseumId(uri, projection, sortOrder);
                break;
            }
            case QUIZ: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MuseumContract.QuizEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case QUIZ_BY_ID: {
                retCursor = getQuizById(uri, projection, sortOrder);
                break;
            }
            case QUIZ_BY_MUSEUM_ID: {
                retCursor = getQuizByMuseumId(uri, projection, sortOrder);
                break;
            }
            case SILHOUETTE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MuseumContract.SilhouetteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case SILHOUETTE_BY_ID: {
                retCursor = getSilhouetteById(uri, projection, sortOrder);
                break;
            }
            case SILHOUETTE_BY_MUSEUM_ID: {
                retCursor = getSilhouetteByMuseumId(uri, projection, sortOrder);
                break;
            }
            case PLAYER: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MuseumContract.PlayerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            /*
            case WEATHER: {
                normalizeDate(values);
                long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = WeatherContract.WeatherEntry.buildWeatherUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            */
            case MUSEUM: {
                long _id = db.insert(MuseumContract.MuseumEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MuseumContract.MuseumEntry.buildUriById(String.valueOf(_id));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case ITEM: {
                long _id = db.insert(MuseumContract.ItemEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MuseumContract.ItemEntry.buildUriById(String.valueOf(_id));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case PLAYER: {
                long _id = db.insert(MuseumContract.ItemEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = MuseumContract.ItemEntry.buildUriById(String.valueOf(_id));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        //is db.close() needed?
        //db.close();
        //NOT NEEDED IN CONTENT PROVIDER
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            /*
            case WEATHER:
                rowsDeleted = db.delete(
                        WeatherContract.WeatherEntry.TABLE_NAME, selection, selectionArgs);
                break;
                */
            case MUSEUM:
                rowsDeleted = db.delete(
                        MuseumContract.MuseumEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case ITEM:
                rowsDeleted = db.delete(
                        MuseumContract.ItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            /*
            case WEATHER:
                normalizeDate(values);
                rowsUpdated = db.update(WeatherContract.WeatherEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
                */
            case MUSEUM:
                rowsUpdated = db.update(MuseumContract.MuseumEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case ITEM:
                rowsUpdated = db.update(MuseumContract.ItemEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PUZZLE:
                rowsUpdated = db.update(MuseumContract.PuzzleEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case QUIZ:
                rowsUpdated = db.update(MuseumContract.QuizEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case SILHOUETTE:
                rowsUpdated = db.update(MuseumContract.SilhouetteEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PLAYER:
                rowsUpdated = db.update(MuseumContract.PlayerEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            /*
            case WEATHER:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        normalizeDate(value);
                        long _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
             */
            case MUSEUM:
                db.beginTransaction();
                try{
                    for(ContentValues value:values){
                        long _id = db.insert(MuseumContract.MuseumEntry.TABLE_NAME, null, value);
                        if(_id!= -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case ITEM:
                db.beginTransaction();
                try{
                    for(ContentValues value:values){
                        long _id = db.insert(MuseumContract.ItemEntry.TABLE_NAME, null, value);
                        if(_id!= -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case MAP:
                db.beginTransaction();
                try{
                    for(ContentValues value:values){
                        long _id = db.insert(MuseumContract.MapEntry.TABLE_NAME, null, value);
                        if(_id!= -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case PUZZLE:
                db.beginTransaction();
                try{
                    for(ContentValues value:values){
                        long _id = db.insert(MuseumContract.PuzzleEntry.TABLE_NAME, null, value);
                        if(_id!= -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case QUIZ:
                db.beginTransaction();
                try{
                    for(ContentValues value:values){
                        long _id = db.insert(MuseumContract.QuizEntry.TABLE_NAME, null, value);
                        if(_id!= -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case SILHOUETTE:
                db.beginTransaction();
                try{
                    for(ContentValues value:values){
                        long _id = db.insert(MuseumContract.SilhouetteEntry.TABLE_NAME, null, value);
                        if(_id!= -1){
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
