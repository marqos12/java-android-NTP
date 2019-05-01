package studia.quiz;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import studia.quiz.model.Answer;
import studia.quiz.model.Question;
import studia.quiz.model.Result;
import studia.quiz.model.Subject;

import static java.lang.Math.ceil;
import static java.lang.Math.floor;

public class Test_demo extends AppCompatActivity {

    TextView questionText;
    RadioGroup radio;
    Button buttonNext;
    Button buttonPrev;
    Button buttonAccept;
    List<Question>questions2;
    List<RelativeLayout> questions=new ArrayList<RelativeLayout>();
    RelativeLayout mainRelativeLayout;
    List<RelativeLayout> allRLayouts = new ArrayList<RelativeLayout>();
    Integer questionIndex = Integer.valueOf(0);
    Subject quizDetails;
    String separatePage;
    String multipleChoice;
    public ProgressDialog progress;

    private GestureDetector myGestureDectector;

    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Gson gson = new Gson();
    Boolean collapsedRules = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_demo);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("name");
        separatePage = intent.getStringExtra("separatePage");
        multipleChoice = intent.getStringExtra("multipleChoice");



        AndroidGestureDectector androidGestureDectector = new AndroidGestureDectector();
        myGestureDectector = new GestureDetector(Test_demo.this, androidGestureDectector);

        findViewById(R.id.main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return myGestureDectector.onTouchEvent(event);
            }
        });

        buttonNext = findViewById(R.id.button2);

        buttonPrev = findViewById(R.id.prevBtn);

        if(separatePage.equals("1")){

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

        }
        else {
            buttonNext.setVisibility(View.INVISIBLE);
            buttonPrev.setVisibility(View.INVISIBLE);
        }

        buttonAccept = findViewById(R.id.accept);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswers();
                Log.d("quiz","klikło accept");
            }
        });

        final TextView about = findViewById(R.id.about1);
        final TextView rules = findViewById(R.id.rules);
        final TextView ponts = findViewById(R.id.points);
        Button buttonRules = findViewById(R.id.rulesButton);
        buttonRules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(collapsedRules){
                    collapsedRules = !collapsedRules;
                    about.setText(getApplicationContext().getString(R.string.testName,quizDetails.getName(),quizDetails.getSubject()));
                    rules.setText(getApplicationContext().getString(R.string.rules,getApplicationContext().getString((quizDetails.getMultipleChoice().equals(1)?R.string.multiplyTrue:R.string.multiplyFalse)),
                            quizDetails.getTime(),quizDetails.getNoQuestions(),quizDetails.getNoQuestions()));
                            int maxPoints = quizDetails.getNoQuestions();
                    ponts.setText(getApplicationContext().getString(R.string.pointsInline, floor(maxPoints*0.59),ceil(maxPoints*0.60),
                             floor(maxPoints*0.64),ceil(maxPoints*0.65),floor(maxPoints*0.74),ceil(maxPoints*0.75),floor(maxPoints*(0.84)),ceil(maxPoints*(0.85)),
                    floor(maxPoints*0.94),ceil(maxPoints*0.95),maxPoints));
                }
                else {
                    collapsedRules = !collapsedRules;
                    about.setText("");
                    rules.setText("");
                    ponts.setText("");
                }
            }
        });



        mainRelativeLayout = findViewById(R.id.mainRelativeLayout);

        try
        {
            String url = "https://marqos12.000webhostapp.com/api/question/demo/WAFQ/"+id;
            Test_demo.GetTestDetails getTestDetails = new Test_demo.GetTestDetails();
            getTestDetails.execute(url);
            progress = ProgressDialog.show(Test_demo.this, "Pobieranie danych ...", "Oczekowanie na dane...", true);
        }
        catch (Exception e)
        {
            Log.e("quiz", e.getMessage());
        }

        String url = "http://marqos12.000webhostapp.com/api/demo/details/" + name;
        new GetQuizDetails().execute(url);

    }

    private class GetTestDetails extends AsyncTask<String, Void, List<Question>> {

        private OkHttpClient mClient = new OkHttpClient()   ;

        @Override
        protected List<Question> doInBackground(String... url)
        {
            String stringResponse="";
            List<Question> questions= new ArrayList<Question>();
            try
            {
                com.squareup.okhttp.Request request = new Request
                        .Builder()
                        .method("GET",null)
                        .url(url[0])
                        .build();


                mClient.setProtocols(Arrays.asList(Protocol.HTTP_1_1));

                Response response = mClient.newCall(request).execute();
                stringResponse = response.body().string();
                JSONArray jsonArray = new JSONArray(stringResponse);
                for (int i = 0; i < jsonArray.length();i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    Question question =  new Question(jsonObject);
                    questions.add(question);
                }
//                JSONObject jsonObject = jsonArray.getJSONObject(0);
  //              textView.setText(jsonObject.toString());
        //        Question question =  new Question(jsonObject);
                return questions ;
            }
            catch (Exception e)
            {
                Log.d("quiz",e.getMessage());
                e.printStackTrace();
                return null;
            }
        }

        @SuppressLint("ResourceAsColor")
        @Override
        protected void onPostExecute(List<Question>questions)
        {
            questions2=questions;
            Integer i = new Integer(1);
            for(Question question: questions){
                RelativeLayout relativeLayout = new RelativeLayout(getApplicationContext());
                    relativeLayout.setId(View.generateViewId());
                    RelativeLayout.LayoutParams rparam = new RelativeLayout.LayoutParams(Resources.getSystem().getDisplayMetrics().widthPixels,RelativeLayout.LayoutParams.WRAP_CONTENT);
                    if(separatePage.equals("1")) {
                        rparam.setMargins(Resources.getSystem().getDisplayMetrics().widthPixels, 0, 0, 0);
                        rparam.addRule(RelativeLayout.BELOW,R.id.rel);
                        }
                    else {
                        if(i>1)rparam.addRule(RelativeLayout.BELOW,allRLayouts.get(i-2).getId());
                    }

                relativeLayout.setLayoutParams(rparam);

                    TextView questionNumber = new TextView(getApplicationContext());
                    questionNumber.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                    questionNumber.setText(i.toString()+"/"+questions.size());
                    //questionNumber.setBackgroundResource(R.drawable.quizcard);
                    questionNumber.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.quizcard));
                    questionNumber.setTextColor(getResources().getColor(R.color.textWhite));
                    //questionNumber.setId(buttonNext.getId()+i*20+1);
                    questionNumber.setId(View.generateViewId());
                relativeLayout.addView(questionNumber);


                    //Log.d("quiz",getApplicationContext().getString(R.string.test,buttonNext.getId()));

                    TextView questionText = new TextView(getApplicationContext());
                    RelativeLayout.LayoutParams paramsText = new RelativeLayout.LayoutParams(Resources.getSystem().getDisplayMetrics().widthPixels,ViewGroup.LayoutParams.WRAP_CONTENT);
                    paramsText.addRule(RelativeLayout.BELOW,questionNumber.getId());
                    questionText.setLayoutParams(paramsText);

                    questionText.setText(questions.get(i-1).getText());
                    questionText.setTextSize(16);
                    //questionText.setBackgroundResource(R.drawable.quizcard);
                    questionText.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.quizcard));
                    questionText.setTextColor(getResources().getColor(R.color.textWhite));
                    questionText.setId(View.generateViewId());
                relativeLayout.addView(questionText);




                    RadioGroup radioGroup = new RadioGroup(getApplicationContext());
                    RelativeLayout.LayoutParams paramsText2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    paramsText2.addRule(RelativeLayout.BELOW,questionText.getId());
                    paramsText2.setMargins(0,60,0,60);
                    radioGroup.setLayoutParams(paramsText2);
                    radioGroup.setId(View.generateViewId());

                    Log.d("quiz1",multipleChoice);
                    if(multipleChoice.equals("0")){
                        for(int j = 0 ; j < 4 ; j++){
                            RadioButton answer1 = new RadioButton(getApplicationContext());
                            RadioGroup.LayoutParams paramsAnswer1 = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            paramsAnswer1.setMargins(0,10,0,0);
                            //((ViewGroup.MarginLayoutParams)paramsAnswer1).topMargin=10;
                            answer1.setLayoutParams(paramsAnswer1);
                            answer1.setId(View.generateViewId());
                            answer1.setText(questions.get(i-1).getAnswers().get(j).getText());
                            answer1.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.quizcard));
                            radioGroup.addView(answer1);
                        }
                    }
                    else {
                        for(int j = 0 ; j < 4 ; j++){
                            CheckBox answer1 = new CheckBox(getApplicationContext());
                            RadioGroup.LayoutParams paramsAnswer1 = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                            paramsAnswer1.setMargins(0,10,0,0);
                            //((ViewGroup.MarginLayoutParams)paramsAnswer1).topMargin=10;
                            answer1.setLayoutParams(paramsAnswer1);
                            answer1.setId(View.generateViewId());
                            answer1.setText(questions.get(i-1).getAnswers().get(j).getText());
                            answer1.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.quizcard));
                            radioGroup.addView(answer1);
                        }
                    }



                relativeLayout.addView(radioGroup);


                if(questions.get(i-1).getCode()!=null){
                    TextView questionCode = new TextView(getApplicationContext());
                    RelativeLayout.LayoutParams paramsCode = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    paramsCode.addRule(RelativeLayout.BELOW,questionText.getId());
                    paramsCode.setMargins(0,10,0,0);
                    questionCode.setLayoutParams(paramsCode);
                    questionCode.setTextColor(getResources().getColor(R.color.codeBorder));
                    questionCode.setText(questions.get(i-1).getCode());
                    questionCode.setTextSize(14);
                    questionCode.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.codecard));
                    questionCode.setId(View.generateViewId());
                    relativeLayout.addView(questionCode);

                    paramsText2.addRule(RelativeLayout.BELOW,questionCode.getId());
                    radioGroup.setLayoutParams(paramsText2);
                }

                if(questions.get(i-1).getImage()!=null){
                    ImageView questionImage = new ImageView(getApplicationContext());
                    RelativeLayout.LayoutParams paramsImage = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    paramsImage.addRule(RelativeLayout.BELOW,questionText.getId());
                    paramsImage.setMargins(0,10,0,0);
                    questionImage.setLayoutParams(paramsImage);
                    /*questionCode.setTextColor(getResources().getColor(R.color.codeBorder));
                    questionCode.setText(questions.get(i-1).getCode());
                    questionCode.setTextSize(14);*/
                    questionImage.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.codecard));
                    questionImage.setId(View.generateViewId());
                    new DownloadImageTask(questionImage).execute(questions.get(i-1).getImage());
                    relativeLayout.addView(questionImage);

                    paramsText2.addRule(RelativeLayout.BELOW,questionImage.getId());
                    radioGroup.setLayoutParams(paramsText2);
                }


                allRLayouts.add(relativeLayout);
                mainRelativeLayout.addView(relativeLayout );
                i++;
            }

            ((RelativeLayout.LayoutParams) allRLayouts.get(0).getLayoutParams()).setMargins(0,0,0,0);

            RelativeLayout.LayoutParams params3 = ((RelativeLayout.LayoutParams) buttonNext.getLayoutParams());

            if(separatePage.equals("1")) params3.addRule(RelativeLayout.BELOW,allRLayouts.get(questionIndex).getId());
            else  params3.addRule(RelativeLayout.BELOW,allRLayouts.get(i-2).getId());
            buttonNext.setLayoutParams(params3);

            progress.dismiss();
        }
    }


    class AndroidGestureDectector implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
            //gestureText.setText("onSingleTapConfirmed");
            Log.d("Gesture ", "onSingleTapConfirmed");
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent motionEvent) {
            // gestureText.setText("onDoubleTap");
            Log.d("Gesture ", "onDoubleTap");
            return false;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent motionEvent) {
            //gestureText.setText("onDoubleTapEvent");
            Log.d("Gesture ", "onDoubleTapEvent");
            return false;
        }

        @Override
        public boolean onDown(MotionEvent motionEvent) {
            //gestureText.setText("onDown");
            Log.d("Gesture ", "onDown");
            return false;
        }

        @Override
        public void onShowPress(MotionEvent motionEvent) {
            //gestureText.setText("onShowPress");
            Log.d("Gesture ", "onShowPress");

        }

        @Override
        public boolean onSingleTapUp(MotionEvent motionEvent) {
            //gestureText.setText("onSingleTapUp");
            Log.d("Gesture ", "onSingleTapUp");
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            //gestureText.setText("onScroll");
            if (motionEvent.getX() < motionEvent1.getX()) {
                Log.d("Gesture ", "Left to Right Scroll: " + motionEvent.getX() + " - " + motionEvent1.getX());
                Log.d("Speed ", String.valueOf(v) + " pixels/second");
                //gestureText.setText("w prawo");
            }
            if (motionEvent.getX() > motionEvent1.getX()) {
                Log.d("Gesture ", "Right to Left Scroll: " + motionEvent.getX() + " - " + motionEvent1.getX());
                Log.d("Speed ", String.valueOf(v) + " pixels/second");
                // gestureText.setText("w lewo");
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent motionEvent) {
            // gestureText.setText("onLongPress");
            Log.d("Gesture ", "onLongPress");

        }

        @Override
        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
            //gestureText.setText("onFling");
            Log.d("Gesture ", separatePage);
            if (motionEvent.getX() < motionEvent1.getX()) {
                Log.d("Gesture ", "Left to Right Fling: " + motionEvent.getX() + " - " + motionEvent1.getX());
                Log.d("Speed ", String.valueOf(v) + " pixels/second");
               if(separatePage.equals("1")) prevQuestion();
            }
            if (motionEvent.getX() > motionEvent1.getX()) {
                Log.d("Gesture ", "Right to Left Fling: " + motionEvent.getX() + " - " + motionEvent1.getX());
                Log.d("Speed ", String.valueOf(v) + " pixels/second");
                if(separatePage.equals("1"))nextQuestion();

            }
            return false;
        }
    }

    private void nextQuestion(){
        if(questionIndex<allRLayouts.size()-1) {

            //RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams) allRLayouts.get(questionIndex).getLayoutParams());
        final Integer id = questionIndex;
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) allRLayouts.get(id).getLayoutParams();
                    params.setMargins((int)(-Resources.getSystem().getDisplayMetrics().widthPixels * interpolatedTime), 0, (int)(Resources.getSystem().getDisplayMetrics().widthPixels * interpolatedTime), 0);
                    allRLayouts.get(id).setLayoutParams(params);
                }
            };
            a.setDuration(500); // in ms
            allRLayouts.get(id).startAnimation(a);

            //params.setMargins(-Resources.getSystem().getDisplayMetrics().widthPixels, 0, Resources.getSystem().getDisplayMetrics().widthPixels, 0);
            //allRLayouts.get(questionIndex).setLayoutParams(params);

            questionIndex++;
