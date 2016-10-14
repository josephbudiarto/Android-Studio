package id.ac.petra.informatika.amuze.android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import id.ac.petra.informatika.amuze.android.data.MuseumContract.MuseumEntry;
import id.ac.petra.informatika.amuze.android.data.MuseumContract.ItemEntry;
import id.ac.petra.informatika.amuze.android.data.MuseumContract.MapEntry;
import id.ac.petra.informatika.amuze.android.data.MuseumContract.PuzzleEntry;
import id.ac.petra.informatika.amuze.android.data.MuseumContract.QuizEntry;
import id.ac.petra.informatika.amuze.android.data.MuseumContract.SilhouetteEntry;
import id.ac.petra.informatika.amuze.android.data.MuseumContract.PlayerEntry;
/**
 * Created by josephnw on 10/7/2015.
 */
public class MuseumDbHelper extends SQLiteOpenHelper{
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "museum.db";

    public MuseumDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MUSEUM_TABLE = "CREATE TABLE " + MuseumEntry.TABLE_NAME + " (" +
                MuseumEntry._ID + " INTEGER PRIMARY KEY," +
                MuseumEntry.COLUMN_MUSEUM_NAME + " TEXT UNIQUE NOT NULL, " +
                MuseumEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                MuseumEntry.COLUMN_PHOTO + " TEXT, " +
                //tambah quiz"
                MuseumEntry.QUIZ_EASY_FINISH + " INTEGER NOT NULL, " +
                MuseumEntry.QUIZ_EASY_UNLOCK + " INTEGER NOT NULL, " +
                MuseumEntry.QUIZ_EASY_REQUIREMENT + " INTEGER NOT NULL, " +
                MuseumEntry.QUIZ_EASY_REWARD + " INTEGER NOT NULL, " +
                MuseumEntry.QUIZ_MEDIUM_FINISH + " INTEGER NOT NULL, " +
                MuseumEntry.QUIZ_MEDIUM_UNLOCK + " INTEGER NOT NULL, " +
                MuseumEntry.QUIZ_MEDIUM_REQUIREMENT + " INTEGER NOT NULL, " +
                MuseumEntry.QUIZ_MEDIUM_REWARD + " INTEGER NOT NULL, " +
                MuseumEntry.QUIZ_HARD_FINISH + " INTEGER NOT NULL, " +
                MuseumEntry.QUIZ_HARD_UNLOCK + " INTEGER NOT NULL, " +
                MuseumEntry.QUIZ_HARD_REQUIREMENT + " INTEGER NOT NULL, " +
                MuseumEntry.QUIZ_HARD_REWARD + " INTEGER NOT NULL, " +
                MuseumEntry.COLUMN_FLAG_DOWNLOAD + " INTEGER NOT NULL " +
                " );";
        final String SQL_CREATE_ITEM_TABLE = "CREATE TABLE " + ItemEntry.TABLE_NAME + " (" +
                ItemEntry._ID + " INTEGER PRIMARY KEY," +
                ItemEntry.COLUMN_MUSEUM_KEY + " INTEGER NOT NULL, " +
                ItemEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                ItemEntry.COLUMN_PHOTO + " TEXT, " +
                ItemEntry.COLUMN_AUDIO + " TEXT, " +
                ItemEntry.COLUMN_SCAN + " INTEGER, " +
                ItemEntry.COLUMN_FAV + " INTEGER, " +
                ItemEntry.COLUMN_VIDEO + " TEXT " +
                " );";
        final String SQL_CREATE_MAP_TABLE = "CREATE TABLE " + MapEntry.TABLE_NAME + " (" +
                MapEntry._ID + " INTEGER PRIMARY KEY," +
                MapEntry.COLUMN_MUSEUM_KEY + " INTEGER NOT NULL, " +
                MapEntry.COLUMN_MAP_NAME + " TEXT NOT NULL, " +
                MapEntry.COLUMN_PHOTO + " TEXT NOT NULL, " +
                MapEntry.COLUMN_GRID + " TEXT NOT NULL " +
                " );";
        final String SQL_CREATE_PUZZLE_TABLE = "CREATE TABLE " + PuzzleEntry.TABLE_NAME + " (" +
                PuzzleEntry._ID + " INTEGER PRIMARY KEY," +
                PuzzleEntry.COLUMN_MUSEUM_KEY + " INTEGER NOT NULL, " +
                PuzzleEntry.COLUMN_PHOTO + " TEXT NOT NULL, " +
                PuzzleEntry.COLUMN_GOLD_REQUIREMENT + " INTEGER NOT NULL, " +
                PuzzleEntry.COLUMN_GOLD_REWARD + " INTEGER NOT NULL, " +

