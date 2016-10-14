package id.ac.petra.informatika.amuze.android.game;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import id.ac.petra.informatika.amuze.android.R;
import id.ac.petra.informatika.amuze.android.Utility;

/**
 * Created by josephnw on 11/2/2015.
 */
public class PuzzleAdapter extends CursorAdapter{
    /**
     * Cache of the children views for a list item.
     */
    public static class ViewHolder {
        public final TextView statusView;
        public final TextView goldReqView;
        public final TextView goldRewardView;
        public final ImageView imageView;

        public ViewHolder(View view) {
            statusView = (TextView) view.findViewById(R.id.list_silhouette_status);
            goldReqView = (TextView) view.findViewById(R.id.list_silhouette_gold_requirement);
            goldRewardView = (TextView) view.findViewById(R.id.list_silhouette_gold_reward);
            imageView = (ImageView) view.findViewById(R.id.list_silhouette_image);
        }
    }

    public PuzzleAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_silhouette, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int finish = cursor.getInt(PuzzleList.COL_FINISH);
        int unlock = cursor.getInt(PuzzleList.COL_UNLOCK);
        int gold_reward = cursor.getInt(PuzzleList.COL_GOLD_REWARD);
        int gold_req = cursor.getInt(PuzzleList.COL_GOLD_REQUIREMENT);
        String photo = cursor.getString(PuzzleList.COL_PHOTO);
        if(finish == 1) {
            viewHolder.statusView.setTextColor(0xff00ff66);
            viewHolder.statusView.setText("Finish");
        }
        else if(unlock == 1) {
            viewHolder.statusView.setTextColor(0xff77ff22);
            viewHolder.statusView.setText("Unlocked");
        }
        else {
            viewHolder.statusView.setTextColor(0xffffff00);
            viewHolder.statusView.setText("Locked");
        }
        viewHolder.goldReqView.setText("Gold requirement: "+String.valueOf(gold_req));
        viewHolder.goldRewardView.setText("Gold reward: "+String.valueOf(gold_reward));
        viewHolder.imageView.setImageBitmap(Utility.loadBitmap(context, photo));
    }
}