package id.ac.petra.informatika.amuze.android.game;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import id.ac.petra.informatika.amuze.android.FragmentActivity;
import id.ac.petra.informatika.amuze.android.R;
import id.ac.petra.informatika.amuze.android.data.MuseumContract;

/**
 * Created by josephnw on 11/16/2015.
 */
public class QuizGame extends AppCompatActivity {
    class Quiz{
        String question;
        String answer[];
        int correctAnswer;
        Quiz(String q, String answer1, String answer2, String answer3, String answer4, String answer5, int correct){
            question = q;
            answer = new String[5];
            answer[0] = answer1;
            answer[1] = answer2;
            answer[2] = answer3;
            answer[3] = answer4;
            answer[4] = answer5;
            correctAnswer = correct;
        }
    }
    private ArrayList<Quiz> quiz;
    AlertDialog dialog;

    int mAnswer;
    int mLevel;
    int mMaxLevel;
    String difficulty;
    private static final String[] QUIZ_COLUMNS = {
            MuseumContract.QuizEntry.COLUMN_QUESTION,
            MuseumContract.QuizEntry.COLUMN_ANSWER,
            MuseumContract.QuizEntry.COLUMN_ANSWER1,
            MuseumContract.QuizEntry.COLUMN_ANSWER2,
            MuseumContract.QuizEntry.COLUMN_ANSWER3,
            MuseumContract.QuizEntry.COLUMN_ANSWER4,
            MuseumContract.QuizEntry.COLUMN_ANSWER5
    };
    static final int COL_QUESTION = 0;
    static final int COL_ANSWER = 1;
    static final int COL_ANSWER1 = 2;
    static final int COL_ANSWER2 = 3;
    static final int COL_ANSWER3 = 4;
    static final int COL_ANSWER4 = 5;
    static final int COL_ANSWER5 = 6;

    private TextView questionTextView;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private RadioButton radioButton4;
    private RadioButton radioButton5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Bundle extras = getIntent().getExtras();
        difficulty = extras.getString("id");
        quiz = new ArrayList<Quiz>();
        Uri quizUri = MuseumContract.QuizEntry.buildUri();

        questionTextView = (TextView) findViewById(R.id.question_textview);
        radioButton1 = (RadioButton) findViewById(R.id.radio1);
        radioButton2 = (RadioButton) findViewById(R.id.radio2);
        radioButton3 = (RadioButton) findViewById(R.id.radio3);
        radioButton4 = (RadioButton) findViewById(R.id.radio4);
        radioButton5 = (RadioButton) findViewById(R.id.radio5);

