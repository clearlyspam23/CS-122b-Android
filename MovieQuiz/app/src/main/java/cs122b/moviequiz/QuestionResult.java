package cs122b.moviequiz;

/**
 * Created by john on 3/15/2015.
 */
public class QuestionResult {

    public boolean correct;
    public long timeTaken;

    public QuestionResult(){

    }

    public QuestionResult(boolean correct, long timeTaken){
        this.correct = correct;
        this.timeTaken = timeTaken;
    }
}
