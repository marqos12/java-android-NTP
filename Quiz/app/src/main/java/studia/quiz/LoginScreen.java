package studia.quiz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import studia.quiz.model.Answer;
import studia.quiz.model.Question;
import studia.quiz.model.User;

public class LoginScreen extends AppCompatActivity {
    String loginURL ;
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Gson gson = new Gson();
    TextView loginFailed;
    TextView inProgress;
    public ProgressDialog progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        loginURL =getApplicationContext().getString(R.string.url, "/api/login");
        final EditText login = findViewById(R.id.login);
        final EditText password = findViewById(R.id.password);
        loginFailed = findViewById(R.id.loginFailedText);
        inProgress = findViewById(R.id.textInProgress);
        Button button3 = findViewById(R.id.loginBtn);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login.setBackgroundColor(getResources().getColor(R.color.buttonBackgroundDisable));
                password.setBackgroundColor(getResources().getColor(R.color.buttonBackgroundDisable));

                if(TextUtils.isEmpty(login.getText())||login.getText().toString().trim().length() == 0){
                   login.setBackgroundColor(getResources().getColor(R.color.textNegative));
                }
                else if(password.getText().equals("")||password.getText().toString().trim().length() == 0){
                    password.setBackgroundColor(getResources().getColor(R.color.textNegative));
                }
                else {
                    User user = new User();
                    user.setEmail(login.getText().toString());
                    user.setPassword(password.getText().toString());
                    new LogIn().execute(user);
                    loginFailed.setText("");
                    inProgress.setVisibility(View.VISIBLE);
                }
            }
        });
        Button button4 = findViewById(R.id.registerBtn);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this, RegisterScreen.class);
                startActivity(intent);
            }
        });

    }

    private class LogIn extends AsyncTask<User, Void, String> {
        private OkHttpClient mClient = new OkHttpClient();

        protected String doInBackground(User... user){

            RequestBody requestBody = RequestBody.create(JSON, gson.toJson(user[0]));

            try {
                com.squareup.okhttp.Request request = new Request
                        .Builder()
                        .post(requestBody)
                        .url(loginURL)
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


        protected void onPostExecute(String userResult) {
            inProgress.setVisibility(View.INVISIBLE);
            try {
                JSONObject jsonObject = new JSONObject(userResult);
                if(jsonObject.has("error")){
                    loginFailed.setText("Logowanie nie powiodło się");
                }
                else {
                    JSONObject userJsonObject = jsonObject.getJSONObject("user");
                    User respUsser = new User(userJsonObject);
                    FileOutputStream outputStream;
                    try {
                        outputStream = openFileOutput("userName", Context.MODE_PRIVATE);
                        outputStream.write(userResult.getBytes());
                        outputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(respUsser.getRole().equals("s")){

                        Intent intent = new Intent(LoginScreen.this, MainStudent.class);
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(LoginScreen.this, MainTeacher.class);
                        startActivity(intent);
                    }
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