        Cursor cursor = getContentResolver().query(quizUri, QUIZ_COLUMNS,
                MuseumContract.QuizEntry.COLUMN_DIFFICULTY + " = ? AND " + MuseumContract.QuizEntry.COLUMN_MUSEUM_KEY + " = ? ",
                new String[]{difficulty, String.valueOf(FragmentActivity.mMuseumId)}, null);
        if (cursor != null){
            while(cursor.moveToNext()){
                quiz.add(new Quiz(cursor.getString(COL_QUESTION), cursor.getString(COL_ANSWER1), cursor.getString(COL_ANSWER2),
                        cursor.getString(COL_ANSWER3), cursor.getString(COL_ANSWER4), cursor.getString(COL_ANSWER5), cursor.getInt(COL_ANSWER)));
            }
        }
        mLevel = 0;
        mMaxLevel = quiz.size();
        nextLevel();
    }

    private void nextLevel(){
        radioButton1.setChecked(false);
        radioButton2.setChecked(false);
        radioButton3.setChecked(false);
        radioButton4.setChecked(false);
        radioButton5.setChecked(false);
        mAnswer = -1;
        mLevel++;
        if(mLevel >= mMaxLevel){
            //done
            //update status finish & gold
            int mGold = 0, mReward = 0;
            Uri playerUri = MuseumContract.PlayerEntry.buildUri();
            Cursor cursor = getContentResolver().query(playerUri, new String[]{MuseumContract.PlayerEntry.COLUMN_GOLD}, MuseumContract.PlayerEntry._ID + "=?", new String[]{"1"}, null);
            if (cursor != null && cursor.moveToFirst()) {
                mGold = cursor.getInt(0);
                cursor.close();
            }
            Uri museumUri = MuseumContract.MuseumEntry.buildUriById(String.valueOf(FragmentActivity.mMuseumId));
            String column="";
            if(difficulty.equals("0"))
                column = MuseumContract.MuseumEntry.QUIZ_EASY_REWARD;
            if(difficulty.equals("1"))
                column = MuseumContract.MuseumEntry.QUIZ_MEDIUM_REWARD;
            if(difficulty.equals("2"))
                column = MuseumContract.MuseumEntry.QUIZ_HARD_REWARD;
            Cursor cursor2 = getContentResolver().query(museumUri, new String[]{ column }, null, null, null);
            if (cursor2 != null && cursor2.moveToFirst()) {
                mReward = cursor2.getInt(0);
                cursor2.close();
            }

            ContentValues values = new ContentValues();
            values.put(MuseumContract.PlayerEntry.COLUMN_GOLD, (mGold + mReward));
            String selection = MuseumContract.PlayerEntry._ID + "=?";
            String[] args = {"1"};
            getContentResolver().update(MuseumContract.PlayerEntry.CONTENT_URI, values, selection, args);

            values.clear();
            values.put(MuseumContract.MuseumEntry._ID, FragmentActivity.mMuseumId);
            if(difficulty.equals("0"))
                values.put(MuseumContract.MuseumEntry.QUIZ_EASY_FINISH, 1);
            if(difficulty.equals("1"))
                values.put(MuseumContract.MuseumEntry.QUIZ_MEDIUM_FINISH, 1);
            if(difficulty.equals("2"))
                values.put(MuseumContract.MuseumEntry.QUIZ_HARD_FINISH, 1);
            String selection2 = MuseumContract.MuseumEntry._ID + "=?";
            String[] args2 = { String.valueOf(FragmentActivity.mMuseumId) };
            getContentResolver().update(MuseumContract.MuseumEntry.CONTENT_URI, values, selection2, args2);

            //Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT);
            dialog = new AlertDialog.Builder(this).
                    setMessage("Correct!").create();
            dialog.show();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                    finish();
                }
            }).start();
//finish();
        }
        else {
            Quiz current = quiz.get(mLevel - 1);
            questionTextView.setText(current.question);
            radioButton1.setText(current.answer[0]);
            radioButton2.setText(current.answer[1]);
            radioButton3.setText(current.answer[2]);
            radioButton4.setText(current.answer[3]);
            radioButton5.setText(current.answer[4]);
        }
    }

    public void quizRadioButton(View view){
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio1:
                if (checked)
                    mAnswer = 1;
                    break;
            case R.id.radio2:
                if (checked)
                    mAnswer = 2;
                break;
            case R.id.radio3:
                if (checked)
                    mAnswer = 3;
                break;
            case R.id.radio4:
                if (checked)
                    mAnswer = 4;
                break;
            case R.id.radio5:
                if (checked)
                    mAnswer = 5;
                break;
        }
    }

    public void send(View view){

        if(mAnswer != -1){
            if(mAnswer == quiz.get(mLevel-1).correctAnswer){
                //berarti benar
                nextLevel();
            }
            else{
                Toast.makeText(this, "Wrong answer, please try again", Toast.LENGTH_LONG).show();
                finish();
            }
        }
        else
            Toast.makeText(this, "Please choose an answer", Toast.LENGTH_SHORT).show();
    }
    public void play(View view) {
        LinearLayout warningLayout, contentLayout;
        warningLayout = (LinearLayout) findViewById(R.id.warning_layout);
        contentLayout = (LinearLayout) findViewById(R.id.content_layout);
        warningLayout.setVisibility(View.GONE);
        contentLayout.setVisibility(View.VISIBLE);
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
}
