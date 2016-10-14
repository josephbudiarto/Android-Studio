package id.ac.petra.informatika.amuze.android;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import id.ac.petra.informatika.amuze.android.data.MuseumContract;
import id.ac.petra.informatika.amuze.android.game.SilhouetteAdapter;
import id.ac.petra.informatika.amuze.android.game.SilhouetteDialogFragment;

public class MapFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    private MapAdapter mAdapter;
    private ListView mListView;
    private Button mButton;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private static final int MAP_LOADER = 0;

    private static final String[] MAP_COLUMNS = {
            MuseumContract.MapEntry.TABLE_NAME + "." + MuseumContract.MapEntry._ID,
            MuseumContract.MapEntry.COLUMN_MAP_NAME
    };
    // These indices are tied to COLUMNS above
    static final int COL_ID = 0;
    static final int COL_NAME = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        /*
        mButton = (Button) rootView.findViewById(R.id.button_map);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapActivity.class)
                        .putExtra("id", String.valueOf(0));
                startActivity(intent);
            }
        });
        */
        // The Adapter will take data from a source and use it to populate the ListView it's attached to.
        mAdapter = new MapAdapter(getActivity(), null, 0);
        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_map);
        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    Intent intent = new Intent(getActivity(), MapActivity.class)
                            .putExtra("id", cursor.getInt(COL_ID));
                    startActivity(intent);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MAP_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri mapUri = MuseumContract.MapEntry.buildUriByMuseumId(String.valueOf(FragmentActivity.mMuseumId));
        String sortOrder = MuseumContract.MapEntry.COLUMN_MAP_NAME + " ASC";
        return new CursorLoader(getActivity(),
                mapUri,
                MAP_COLUMNS,
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