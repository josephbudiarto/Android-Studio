package id.ac.petra.informatika.amuze.android;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import id.ac.petra.informatika.amuze.android.data.MuseumContract;

/**
 * Created by josephnw on 10/26/2015.
 */
public class ItemActivity extends AppCompatActivity {
    private static final String[] ITEM_COLUMNS = {
            MuseumContract.ItemEntry.TABLE_NAME + "." + MuseumContract.ItemEntry._ID,
            MuseumContract.ItemEntry.COLUMN_ITEM_NAME,
            MuseumContract.ItemEntry.COLUMN_DESCRIPTION,
            MuseumContract.ItemEntry.COLUMN_PHOTO,
            MuseumContract.ItemEntry.COLUMN_VIDEO,
            MuseumContract.ItemEntry.COLUMN_AUDIO,
            MuseumContract.ItemEntry.COLUMN_SCAN,
            MuseumContract.ItemEntry.COLUMN_FAV
    };
    static final int COL_ITEM_ID = 0;
    static final int COL_ITEM_NAME = 1;
    static final int COL_ITEM_DESCRIPTION = 2;
    static final int COL_ITEM_PHOTO = 3;
    static final int COL_ITEM_VIDEO = 4;
    static final int COL_ITEM_AUDIO = 5;
    static final int COL_ITEM_SCAN = 6;
    static final int COL_ITEM_FAV = 7;
    private String mItemId;
    private ImageButton mFavButton;
    private Button mVideoButton, mAudioButton;
    private int mFavorite;
    private MediaPlayer mediaPlayer;
    private String mVideo, mAudio;

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null) mediaPlayer.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Bundle extras = getIntent().getExtras();
        //if (extras != null) {        }
        mItemId = extras.getString("id");

        Uri itemUri = MuseumContract.ItemEntry.buildUriById(mItemId);
        Cursor cursor = getContentResolver().query(itemUri, ITEM_COLUMNS, MuseumContract.ItemEntry._ID + "=?", new String[]{mItemId}, null);

        if (cursor != null && cursor.moveToFirst()) {
            TextView nameTextView = (TextView) findViewById(R.id.item_name_textview);
            TextView descriptionTextView = (TextView) findViewById(R.id.item_description_textview);
            ImageView itemImageView = (ImageView) findViewById(R.id.item_imageview);

            nameTextView.setText(cursor.getString(COL_ITEM_NAME));
            descriptionTextView.setText(cursor.getString(COL_ITEM_DESCRIPTION));


            if (!cursor.isNull(COL_ITEM_PHOTO) && !cursor.getString(COL_ITEM_PHOTO).isEmpty() ) {
                itemImageView.setImageBitmap(Utility.loadBitmap(this, cursor.getString(COL_ITEM_PHOTO)));
            }
            //youtube
            mVideoButton = (Button) findViewById(R.id.item_video_button);
            if (!cursor.isNull(COL_ITEM_VIDEO) && !cursor.getString(COL_ITEM_VIDEO).isEmpty() ) {
                mVideo = cursor.getString(COL_ITEM_VIDEO);
                mVideoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        watchYoutubeVideo(mVideo);
                    }
                });
            }
            else
                mVideoButton.setVisibility(View.INVISIBLE);
            //audio
            mAudioButton = (Button) findViewById(R.id.item_audio_button);
            if (!cursor.isNull(COL_ITEM_AUDIO) && !cursor.getString(COL_ITEM_AUDIO).isEmpty() ) {
                mAudio = cursor.getString(COL_ITEM_AUDIO);
                mAudioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            FileInputStream fileInputStream = openFileInput(mAudio);//new FileInputStream(getFilesDir().);
                            //fileInputStream.getFD();
                            //mediaPlayer.setDataSource(fileInputStream.getFD());
                            /*
                            Uri myUri = ....; // initialize Uri here
                            MediaPlayer mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            mediaPlayer.setDataSource(getApplicationContext(), myUri);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            */
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                            try {
                                mediaPlayer.setDataSource(fileInputStream.getFD());
                            } catch (IllegalArgumentException e1) {
                                e1.printStackTrace();
                            } catch (IllegalStateException e1) {
                                e1.printStackTrace();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            try {
                                mediaPlayer.prepare();
                            } catch (IllegalStateException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            mediaPlayer.setLooping(false);
                            mediaPlayer.start();
                        }
                        catch(FileNotFoundException e){

                        }

                    }
                });
            }
            else
                mAudioButton.setVisibility(View.INVISIBLE);

            mFavorite = cursor.getInt(COL_ITEM_FAV);
            mFavButton = (ImageButton) findViewById(R.id.item_toggleFav);
            if (mFavorite == 0) {
                mFavButton.setImageDrawable(getResources().getDrawable(R.drawable.fav));
            } else {
                mFavButton.setImageDrawable(getResources().getDrawable(R.drawable.unfav));
            }
            if (cursor.getInt(COL_ITEM_SCAN) == 0) {
                TextView scanTextView = (TextView) findViewById(R.id.item_scan);
                scanTextView.setText("Not scanned");
            }
            cursor.close();
        }

    }

    public void toggleFav(View v) {
        int newFav;
        if (mFavorite == 0) {
            newFav = 1;
        } else {
            newFav = 0;
        }

        //update database newFav
        ContentValues values = new ContentValues();
        values.put(MuseumContract.ItemEntry._ID, mItemId);
        values.put(MuseumContract.ItemEntry.COLUMN_FAV, newFav);
        //update
        String selection = MuseumContract.ItemEntry._ID + "=?";
        String[] args = { String.valueOf(mItemId) };
        this.getContentResolver().update(MuseumContract.ItemEntry.CONTENT_URI, values, selection, args);

        mFavorite = newFav;

        if (mFavorite == 0) {
            mFavButton.setImageDrawable(getResources().getDrawable(R.drawable.fav));
        } else {
            mFavButton.setImageDrawable(getResources().getDrawable(R.drawable.unfav));
        }
    }


    private void watchYoutubeVideo(String id){
        try{
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + id));
            startActivity(intent);
        }catch (ActivityNotFoundException ex){
            Intent intent=new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + id));
            startActivity(intent);
        }
    }

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

