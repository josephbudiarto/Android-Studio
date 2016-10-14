package com.example.owner.gson;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewActivity extends AppCompatActivity {
    ImageView imageView;
    TextView responseView;
    TextView titleView;
    JSONArray jsonArray;
    String value;
    ProgressBar progressBar;
    String data = "";
    String title ="";
    String photo ="";
    public static final String API_URL = "https://opensource.petra.ac.id/~rina/requestArticle.php?article_id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("key");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        imageView = (ImageView)findViewById(R.id.imageView);
        titleView = (TextView)findViewById(R.id.title_view);
        responseView = (TextView)findViewById(R.id.textView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        new RetrieveFeedTask().execute();

    }
    private class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
            titleView.setText("");
        }

        protected String doInBackground(Void... urls) {
            try {
                URL urll = new URL(API_URL + value);
                HttpURLConnection urlConnection = (HttpURLConnection) urll.openConnection();
                try {
                    BufferedReader bufferedReaderr = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilderr = new StringBuilder();
                    String line;
                    while ((line = bufferedReaderr.readLine()) != null) {
                        stringBuilderr.append(line).append("\n");
                    }
                    bufferedReaderr.close();
                    return stringBuilderr.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            try{
                jsonArray = new JSONArray(response);
                for(int i=0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    //int id = Integer.parseInt(jsonObject.optString("idArticle").toString());
                    String title_content = jsonObject.optString("ArticleTitle").toString();
                    String content = jsonObject.optString("ArticleText").toString();
                    String author = jsonObject.optString("AuthorName").toString();
                    String photo_path = jsonObject.optString("ArticlePhotoPath").toString();
                    title += title_content + "\n";
                    data += content +" \n ";
                    photo += "https://opensource.petra.ac.id/~rina/"+photo_path;
                }
                titleView.setText(title);
                responseView.setText(data);
                Picasso.with(getParent()).load(photo).placeholder(R.drawable.load).error(R.drawable.error).into(imageView);
            } catch (JSONException e) {e.printStackTrace();}
        }
    }
}