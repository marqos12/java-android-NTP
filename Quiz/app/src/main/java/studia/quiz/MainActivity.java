package studia.quiz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import studia.quiz.model.User;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String userString = "";
        FileInputStream inputStream;
        try {
            inputStream = openFileInput("userName");
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            userString = bufferedReader.readLine();
            inputStream.close();
            JSONObject userJSON = new JSONObject(userString);
            Log.d("quiz1",userString);
            User user = new User(userJSON.optJSONObject("user"));
            if(user.getRole().equals("s")){

                Intent intent = new Intent(MainActivity.this, MainStudent.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(MainActivity.this, MainTeacher.class);
                startActivity(intent);
            }
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Button button1 = findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Test_demo_begin.class);
                intent.putExtra("name", "web");
                startActivity(intent);
                finish();
            }
        });

        Button button2 = findViewById(R.id.button5);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Test_demo_begin.class);
                intent.putExtra("name", "java");
                startActivity(intent);
                finish();
            }
        });

        Button button3 = findViewById(R.id.loginBtn);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(intent);

            }
        });

        Button button4 = findViewById(R.id.registerBtn);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterScreen.class);
                startActivity(intent);

            }
        });
    }
}