/*
            RelativeLayout.LayoutParams params2 = ((RelativeLayout.LayoutParams) allRLayouts.get(questionIndex).getLayoutParams());
            params2.setMargins(0, 0, 0, 0);
            allRLayouts.get(questionIndex).setLayoutParams(params2);

*/


            final Integer id2 = questionIndex;

            Animation a2 = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) allRLayouts.get(id2).getLayoutParams();
                    Log.d("quiz1",String.valueOf(interpolatedTime));
                    params.setMargins((int)(Resources.getSystem().getDisplayMetrics().widthPixels-Resources.getSystem().getDisplayMetrics().widthPixels * interpolatedTime), 0,
                            (int) -(Resources.getSystem().getDisplayMetrics().widthPixels-Resources.getSystem().getDisplayMetrics().widthPixels * interpolatedTime), 0);
                    allRLayouts.get(id2).setLayoutParams(params);
                }
            };
            a2.setDuration(500); // in ms
            allRLayouts.get(id2).startAnimation(a2);



            RelativeLayout.LayoutParams params3 = ((RelativeLayout.LayoutParams) buttonNext.getLayoutParams());

            params3.addRule(RelativeLayout.BELOW, allRLayouts.get(questionIndex).getId());
            buttonNext.setLayoutParams(params3);
            if(questionIndex==allRLayouts.size()-1) buttonNext.setVisibility(View.INVISIBLE);
            buttonPrev.setVisibility(View.VISIBLE);
        }
    }
    private void prevQuestion(){
        if(questionIndex>0) {
            /*RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams) allRLayouts.get(questionIndex).getLayoutParams());
            params.setMargins(+Resources.getSystem().getDisplayMetrics().widthPixels, 0, 0, 0);
            allRLayouts.get(questionIndex).setLayoutParams(params);
            questionIndex--;
            RelativeLayout.LayoutParams params2 = ((RelativeLayout.LayoutParams) allRLayouts.get(questionIndex).getLayoutParams());
            params2.setMargins(0, 0, 0, 0);
            allRLayouts.get(questionIndex).setLayoutParams(params2);
*/
            final Integer id = questionIndex;
            Animation a = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) allRLayouts.get(id).getLayoutParams();
                    params.setMargins((int)(Resources.getSystem().getDisplayMetrics().widthPixels * interpolatedTime), 0,
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
                    Log.d("quiz1",String.valueOf(interpolatedTime));
                    params.setMargins((int)(-Resources.getSystem().getDisplayMetrics().widthPixels+Resources.getSystem().getDisplayMetrics().widthPixels * interpolatedTime), 0,
                            (int)(Resources.getSystem().getDisplayMetrics().widthPixels-Resources.getSystem().getDisplayMetrics().widthPixels * interpolatedTime), 0);
                    allRLayouts.get(id2).setLayoutParams(params);
                }
            };
            a2.setDuration(500);
            allRLayouts.get(id2).startAnimation(a2);


            RelativeLayout.LayoutParams params3 = ((RelativeLayout.LayoutParams) buttonNext.getLayoutParams());
            params3.addRule(RelativeLayout.BELOW, allRLayouts.get(questionIndex).getId());
            buttonNext.setLayoutParams(params3);
            if(questionIndex==0) buttonPrev.setVisibility(View.INVISIBLE);
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
                Log.e("Error", e.getMessage());
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


    void checkAnswers(){
        Integer index = new Integer(0);
        for(RelativeLayout relativeLayout: allRLayouts){
            //List<Answer> answer = new ArrayList<Answer>();
            RadioGroup radio = (RadioGroup)relativeLayout.getChildAt(2);
            if(multipleChoice.equals("0")){
                for(int i = 0; i < 4; i++){
                    //Answer answerek = new Answer(questions2.get(index).getAnswers().get(i).getId());
                    RadioButton radioButton = (RadioButton)radio.getChildAt(i);
                    if(radioButton.isChecked()) questions2.get(index).getAnswers().get(i).setValue(1);
                    else questions2.get(index).getAnswers().get(i).setValue(0);
                    //answer.add(answerek);
                }
            }
            else {
                for(int i = 0; i < 4; i++){
                    //Answer answerek = new Answer(questions2.get(index).getAnswers().get(i).getId());
                    CheckBox radioButton = (CheckBox) radio.getChildAt(i);
                    if(radioButton.isChecked()) questions2.get(index).getAnswers().get(i).setValue(1);
                    else questions2.get(index).getAnswers().get(i).setValue(0);
                    //answer.add(answerek);
                }
            }
            //answers.add(answer);
            index++;
        }
        //zapytanie

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
        progress = ProgressDialog.show(Test_demo.this, "Sprawdzanie odpowiedzi ...", "Oczekowanie na dane...", true);

    }


    private class CheckAnswers extends AsyncTask<List<Question>, Void, String> {

        private OkHttpClient mClient = new OkHttpClient()   ;

        protected String doInBackground(List<Question>... answers) {
            String url = "http://marqos12.000webhostapp.com/api/question/checkWOR";

            RequestBody requestBody = RequestBody.create(JSON,gson.toJson(answers[0]));
            try {
                com.squareup.okhttp.Request request = new Request
                        .Builder()
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
            return null;//mIcon11;
        }

        protected void onPostExecute(String result) {
           // bmImage.setImageBitmap(result);
                Intent intent = new Intent( Test_demo.this, TestDemoResult.class);
                intent.putExtra("result", result);
                intent.putExtra("answers", gson.toJson(questions2));
                intent.putExtra("multipleChoice", multipleChoice);
                startActivity(intent);
                finish();
                progress.dismiss();

        }
    }

    private class GetQuizDetails extends AsyncTask<String, Void, Subject> {

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
           quizDetails = result;
        }
    }

}
