package id.ac.petra.informatika.amuze.android;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import id.ac.petra.informatika.amuze.android.data.MuseumContract;

/**
 * Created by josephnw on 11/2/2015.
 */
public class FavoriteDialogFragment extends DialogFragment {
    private int mId;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle extras = getArguments();
        mId = extras.getInt("id");
        //Toast.makeText(getActivity(), "ID ITEM: " + String.valueOf(mId), Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String[] itemz={"View Item","Unfavorite","Cancel"};
        builder.setTitle("Favorite")
                .setItems(itemz, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        if (item == 0) {
                            //intent
                            Intent intent = new Intent(getActivity(), ItemActivity.class)
                                    .putExtra("id", String.valueOf(mId));
                            startActivity(intent);
                        } else if (item == 1) {
                            //update database
                            ContentValues values = new ContentValues();
                            values.put(MuseumContract.ItemEntry._ID, mId);
                            values.put(MuseumContract.ItemEntry.COLUMN_FAV, 0);
                            //update
                            String selection = MuseumContract.ItemEntry._ID + "=?";
                            String[] args = { String.valueOf(mId) };
                            getActivity().getContentResolver().update(MuseumContract.ItemEntry.CONTENT_URI, values, selection, args);
                        }
                    }
                });

        return builder.create();
    }
}
