package cs122b.moviequiz;

import android.database.sqlite.SQLiteDatabase;

import java.util.Random;

/**
 * Created by daniellancehirsch on 3/15/15.
 */
public class QuestionGenerator {
    private static String DB_PATH = "/data/data/cs122b.moviequiz/databases/";
    private static String DB_NAME = "myDB";
    private SQLiteDatabase myDB;


    public void QuestionGenerator() {
        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    public Question generateRandomQuestion() {
        Random random = new Random();

        int choice = random.nextInt(10);

        switch(choice) {
            case 0:
                return whoDirectedMovie();
            case 1:
                return whenWasMovieReleased();
            case 2:
                return whichStarWasInMovie();
            case 3:
                return whichStarWasNotInMovie();
            case 4:
                return whichMovieDidTwoStarsAppearTogether();
            case 5:
                return whoDirectedStar();
            case 6:
                return whoDidNotDirectStar();
            case 7:
                return whichStarAppearsInBothMovies();
            case 8:
                return whichStarDidNotAppearInMovieWithThisStar();
            case 9:
                return whoDirectedStarInYear();
            default:
                throw new IllegalArgumentException();
        }

    }

    private Question whoDirectedMovie() {
        return null;
    }

    private Question whenWasMovieReleased() {
        return null;
    }

    private Question whichStarWasInMovie() {
        return null;
    }

    private Question whichStarWasNotInMovie() {
        return null;
    }

    private Question whichMovieDidTwoStarsAppearTogether() {
        return null;
    }

    private Question whoDirectedStar() {
        return null;
    }

    private Question whoDidNotDirectStar() {
        return null;
    }

    private Question whichStarAppearsInBothMovies() {
        return null;
    }

    private Question whichStarDidNotAppearInMovieWithThisStar() {
        return null;
    }

    private Question whoDirectedStarInYear() {
        return null;
    }
}
