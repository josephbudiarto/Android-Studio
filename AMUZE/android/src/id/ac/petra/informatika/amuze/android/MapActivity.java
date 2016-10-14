package id.ac.petra.informatika.amuze.android;

import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import id.ac.petra.informatika.amuze.android.data.MuseumContract;

/**
 * Created by josephnw on 11/4/2015.
 */
public class MapActivity extends AppCompatActivity {
    private MapCanvas mCanvas;
    private int mId;

    private static final String[] MAP_COLUMNS = {
            MuseumContract.MapEntry.TABLE_NAME + "." + MuseumContract.MapEntry._ID,
            MuseumContract.MapEntry.COLUMN_MAP_NAME,
            MuseumContract.MapEntry.COLUMN_PHOTO,
            MuseumContract.MapEntry.COLUMN_GRID
    };
    // These indices are tied to COLUMNS above
    static final int COL_ID = 0;
    static final int COL_NAME = 1;
    static final int COL_PHOTO = 2;
    static final int COL_GRID = 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Bundle extras = getIntent().getExtras();
        mId = extras.getInt("id");

        Uri mapUri = MuseumContract.MapEntry.buildUriById(mId+"");
        Cursor cursor = getContentResolver().query(mapUri, MAP_COLUMNS, null,null, null);

        if (cursor != null && cursor.moveToFirst()) {
            mCanvas = (MapCanvas) findViewById(R.id.map_canvas);
            mCanvas.setBackgroundColor(Color.WHITE);
            //Log.e("MapActivity", cursor.getString(COL_GRID));
            mCanvas.setData(cursor.getString(COL_GRID), cursor.getString(COL_PHOTO));
            //mCanvas.setData("{\"0\":{\"r\":18,\"c\":39,\"s\":5,\"t\":1,\"q\":\"\"},\"1\":{\"r\":19,\"c\":25,\"s\":5,\"t\":1,\"q\":\"\"},\"2\":{\"r\":22,\"c\":30,\"s\":5,\"t\":1,\"q\":\"\"},\"3\":{\"r\":29,\"c\":34,\"s\":5,\"t\":1,\"q\":\"\"},\"4\":{\"r\":32,\"c\":34,\"s\":5,\"t\":1,\"q\":\"\"},\"5\":{\"r\":10,\"c\":53,\"s\":5,\"t\":1,\"q\":\"\"}}");

            cursor.close();
        }


        /*
        MapCanvas canvas;
        canvas = new MapCanvas(this,"");
        canvas.setBackgroundColor(Color.WHITE);
        setContentView(canvas);
        */
        /*
        drawView = new SampleCanvasActivity(this);
        drawView.setBackgroundColor(Color.WHITE);
        setContentView(drawView);
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCanvas.fillItemStatus();
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
