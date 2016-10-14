package id.ac.petra.informatika.amuze.android.game;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import id.ac.petra.informatika.amuze.android.FragmentActivity;
import id.ac.petra.informatika.amuze.android.R;
import id.ac.petra.informatika.amuze.android.data.MuseumContract;

/**
 * Created by josephnw on 11/8/2015.
 */
public class QuizList extends AppCompatActivity implements QuizDialogFragment.Callback {
    //private ImageButton mButtonEasy, mButtonMedium, mButtonHard;
    private TextView mCurrentGold;
    private TextView quizStatusEasy, quizStatusMedium, quizStatusHard, quizRequirementEasy, quizRequirementMedium, quizRequirementHard,
            quizRewardEasy, quizRewardMedium, quizRewardHard;
    private LinearLayout quizEasy, quizMedium, quizHard;
    private int quizFinish[] = new int[3];
    private int quizUnlock[] = new int[3];
    private int quizRequirement[] = new int[3];
    private int quizReward[] = new int[3];
    private static final String[] MUSEUM_COLUMNS = {
            MuseumContract.MuseumEntry.QUIZ_HARD_REWARD,
            MuseumContract.MuseumEntry.QUIZ_HARD_REQUIREMENT,
            MuseumContract.MuseumEntry.QUIZ_HARD_UNLOCK,
            MuseumContract.MuseumEntry.QUIZ_HARD_FINISH,
            MuseumContract.MuseumEntry.QUIZ_EASY_REWARD,
            MuseumContract.MuseumEntry.QUIZ_EASY_REQUIREMENT,
            MuseumContract.MuseumEntry.QUIZ_EASY_UNLOCK,
            MuseumContract.MuseumEntry.QUIZ_EASY_FINISH,
            MuseumContract.MuseumEntry.QUIZ_MEDIUM_REWARD,
            MuseumContract.MuseumEntry.QUIZ_MEDIUM_REQUIREMENT,
            MuseumContract.MuseumEntry.QUIZ_MEDIUM_UNLOCK,
            MuseumContract.MuseumEntry.QUIZ_MEDIUM_FINISH
    };
    static final int COL_QUIZ_HARD_REWARD = 0;
    static final int COL_QUIZ_HARD_REQUIREMENT = 1;
    static final int COL_QUIZ_HARD_UNLOCK = 2;
    static final int COL_QUIZ_HARD_FINISH = 3;
    static final int COL_QUIZ_EASY_REWARD = 4;
    static final int COL_QUIZ_EASY_REQUIREMENT = 5;
    static final int COL_QUIZ_EASY_UNLOCK = 6;
    static final int COL_QUIZ_EASY_FINISH = 7;
    static final int COL_QUIZ_MEDIUM_REWARD = 8;
    static final int COL_QUIZ_MEDIUM_REQUIREMENT = 9;
    static final int COL_QUIZ_MEDIUM_UNLOCK = 10;
    static final int COL_QUIZ_MEDIUM_FINISH = 11;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_quiz);
        quizStatusEasy = (TextView) findViewById(R.id.quiz_status_easy);
        quizStatusMedium = (TextView) findViewById(R.id.quiz_status_medium);
        quizStatusHard = (TextView) findViewById(R.id.quiz_status_hard);
        quizRequirementEasy = (TextView) findViewById(R.id.quiz_requirement_easy);
        quizRequirementMedium = (TextView) findViewById(R.id.quiz_requirement_medium);
        quizRequirementHard = (TextView) findViewById(R.id.quiz_requirement_hard);
        quizRewardEasy = (TextView) findViewById(R.id.quiz_reward_easy);
        quizRewardMedium = (TextView) findViewById(R.id.quiz_reward_medium);
        quizRewardHard = (TextView) findViewById(R.id.quiz_reward_hard);

        quizEasy = (LinearLayout) findViewById(R.id.quiz_easy);
        quizMedium = (LinearLayout) findViewById(R.id.quiz_medium);
        quizHard = (LinearLayout) findViewById(R.id.quiz_hard);

        quizEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quizFinish[0] != 1) {
                    Bundle args = new Bundle();
                    args.putInt("id", 0);
                    args.putInt("status", quizUnlock[0]);
                    args.putInt("requirement", quizRequirement[0]);
                    QuizDialogFragment fragment = new QuizDialogFragment();
                    fragment.setArguments(args);
                    fragment.show(getSupportFragmentManager(), QuizList.class.getSimpleName());
                }
            }
        });
        quizMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quizFinish[1] != 1) {
                    Bundle args = new Bundle();
                    args.putInt("id", 1);
                    args.putInt("status", quizUnlock[1]);
                    args.putInt("requirement", quizRequirement[1]);
                    QuizDialogFragment fragment = new QuizDialogFragment();
                    fragment.setArguments(args);
                    fragment.show(getSupportFragmentManager(), QuizList.class.getSimpleName());
                }
            }
        });
        quizHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quizFinish[2] != 1) {
                    Bundle args = new Bundle();
                    args.putInt("id", 2);
                    args.putInt("status", quizUnlock[2]);
                    args.putInt("requirement", quizRequirement[2]);
                    QuizDialogFragment fragment = new QuizDialogFragment();
                    fragment.setArguments(args);
                    fragment.show(getSupportFragmentManager(), QuizList.class.getSimpleName());
                }
            }
        });
        //perlu cek apakah quiz ada isinya easy-medium-hard?
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

        Uri quizUri = MuseumContract.MuseumEntry.buildUriById(String.valueOf(FragmentActivity.mMuseumId));
        cursor = getContentResolver().query(quizUri, MUSEUM_COLUMNS, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            quizRequirementEasy.setText("Gold requirement: " + cursor.getInt(COL_QUIZ_EASY_REQUIREMENT));
            quizRequirementMedium.setText("Gold requirement: " + cursor.getInt(COL_QUIZ_MEDIUM_REQUIREMENT));
            quizRequirementHard.setText("Gold requirement: " + cursor.getInt(COL_QUIZ_HARD_REQUIREMENT));
            quizRewardEasy.setText("Gold reward: " + cursor.getInt(COL_QUIZ_EASY_REWARD));
            quizRewardMedium.setText("Gold reward: " + cursor.getInt(COL_QUIZ_MEDIUM_REWARD));
            quizRewardHard.setText("Gold reward: " + cursor.getInt(COL_QUIZ_HARD_REWARD));
            quizFinish[0] = cursor.getInt(COL_QUIZ_EASY_FINISH);
            quizFinish[1] = cursor.getInt(COL_QUIZ_MEDIUM_FINISH);
            quizFinish[2] = cursor.getInt(COL_QUIZ_HARD_FINISH);
            quizUnlock[0] = cursor.getInt(COL_QUIZ_EASY_UNLOCK);
            quizUnlock[1] = cursor.getInt(COL_QUIZ_MEDIUM_UNLOCK);
            quizUnlock[2] = cursor.getInt(COL_QUIZ_HARD_UNLOCK);
            if(quizFinish[0] == 1){
                quizStatusEasy.setTextColor(0xff00ff66);
                quizStatusEasy.setText("Finished");
            }
            else if(quizUnlock[0] == 1){
                quizStatusEasy.setTextColor(0xff77ff22);
                quizStatusEasy.setText("Unlocked");
            }
            else{
                quizStatusEasy.setTextColor(0xffffff00);
                quizStatusEasy.setText("Locked");
            }
            if(quizFinish[1] == 1){
                quizStatusMedium.setTextColor(0xff00ff66);
                quizStatusMedium.setText("Finished");
            }
            else if(quizUnlock[1] == 1){
                quizStatusMedium.setTextColor(0xff77ff22);
                quizStatusMedium.setText("Unlocked");
            }
            else{
                quizStatusMedium.setTextColor(0xffffff00);
                quizStatusMedium.setText("Locked");
            }
            if(quizFinish[2] == 1){
                quizStatusHard.setTextColor(0xff00ff66);
                quizStatusHard.setText("Finished");
            }
            else if(quizUnlock[2] == 1){
                quizStatusHard.setTextColor(0xff77ff22);
                quizStatusHard.setText("Unlocked");
            }
            else{
                quizStatusHard.setTextColor(0xffffff00);
                quizStatusHard.setText("Locked");
            }
            quizRequirement[0] = cursor.getInt(COL_QUIZ_EASY_REQUIREMENT);
            quizRequirement[1] = cursor.getInt(COL_QUIZ_MEDIUM_REQUIREMENT);
            quizRequirement[2] = cursor.getInt(COL_QUIZ_HARD_REQUIREMENT);
            quizReward[0] = cursor.getInt(COL_QUIZ_EASY_REWARD);
            quizReward[1] = cursor.getInt(COL_QUIZ_MEDIUM_REWARD);
            quizReward[2] = cursor.getInt(COL_QUIZ_HARD_REWARD);
            cursor.close();
        }
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
        onResume();
    }
}
