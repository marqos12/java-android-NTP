package studia.quiz.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Answer {
    private Integer id;
    private String text;
    private String idQuestion;
    private String status;


    Answer(JSONObject jsonObject)throws JSONException {
        this.id = (Integer) jsonObject.get("id");
        this.text = (String) jsonObject.get("text");
        this.idQuestion = (String) jsonObject.get("idQuestion");

        if (jsonObject.get("status").toString()!="null")
        this.status = (String) jsonObject.get("status");
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getIdQuestion() {
        return idQuestion;
    }

    public void setIdQuestion(String idQuestion) {
        this.idQuestion = idQuestion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
