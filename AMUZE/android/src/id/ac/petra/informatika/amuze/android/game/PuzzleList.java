package id.ac.petra.informatika.amuze.android.game;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import id.ac.petra.informatika.amuze.android.FragmentActivity;
import id.ac.petra.informatika.amuze.android.R;
import id.ac.petra.informatika.amuze.android.data.MuseumContract;

/**
 * Created by josephnw on 11/1/2015.
 */
public class PuzzleList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, PuzzleDialogFragment.Callback {
    private PuzzleAdapter mAdapter;
    private ListView mListView;
    private TextView mCurrentGold;

    private static final int PUZZLE_LOADER = 0;
    private static final String[] PUZZLE_COLUMNS = {
            MuseumContract.PuzzleEntry.TABLE_NAME + "." + MuseumContract.PuzzleEntry._ID,
            MuseumContract.PuzzleEntry.COLUMN_FINISH,
            MuseumContract.PuzzleEntry.COLUMN_UNLOCK,
            MuseumContract.PuzzleEntry.COLUMN_GOLD_REQUIREMENT,
            MuseumContract.PuzzleEntry.COLUMN_GOLD_REWARD,
            MuseumContract.PuzzleEntry.COLUMN_PHOTO
    };
    // These indices are tied to COLUMNS above
    static final int COL_ID = 0;
    static final int COL_FINISH = 1;
    static final int COL_UNLOCK = 2;
    static final int COL_GOLD_REQUIREMENT = 3;
    static final int COL_GOLD_REWARD = 4;
    static final int COL_PHOTO = 5;

    public PuzzleList() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list_silhouette);
        // The Adapter will take data from a source and use it to populate the ListView it's attached to.
        mAdapter = new PuzzleAdapter(this, null, 0);
        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) findViewById(R.id.listview_silhouette);
        mListView.setAdapter(mAdapter);
        getLoaderManager().initLoader(PUZZLE_LOADER, null, this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    if (cursor.getInt(COL_FINISH) != 1)
                    {
                        Bundle args = new Bundle();
                        args.putInt("id", cursor.getInt(COL_ID));
                        args.putInt("status", cursor.getInt(COL_UNLOCK));
                        args.putInt("requirement", cursor.getInt(COL_GOLD_REQUIREMENT));
                        PuzzleDialogFragment fragment = new PuzzleDialogFragment();
                        fragment.setArguments(args);
                        fragment.show(getSupportFragmentManager(), PuzzleList.class.getSimpleName());
                    }

                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        //gold
        mCurrentGold = (TextView) findViewById(R.id.current_gold);
        Uri playerUri = MuseumContract.PlayerEntry.buildUri();
        Cursor cursor = getContentResolver().query(playerUri, new String[]{MuseumContract.PlayerEntry.COLUMN_GOLD}, MuseumContract.PlayerEntry._ID + "=?", new String[]{"1"}, null);

        if (cursor != null && cursor.moveToFirst()) {
            mCurrentGold.setText("Current Gold: " + cursor.getInt(0));
            cursor.close();
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri queryUri;
        queryUri = MuseumContract.PuzzleEntry.buildUriByMuseumId(String.valueOf(FragmentActivity.mMuseumId));
        String sortOrder = MuseumContract.PuzzleEntry.COLUMN_GOLD_REWARD + " ASC";
        return new CursorLoader(this,
                queryUri,
                PUZZLE_COLUMNS,
                null,
                null,
                sortOrder);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
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

    @Override
    public void onUpdateGold(int gold) {
        mCurrentGold.setText("Current Gold: " + gold);
    }
}
