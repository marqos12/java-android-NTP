package studia.quiz;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import studia.quiz.model.Question;
import studia.quiz.model.Subject;

public class Test_demo_begin extends AppCompatActivity {

    public static TextView textView ;
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


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                URL testDemoDetails = null;
                try {
                    testDemoDetails = new URL("https://marqos12.000webhostapp.com/api/demo/details/web");
                    HttpsURLConnection connection = (HttpsURLConnection) testDemoDetails.openConnection();
                    connection.setRequestMethod("GET");
                    if (connection.getResponseCode() == 200) {
                        InputStream responseBody = connection.getInputStream();
                        Test_demo_begin.textView.setText(responseBody.toString());
                        JSONObject jsonObject = new JSONObject(responseBody.toString());

                        connection.disconnect();
                    } else {
                        // Error handling code goes here
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
