package id.ac.petra.informatika.amuze.android.game;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;

import id.ac.petra.informatika.amuze.PuzzleGame;
import id.ac.petra.informatika.amuze.android.data.MuseumContract;

public class PuzzleActivity extends AndroidApplication implements PuzzleGame.Callback {
    private String mId;
    private int mGold, mReward;
    AlertDialog dialog;
	@Override
	public void onWin(String id) {
		//update database
        Uri playerUri = MuseumContract.PlayerEntry.buildUri();
        Cursor cursor = getContentResolver().query(playerUri,
                new String[]{MuseumContract.PlayerEntry.COLUMN_GOLD}, MuseumContract.PlayerEntry._ID + "=?", new String[]{"1"}, null);
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
        values.put(MuseumContract.PuzzleEntry.COLUMN_FINISH, 1);
        String selection2 = MuseumContract.PuzzleEntry._ID + "=?";
        String[] args2 = {id};
        this.getContentResolver().update(MuseumContract.PuzzleEntry.CONTENT_URI, values, selection2, args2);
        Log.e("PuzzleUpdate", id);

        //Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT);
        /*
        dialog = new AlertDialog.Builder(PuzzleActivity.this).
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
        */
        //finish();
    }

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/*
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new PuzzleGame(), config);
        */
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			//Toast.makeText(this, "ID ITEM: " + extras.get("id"), Toast.LENGTH_SHORT).show();
			String mId = extras.getString("id");
            Uri puzzleUri = MuseumContract.PuzzleEntry.buildUriById(mId);
            Cursor cursor = getContentResolver().query(puzzleUri,
                    new String[]{MuseumContract.PuzzleEntry.COLUMN_PHOTO, MuseumContract.PuzzleEntry.COLUMN_GOLD_REWARD},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                mReward = cursor.getInt(1);
                PuzzleGame temp = new PuzzleGame(this, cursor.getString(0), mId);

                temp.setPuzzleSize(2,2);
                //temp.setCallBack((MyGdxGame.gameCallBack) this);
                initialize(temp);
                //return initializeForView(temp);
                cursor.close();
            }
		}


	}
}
