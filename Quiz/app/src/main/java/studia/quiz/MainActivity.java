package studia.quiz;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {


    TextView textView;
    RadioGroup radio;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        radio = findViewById(R.id.radioGroup);


        radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                textView.setText(radioButton.getText().toString());
            }
        });
        View myView = findViewById(R.id.relativeLayout);
        myView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // ... Respond to touch events
                Log.d("chuj","dupa");

                return true;
            }
        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Toast.makeText(this, "kurwa ", Toast.LENGTH_SHORT);

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case (MotionEvent.ACTION_DOWN):
                Toast.makeText(this, "Action was DOWN ", Toast.LENGTH_SHORT);
                return true;
            case (MotionEvent.ACTION_MOVE):
                Toast.makeText(this, "Action was MOVE ", Toast.LENGTH_SHORT);
                return true;
            case (MotionEvent.ACTION_UP):
                Toast.makeText(this, "Action was UP ", Toast.LENGTH_SHORT);
                return true;
            case (MotionEvent.ACTION_CANCEL):
                Toast.makeText(this, "Action was CANCEL ", Toast.LENGTH_SHORT);
                return true;
            case (MotionEvent.ACTION_OUTSIDE):
                Toast.makeText(this, "\"Movement occurred outside bounds \" +\n" +
                        "                        \"of current screen elemen ", Toast.LENGTH_SHORT);
                return true;
            default:
                return super.onTouchEvent(event);
        }

    }

    }
