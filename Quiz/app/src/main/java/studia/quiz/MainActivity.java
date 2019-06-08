package studia.quiz;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import studia.quiz.model.User;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FileInputStream inputStream;
        try {
            inputStream = openFileInput("userName");
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            String userString = bufferedReader.readLine();
            inputStream.close();
            String tokenJSON = userString.split("\\.")[1];
            Log.e("quiz1",tokenJSON);
            byte[] decodedBytes = Base64.decode(tokenJSON, Base64.URL_SAFE);
            String decodedTokenJSON = new String(decodedBytes, "UTF-8");
            JSONObject jsonObject1 = new JSONObject(decodedTokenJSON);
            JSONObject userJsonObject = jsonObject1.getJSONObject("user");
            User user = new User(userJsonObject);

            Log.e("quiz1",user.getRole());
            if(user.getRole().equals("ROLE_USER")){

                Intent intent = new Intent(MainActivity.this, MainStudent.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(MainActivity.this, MainTeacher.class);
                startActivity(intent);
            }
            finish();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Button button1 = findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Test_demo_begin.class);
                intent.putExtra("name", "demoweb");
                startActivity(intent);
                finish();
            }
        });

        Button button2 = findViewById(R.id.button5);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Test_demo_begin.class);
                intent.putExtra("name", "demojava");
                startActivity(intent);
                finish();
            }
        });

       /* Button button3 = findViewById(R.id.loginBtn);
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
        });*/
    }
}
