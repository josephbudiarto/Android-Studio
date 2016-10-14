package com.example.owner.gson;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

/*import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
*/
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static final String URL = "https://opensource.petra.ac.id/~rina/coba.php";
            //"http://blog.teamtreehouse.com/api/get_recent_summary/";

    private ListView mListView;
    private CustomAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listView);

        new SimpleTask().execute();

        mListView.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    //String str = adapterView.getItemAtPosition(i).toString();
                    //Toast.makeText(getApplicationContext(), str,Toast.LENGTH_LONG).show();

                    TextView label=(TextView) view.findViewById(R.id.post_id);
                    String xyz = label.getText().toString();
                    Toast.makeText(MainActivity.this, xyz, Toast.LENGTH_LONG).show();

                    Intent newIntent = new Intent(getApplicationContext(), NewActivity.class);
                    newIntent.putExtra("key",xyz);
                    startActivity(newIntent);
                }
            }
        );
    }

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
                java.net.URL url = new URL(URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
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

        protected void onPostExecute(String jsonString)  {
            showData(jsonString);
        }
    }

    private void showData(String jsonString) {
        Gson gson = new Gson();
        Blog blog = gson.fromJson(jsonString, Blog.class);
        List<Post> posts = blog.getPosts();


        mAdapter = new CustomAdapter(this, posts);
        mListView.setAdapter(mAdapter);
    }

}