package cs122b.moviequiz;

import android.view.View;

/**
 * Created by john on 3/15/2015.
 */
public class ResultsController extends Controller {
    public ResultsController() {
        super(R.layout.activity_results);
    }

    @Override
    protected void onShow() {
        getButton(R.id.statsBackButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
        ResultsStore results = getActivity().getResults();
        getTextView(R.id.statsNumQuizes).setText(String.valueOf(results.getQuizCount()));
        getTextView(R.id.statsQuestionsAnswered).setText(String.valueOf(results.getQuestionsTaken()));
        getTextView(R.id.statsCorrectAnswers).setText(String.valueOf(results.getCorrectAnswers()));
        getTextView(R.id.statsWrongAnswers).setText(String.valueOf(results.getWrongAnswers()));
        getTextView(R.id.statsAverageTime).setText(calcSeconds(results.getAverageQuizTime()));
        getTextView(R.id.statsTotalTime).setText(calcSeconds(results.getTotalQuizTime()));
    }

    private String calcSeconds(int result){
        String outseconds =  "" + result/1000;
        String outMillis = "" + result%1000;
        while(outMillis.length()<4){
            outMillis = "0" + outMillis;
        }
        while(outMillis.length()>1&&outMillis.charAt(outMillis.length()-1)=='0'){
            outMillis = outMillis.substring(0, outMillis.length()-1);
        }
        return outseconds + "." + outMillis;
    }

    @Override
    protected void onHide() {

    }
}
