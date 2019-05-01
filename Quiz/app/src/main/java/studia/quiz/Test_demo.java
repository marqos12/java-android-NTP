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
import android.widget.Button;
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
    public ProgressDialog progress;

    private GestureDetector myGestureDectector;

    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_demo);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");



        AndroidGestureDectector androidGestureDectector = new AndroidGestureDectector();
        myGestureDectector = new GestureDetector(Test_demo.this, androidGestureDectector);

        findViewById(R.id.main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return myGestureDectector.onTouchEvent(event);
            }
        });

        buttonNext = findViewById(R.id.button2);
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });

        buttonPrev = findViewById(R.id.prevBtn);
        buttonPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prevQuestion();
            }
        });


        buttonAccept = findViewById(R.id.accept);
        buttonAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswers();
                Log.d("quiz","klikło accept");
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
                    RelativeLayout.LayoutParams rparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
                    rparam.setMargins(Resources.getSystem().getDisplayMetrics().widthPixels,0,0,0);
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
                    RelativeLayout.LayoutParams paramsText = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
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

            params3.addRule(RelativeLayout.BELOW,allRLayouts.get(questionIndex).getId());
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
            Log.d("Gesture ", "Kupa gówna");
            if (motionEvent.getX() < motionEvent1.getX()) {
                Log.d("Gesture ", "Left to Right Fling: " + motionEvent.getX() + " - " + motionEvent1.getX());
                Log.d("Speed ", String.valueOf(v) + " pixels/second");
                prevQuestion();
            }
            if (motionEvent.getX() > motionEvent1.getX()) {
                Log.d("Gesture ", "Right to Left Fling: " + motionEvent.getX() + " - " + motionEvent1.getX());
                Log.d("Speed ", String.valueOf(v) + " pixels/second");
                nextQuestion();

            }
            return false;
        }
    }

    private void nextQuestion(){
        if(questionIndex<allRLayouts.size()-1) {
            RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams) allRLayouts.get(questionIndex).getLayoutParams());
            params.setMargins(-Resources.getSystem().getDisplayMetrics().widthPixels, 0, Resources.getSystem().getDisplayMetrics().widthPixels, 0);
            allRLayouts.get(questionIndex).setLayoutParams(params);
            questionIndex++;
            RelativeLayout.LayoutParams params2 = ((RelativeLayout.LayoutParams) allRLayouts.get(questionIndex).getLayoutParams());
            params2.setMargins(0, 0, 0, 0);
            allRLayouts.get(questionIndex).setLayoutParams(params2);

            RelativeLayout.LayoutParams params3 = ((RelativeLayout.LayoutParams) buttonNext.getLayoutParams());

            params3.addRule(RelativeLayout.BELOW, allRLayouts.get(questionIndex).getId());
            buttonNext.setLayoutParams(params3);
            if(questionIndex==allRLayouts.size()-1) buttonNext.setVisibility(View.INVISIBLE);
            buttonPrev.setVisibility(View.VISIBLE);
        }
    }
    private void prevQuestion(){
        if(questionIndex>0) {
            RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams) allRLayouts.get(questionIndex).getLayoutParams());
            params.setMargins(+Resources.getSystem().getDisplayMetrics().widthPixels, 0, 0, 0);
            allRLayouts.get(questionIndex).setLayoutParams(params);
            questionIndex--;
            RelativeLayout.LayoutParams params2 = ((RelativeLayout.LayoutParams) allRLayouts.get(questionIndex).getLayoutParams());
            params2.setMargins(0, 0, 0, 0);
            allRLayouts.get(questionIndex).setLayoutParams(params2);

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
            for(int i = 0; i < 4; i++){
                //Answer answerek = new Answer(questions2.get(index).getAnswers().get(i).getId());
                RadioButton radioButton = (RadioButton)radio.getChildAt(i);
                if(radioButton.isChecked()) questions2.get(index).getAnswers().get(i).setValue(1);
                else questions2.get(index).getAnswers().get(i).setValue(0);
                //answer.add(answerek);
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
                startActivity(intent);
                finish();
                progress.dismiss();

        }
    }

}
