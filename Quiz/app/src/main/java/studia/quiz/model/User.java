package studia.quiz.model;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private Integer id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private String password;
    private String role;
    private String course;

    private String c_password;

    public User(JSONObject jsonObject) throws JSONException {
        id = (Integer) jsonObject.get("id");
        name = jsonObject.getString("name");
        surname = jsonObject.getString("surname");
        username = jsonObject.getString("username");
        role = jsonObject.getString("role");
        course = jsonObject.getString("course");
        email = jsonObject.getString("email");
    }

    public User(){};

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getC_password() {
        return c_password;
    }

    public void setC_password(String c_password) {
        this.c_password = c_password;
    }
}
