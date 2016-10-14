package id.ac.petra.informatika.amuze.android;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import id.ac.petra.informatika.amuze.android.data.MuseumContract;
import id.ac.petra.informatika.amuze.android.service.DownloadReceiver;
import id.ac.petra.informatika.amuze.android.service.MuseumService;
import id.ac.petra.informatika.amuze.android.sync.MuseumSyncAdapter;

/**
 * Created by josephnw on 10/6/2015.
 */
public class MuseumFragment extends Fragment implements SearchView.OnQueryTextListener, LoaderManager.LoaderCallbacks<Cursor>, DownloadReceiver.Receiver {
    private MuseumAdapter mAdapter;
    private ListView mListView;
    private Button mButton;
    private SearchView mSearchView;
    private ProgressBar mProgressBar;
    private DownloadReceiver mReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReceiver = new DownloadReceiver(new Handler());
        mReceiver.setReceiver(this);
    }

    private static final int MUSEUM_LOADER = 0;
    private int mSelected = -1;
    private String mSelectedName;
    private int mSelectedProgress;

    private int mLastUpdate = -1;

    // If non-null, this is the current filter the user has provided.
    String mCurFilter;

    private static final String[] MUSEUM_COLUMNS = {
            MuseumContract.MuseumEntry.TABLE_NAME + "." + MuseumContract.MuseumEntry._ID,
            MuseumContract.MuseumEntry.COLUMN_MUSEUM_NAME,
            MuseumContract.MuseumEntry.COLUMN_FLAG_DOWNLOAD
    };
    // These indices are tied to COLUMNS above
    static final int COL_MUSEUM_ID = 0;
    static final int COL_MUSEUM_NAME = 1;
    static final int COL_FLAG_DOWNLOAD = 2;

    @Override
    public boolean onQueryTextSubmit(String arg0) {
        //do nothing
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // Called when the action bar search text has changed.  Update
        // the search filter, and restart the loader to do a new query
        // with this filter.
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
        // Don't do anything if the filter hasn't actually changed.
        // Prevents restarting the loader when restoring state.
        if (mCurFilter == null && newFilter == null) {
            return true;//search kosong
        }
        if (mCurFilter != null && mCurFilter.equals(newFilter)) {
            return true;//search tetap
        }
        mCurFilter = newFilter;
        getLoaderManager().restartLoader(0, null, this);
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        //perlu dicek kalau sedang download baru diset
        mReceiver.setReceiver(this);
        getLoaderManager().restartLoader(0, null, this);
        Uri museumUri = MuseumContract.MuseumEntry.buildUriById(String.valueOf(mSelected));
        Cursor cursor = getActivity().getContentResolver().query(museumUri, MUSEUM_COLUMNS, MuseumContract.MuseumEntry._ID + "=?", new String[]{String.valueOf(mSelected)}, null);
        if( cursor != null && cursor.moveToFirst() ){
            updateView(cursor.getInt(COL_FLAG_DOWNLOAD));
            cursor.close();
        }
    }

    public void onPause() {
        super.onPause();
        mReceiver.setReceiver(null);
    }

    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
            case MuseumService.UPDATE_PROGRESS:
                int progress = resultData.getInt("progress");
                mLastUpdate = resultData.getInt("selected");
                if(mSelected == mLastUpdate) {
                    updateView(progress);
                }
                break;
        }
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {
        public void onItemSelected(Uri dateUri);
    }

    public MuseumFragment() {

    }
    //uncomment for menu functionality
    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateWeather();
            return true;
        }
        if (id == R.id.action_map) {
            openPreferredLocationInMap();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }   */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //search
        mSearchView = (SearchView) rootView.findViewById(R.id.museum_searchview);
        mSearchView.setOnQueryTextListener(this);

        //progress bar
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.museum_progressbar);
/*
        mNotifyManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getActivity());
        mBuilder.setContentTitle("Museum Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.background);
        mBuilder.setProgress(100, 0, false);
        // Displays the progress bar for the first time.
        mNotifyManager.notify(0, mBuilder.build());
        mBuilder.setContentText("Download complete")
                .setProgress(0, 0, false);
        mNotifyManager.notify(10, mBuilder.build());
*/
        //button
        mButton = (Button) rootView.findViewById(R.id.start_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String action;
                if (mSelectedProgress == MuseumContract.MuseumEntry.FLAG_YES)  {
                    Intent intent = new Intent(getActivity(), FragmentActivity.class)
                            .putExtra("id", mSelected);
                    startActivity(intent);
                }
                else {
                    mButton.setVisibility(View.INVISIBLE);
                    //check existing service
                    if(mLastUpdate == -1)
                        mLastUpdate = mSelected;
                    //mark to download
                    if(mSelectedProgress == MuseumContract.MuseumEntry.FLAG_NO) {
                        ContentValues values = new ContentValues();
                        values.put(MuseumContract.MuseumEntry._ID, mSelected);
                        //values.put(MuseumContract.MuseumEntry.COLUMN_MUSEUM_NAME, cursor.getString(COL_MUSEUM_NAME));
                        values.put(MuseumContract.MuseumEntry.COLUMN_FLAG_DOWNLOAD, MuseumContract.MuseumEntry.FLAG_NO + 1);
                        String selection = MuseumContract.MuseumEntry._ID + "=?";
                        String[] args = {String.valueOf(mSelected)};
                        getContext().getContentResolver().update(MuseumContract.MuseumEntry.CONTENT_URI, values, selection, args);
                        reset();
                    }
                    Intent intent = new Intent(getActivity(), MuseumService.class);
                    intent.putExtra(MuseumService.ID_EXTRA, String.valueOf(mSelected));
                    intent.putExtra(MuseumService.NAME_EXTRA, mSelectedName);
                    intent.putExtra(MuseumService.RECEIVER_EXTRA, mReceiver);

                    getActivity().startService(intent);
                }
            }
        });

        // The Adapter will take data from a source and use it to populate the ListView it's attached to.
        mAdapter = new MuseumAdapter(getActivity(), null, 0);
        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_museum);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    //String locationSetting = Utility.getPreferredLocation(getActivity());
                    mSelected = cursor.getInt(COL_MUSEUM_ID);
                    mSelectedName = cursor.getString(COL_MUSEUM_NAME);
                    mSelectedProgress = cursor.getInt(COL_FLAG_DOWNLOAD);

                    updateView(mSelectedProgress);

                    /*
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSetting, cursor.getLong(COL_WEATHER_DATE)));
                    startActivity(intent);
                    */
                    /*
                    ((Callback) getActivity())
                            .onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
                            ));
                    */
                }
                //mPosition = position;
            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        /*
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        */
        return rootView;
    }
    private void reset(){
        getLoaderManager().restartLoader(0, null, this);
    }
    private void updateView(int progress){
        mSelectedProgress = progress;
        if (progress == MuseumContract.MuseumEntry.FLAG_NO) {
            mButton.setText("Download");
            mButton.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
        else if (progress == MuseumContract.MuseumEntry.FLAG_YES){
            mButton.setText("Play");
            mButton.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.INVISIBLE);
        }
        else{
            //downloading
            //start service if it's not running already
            if(mSelected == mLastUpdate) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(progress);
                mButton.setVisibility(View.INVISIBLE);
            }
            else{
                mProgressBar.setVisibility(View.INVISIBLE);
                mButton.setVisibility(View.VISIBLE);
                mButton.setText("Queue Download");
            }

        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MUSEUM_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    // since we read the location when we create the loader, all we need to do is restart things
    /*
    void onLocationChanged( ) {
        updateWeather();
        getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
    }
    */

    private void updateMuseum() {
        MuseumSyncAdapter.syncImmediately(getActivity());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        Uri museumUri;
        if (mCurFilter == null)
            museumUri = MuseumContract.MuseumEntry.buildUri();
        else
            museumUri = MuseumContract.MuseumEntry.buildUriByName(mCurFilter);
        String sortOrder = MuseumContract.MuseumEntry.COLUMN_MUSEUM_NAME + " ASC";
        return new CursorLoader(getActivity(),
                museumUri,
                MUSEUM_COLUMNS,
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
}
