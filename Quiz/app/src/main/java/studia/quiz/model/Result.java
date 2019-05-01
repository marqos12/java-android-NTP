package studia.quiz.model;

import com.squareup.okhttp.RequestBody;

import org.json.JSONException;
import org.json.JSONObject;

public class Result {
    private Integer total;
    private Integer correct;

    public Result(){};

    public Result(JSONObject jsonObject)throws JSONException {
        this.total = (Integer) jsonObject.get("total");
        this.correct = (Integer) jsonObject.get("true");

    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCorrect() {
        return correct;
    }

    public void setCorrect(Integer correct) {
        this.correct = correct;
    }
}
