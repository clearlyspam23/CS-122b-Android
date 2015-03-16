package cs122b.moviequiz;

import java.util.List;

/**
 * Created by daniellancehirsch on 3/15/15.
 */
public class Question {
    private String question;
    private String answer;
    private List<String> options;

    public Question(String question, String answer, List<String> options) {
        this.question = question;
        this.answer = answer;
        this.options = options;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public List<String> getOptions() {
        return options;
    }

    public String toString() {
        StringBuffer toReturn = new StringBuffer();
        toReturn.append("Question: " + question + " | Answer: " + answer + " | options: [");
        for(int i=0; i<4; i++) {
            toReturn.append(options.get(i));
            if(i != 3) {
                toReturn.append(",");
            }
        }
        toReturn.append("]");

        return toReturn.toString();
    }
}
