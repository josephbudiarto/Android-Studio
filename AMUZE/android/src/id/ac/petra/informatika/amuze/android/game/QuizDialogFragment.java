package id.ac.petra.informatika.amuze.android.game;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import id.ac.petra.informatika.amuze.android.FragmentActivity;
import id.ac.petra.informatika.amuze.android.data.MuseumContract;

/**
 * Created by josephnw on 11/2/2015.
 */
public class QuizDialogFragment extends DialogFragment {
    private int mId;
    private int mGold, mRequirement;
    private int mStatus;
    public interface Callback {
        public void onUpdateGold(int gold);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        mId = args.getInt("id");
        mRequirement = args.getInt("requirement");
        mStatus = args.getInt("status");

        Uri playerUri = MuseumContract.PlayerEntry.buildUri();
        Cursor cursor = getContext().getContentResolver().query(playerUri, new String[]{MuseumContract.PlayerEntry.COLUMN_GOLD}, MuseumContract.PlayerEntry._ID + "=?", new String[]{"1"}, null);
        mGold = 0;
        if (cursor != null && cursor.moveToFirst()) {
            mGold = cursor.getInt(0);
            cursor.close();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        String action;
        if(mStatus == 0)
            action = "Unlock";
        else
            action = "Play";
        String[] itemz={action,"Cancel"};
        builder.setTitle("Quiz")
                .setItems(itemz, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        // Do something with the selection
                        if (item == 0) {
                            if(mStatus == 0) {
                                //unlock - update database, gold dan unlock game
                                if(mGold >= mRequirement) {
                                    ContentValues values = new ContentValues();
                                    //values.put(MuseumContract.PlayerEntry._ID, 1);
                                    values.put(MuseumContract.PlayerEntry.COLUMN_GOLD, (mGold - mRequirement));
                                    String selection = MuseumContract.MuseumEntry._ID + "=?";
                                    String[] args = {"1"};
                                    getContext().getContentResolver().update(MuseumContract.PlayerEntry.CONTENT_URI, values, selection, args);

                                    values.clear();
                                    if(mId == 0)
                                        values.put(MuseumContract.MuseumEntry.QUIZ_EASY_UNLOCK, 1);
                                    else if(mId == 1)
                                        values.put(MuseumContract.MuseumEntry.QUIZ_MEDIUM_UNLOCK, 1);
                                    else if(mId == 2)
                                        values.put(MuseumContract.MuseumEntry.QUIZ_HARD_UNLOCK, 1);
                                    String selection2 = MuseumContract.MuseumEntry._ID + "=?";
                                    String[] args2 = {String.valueOf(FragmentActivity.mMuseumId)};
                                    getContext().getContentResolver().update(MuseumContract.MuseumEntry.CONTENT_URI, values, selection2, args2);

                                    ((Callback) getActivity()).onUpdateGold(mGold - mRequirement);
                                }
                            }
                            else{
                                //play
                                Intent intent = new Intent(getActivity(), QuizGame.class)
                                        .putExtra("id", String.valueOf(mId));
                                startActivity(intent);
                            }
                        }
                    }
                });

        return builder.create();
    }
}