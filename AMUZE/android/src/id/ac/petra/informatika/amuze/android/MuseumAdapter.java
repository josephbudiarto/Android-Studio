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
public class MuseumAdapter extends CursorAdapter {
    /**
     * Cache of the children views for a list item.
     */
    public static class ViewHolder {
        public final TextView museumView;
        public final ImageView imageView;

        public ViewHolder(View view) {
            museumView = (TextView) view.findViewById(R.id.list_item_museum_textview);
            imageView = (ImageView) view.findViewById(R.id.list_item_museum_imageview);
        }
    }

    public MuseumAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_museum, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        String name = cursor.getString(MuseumFragment.COL_MUSEUM_NAME);
        viewHolder.museumView.setText(name);


        if (cursor.getInt(MuseumFragment.COL_FLAG_DOWNLOAD) == MuseumContract.MuseumEntry.FLAG_NO)
            viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.download));
        else if(cursor.getInt(MuseumFragment.COL_FLAG_DOWNLOAD) < MuseumContract.MuseumEntry.FLAG_YES)
            viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.download_progress));
        else
            viewHolder.imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.downloaded));

    }
}
