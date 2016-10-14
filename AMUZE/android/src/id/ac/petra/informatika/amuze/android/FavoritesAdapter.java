package id.ac.petra.informatika.amuze.android;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by josephnw on 10/6/2015.
 */
public class FavoritesAdapter extends CursorAdapter {
    /**
     * Cache of the children views for a list item.
     */
    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView textView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.list_item_favorites_imageview);
            textView = (TextView) view.findViewById(R.id.list_item_favorites_textview);
        }

    }

    public FavoritesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_favorites, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        // Read weather forecast from cursor
        String name = cursor.getString(FavoritesFragment.COL_ITEM_NAME);
        viewHolder.textView.setText(name);
        if(!cursor.isNull(FavoritesFragment.COL_ITEM_PHOTO)&&cursor.getString(FavoritesFragment.COL_ITEM_PHOTO).isEmpty() == false){
            viewHolder.imageView.setImageBitmap(Utility.loadBitmap(context, cursor.getString(FavoritesFragment.COL_ITEM_PHOTO)));
        }
        //viewHolder.museumView.setTag(TAG_FLAG_DOWNLOAD, Integer.valueOf(cursor.getInt(MuseumFragment.COL_FLAG_DOWNLOAD)));
    }
}
