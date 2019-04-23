package studia.quiz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import studia.quiz.model.Question;

public class Test_demo extends AppCompatActivity {

    TextView gestureText;
    RadioGroup radio;

    private GestureDetector myGestureDectector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_demo);
        gestureText = findViewById(R.id.textView);
        radio = findViewById(R.id.radioGroup);


        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                gestureText.setText(radioButton.getText().toString());
            }
        });

        AndroidGestureDectector androidGestureDectector = new AndroidGestureDectector();
        myGestureDectector = new GestureDetector(Test_demo.this, androidGestureDectector);

        findViewById(R.id.main).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return myGestureDectector.onTouchEvent(event);
            }
        });


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
