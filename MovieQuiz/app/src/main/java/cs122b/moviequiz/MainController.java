package cs122b.moviequiz;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by john on 3/15/2015.
 */
public class MainController extends Controller implements View.OnClickListener{

    private Button takeQuizButton;
    private Button viewStatisticsButton;

    public QuizController quizController;
    public ResultsController resultsController;

    public MainController() {
        super(R.layout.activity_main);
        quizController = new QuizController();
        resultsController = new ResultsController();
    }

    @Override
    protected void onShow() {
        takeQuizButton = getButton(R.id.takeQuizButton);
        takeQuizButton.setOnClickListener(this);
        viewStatisticsButton = getButton(R.id.quizStatsButton);
        viewStatisticsButton.setOnClickListener(this);
    }

    @Override
    protected void onHide() {

    }


    @Override
    public void onClick(View v) {
        if(v.equals(takeQuizButton)){
            goToController(quizController);
        }
        else if(v.equals(viewStatisticsButton)){
            goToController(resultsController);
        }
    }

    public void onRestoreInstanceState(Bundle inState){
        quizController.onRestoreInstanceState(inState);
        resultsController.onRestoreInstanceState(inState);
    }

    public void onSaveInstanceState(Bundle outState){
        quizController.onSaveInstanceState(outState);
        resultsController.onSaveInstanceState(outState);
    }
}
