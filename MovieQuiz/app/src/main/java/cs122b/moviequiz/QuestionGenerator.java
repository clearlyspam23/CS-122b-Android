package cs122b.moviequiz;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by daniellancehirsch on 3/15/15.
 */
public class QuestionGenerator {
    private static String DB_PATH = "/data/data/cs122b.moviequiz/databases/";
    private static String DB_NAME = "mydb";
    private SQLiteDatabase myDB;


    public QuestionGenerator() {
        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    public Question generateRandomQuestion() {
        Random random = new Random();

        //int choice = random.nextInt(10);
        int choice = 0; // TODO: REMOVE

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
        String questionTemplate = "Who directed the movie @MOVIE@?";

        // Get the correct answer and movie title
        String correctAnswerQuery = "SELECT director, title " +
                                    "FROM movies " +
                                    "WHERE director != '' AND title != '' " +
                                    "ORDER BY RANDOM() " +
                                    "LIMIT 1;";
        Cursor answerCursor = myDB.rawQuery(correctAnswerQuery, null);
        answerCursor.moveToFirst();
        String directorAnswer = answerCursor.getString(0); // This is the answer to the question
        String movieTitle = answerCursor.getString(1);
        answerCursor.close();

        // Add the correct answer to the list of user options
        List<String> options = new ArrayList<String>();
        options.add(directorAnswer);

        // Get the wrong answer options
        String wrongAnswerQuery = "SELECT director " +
                                  "FROM movies " +
                                  "WHERE director != '' AND title != '' AND director != ? " +
                                  "ORDER BY RANDOM() " +
                                  "LIMIT 3;";
        Cursor wrongAswersCursor = myDB.rawQuery(wrongAnswerQuery, new String[]{directorAnswer} );
        while (wrongAswersCursor.moveToNext()) {
            // Add each wrong answer to the list of user options
            options.add(wrongAswersCursor.getString(0));
        }
        wrongAswersCursor.close();

        // Generate the Question object and return it
        String question = questionTemplate.replace("@MOVIE@", movieTitle);
        String answer = directorAnswer;
        Collections.shuffle(options); // Randomize the user options

        return new Question(question, answer, options);
    }

    private Question whenWasMovieReleased() {
        String questionTemplate = "When was the movie @MOVIE@ released?";
        return null;
    }

    private Question whichStarWasInMovie() {
        String questionTemplate = "Which star was in the movie @MOVIE@?";
        return null;
    }

    private Question whichStarWasNotInMovie() {
        String questionTemplate = "Which star was not in the movie @MOVIE@?";
        return null;
    }

    private Question whichMovieDidTwoStarsAppearTogether() {
        String questionTemplate = "In which movie the stars @STAR1@ and @STAR2@ appear together?";
        return null;
    }

    private Question whoDirectedStar() {
        String questionTemplate = "Who directed the star @STAR@?";
        return null;
    }

    private Question whoDidNotDirectStar() {
        String questionTemplate = "Who did not direct the star @STAR@?";
        return null;
    }

    private Question whichStarAppearsInBothMovies() {
        String questionTemplate = "Which star appears in both movies @MOVIE1@ and @MOVIE2@?";
        return null;
    }

    private Question whichStarDidNotAppearInMovieWithThisStar() {
        String questionTemplate = "Which star did not appear in the same movie with the star @STAR@?";
        return null;
    }

    private Question whoDirectedStarInYear() {
        String questionTemplate = "Who directed the star @STAR@ in year @YEAR@?";
        return null;
    }
}
