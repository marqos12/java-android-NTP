package studia.quiz.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private Integer id;
    private String category;
    private Integer idSubject;
    private String text;
    private String code;
    private String image;
    private List<Answer> answers;

    public Question(JSONObject jsonObject)throws JSONException {
        this.id = (Integer) jsonObject.get("id");
       // this.category = (String) jsonObject.get("category");
        this.idSubject = (Integer) jsonObject.get("idSubject");
        this.text = (String) jsonObject.get("text");
        if (jsonObject.get("code").toString()!="null")
            this.code = (String) jsonObject.get("code");
        if (jsonObject.get("image").toString()!="null")
            this.image = (String) jsonObject.get("image");
        JSONArray jsonArray = (JSONArray) jsonObject.get("answers");
        this.answers = new ArrayList<Answer>();
        this.answers.add(new Answer(jsonArray.getJSONObject(0)));
        this.answers.add(new Answer(jsonArray.getJSONObject(1)));
        this.answers.add(new Answer(jsonArray.getJSONObject(2)));
        this.answers.add(new Answer(jsonArray.getJSONObject(3)));
    }
    public Question(){}


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getIdSubject() {
        return idSubject;
    }

    public void setIdSubject(Integer idSubject) {
        this.idSubject = idSubject;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}
