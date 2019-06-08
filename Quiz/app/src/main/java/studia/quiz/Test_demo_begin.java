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

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public class Test_demo_begin extends AppCompatActivity {

    public static TextView about;
    public static TextView rules;
    public String id;
    public ProgressDialog progress;
    String points = "";
    Button showPoints;
    Subject subject;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_demo_begin);
        Intent intent = getIntent();
        final String message = intent.getStringExtra("name");
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
                intent.putExtra("name", message);
                intent.putExtra("multipleChoice", Boolean.toString(subject.getMultipleChoice()));
                intent.putExtra("separatePage", Boolean.toString(subject.getSeparatePage()));
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
            JSONObject jsonObject = new JSONObject(getApplicationContext().getString((message.equals("demojava")) ? R.string.demojava : R.string.demoweb));

            Subject result = subject = new Subject(jsonObject);
            Test_demo_begin.about.setText(getApplicationContext().getString(R.string.testName, result.getName(), (result.getSubject().equals("web") ? "Technologie Interetowe" : (result.getSubject().equals("java") ? "Język Java" : result.getSubject()))));
            Test_demo_begin.rules.setText(getApplicationContext().getString(R.string.rules, getApplicationContext().getString((result.getMultipleChoice() ? R.string.multiplyTrue : R.string.multiplyFalse)),
                    result.getTime(), result.getNoQuestions(), result.getNoQuestions()));
            id = result.getId().toString();
            Integer maxPoints = result.getNoQuestions();

            points = getApplicationContext().getString(R.string.points, floor(maxPoints * 0.59), ceil(maxPoints * 0.60),
                    floor(maxPoints * 0.64), ceil(maxPoints * 0.65), floor(maxPoints * 0.74), ceil(maxPoints * 0.75), floor(maxPoints * (0.84)), ceil(maxPoints * (0.85)),
                    floor(maxPoints * 0.94), ceil(maxPoints * 0.95), maxPoints);
        }
        catch (Exception e){
            Log.e("quiz1",e.getMessage());
        }
    }
}
