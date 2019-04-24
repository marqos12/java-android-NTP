package studia.quiz.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Subject {
    private Integer id;
    private String name;
    private Integer idAuthor;
    private Integer noQuestions;
    private Integer multipleChoice;
    private Integer separatePage;
    private Integer canBack;
    private Integer randomize;
    private Integer limitedTime;
    private Integer time;
    private String course;
    private String description;
    private String subject;

    public Subject(JSONObject jsonObject)throws JSONException {
        this.id = (Integer) jsonObject.get("id");
        this.name = (String) jsonObject.get("name");
        this.idAuthor = (Integer) jsonObject.get("idAuthor");
        this.noQuestions = (Integer) jsonObject.get("noQuestions");
        this.multipleChoice = (Integer) jsonObject.get("multipleChoice");
        this.separatePage = (Integer) jsonObject.get("separatePage");
        this.canBack = (Integer) jsonObject.get("canBack");
        this.randomize = (Integer) jsonObject.get("randomize");
        this.limitedTime = (Integer) jsonObject.get("limitedTime");
        this.time = (Integer) jsonObject.get("time");
        this.course = (String) jsonObject.get("course");
        if (jsonObject.get("description").toString()!="null")
            this.description = (String) jsonObject.get("description");
        this.subject = (String) jsonObject.get("subject");




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

    public Integer getMultipleChoice() {
        return multipleChoice;
    }

    public void setMultipleChoice(Integer multipleChoice) {
        this.multipleChoice = multipleChoice;
    }

    public Integer getSeparatePage() {
        return separatePage;
    }

    public void setSeparatePage(Integer separatePage) {
        this.separatePage = separatePage;
    }

    public Integer getCanBack() {
        return canBack;
    }

    public void setCanBack(Integer canBack) {
        this.canBack = canBack;
    }

    public Integer getRandomize() {
        return randomize;
    }

    public void setRandomize(Integer randomize) {
        this.randomize = randomize;
    }

    public Integer getLimitedTime() {
        return limitedTime;
    }

    public void setLimitedTime(Integer limitedTime) {
        this.limitedTime = limitedTime;
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
}
