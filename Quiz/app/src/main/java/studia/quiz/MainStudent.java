package studia.quiz;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import studia.quiz.model.Question;
import studia.quiz.model.Subject;
import studia.quiz.model.User;

public class MainStudent extends AppCompatActivity {

    String JWT ="";
    User user;
    MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    Gson gson = new Gson();
    String getCourseTestList;
    RelativeLayout quizList;
    public ProgressDialog progress;
    List<Subject> subjectList = new ArrayList<Subject>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_student);
        getCourseTestList =getApplicationContext().getString(R.string.url, "/api/subject/list/course/");
        quizList = findViewById(R.id.quizList);
        String userString = "";
        FileInputStream inputStream;
        try {
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
            JSONObject userJSON = new JSONObject(userString);
            Log.d("quiz1",userString);
            JWT = userJSON.getString("token");
            user = new User(userJSON.optJSONObject("user"));
            new GetSubjectList().execute(user);
            progress = ProgressDialog.show(MainStudent.this, "Pobieranie danych ...", "Oczekowanie na dane...", true);

        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(MainStudent.this, MainActivity.class);
            startActivity(intent);
        }

        Button editProfile = findViewById(R.id.editProfileBtn);
        final String finalUserString = userString;
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFile("userName");
                Intent intent = new Intent(MainStudent.this, editProfile.class);
                intent.putExtra("user", finalUserString);
                startActivity(intent);

            }
        });



        Button logout = findViewById(R.id.logoutBtn);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteFile("userName");
                Intent intent = new Intent(MainStudent.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



    }


    private class GetSubjectList extends AsyncTask<User, Void, String> {
        private OkHttpClient mClient = new OkHttpClient();

        protected String doInBackground(User... user){


            try {
                com.squareup.okhttp.Request request = new Request
                        .Builder()
                        .header("Authorization", "Bearer "+JWT)
                        .url(getCourseTestList+user[0].getCourse())
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


        protected void onPostExecute(String quizListString) {
            Log.d("quiz1",quizListString);
            progress.dismiss();
            try {
                JSONArray quizListArray = new JSONArray(quizListString);
                for (int i = 0; i < quizListArray.length(); i++) {
                    JSONObject jsonObject = quizListArray.getJSONObject(i);
                    final Subject subject = new Subject(jsonObject);
                    subjectList.add(subject);

                    TextView subjectView = new TextView(getApplicationContext());
                    RelativeLayout.LayoutParams subjectParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    if(i>0)
                    {
                        subjectParam.addRule(RelativeLayout.BELOW,quizList.getChildAt(quizList.getChildCount()-1).getId());
                        subjectParam.setMargins(0, 20, 0, 0);

                    }
                    subjectView.setLayoutParams(subjectParam);
                    subjectView.setText(Integer.toString(i+1) + ". " + subject.getName());
                    //subjectView.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.quizcard));
                    subjectView.setTextColor(getResources().getColor(R.color.textWhite));
                    subjectView.setId(View.generateViewId());
                    subjectView.setTextSize(16);
                    subjectView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //TODO: start nowej aktywności z testem
                            Intent intent = new Intent(MainStudent.this, TestBegin.class);
                            intent.putExtra("id",Integer.toString(subject.getId()));
                            intent.putExtra("jwt",JWT);
                            startActivity(intent);
                        }
                    });
                    quizList.addView(subjectView);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            /*try {
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
            }*/

        }
    }
}
