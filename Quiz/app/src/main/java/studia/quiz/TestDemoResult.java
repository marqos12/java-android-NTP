package studia.quiz;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import studia.quiz.model.Answer;
import studia.quiz.model.Question;
import studia.quiz.model.Result;

public class TestDemoResult extends AppCompatActivity {

    TextView summarry;
    TextView isPassedText;
    Result resultObj;
    List<Question> questions = new ArrayList<Question>();
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Gson gson = new Gson();
    Integer counter = new Integer(0);
    Button seeAnswers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_demo_result);
        Intent intent = getIntent();
        String result = intent.getStringExtra("result");
        Log.d("quiz",result);
        String answers = intent.getStringExtra("answers");

        isPassedText = findViewById(R.id.textSummaryPos);
        summarry = findViewById(R.id.textSummary);
        try {
            resultObj = new Result(new JSONObject(result));

            summarry.setText(getApplicationContext().getString(R.string.summaryGet,resultObj.getCorrect(),resultObj.getTotal(),
                    ((resultObj.getCorrect().floatValue()/resultObj.getTotal().floatValue()*100.0f)),'%'));

            if((resultObj.getCorrect().floatValue()/resultObj.getTotal().floatValue())>0.5f){isPassedText.setText(getApplicationContext().getString(R.string.summaryPositive));isPassedText.setTextColor(getResources().getColor(R.color.textPositive));}
            else {isPassedText.setText(getApplicationContext().getString(R.string.summaryNegative));isPassedText.setTextColor(getResources().getColor(R.color.textNegative));}
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        seeAnswers = findViewById(R.id.buttonSeeAnswers);
        seeAnswers.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });
        Drawable img = getApplicationContext().getResources().getDrawable( R.drawable.exfl);
        //img.setBounds( 10, 10, 10, 10 );

        seeAnswers.setCompoundDrawablesWithIntrinsicBounds( img, null, null, null);




        Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( TestDemoResult.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button repeatButton = findViewById(R.id.buttonRepeat);
        repeatButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name="";
                FileInputStream inputStream;
                try {
                    inputStream = openFileInput("demoName");
                    InputStreamReader isr = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(isr);
                    name = bufferedReader.readLine();

                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent( TestDemoResult.this, Test_demo_begin.class);
                intent.putExtra("name",name);
                startActivity(intent);
                finish();
            }
        });

        try {
            JSONArray questionsJason = new JSONArray(answers);
            for (int i = 0; i < questionsJason.length();i++){
                JSONObject jsonObject = questionsJason.getJSONObject(i);
                Question question =  new Question(jsonObject);
                questions.add(question);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private class CheckAnswer extends AsyncTask<Question, Void, Question> {

        private OkHttpClient mClient = new OkHttpClient()   ;

            protected Question doInBackground(Question... question) {
            String url = "http://marqos12.000webhostapp.com/api/question/demo/WA/";
            Question respQuestion = question[0];
            try {
                com.squareup.okhttp.Request request = new Request
                        .Builder()
                        .url(url+Integer.toString(respQuestion.getId()))
                        .build();
                mClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

                Response response = mClient.newCall(request).execute();
                String stringResponse = response.body().string();
                JSONObject jsonObject = new JSONObject(stringResponse);
                Question newQuestion = new Question(jsonObject);
                int i = 0;
                for (Answer answer : respQuestion.getAnswers()){
                    answer.setTrueAnswer(newQuestion.getAnswers().get(i).getValue());
                    i++;
                }
                respQuestion = new Question(jsonObject);
                return respQuestion;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
                return null;//mIcon11;
        }

        protected void onPostExecute(Question result) {
            // bmImage.setImageBitmap(result);
            counter++;
            if(counter == questions.size()-1){

            }

        }
    }




}
