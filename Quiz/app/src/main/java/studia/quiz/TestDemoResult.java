package studia.quiz;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
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
import java.io.InputStream;
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
    Gson gson = new Gson();
    Integer counter = new Integer(0);
    Button seeAnswers;
    Boolean loaded = false;
    RelativeLayout mainRelativeLayout;
    List<RelativeLayout> allRLayouts = new ArrayList<RelativeLayout>();
    String multipleChoice;

    String getDemoQuestionsWAURL;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_demo_result);
        Intent intent = getIntent();
        getDemoQuestionsWAURL =getApplicationContext().getString(R.string.url, "/api/subjects/demo/withAnswers/");
        String result = intent.getStringExtra("result");
        String answers = intent.getStringExtra("answers");
         name = intent.getStringExtra("name");
        multipleChoice = intent.getStringExtra("multipleChoice");
        mainRelativeLayout = findViewById(R.id.main);
        isPassedText = findViewById(R.id.textSummaryPos);
        summarry = findViewById(R.id.textSummary);

        final Button back2Button = findViewById(R.id.buttonBack2);
        final Button repeat2Button = findViewById(R.id.buttonRepeat2);

        try {
            resultObj = new Result(new JSONObject(result));
            summarry.setText(getApplicationContext().getString(R.string.summaryGet, resultObj.getCorrect(), resultObj.getTotal(),
                    ((resultObj.getCorrect().floatValue() / resultObj.getTotal().floatValue() * 100.0f)), '%'));
            if ((resultObj.getCorrect().floatValue() / resultObj.getTotal().floatValue()) > 0.6f) {
                isPassedText.setText(getApplicationContext().getString(R.string.summaryPositive));
                isPassedText.setTextColor(getResources().getColor(R.color.textPositive));
            } else {
                isPassedText.setText(getApplicationContext().getString(R.string.summaryNegative));
                isPassedText.setTextColor(getResources().getColor(R.color.textNegative));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        seeAnswers = findViewById(R.id.buttonSeeAnswers);
        seeAnswers.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loaded) {
                    showAnswers();
                    back2Button.setVisibility(View.VISIBLE);
                    repeat2Button.setVisibility(View.VISIBLE);
                }
            }
        });
        seeAnswers.setBackgroundColor(getResources().getColor(R.color.buttonBackgroundDisable));
        seeAnswers.setTextColor(getResources().getColor(R.color.buttonTextDisable));

        final Button backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestDemoResult.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        final Button repeatButton = findViewById(R.id.buttonRepeat);
        repeatButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = "";
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

                Intent intent = new Intent(TestDemoResult.this, Test_demo_begin.class);
                intent.putExtra("name", name);
                startActivity(intent);
                finish();
            }
        });

        back2Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                backButton.callOnClick();
            }
        });

        repeat2Button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatButton.callOnClick();
            }
        });

        new CheckAnswer().execute(name);
        try {
            JSONArray questionsJason = new JSONArray(answers);
            for (int i = 0; i < questionsJason.length(); i++) {
                JSONObject jsonObject = questionsJason.getJSONObject(i);
                Question question = new Question(jsonObject);
                questions.add(question);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private class CheckAnswer extends AsyncTask<String, Void, String> {
        private OkHttpClient mClient = new OkHttpClient();

        protected String doInBackground(String... name) {

            String url = getDemoQuestionsWAURL;
            try {
                com.squareup.okhttp.Request request = new Request
                        .Builder()
                        .url(url + name[0])
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


        protected void onPostExecute(String response) {


            try {
                JSONObject jsonObject = new JSONObject(response);
                JSONArray questionsJArray = jsonObject.getJSONArray("questions");
                for (int i = 0; i < questionsJArray.length(); i++) {
                    Log.e("quiz1",questionsJArray.getJSONObject(i).toString());
                    JSONObject Jquestion = questionsJArray.getJSONObject(i);
                    Question question = new Question(Jquestion);
                    for(int j = 0; j < 4; j++) questions.get(i).getAnswers().get(j).setValue(question.getAnswers().get(j).getStatus());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
                seeAnswers.setText(getApplicationContext().getString(R.string.seeAnswers));
                loaded = true;
                seeAnswers.setBackgroundColor(getResources().getColor(R.color.buttonBackgroundGray));
                seeAnswers.setTextColor(getResources().getColor(R.color.blackText));

        }
    }


    private void showAnswers() {
        Integer i = new Integer(1);
        for (Question question : questions) {


            RelativeLayout relativeLayout = new RelativeLayout(getApplicationContext());
            relativeLayout.setId(View.generateViewId());
            RelativeLayout.LayoutParams rparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

            if (i > 1) rparam.addRule(RelativeLayout.BELOW, allRLayouts.get(i - 2).getId());
            relativeLayout.setLayoutParams(rparam);

            TextView questionNumber = new TextView(getApplicationContext());
            questionNumber.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            questionNumber.setText(i.toString() + "/" + questions.size());
            questionNumber.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.quizcard));
            questionNumber.setTextColor(getResources().getColor(R.color.textWhite));
            questionNumber.setId(View.generateViewId());
            relativeLayout.addView(questionNumber);

            TextView questionText = new TextView(getApplicationContext());
            RelativeLayout.LayoutParams paramsText = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsText.addRule(RelativeLayout.BELOW, questionNumber.getId());
            questionText.setLayoutParams(paramsText);
            questionText.setText(questions.get(i - 1).getText());
            questionText.setTextSize(16);
            questionText.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.quizcard));
            questionText.setTextColor(getResources().getColor(R.color.textWhite));
            questionText.setId(View.generateViewId());
            relativeLayout.addView(questionText);

            RadioGroup radioGroup = new RadioGroup(getApplicationContext());
            RelativeLayout.LayoutParams paramsText2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsText2.addRule(RelativeLayout.BELOW, questionText.getId());
            paramsText2.setMargins(0, 60, 0, 60);
            radioGroup.setLayoutParams(paramsText2);
            radioGroup.setId(View.generateViewId());

            Boolean positive = true;
            if (multipleChoice.equals("false")) {
                for (int j = 0; j < 4; j++) {
                    RadioButton answer1 = new RadioButton(getApplicationContext());
                    RadioGroup.LayoutParams paramsAnswer1 = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    paramsAnswer1.setMargins(0, 10, 0, 0);
                    answer1.setLayoutParams(paramsAnswer1);
                    answer1.setId(View.generateViewId());
                    answer1.setEnabled(false);
                    answer1.setChecked(question.getAnswers().get(j).getStatus());
                    if (question.getAnswers().get(j).getStatus()) {
                        if (question.getAnswers().get(j).getValue()) {
                            answer1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.positive, 0);
                        } else {
                            answer1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.negative, 0);
                            positive = false;
                        }
                    } else if (question.getAnswers().get(j).getValue()) {
                        positive = false;
                        answer1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.positive, 0);
                    }
                    answer1.setText(questions.get(i - 1).getAnswers().get(j).getText());
                    answer1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.quizcard));
                    radioGroup.addView(answer1);
                }
            } else {
                for (int j = 0; j < 4; j++) {
                    CheckBox answer1 = new CheckBox(getApplicationContext());
                    RadioGroup.LayoutParams paramsAnswer1 = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    paramsAnswer1.setMargins(0, 10, 0, 0);
                    answer1.setLayoutParams(paramsAnswer1);
                    answer1.setId(View.generateViewId());
                    answer1.setEnabled(false);
                    answer1.setChecked(question.getAnswers().get(j).getStatus());
                    if (question.getAnswers().get(j).getStatus()) {
                        if (question.getAnswers().get(j).getValue()) {
                            answer1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.positive, 0);
                        } else {
                            answer1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.negative, 0);
                            positive = false;
                        }
                    } else if (question.getAnswers().get(j).getValue()) {
                        positive = false;
                        answer1.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.positive, 0);
                    }
                    answer1.setText(questions.get(i - 1).getAnswers().get(j).getText());
                    answer1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.quizcard));
                    radioGroup.addView(answer1);
                }
            }

            relativeLayout.addView(radioGroup);
            if (positive)
                questionNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.positive, 0);
            else
                questionNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.negative, 0);
            if (questions.get(i - 1).getCode() != null) {
                TextView questionCode = new TextView(getApplicationContext());
                RelativeLayout.LayoutParams paramsCode = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsCode.addRule(RelativeLayout.BELOW, questionText.getId());
                paramsCode.setMargins(0, 10, 0, 0);
                questionCode.setLayoutParams(paramsCode);
                questionCode.setTextColor(getResources().getColor(R.color.codeBorder));
                questionCode.setText(questions.get(i - 1).getCode());
                questionCode.setTextSize(14);
                questionCode.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.codecard));
                questionCode.setId(View.generateViewId());
                relativeLayout.addView(questionCode);
                paramsText2.addRule(RelativeLayout.BELOW, questionCode.getId());
                radioGroup.setLayoutParams(paramsText2);
            }

            if (questions.get(i - 1).getImage() != null) {
                ImageView questionImage = new ImageView(getApplicationContext());
                RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                paramsImage.addRule(RelativeLayout.BELOW, questionText.getId());
                paramsImage.setMargins(0, 10, 0, 0);
                questionImage.setLayoutParams(paramsImage);
                questionImage.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.codecard));
                questionImage.setId(View.generateViewId());
                new DownloadImageTask(questionImage).execute(questions.get(i - 1).getImage());
                relativeLayout.addView(questionImage);

                paramsText2.addRule(RelativeLayout.BELOW, questionImage.getId());
                radioGroup.setLayoutParams(paramsText2);
            }

            allRLayouts.add(relativeLayout);
            mainRelativeLayout.addView(relativeLayout);
            i++;
        }

        ((RelativeLayout.LayoutParams) allRLayouts.get(0).getLayoutParams()).setMargins(0, 0, 0, 0);
        RelativeLayout.LayoutParams params3 = ((RelativeLayout.LayoutParams) seeAnswers.getLayoutParams());
        params3.addRule(RelativeLayout.BELOW, allRLayouts.get(i - 2).getId());
        seeAnswers.setLayoutParams(params3);
        seeAnswers.setText(getApplicationContext().getString(R.string.hideAnswers));
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
