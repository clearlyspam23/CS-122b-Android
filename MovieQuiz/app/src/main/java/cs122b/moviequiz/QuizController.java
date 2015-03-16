package cs122b.moviequiz;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by john on 3/12/2015.
 */
public class QuizController extends Controller implements View.OnClickListener{

    private Button[] buttons;
    private Button backButton;

    private TableLayout tableLayout;

    private TextView timeField;
    private TextView questionField;

    private long quizDuration;

    private long currentStart;

    private long currentTime;

    private Question currentQuestion;

    private QuestionGenerator generator = new QuestionGenerator();

    private CountDownTimer quizTimer;

    private ArrayList<QuestionResult> results;
    private int questionsAnswered;
    private int questionsCorrect;

    public QuizController(){
        this(180);
    }

    public QuizController(int seconds){
        super(R.layout.activity_quiz);
        quizDuration = seconds * 1000l;
        results = new ArrayList<QuestionResult>();
    }

    @Override
    protected void onShow() {
        buttons = new Button[]{
                getButton(R.id.answerButton1),
                getButton(R.id.answerButton2),
                getButton(R.id.answerButton3),
                getButton(R.id.answerButton4)
        };
        for(Button b : buttons) {
            b.setOnClickListener(this);
        }
        tableLayout = getView(R.id.tableLayout, TableLayout.class);
        timeField = getTextView(R.id.countdownText);
        questionField = getTextView(R.id.questionText);
        backButton = getButton(R.id.backButton);

        timeField.setText(calculateTime(quizDuration));
        questionField.setTextColor(Color.BLACK);
        questionField.setText("Ready?");
        results.clear();

        new CountDownTimer(4000, 4000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                questionField.setText("GO!");
                new CountDownTimer(2000, 2000) {

                    @Override
                    public void onTick(long millisUntilFinished) {}

                    public void onFinish() {
                        startQuiz();
                    }
                }.start();
            }
        }.start();
    }

    private String calculateTime(long millis){
        int totalSeconds = (int) millis/1000;
        int minutes = totalSeconds/60;
        String output = minutes + ":";
        if(output.length()<3) {
            output = "0" + output;
        }
        String outSeconds = "" + totalSeconds%60;
        if(outSeconds.length()<2){
            outSeconds = "0" + outSeconds;
        }
        return output + outSeconds;
    }

    private CountDownTimer generateTimer(long timeMillis){
        return new CountDownTimer(timeMillis, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                currentTime = quizDuration - millisUntilFinished;
                timeField.setText(calculateTime(millisUntilFinished));
            }

            public void onFinish() {
                finishQuiz();
            }
        }.start();
    }

    private void startQuiz(){
        currentTime = 0;
        tableLayout.setVisibility(View.VISIBLE);
        quizTimer = generateTimer(quizDuration);
        generateQuestion();
     }

    private void generateQuestion(){
        currentStart = currentTime;
        currentQuestion = generator.generateRandomQuestion();
        questionField.setTextColor(Color.BLACK);
        questionField.setText(currentQuestion.getQuestion());
        for(int i = 0; i < Math.min(currentQuestion.getOptions().size(), buttons.length); i++){
            buttons[i].setTextColor(Color.BLACK);
            buttons[i].setText(currentQuestion.getOptions().get(i));
            buttons[i].setClickable(true);
        }
    }

    private void finishQuiz(){
        questionField.setText("Times Up!");
        questionField.setTextColor(Color.BLACK);
        for(Button b : buttons){
            b.setClickable(false);
        }
        tableLayout.setVisibility(View.INVISIBLE);
        new CountDownTimer(2000, 2000) {

            @Override
            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                questionField.setText("Correct : (" + questionsCorrect + ") Wrong: (" + (questionsAnswered - questionsCorrect) + ")");
                backButton.setVisibility(View.VISIBLE);
                backButton.setEnabled(true);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        back();
                    }
                });
            }
        }.start();
        //store the results somewhere
    }

    @Override
    protected void onHide() {
        backButton.setVisibility(View.INVISIBLE);
        backButton.setEnabled(false);
    }

    private Button findRightButton(){
        for(Button b : buttons){
            if(b.getText().equals(currentQuestion.getAnswer()))
                return b;
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        //check if the answer is right and store it
        boolean correct = currentQuestion.getAnswer().equals(((Button)v).getText());
        long timeTaken = currentTime - currentStart;
        results.add(new QuestionResult(correct, timeTaken));
        questionsAnswered++;
        if(!correct) {
            //the answer is wrong
            ((Button) v).setTextColor(Color.RED);
        }
        else
            questionsCorrect++;
        //highlight the right answer
        findRightButton().setTextColor(Color.GREEN);
        for(Button b : buttons){
            b.setClickable(false);
        }
        questionField.setText((correct ? "Correct!" : "Wrong!"));
        questionField.setTextColor((correct ? Color.GREEN : Color.RED));
        new CountDownTimer(2000, 2000) {

            @Override
            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
                generateQuestion();
            }
        }.start();
    }
}
