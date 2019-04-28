package studia.quiz;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
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
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import studia.quiz.model.Question;
import studia.quiz.model.Subject;

public class Test_demo extends AppCompatActivity {

    TextView questionText;
    RadioGroup radio;
    Button buttonNext;
    List<RelativeLayout> questions=new ArrayList<RelativeLayout>();
    RelativeLayout mainRelativeLayout;
    List<RelativeLayout> allRLayouts = new ArrayList<RelativeLayout>();
    public ProgressDialog progress;

    private GestureDetector myGestureDectector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_demo);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");



        AndroidGestureDectector androidGestureDectector = new AndroidGestureDectector();
       // myGestureDectector = new GestureDetector(Test_demo.this, androidGestureDectector);

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
                Log.d("quiz","klikło");
                RelativeLayout.LayoutParams params = ((RelativeLayout.LayoutParams) allRLayouts.get(0).getLayoutParams());
                params.setMargins(-Resources.getSystem().getDisplayMetrics().widthPixels,0,0,0);
                allRLayouts.get(0).setLayoutParams(params);
                //((RelativeLayout.LayoutParams) allRLayouts.get(0).getLayoutParams()).setMarginStart(-Resources.getSystem().getDisplayMetrics().widthPixels);
                //((RelativeLayout.LayoutParams) allRLayouts.get(1).getLayoutParams()).setMarginStart(0);
                RelativeLayout.LayoutParams params2 = ((RelativeLayout.LayoutParams) allRLayouts.get(1).getLayoutParams());
                params2.setMargins(0,0,0,0);
                allRLayouts.get(1).setLayoutParams(params2);

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

        @Override
        protected void onPostExecute(List<Question>questions)
        {
            Integer i = new Integer(1);
            for(Question question: questions){
                RelativeLayout relativeLayout = new RelativeLayout(getApplicationContext());

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
                    RelativeLayout.LayoutParams paramsText = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    paramsText.addRule(RelativeLayout.BELOW,questionNumber.getId());
                    questionText.setLayoutParams(paramsText);

                    questionText.setText(questions.get(0).getText());
                    questionText.setTextSize(16);
                    //questionText.setBackgroundResource(R.drawable.quizcard);
                    questionText.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.quizcard));
                    questionText.setTextColor(getResources().getColor(R.color.textWhite));
                    questionText.setId(View.generateViewId());
                relativeLayout.addView(questionText);

                    RadioGroup radioGroup = new RadioGroup(getApplicationContext());
                    RelativeLayout.LayoutParams paramsText2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    paramsText2.addRule(RelativeLayout.BELOW,questionText.getId());
                    paramsText2.setMargins(0,60,0,100);
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

                allRLayouts.add(relativeLayout);
                mainRelativeLayout.addView(relativeLayout );
                i++;
            }

            ((RelativeLayout.LayoutParams) allRLayouts.get(0).getLayoutParams()).setMargins(0,0,0,0);
            
            progress.dismiss();
        }
    }
/*
        request();


    }

    void request(){
        ///////////////////RESST////////////
        String url = "https://marqos12.000webhostapp.com/api/question/demo/WAFQ/33";

        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(this);
        StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText(response);
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    textView.setText(jsonObject.toString());
                    Question question =  new Question(jsonObject);
                    textView.setText(question.getText());
                    for(int i = 0 ; i < 4 ; i++){
                        RadioButton radioButton = (RadioButton) radio.getChildAt(i);
                        radioButton.setText(question.getAnswers().get(i).getText());

                    }
                }
                catch (Exception e){
                    Toast.makeText(Test_demo.this, "jebli", Toast.LENGTH_SHORT).show();
                    Log.d("jebło",e.getMessage());
                    request();
                }
            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText("#jebło!");
                Log.d("jebło",error.toString());
                request();
                //This code is executed if there is an error.
            }
        });
        ExampleRequestQueue.add(ExampleStringRequest);

    }
*/










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
            if (motionEvent.getY() < motionEvent1.getY()) {
                Log.d("Gesture ", "Up to Down Scroll: " + motionEvent.getY() + " - " + motionEvent1.getY());
                Log.d("Speed ", String.valueOf(v1) + " pixels/second");
            }
            if (motionEvent.getY() > motionEvent1.getY()) {
                Log.d("Gesture ", "Down to Up Scroll: " + motionEvent.getY() + " - " + motionEvent1.getY());
                Log.d("Speed ", String.valueOf(v1) + " pixels/second");
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
            if (motionEvent.getX() < motionEvent1.getX()) {
                Log.d("Gesture ", "Left to Right Fling: " + motionEvent.getX() + " - " + motionEvent1.getX());
                Log.d("Speed ", String.valueOf(v) + " pixels/second");
            }
            if (motionEvent.getX() > motionEvent1.getX()) {
                Log.d("Gesture ", "Right to Left Fling: " + motionEvent.getX() + " - " + motionEvent1.getX());
                Log.d("Speed ", String.valueOf(v) + " pixels/second");
            }
            if (motionEvent.getY() < motionEvent1.getY()) {
                Log.d("Gesture ", "Up to Down Fling: " + motionEvent.getY() + " - " + motionEvent1.getY());
                Log.d("Speed ", String.valueOf(v1) + " pixels/second");
            }
            if (motionEvent.getY() > motionEvent1.getY()) {
                Log.d("Gesture ", "Down to Up Fling: " + motionEvent.getY() + " - " + motionEvent1.getY());
                Log.d("Speed ", String.valueOf(v1) + " pixels/second");
            }
            return false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        myGestureDectector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }
}
