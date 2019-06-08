package studia.quiz.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Subject {
    private Integer id;
    private String name;
    private Integer idAuthor;
    private Integer noQuestions;

    private Boolean multipleChoice;
    private Boolean separatePage;
    private Boolean canBack;
    private Boolean randomize;
    private Boolean limitedTime;
    private Integer time;
    private String course;
    private String description;
    private String subject;
    private List<Question> questions;

    public Subject(JSONObject jsonObject)throws JSONException {
        this.id = (Integer) jsonObject.get("id");
        this.name = (String) jsonObject.get("name");
        //this.idAuthor = (Integer) jsonObject.get("idAuthor");
        if(!jsonObject.isNull("noQuestions"))
        this.noQuestions = (Integer) jsonObject.get("noQuestions");
        this.multipleChoice =  jsonObject.getBoolean("multipleChoice");
        this.separatePage =  jsonObject.getBoolean("separatePage");
        this.canBack =  jsonObject.getBoolean("canBack");
        this.randomize =  jsonObject.getBoolean("randomize");
        this.limitedTime =  jsonObject.getBoolean("limitedTime");
        this.time = (Integer) jsonObject.get("time");
        this.course = (String) jsonObject.get("course");
        if (jsonObject.get("description").toString()!="null")
            this.description = (String) jsonObject.get("description");
        this.subject = (String) jsonObject.get("subject");

        this.questions = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("questions");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
            Question question = new Question(jsonObject2,this.id );
            this.questions.add(question);
        }


    }


        public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(Integer idAuthor) {
        this.idAuthor = idAuthor;
    }

    public Integer getNoQuestions() {
        return noQuestions;
    }

    public void setNoQuestions(Integer noQuestions) {
        this.noQuestions = noQuestions;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Boolean getMultipleChoice() {
        return multipleChoice;
    }

    public void setMultipleChoice(Boolean multipleChoice) {
        this.multipleChoice = multipleChoice;
    }

    public Boolean getSeparatePage() {
        return separatePage;
    }

    public void setSeparatePage(Boolean separatePage) {
        this.separatePage = separatePage;
    }

    public Boolean getCanBack() {
        return canBack;
    }

    public void setCanBack(Boolean canBack) {
        this.canBack = canBack;
    }

    public Boolean getRandomize() {
        return randomize;
    }

    public void setRandomize(Boolean randomize) {
        this.randomize = randomize;
    }

    public Boolean getLimitedTime() {
        return limitedTime;
    }

    public void setLimitedTime(Boolean limitedTime) {
        this.limitedTime = limitedTime;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}
