package id.ac.petra.informatika.amuze.android;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import id.ac.petra.informatika.amuze.android.data.MuseumContract;

/**
 * Created by josephnw on 10/6/2015.
 */
public class MapAdapter extends CursorAdapter {
    /**
     * Cache of the children views for a list item.
     */
    public static class ViewHolder {
        public final TextView mapView;

        public ViewHolder(View view) {
            mapView = (TextView) view.findViewById(R.id.list_item_map_textview);
        }
    }

    public MapAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_map, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String name = cursor.getString(MapFragment.COL_NAME);
        viewHolder.mapView.setText(name);
    }
}
