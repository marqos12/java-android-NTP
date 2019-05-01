package studia.quiz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.json.JSONObject;

import java.io.FileOutputStream;

import studia.quiz.model.Subject;

public class Test_demo_begin extends AppCompatActivity {

    public static TextView about;
    public static TextView rules;
    public String id;
    public ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_demo_begin);
        Intent intent = getIntent();
        String message = intent.getStringExtra("name");

        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("demoName", Context.MODE_PRIVATE);
            outputStream.write(message.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        about = findViewById(R.id.about1);
        rules = findViewById(R.id.rules);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Test_demo_begin.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Test_demo_begin.this, Test_demo.class);
                intent.putExtra("id", id);
                startActivity(intent);
                finish();
            }
        });

        try
        {
           String url = "http://marqos12.000webhostapp.com/api/demo/details/" + message;
            GetTestDetails getTestDetails = new GetTestDetails();
            getTestDetails.execute(url);
            progress = ProgressDialog.show(Test_demo_begin.this, "Pobieranie danych ...", "Oczekowanie na dane...", true);
        }
        catch (Exception e)
        {
            Log.e("quiz", e.getMessage());
        }

    }

    private class GetTestDetails extends AsyncTask<String, Void, Subject> {

        private OkHttpClient mClient = new OkHttpClient()   ;

        @Override
        protected Subject doInBackground(String... url)
        {
            String stringResponse="";
            try
            {
                Request request = new Request
                        .Builder()
                        .method("GET",null)
                        .url(url[0])
                        .build();
                Response response = mClient.newCall(request).execute();
                stringResponse = response.body().string();
                JSONObject jsonObject = new JSONObject(stringResponse);
                Subject subject = new Subject(jsonObject);
                return subject ;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Subject result)
        {
            Test_demo_begin.about.setText(getApplicationContext().getString(R.string.testName,result.getName(),result.getSubject()));
            Test_demo_begin.rules.setText(getApplicationContext().getString(R.string.rules,getApplicationContext().getString((result.getMultipleChoice().equals(1)?R.string.multiplyTrue:R.string.multiplyFalse)),
                    result.getTime(),result.getNoQuestions(),result.getNoQuestions()));
            id=result.getId().toString();
            progress.dismiss();
        }
    }
}
