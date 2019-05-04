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
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import studia.quiz.model.Subject;
import studia.quiz.model.User;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public class TestBegin extends AppCompatActivity {
    public static TextView about;
    public static TextView rules;
    public String id;
    public ProgressDialog progress;
    String points = "";
    Button showPoints;

    Subject subject;
    String getTestDetailsURL = "http://marqos12.000webhostapp.com/api/subject/details/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_begin);
        final String id = getIntent().getStringExtra("id");
        final String JWT = getIntent().getStringExtra("jwt");



        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("testName", Context.MODE_PRIVATE);
            outputStream.write(id.getBytes());
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
                Intent intent = new Intent(TestBegin.this, MainStudent.class);
                startActivity(intent);
                finish();
            }
        });

        Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestBegin.this, Test.class);
                intent.putExtra("id", id);
                intent.putExtra("jwt", JWT);
                intent.putExtra("multipleChoice", Integer.toString(subject.getMultipleChoice()));
                intent.putExtra("separatePage", Integer.toString(subject.getSeparatePage()));
                startActivity(intent);
                finish();
            }
        });

        showPoints = findViewById(R.id.buttonPoints);
        showPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textPoints = findViewById(R.id.textPoints);
                textPoints.setText(points);
                showPoints.setVisibility(View.INVISIBLE);
            }
        });

        try {

            new GetDetails().execute(getTestDetailsURL+id,JWT);
            progress = ProgressDialog.show(TestBegin.this, "Pobieranie danych ...", "Oczekowanie na dane...", true);
        } catch (Exception e) {

        }





    }

    private class GetDetails extends AsyncTask<String, Void, String> {
        private OkHttpClient mClient = new OkHttpClient();

        protected String doInBackground(String... url){


            try {
                com.squareup.okhttp.Request request = new Request
                        .Builder()
                        .header("Authorization", "Bearer "+url[1])
                        .url(url[0])
                        .build();
                mClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

                Response response = mClient.newCall(request).execute();
                String stringResponse = response.body().string();
                return stringResponse;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        protected void onPostExecute(String subjectDetailsString) {
           Log.d("quiz1",subjectDetailsString);
             try {
                JSONObject jsonObject = new JSONObject(subjectDetailsString);
                if(jsonObject.has("error")){
                    //loginFailed.setText("Logowanie nie powiodło się");
                }
                else {
                    subject = new Subject(jsonObject);
                    about.setText(getApplicationContext().getString(R.string.testName, subject.getName(), subject.getSubject()));
                    rules.setText(getApplicationContext().getString(R.string.rules, getApplicationContext().getString((subject.getMultipleChoice().equals(1) ? R.string.multiplyTrue : R.string.multiplyFalse)),
                            subject.getTime(), subject.getNoQuestions(), subject.getNoQuestions()));
                    id = subject.getId().toString();
                    Integer maxPoints = subject.getNoQuestions();

                    points = getApplicationContext().getString(R.string.points, floor(maxPoints * 0.59), ceil(maxPoints * 0.60),
                    floor(maxPoints * 0.64), ceil(maxPoints * 0.65), floor(maxPoints * 0.74), ceil(maxPoints * 0.75), floor(maxPoints * (0.84)), ceil(maxPoints * (0.85)),
                    floor(maxPoints * 0.94), ceil(maxPoints * 0.95), maxPoints);

                    progress.dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
