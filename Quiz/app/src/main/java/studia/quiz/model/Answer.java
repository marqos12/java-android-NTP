package studia.quiz.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Answer {
    private Integer id;
    private String text;
    private String idQuestion;
    private Boolean status;
    private Boolean value;


    public Answer(Integer id ){
        this.id = id;
    }

    Answer(JSONObject jsonObject)throws JSONException {
        this.id = (Integer) jsonObject.get("id");
        this.text = (String) jsonObject.get("text");
        //this.idQuestion = (String) jsonObject.get("id").toString() ;
        try {
            this.status = jsonObject.getBoolean("status");
        }catch (Exception e){
            //e.printStackTrace();
        }

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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public void setValue(Boolean value ){
        this.value = value;
    }

    public Boolean getValue(){
        return value;
    }

}
