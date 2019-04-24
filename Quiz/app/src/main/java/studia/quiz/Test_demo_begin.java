package studia.quiz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import studia.quiz.model.Question;
import studia.quiz.model.Subject;

public class Test_demo_begin extends AppCompatActivity {

    public static TextView textView;
    public ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_demo_begin);
        Intent intent = getIntent();
        String message = intent.getStringExtra("name");


        textView = findViewById(R.id.textView4);
        textView.setText(message);
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Test_demo_begin.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Test_demo_begin.this, Test_demo.class);
                startActivity(intent);
            }
        });

        //request(message);

        ///////////////////////////////////////////////////////////////kurwa wątek




        ///////////////////////////////////////////////

        try
        {
           String url = "http://marqos12.000webhostapp.com/api/demo/details/" + message;
            GetTestDetails getTestDetails = new GetTestDetails();
            getTestDetails.execute(url);
          //  progress = ProgressDialog.show(Test_demo_begin.this, "Getting Data ...", "Waiting For Results...", true);
        }
        catch (Exception e)
        {
            Log.e("gej", e.getMessage());
        }

    }
/*
    void request(final String details) {
        String url = "http://marqos12.000webhostapp.com/api/demo/details/" + details;

        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(this);
        StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                textView.setText(response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    textView.setText(jsonObject.toString());
                    Subject subject = new Subject(jsonObject);
                    textView.setText(subject.getName());
                } catch (Exception e) {
                    Toast.makeText(Test_demo_begin.this, "jebli", Toast.LENGTH_SHORT).show();
                    Log.d("jebło", e.getMessage());
                    request(details);
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText("#jebło!");
                Log.d("jebło", error.toString());
                request(details);
                //This code is executed if there is an error.
            }
        });
        ExampleRequestQueue.add(ExampleStringRequest);

    }*/

    private class GetTestDetails extends AsyncTask<String, Void, String> {

        private static final String TAG = "AARestTask";
        public static final String HTTP_RESPONSE = "httpResponse";

        private Context mContext;
        private OkHttpClient mClient = new OkHttpClient()   ;
        private String mAction;

        public void GetTestDetails(Context context, String action)
        {
            mContext = context;
            mAction = action;
            mClient = new  OkHttpClient();
        }

        public void  GetTestDetails(Context context, String action, OkHttpClient client)
        {
            mContext = context;
            mAction = action;
            mClient = client;
        }

        @Override
        protected String doInBackground(String... url)
        {
            String stringResponse="";
            try
            {
                Log.e("gej","start");
                Request request = new Request
                        .Builder()
                        .method("GET",null)
                        .url(url[0])
                        .build();
                Response response = mClient.newCall(request).execute();

                Log.e("gej", "po = "+response.body().string());
                stringResponse = response.body().string();
                return stringResponse ;

                /*mClient.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(Request request, IOException e) {

                    }

                    @Override
                    public void onResponse(Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        } else {
                            // do something wih the result

                        }
                    }
                });*/
            }
            catch (Exception e)
            {
                // TODO handle this properly
                Log.e("gej", "błond");
                e.printStackTrace();
                return stringResponse;
            }
        }

        /**
         * `onPostExecute` is run after `doInBackground`, and it's
         * run on the main/ui thread, so you it's safe to update ui
         * components from it. (this is the correct way to update ui
         * components.)
         */
        @Override
        protected void onPostExecute(String result)
        {
            Log.i("gej", "RESULT = " + result);
            Test_demo_begin.textView.setText(result);
            //Intent intent = new Intent(mAction);
            //intent.putExtra(HTTP_RESPONSE, result);

            // broadcast the completion
            //mContext.sendBroadcast(intent);
        }

    }


}
