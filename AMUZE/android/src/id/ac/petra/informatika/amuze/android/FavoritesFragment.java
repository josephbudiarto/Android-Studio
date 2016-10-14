package id.ac.petra.informatika.amuze.android;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import id.ac.petra.informatika.amuze.android.data.MuseumContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private FavoritesAdapter mAdapter;
    private ListView mListView;

    public FavoritesFragment() {
        // Required empty public constructor
    }
    private static final int FAVORITE_LOADER = 0;
    private static final String[] ITEM_COLUMNS = {
            MuseumContract.ItemEntry.TABLE_NAME + "." + MuseumContract.ItemEntry._ID,
            MuseumContract.ItemEntry.COLUMN_ITEM_NAME,
            MuseumContract.ItemEntry.COLUMN_PHOTO
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_ITEM_ID = 0;
    static final int COL_ITEM_NAME = 1;
    static final int COL_ITEM_PHOTO = 2;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_favorites, container, false);

        mAdapter = new FavoritesAdapter(getActivity(), null, 0);
        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_favorites);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    //Toast.makeText(getActivity(), "ID ITEM: " + String.valueOf(cursor.getInt(COL_ITEM_ID)), Toast.LENGTH_SHORT).show();
                    Bundle args = new Bundle();
                    args.putInt("id", cursor.getInt(COL_ITEM_ID));
                    FavoriteDialogFragment fragment = new FavoriteDialogFragment();
                    fragment.setArguments(args);
                    fragment.show(getActivity().getSupportFragmentManager(), "favorite");
                    //String locationSetting = Utility.getPreferredLocation(getActivity());
                    /*
                    mSelected = cursor.getInt(COL_MUSEUM_ID);
                    mSelectedName = cursor.getString(COL_MUSEUM_NAME);
                    mSelectedProgress = cursor.getInt(COL_FLAG_DOWNLOAD);
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(locationSetting, cursor.getLong(COL_WEATHER_DATE)));
                    startActivity(intent);
                    ((Callback) getActivity())
                            .onItemSelected(WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                                    locationSetting, cursor.getLong(COL_WEATHER_DATE)
                            ));
                    */

                }
            }
        });
        /*
        String[] favArray = {
                "Barang1",
                "Barang2. Lorem ipsum second item",
                "Barang3",
                "Barang4",
                "Barang5",
                "Barang6"
        };
        List<String> favList = new ArrayList<String>(Arrays.asList(favArray));
        mAdapter = new ArrayAdapter<String>(
                getActivity(), //current context (this fragment's parent activity
                R.layout.list_item_favorites, //ID of list item layout
                R.id.list_item_favorites_textview, //id of textview to populate
                favList);
        mListView = (ListView) rootView.findViewById(R.id.listview_favorites);
        mListView.setAdapter(mAdapter);
        */

        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FAVORITE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri itemUri;
        itemUri = MuseumContract.ItemEntry.buildUriFav(String.valueOf(FragmentActivity.mMuseumId));
        String sortOrder = MuseumContract.ItemEntry.COLUMN_ITEM_NAME + " ASC";
        return new CursorLoader(getActivity(),
                itemUri,
                ITEM_COLUMNS,
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