                PuzzleEntry.COLUMN_UNLOCK + " INTEGER NOT NULL, " +
                PuzzleEntry.COLUMN_FINISH + " INTEGER NOT NULL " +
                " );";
        final String SQL_CREATE_QUIZ_TABLE = "CREATE TABLE " + QuizEntry.TABLE_NAME + " (" +
                QuizEntry._ID + " INTEGER PRIMARY KEY," +
                QuizEntry.COLUMN_MUSEUM_KEY + " INTEGER NOT NULL, " +
                QuizEntry.COLUMN_QUESTION + " TEXT NOT NULL, " +
                QuizEntry.COLUMN_ANSWER1 + " TEXT NOT NULL, " +
                QuizEntry.COLUMN_ANSWER2 + " TEXT NOT NULL, " +
                QuizEntry.COLUMN_ANSWER3 + " TEXT NOT NULL, " +
                QuizEntry.COLUMN_ANSWER4 + " TEXT NOT NULL, " +
                QuizEntry.COLUMN_ANSWER5 + " TEXT NOT NULL, " +
                QuizEntry.COLUMN_ANSWER + " TEXT NOT NULL, " +
                //to delete
                /*
                QuizEntry.COLUMN_GOLD_REQUIREMENT + " INTEGER NOT NULL, " +
                QuizEntry.COLUMN_GOLD_REWARD + " INTEGER NOT NULL, " +
                QuizEntry.COLUMN_UNLOCK + " INTEGER NOT NULL, " +
                QuizEntry.COLUMN_FINISH + " INTEGER NOT NULL, " +
                */
                //difficulty
                QuizEntry.COLUMN_DIFFICULTY + " INTEGER NOT NULL " +
                " );";
        final String SQL_CREATE_SILHOUETTE_TABLE = "CREATE TABLE " + SilhouetteEntry.TABLE_NAME + " (" +
                SilhouetteEntry._ID + " INTEGER PRIMARY KEY," +
                SilhouetteEntry.COLUMN_MUSEUM_KEY + " INTEGER NOT NULL, " +
                SilhouetteEntry.COLUMN_ITEM_KEY + " INTEGER NOT NULL, " +
                SilhouetteEntry.COLUMN_PHOTO + " TEXT NOT NULL, " +
                SilhouetteEntry.COLUMN_GOLD_REQUIREMENT + " INTEGER NOT NULL, " +
                SilhouetteEntry.COLUMN_GOLD_REWARD + " INTEGER NOT NULL, " +

                SilhouetteEntry.COLUMN_UNLOCK + " INTEGER NOT NULL, " +
                SilhouetteEntry.COLUMN_FINISH + " INTEGER NOT NULL " +
                " );";

        final String SQL_CREATE_PLAYER_TABLE = "CREATE TABLE " + PlayerEntry.TABLE_NAME + " (" +
                PlayerEntry._ID + " INTEGER PRIMARY KEY," +
                PlayerEntry.COLUMN_GOLD + " INTEGER NOT NULL " +
                " );";
        final String SQL_PREPARE_PLAYER_TABLE = "INSERT INTO " + PlayerEntry.TABLE_NAME + " VALUES(1,20);";
        sqLiteDatabase.execSQL(SQL_CREATE_MUSEUM_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_ITEM_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_MAP_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PUZZLE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_QUIZ_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_SILHOUETTE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_PLAYER_TABLE);
        sqLiteDatabase.execSQL(SQL_PREPARE_PLAYER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MuseumEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ItemEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MapEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PuzzleEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + QuizEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + SilhouetteEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PlayerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
