package cs122b.moviequiz;

import android.widget.Button;
import android.widget.TextView;

/**
 * Created by john on 3/12/2015.
 */
public class QuizController extends Controller {

    private Button answer1;
    private Button answer2;
    private Button answer3;
    private Button answer4;

    private TextView timeField;
    private TextView questionField;

    public QuizController(){
        super(R.layout.activity_quiz);
    }

    @Override
    protected void onShow() {
        answer1 = getButton(R.id.answerButton1);
        answer2 = getButton(R.id.answerButton2);
        answer3 = getButton(R.id.answerButton3);
        answer4 = getButton(R.id.answerButton4);
        timeField = getTextView(R.id.countdownText);
        questionField = getTextView(R.id.questionText);

        timeField.setText("03:00");
        questionField.setText("Ready?");
    }

    @Override
    protected void onHide() {

    }
}
