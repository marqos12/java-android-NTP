package studia.quiz.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Subject {
    private Long id;
    private String name;
    private Long idAuthor;
    private Long noQuestions;
    private Boolean multipleChoice;
    private Boolean separatePage;
    private Boolean canBack;
    private Boolean randomize;
    private Boolean limitedTime;
    private Long time;
    private String course;
    private String description;
    private String subject;

    public Subject(JSONObject jsonObject)throws JSONException {
        this.id = (Long) jsonObject.get("id");
        this.name = (String) jsonObject.get("name");
        this.idAuthor = (Long) jsonObject.get("idAuthor");
        this.noQuestions = (Long) jsonObject.get("noQuestions");
        this.multipleChoice = (Boolean) jsonObject.get("multipleChoice");
        this.separatePage = (Boolean) jsonObject.get("separatePage");
        this.canBack = (Boolean) jsonObject.get("canBack");
        this.randomize = (Boolean) jsonObject.get("randomize");
        this.limitedTime = (Boolean) jsonObject.get("limitedTime");
        this.time = (Long) jsonObject.get("time");
        this.course = (String) jsonObject.get("course");
        this.description = (String) jsonObject.get("description");
        this.subject = (String) jsonObject.get("subject");




    }


        public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getIdAuthor() {
        return idAuthor;
    }

    public void setIdAuthor(Long idAuthor) {
        this.idAuthor = idAuthor;
    }

    public Long getNoQuestions() {
        return noQuestions;
    }

    public void setNoQuestions(Long noQuestions) {
        this.noQuestions = noQuestions;
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

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
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
