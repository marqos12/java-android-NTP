package studia.quiz;

import android.content.Intent;
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

import java.io.IOException;
import java.util.Arrays;

import studia.quiz.model.User;

public class RegisterScreen extends AppCompatActivity {
    String registerURL ;
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Gson gson = new Gson();

    EditText name;
    EditText surname;
    EditText email;
    EditText course;
    EditText password;
    EditText confirmPassword;

    TextView loginFailed;
    TextView inProgress;
    String JWT;

    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

       /* String userString = getIntent().getStringExtra("user");
        JSONObject userJSON = null;
        try {
            userJSON = new JSONObject(userString);
            Log.d("quiz1",userString);
            JWT = userJSON.getString("token");
            user = new User(userJSON.optJSONObject("user"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
*/
        registerURL =getApplicationContext().getString(R.string.url, "/api/register");
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        email = findViewById(R.id.login);
        course = findViewById(R.id.course);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.passwordConfirmed);

        /*name.setText(user.getName());
        surname.setText(user.getSurname());
        email.setText(user.getEmail());
        course.setText(user.getCourse());*/






        loginFailed = findViewById(R.id.loginFailedText);
        inProgress = findViewById(R.id.textInProgress);

        Button button3 = findViewById(R.id.loginBtn);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(RegisterScreen.this, LoginScreen.class);
                startActivity(intent);
            }
        });
        Button button4 = findViewById(R.id.registerBtn);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setBackgroundColor(getResources().getColor(R.color.buttonBackgroundDisable));
                surname.setBackgroundColor(getResources().getColor(R.color.buttonBackgroundDisable));
                email.setBackgroundColor(getResources().getColor(R.color.buttonBackgroundDisable));
                password.setBackgroundColor(getResources().getColor(R.color.buttonBackgroundDisable));
                confirmPassword.setBackgroundColor(getResources().getColor(R.color.buttonBackgroundDisable));
                course.setBackgroundColor(getResources().getColor(R.color.buttonBackgroundDisable));
                Boolean correct = true;
                if(TextUtils.isEmpty(name.getText())||name.getText().toString().trim().length() == 0){
                    name.setBackgroundColor(getResources().getColor(R.color.textNegative));
                    correct = false;
                }
                if(TextUtils.isEmpty(surname.getText())||surname.getText().toString().trim().length() == 0){
                    surname.setBackgroundColor(getResources().getColor(R.color.textNegative));
                    correct = false;
                }
                if(TextUtils.isEmpty(email.getText())||email.getText().toString().trim().length() == 0){
                    email.setBackgroundColor(getResources().getColor(R.color.textNegative));
                    correct = false;
                }
                if(TextUtils.isEmpty(course.getText())||course.getText().toString().trim().length() == 0){
                    course.setBackgroundColor(getResources().getColor(R.color.textNegative));
                    correct = false;
                }
                if(TextUtils.isEmpty(password.getText())||password.getText().toString().trim().length() == 0){
                    password.setBackgroundColor(getResources().getColor(R.color.textNegative));
                    confirmPassword.setBackgroundColor(getResources().getColor(R.color.textNegative));
                    correct = false;
                }
                if(!confirmPassword.getText().toString().equals(password.getText().toString())){
                    confirmPassword.setBackgroundColor(getResources().getColor(R.color.textNegative));
                    correct = false;
                }

                if (correct){
                    User user = new User();
                    user.setPassword(password.getText().toString());
                    user.setC_password(confirmPassword.getText().toString());
                    user.setEmail(email.getText().toString());
                    user.setCourse(course.getText().toString());
                    user.setName(name.getText().toString());
                    user.setSurname(surname.getText().toString());
                    new Register().execute(user);
                    loginFailed.setText("");
                    inProgress.setVisibility(View.VISIBLE);
                }

            }
        });
    }
    private class Register extends AsyncTask<User, Void, String> {
        private OkHttpClient mClient = new OkHttpClient();

        protected String doInBackground(User... user){

            RequestBody requestBody = RequestBody.create(JSON, gson.toJson(user[0]));

            try {
                com.squareup.okhttp.Request request = new Request
                        .Builder()
                        .post(requestBody)
                        .url(registerURL)
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
            Log.d("quiz1",userResult);
            try {
                JSONObject jsonObject = new JSONObject(userResult);
                if(jsonObject.has("exception")||jsonObject.has("error")){
                    loginFailed.setText("Rejestracja nie powiodła się");
                }
                else {
                    JSONObject userJsonObject = jsonObject.getJSONObject("user");
                    User respUsser = new User(userJsonObject);

                    Intent intent = new Intent(RegisterScreen.this, LoginScreen.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
