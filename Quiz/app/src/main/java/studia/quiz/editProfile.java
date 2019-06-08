package studia.quiz;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import studia.quiz.model.User;

public class editProfile extends AppCompatActivity {

    String editProfileURL ;
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
        setContentView(R.layout.activity_edit_profile);
        editProfileURL =getApplicationContext().getString(R.string.url, "/api/users/update");
        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        email = findViewById(R.id.login);
        course = findViewById(R.id.course);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.passwordConfirmed);
        loginFailed = findViewById(R.id.loginFailedText);
        inProgress = findViewById(R.id.textInProgress);


        String userString ;
        JSONObject userJSON = null;
        FileInputStream inputStream;
        try {

            File file = getBaseContext().getFileStreamPath("userName");
            Log.e("quiz1", Boolean.toString(file.exists()));
            inputStream = openFileInput("userName");
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(isr);
            userString = bufferedReader.readLine();
            inputStream.close();
            /*FileOutputStream outputStream;
            try {
                outputStream = openFileOutput("userName", Context.MODE_PRIVATE);
                outputStream.write(userString.getBytes());
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }*/

            JWT = userString;
            String tokenJSON = JWT.split("\\.")[1];
            Log.e("quiz1",tokenJSON);
            byte[] decodedBytes = Base64.decode(tokenJSON, Base64.URL_SAFE);
            String decodedTokenJSON = new String(decodedBytes, "UTF-8");

            Log.e("quiz1",decodedTokenJSON);
            JSONObject jsonObject1 = new JSONObject(decodedTokenJSON);
            JSONObject userJsonObject = jsonObject1.getJSONObject("user");
            user = new User(userJsonObject);


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        name = findViewById(R.id.name);
        surname = findViewById(R.id.surname);
        email = findViewById(R.id.login);
        course = findViewById(R.id.course);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.passwordConfirmed);

        name.setText(user.getName());
        surname.setText(user.getSurname());
        email.setText(user.getEmail());
        course.setText(user.getCourse());
        email.setKeyListener(null);



        Button button3 = findViewById(R.id.backBtn);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(editProfile.this, MainStudent.class);
                startActivity(intent);
                finish();
            }
        });
        Button button4 = findViewById(R.id.editBtn);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.setBackgroundColor(getResources().getColor(R.color.buttonBackgroundDisable));
                surname.setBackgroundColor(getResources().getColor(R.color.buttonBackgroundDisable));
                //email.setBackgroundColor(getResources().getColor(R.color.buttonBackgroundDisable));
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
                if(TextUtils.isEmpty(course.getText())||course.getText().toString().trim().length() == 0){
                    course.setBackgroundColor(getResources().getColor(R.color.textNegative));
                    correct = false;
                }

                if(!confirmPassword.getText().toString().equals(password.getText().toString())){
                    confirmPassword.setBackgroundColor(getResources().getColor(R.color.textNegative));
                    correct = false;
                }

                if (correct){
                    Log.e("quiz1",user.getUsername());
                    User userN = new User();
                    userN.setId(user.getId());
                    userN.setPassword(password.getText().toString());
                    userN.setC_password(confirmPassword.getText().toString());
                    userN.setEmail(user.getEmail());
                    userN.setUsername(user.getUsername());
                    userN.setCourse(course.getText().toString());
                    userN.setName(name.getText().toString());
                    userN.setSurname(surname.getText().toString());

                    userN.setRole("user");
                    new EditUser().execute(userN);
                    loginFailed.setText("");
                    inProgress.setVisibility(View.VISIBLE);
                }

            }
        });
    }
    private class EditUser extends AsyncTask<User, Void, String> {
        private OkHttpClient mClient = new OkHttpClient();

        protected String doInBackground(User... user){
            Log.e("quiz1",Integer.toString(user[0].getId()));
            RequestBody requestBody = RequestBody.create(JSON, gson.toJson(user[0]));

            try {
                com.squareup.okhttp.Request request = new Request
                        .Builder()
                        .header("Authorization", "Bearer "+JWT)
                        .put(requestBody)
                        .url(editProfileURL)
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
                    loginFailed.setText("Edycja nie powiodła się");
                }
                else {

                    deleteFile("userName");
                    Intent intent = new Intent(editProfile.this, ReLogin.class);
                    startActivity(intent);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
