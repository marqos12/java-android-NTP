package studia.quiz;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import studia.quiz.model.Question;
import studia.quiz.model.Subject;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public class Test extends AppCompatActivity {


    Button buttonNext;
    Button buttonPrev;
    Button buttonAccept;
    List<Question> questions2;

    RelativeLayout mainRelativeLayout;
    List<RelativeLayout> allRLayouts = new ArrayList<RelativeLayout>();
    Integer questionIndex = Integer.valueOf(0);
    Subject quizDetails;
    String separatePage;
    String multipleChoice;
    public ProgressDialog progress;
    CountDownTimer cTimer = null;
    List<TextView> watchList = new ArrayList<TextView>();
    String timeString = "";
    private GestureDetector myGestureDectector;
    String JWT;
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Gson gson = new Gson();
    Boolean collapsedRules = true;

    String checkAnswersURL = "http://marqos12.000webhostapp.com/api/question/checkWR";
    String getTestDetailsURL = "http://marqos12.000webhostapp.com/api/subject/details/";
    String getQuestionsURL = "http://marqos12.000webhostapp.com/api/question/WAFQ/";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        JWT = intent.getStringExtra("jwt");
        separatePage = intent.getStringExtra("separatePage");
        multipleChoice = intent.getStringExtra("multipleChoice");

        AndroidGestureDectector androidGestureDectector = new AndroidGestureDectector();
        myGestureDectector = new GestureDetector(Test.this, androidGestureDectector);

        findViewById(R.id.main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return myGestureDectector.onTouchEvent(event);
            }
        });

        buttonNext = findViewById(R.id.button2);

        buttonPrev = findViewById(R.id.prevBtn);

        if (separatePage.equals("1")) {

            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nextQuestion();
                }
            });
            buttonPrev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prevQuestion();
                }
            });

        } else {
            buttonNext.setVisibility(View.INVISIBLE);
            buttonPrev.setVisibility(View.INVISIBLE);
        }

        buttonAccept = findViewById(R.id.accept);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswers();
                if (cTimer != null)
                    cTimer.cancel();
            }
        });

        final TextView about = findViewById(R.id.about1);
        final TextView rules = findViewById(R.id.rules);
        final TextView ponts = findViewById(R.id.points);
        Button buttonRules = findViewById(R.id.rulesButton);
        buttonRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (collapsedRules) {
                    collapsedRules = !collapsedRules;
                    about.setText(getApplicationContext().getString(R.string.testName, quizDetails.getName(), quizDetails.getSubject()));
                    rules.setText(getApplicationContext().getString(R.string.rules, getApplicationContext().getString((quizDetails.getMultipleChoice().equals(1) ? R.string.multiplyTrue : R.string.multiplyFalse)),
                            quizDetails.getTime(), quizDetails.getNoQuestions(), quizDetails.getNoQuestions()));
                    int maxPoints = quizDetails.getNoQuestions();
                    ponts.setText(getApplicationContext().getString(R.string.pointsInline, floor(maxPoints * 0.59), ceil(maxPoints * 0.60),
                            floor(maxPoints * 0.64), ceil(maxPoints * 0.65), floor(maxPoints * 0.74), ceil(maxPoints * 0.75), floor(maxPoints * (0.84)), ceil(maxPoints * (0.85)),
                            floor(maxPoints * 0.94), ceil(maxPoints * 0.95), maxPoints));
                } else {
                    collapsedRules = !collapsedRules;
                    about.setText("");
                    rules.setText("");
                    ponts.setText("");
                }
            }
        });

        mainRelativeLayout = findViewById(R.id.mainRelativeLayout);

        try {
            GetTestDetails getTestDetails = new GetTestDetails();
            getTestDetails.execute(getQuestionsURL + id,JWT);
            progress = ProgressDialog.show(Test.this, "Pobieranie danych ...", "Oczekowanie na dane...", true);
        } catch (Exception e) {

        }

        new GetQuizDetails().execute(getTestDetailsURL + id);
    }



    private class GetTestDetails extends AsyncTask<String, Void, List<Question>> {

        private OkHttpClient mClient = new OkHttpClient();

        @Override
        protected List<Question> doInBackground(String... url) {
            String stringResponse = "";
            List<Question> questions = new ArrayList<Question>();
            try {
                com.squareup.okhttp.Request request = new Request
                        .Builder()
                        .header("Authorization", "Bearer "+url[1])
                        .method("GET", null)
                        .url(url[0])
                        .build();

                mClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

                Response response = mClient.newCall(request).execute();
                stringResponse = response.body().string();
                JSONArray jsonArray = new JSONArray(stringResponse);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Question question = new Question(jsonObject);
                    questions.add(question);
                }

                return questions;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @SuppressLint("ResourceAsColor")
        @Override
        protected void onPostExecute(List<Question> questions) {
            questions2 = questions;
            Integer i = new Integer(1);
            for (Question question : questions) {
                RelativeLayout relativeLayout = new RelativeLayout(getApplicationContext());
                relativeLayout.setId(View.generateViewId());
                RelativeLayout.LayoutParams rparam = new RelativeLayout.LayoutParams(Resources.getSystem().getDisplayMetrics().widthPixels, RelativeLayout.LayoutParams.WRAP_CONTENT);
                if (separatePage.equals("1")) {
                    rparam.setMargins(Resources.getSystem().getDisplayMetrics().widthPixels, 0, 0, 0);
                    rparam.addRule(RelativeLayout.BELOW, R.id.rel);
                } else {
                    if (i > 1) rparam.addRule(RelativeLayout.BELOW, allRLayouts.get(i - 2).getId());
                    else rparam.addRule(RelativeLayout.BELOW, R.id.rel);

                }

                relativeLayout.setLayoutParams(rparam);

                TextView questionNumber = new TextView(getApplicationContext());
                RelativeLayout.LayoutParams questionNumberParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                questionNumberParam.setMargins(0, 20, 0, 0);
                questionNumber.setLayoutParams(questionNumberParam);
                questionNumber.setText(i.toString() + "/" + questions.size());
                questionNumber.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.quizcard));
                questionNumber.setTextColor(getResources().getColor(R.color.textWhite));
                questionNumber.setId(View.generateViewId());
                relativeLayout.addView(questionNumber);

                TextView questionText = new TextView(getApplicationContext());
                RelativeLayout.LayoutParams paramsText = new RelativeLayout.LayoutParams(Resources.getSystem().getDisplayMetrics().widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT);
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

                if (multipleChoice.equals("0")) {
                    for (int j = 0; j < 4; j++) {
                        RadioButton answer1 = new RadioButton(getApplicationContext());
                        RadioGroup.LayoutParams paramsAnswer1 = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        paramsAnswer1.setMargins(0, 10, 0, 0);
                        answer1.setLayoutParams(paramsAnswer1);
                        answer1.setId(View.generateViewId());
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
                        answer1.setText(questions.get(i - 1).getAnswers().get(j).getText());
                        answer1.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.quizcard));
                        radioGroup.addView(answer1);
                    }
                }
                relativeLayout.addView(radioGroup);

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

                TextView questionTimer = new TextView(getApplicationContext());
                RelativeLayout.LayoutParams questionTimerParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                questionTimerParam.addRule(RelativeLayout.ALIGN_BOTTOM, questionNumber.getId());
                questionTimerParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                questionTimerParam.setMargins(0, 0, 0, 10);
                questionTimer.setLayoutParams(questionTimerParam);
                questionTimer.setCompoundDrawablesWithIntrinsicBounds(R.drawable.watch, 0, 0, 0);
                questionTimer.setText("20:00");
                questionTimer.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.quizcard));
                questionTimer.setTextColor(getResources().getColor(R.color.textWhite));
                questionTimer.setId(View.generateViewId());
                relativeLayout.addView(questionTimer);
                watchList.add(questionTimer);
                allRLayouts.add(relativeLayout);
                mainRelativeLayout.addView(relativeLayout);
                i++;
            }

            ((RelativeLayout.LayoutParams) allRLayouts.get(0).getLayoutParams()).setMargins(0, 0, 0, 0);

            RelativeLayout.LayoutParams params3 = ((RelativeLayout.LayoutParams) buttonNext.getLayoutParams());

            if (separatePage.equals("1"))
                params3.addRule(RelativeLayout.BELOW, allRLayouts.get(questionIndex).getId());
            else params3.addRule(RelativeLayout.BELOW, allRLayouts.get(i - 2).getId());
            buttonNext.setLayoutParams(params3);

            progress.dismiss();
        }
    }


    class AndroidGestureDectector implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            if (motionEvent.getX() < motionEvent1.getX()) {
                if (separatePage.equals("1")) prevQuestion();
            }
            if (motionEvent.getX() > motionEvent1.getX()) {
                if (separatePage.equals("1")) nextQuestion();
            }
            return false;
        }
    }

    private void nextQuestion() {
        if (questionIndex < allRLayouts.size() - 1) {
            final Integer id = questionIndex;
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) allRLayouts.get(id).getLayoutParams();
                    params.setMargins((int) (-Resources.getSystem().getDisplayMetrics().widthPixels * interpolatedTime), 0, (int) (Resources.getSystem().getDisplayMetrics().widthPixels * interpolatedTime), 0);
                    allRLayouts.get(id).setLayoutParams(params);
                }
            };
            a.setDuration(500);
            allRLayouts.get(id).startAnimation(a);
            questionIndex++;

            final Integer id2 = questionIndex;
            Animation a2 = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) allRLayouts.get(id2).getLayoutParams();
                    params.setMargins((int) (Resources.getSystem().getDisplayMetrics().widthPixels - Resources.getSystem().getDisplayMetrics().widthPixels * interpolatedTime), 0,
                            (int) -(Resources.getSystem().getDisplayMetrics().widthPixels - Resources.getSystem().getDisplayMetrics().widthPixels * interpolatedTime), 0);
                    allRLayouts.get(id2).setLayoutParams(params);
                }
            };
            a2.setDuration(500);
            allRLayouts.get(id2).startAnimation(a2);

            RelativeLayout.LayoutParams params3 = ((RelativeLayout.LayoutParams) buttonNext.getLayoutParams());

            params3.addRule(RelativeLayout.BELOW, allRLayouts.get(questionIndex).getId());
            buttonNext.setLayoutParams(params3);
            if (questionIndex == allRLayouts.size() - 1) buttonNext.setVisibility(View.INVISIBLE);
            buttonPrev.setVisibility(View.VISIBLE);
        }
    }

    private void prevQuestion() {
        if (questionIndex > 0) {
            final Integer id = questionIndex;
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) allRLayouts.get(id).getLayoutParams();
                    params.setMargins((int) (Resources.getSystem().getDisplayMetrics().widthPixels * interpolatedTime), 0,
                            (int) -(Resources.getSystem().getDisplayMetrics().widthPixels * interpolatedTime), 0);
                    allRLayouts.get(id).setLayoutParams(params);
                }
            };
            a.setDuration(500);
            allRLayouts.get(id).startAnimation(a);

            questionIndex--;

            final Integer id2 = questionIndex;
            Animation a2 = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) allRLayouts.get(id2).getLayoutParams();
                    Log.d("quiz1", String.valueOf(interpolatedTime));
                    params.setMargins((int) (-Resources.getSystem().getDisplayMetrics().widthPixels + Resources.getSystem().getDisplayMetrics().widthPixels * interpolatedTime), 0,
                            (int) (Resources.getSystem().getDisplayMetrics().widthPixels - Resources.getSystem().getDisplayMetrics().widthPixels * interpolatedTime), 0);
                    allRLayouts.get(id2).setLayoutParams(params);
                }
            };
            a2.setDuration(500);
            allRLayouts.get(id2).startAnimation(a2);

            RelativeLayout.LayoutParams params3 = ((RelativeLayout.LayoutParams) buttonNext.getLayoutParams());
            params3.addRule(RelativeLayout.BELOW, allRLayouts.get(questionIndex).getId());
            buttonNext.setLayoutParams(params3);
            if (questionIndex == 0) buttonPrev.setVisibility(View.INVISIBLE);
            buttonNext.setVisibility(View.VISIBLE);
        }
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        myGestureDectector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }


    void checkAnswers() {
        Integer index = new Integer(0);
        for (RelativeLayout relativeLayout : allRLayouts) {
            RadioGroup radio = (RadioGroup) relativeLayout.getChildAt(2);
            if (multipleChoice.equals("0")) {
                for (int i = 0; i < 4; i++) {
                    RadioButton radioButton = (RadioButton) radio.getChildAt(i);
                    if (radioButton.isChecked())
                        questions2.get(index).getAnswers().get(i).setValue(1);
                    else questions2.get(index).getAnswers().get(i).setValue(0);
                }
            } else {
                for (int i = 0; i < 4; i++) {
                    CheckBox radioButton = (CheckBox) radio.getChildAt(i);
                    if (radioButton.isChecked())
                        questions2.get(index).getAnswers().get(i).setValue(1);
                    else questions2.get(index).getAnswers().get(i).setValue(0);
                }
            }
            index++;
        }

        String questionsJson = gson.toJson(questions2);
        FileOutputStream outputStream;
        try {
            outputStream = openFileOutput("quizAnswers", Context.MODE_PRIVATE);
            outputStream.write(questionsJson.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        new CheckAnswers().execute(questions2);
        progress = ProgressDialog.show(Test.this, "Sprawdzanie odpowiedzi ...", "Oczekowanie na dane...", true);

    }


    private class CheckAnswers extends AsyncTask<List<Question>, Void, String> {
        private OkHttpClient mClient = new OkHttpClient();

        protected String doInBackground(List<Question>... answers) {
            String url = checkAnswersURL;

            RequestBody requestBody = RequestBody.create(JSON, gson.toJson(answers[0]));
            try {
                com.squareup.okhttp.Request request = new Request
                        .Builder()
                        .header("Authorization", "Bearer "+JWT)
                        .post(requestBody)
                        .url(url)
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

        protected void onPostExecute(String result) {
            Intent intent = new Intent(Test.this, TestResult.class);
            intent.putExtra("result", result);
            intent.putExtra("jwt", JWT);
            intent.putExtra("answers", gson.toJson(questions2));
            intent.putExtra("multipleChoice", multipleChoice);
            startActivity(intent);
            finish();
            progress.dismiss();
        }
    }

    private class GetQuizDetails extends AsyncTask<String, Void, Subject> {
        private OkHttpClient mClient = new OkHttpClient();

        @Override
        protected Subject doInBackground(String... url) {
            String stringResponse = "";
            try {
                Request request = new Request
                        .Builder()
                        .header("Authorization", "Bearer "+JWT)
                        .method("GET", null)
                        .url(url[0])
                        .build();
                Response response = mClient.newCall(request).execute();
                stringResponse = response.body().string();
                JSONObject jsonObject = new JSONObject(stringResponse);
                Subject subject = new Subject(jsonObject);
                return subject;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Subject result) {
            quizDetails = result;
            timeString = Integer.toString(result.getTime()) + ":00";

            cTimer = new CountDownTimer(result.getTime() * 1000 * 60, 1000) {
                public void onTick(long millisUntilFinished) {
                    long minsLeft = (millisUntilFinished / 1000 / 60);
                    long secLeft = (millisUntilFinished / 1000) % 60;
                    if (secLeft < 10)
                        timeString = Long.toString(minsLeft) + ":0" + Long.toString(secLeft);
                    else timeString = Long.toString(minsLeft) + ":" + Long.toString(secLeft);
                    for (TextView textView : watchList)
                        textView.setText(timeString);
                }

                public void onFinish() {
                    checkAnswers();
                }
            };
            cTimer.start();
        }
    }

}
