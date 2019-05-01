package studia.quiz.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Answer {
    private Integer id;
    private String text;
    private String idQuestion;
    private Integer status;
    private Integer value;


    public Answer(Integer id ){
        this.id = id;
    }

    Answer(JSONObject jsonObject)throws JSONException {
        this.id = (Integer) jsonObject.get("id");
        this.text = (String) jsonObject.get("text");
        this.idQuestion = (String) jsonObject.get("idQuestion").toString() ;
        try {
            this.status = (Integer) jsonObject.get("status");
        }catch (Exception e){
            //e.printStackTrace();
        }
        try {this.value = (Integer)jsonObject.get("value");}
        catch (Exception e){}
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setValue(Integer value ){
        this.value = value;
    }

    public Integer getValue(){
        return value;
    }

}
