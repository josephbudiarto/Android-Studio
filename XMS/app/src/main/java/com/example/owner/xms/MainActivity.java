package com.example.owner.xms;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView responseView;
    JSONArray jsonArray;
    String value;
    ProgressBar progressBar;
    public static final String API_URL = "https://opensource.petra.ac.id/~rina/request.php?table_req=Museums";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new SimpleTask().execute();
        responseView = (TextView)findViewById(R.id.textView);
    }
    String line;
    BufferedReader bufferedReader;
    HttpURLConnection urlConnection;
    private class SimpleTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            // Create Show ProgressBar
        }

        protected String doInBackground(Void... urls)   {
            /*String result = "";
            try {

                HttpGet httpGet = new HttpGet(urls[0]);
                HttpClient client = new DefaultHttpClient();

                HttpResponse response = client.execute(httpGet);

                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode == 200) {
                    InputStream inputStream = response.getEntity().getContent();
                    BufferedReader reader = new BufferedReader
                            (new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result += line;
                    }
                }

            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }
            return result;*/
            try {
                java.net.URL url = new URL(API_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
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

        protected void onPostExecute(String response)  {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            String data = "";
            try{
                jsonArray = new JSONArray(response);
                for(int i=0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    int id = Integer.parseInt(jsonObject.optString("id").toString());
                    String name = jsonObject.optString("MuseumName").toString();
                    String photo = jsonObject.optString("PhotoPath").toString();
                    data += "Node"+i+" : \n id= "+ id +" \n Name= "+ name +" \n Path= "+ photo +" \n ";
                }
                responseView.setText(data);
            } catch (JSONException e) {e.printStackTrace();}
        }
    }

}