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
                return whichStarAppearsInBothMovies();
            case 7:
                return whoDirectedStarInYear();
            case 8:
                return whichStarDidNotAppearInMovieWithThisStar();
            case 9:
                return whoDidNotDirectStar(); // THIS IS INCOMPLETE
            default:
                throw new IllegalArgumentException();
        }

    }

    private Question whoDirectedMovie() {
        // The following string will be the template from which the question will be generated
        String questionTemplate = "Who directed the movie @MOVIE@?";
        String textToReplace = "@MOVIE@"; // We want to replace the following string

        // The following query will find the correct answer
        String correctAnswerSQL = "SELECT director, title " +
                "FROM movies " +
                "WHERE director != '' AND title != '' " +
                "ORDER BY RANDOM() " +
                "LIMIT 1;";
        // In the above query, we expect to find the answer at the following index
        int indexOfAnswer = 0;
        // In the above query, we expect to find the replacement at the following index
        int replacementIndex = 1;

        // The following query will find the wrong answers
        String incorrectAnswerSQL = "SELECT DISTINCT director " +
                "FROM movies " +
                "WHERE director != '' AND title != '' AND director != ? " +
                "ORDER BY RANDOM() " +
                "LIMIT 3;";

        return generateQuestion(questionTemplate, correctAnswerSQL, incorrectAnswerSQL, indexOfAnswer, replacementIndex, textToReplace);
    }

    private Question whenWasMovieReleased() {
        // The following string will be the template from which the question will be generated
        String questionTemplate = "When was the movie @MOVIE@ released?";
        String textToReplace = "@MOVIE@"; // We want to replace the following string

        // The following query will find the correct answer
        String correctAnswerSQL = "SELECT year, title " +
                                  "FROM movies " +
                                  "WHERE title != '' AND year != '' " +
                                  "ORDER BY RANDOM() " +
                                  "LIMIT 1;";
        // In the above query, we expect to find the answer at the following index
        int indexOfAnswer = 0;
        // In the above query, we expect to find the replacement at the following index
        int replacementIndex = 1;

        // The following query will find the wrong answers
        String incorrectAnswerSQL = "SELECT DISTINCT year " +
                                    "FROM movies " +
                                    "WHERE title != '' AND year != '' AND year != ? " +
                                    "ORDER BY RANDOM() " +
                                    "LIMIT 3;";

        return generateQuestion(questionTemplate, correctAnswerSQL, incorrectAnswerSQL, indexOfAnswer, replacementIndex, textToReplace);
    }

    private Question whichStarWasInMovie() {
        String questionTemplate = "Which star was in the movie @MOVIE@?";
        String textToReplace = "@MOVIE@"; // We want to replace the following string

        // The following query will find the correct answer
        String correctAnswerSQL = "SELECT first_name||' '||last_name, title " +
                                  "FROM movies M JOIN stars_in_movies SIM " +
                                      "ON M.id = SIM.movie_id JOIN stars S " +
                                      "ON SIM.star_id = S.id " +
                                  "WHERE title != '' AND first_name != '' AND last_name != '' " +
                                  "ORDER BY RANDOM() " +
                                  "LIMIT 1;";
        // In the above query, we expect to find the answer at the following index
        int indexOfAnswer = 0;
        // In the above query, we expect to find the replacement at the following index
        int replacementIndex = 1;

        // The following query will find the wrong answers
        String incorrectAnswerSQL = "SELECT DISTINCT first_name||' '||last_name AS name " +
                                    "FROM movies M JOIN stars_in_movies SIM " +
                                        "ON M.id = SIM.movie_id JOIN stars S " +
                                        "ON SIM.star_id = S.id " +
                                    "WHERE title != '' AND first_name != '' AND last_name != '' AND name != ? " +
                                    "ORDER BY RANDOM() " +
                                    "LIMIT 3;";

        return generateQuestion(questionTemplate, correctAnswerSQL, incorrectAnswerSQL, indexOfAnswer, replacementIndex, textToReplace);
    }

    private Question whichStarWasNotInMovie() {
        String questionTemplate = "Which star was not in the movie @MOVIE@?";
        String textToReplace = "@MOVIE@"; // We want to replace the following string

        String moviesWithThreeActorsSQL = "SELECT DISTINCT M.title as title " +
                                       "FROM stars_in_movies SIM1, stars_in_movies SIM2, stars_in_movies SIM3, movies M " +
                                       "WHERE SIM1.movie_id = SIM2.movie_id " +
                                            "AND SIM2.movie_id = SIM3.movie_id " +
                                            "AND SIM1.star_id != SIM2.star_id " +
                                            "AND SIM2.star_id != SIM3.star_id " +
                                            "AND M.id = SIM1.movie_id " +
                                       "ORDER BY RANDOM() " +
                                       "LIMIT 1;";
        Cursor replacementTextCursor = myDB.rawQuery(moviesWithThreeActorsSQL, null);
        replacementTextCursor.moveToFirst();
        String title = replacementTextCursor.getString(0);
        replacementTextCursor.close();

        String answerSQL = "SELECT DISTINCT first_name||' '||last_name AS name " +
                        "FROM movies M JOIN stars_in_movies SIM " +
                        "   ON M.id = SIM.movie_id JOIN stars S " +
                        "   ON SIM.star_id = S.id " +
                        "WHERE M.title != '' AND S.first_name != '' AND S.last_name != '' AND M.title != ? " +
                        "ORDER BY RANDOM() " +
                        "LIMIT 1;";
        Cursor answerCursor = myDB.rawQuery(answerSQL, new String[]{title});
        answerCursor.moveToFirst();
        String answer = answerCursor.getString(0);
        answerCursor.close();

        // Add the correct answer to the list of user options
        List<String> options = new ArrayList<String>();
        options.add(answer);

        String incorrectAnswerSQL = "SELECT DISTINCT first_name||' '||last_name AS name " +
                                 "FROM movies M JOIN stars_in_movies SIM " +
                                     "ON M.id = SIM.movie_id JOIN stars S " +
                                     "ON SIM.star_id = S.id " +
                                 "WHERE M.title != '' AND S.first_name != '' AND S.last_name != '' AND M.title = ? " +
                                 "LIMIT 3;";
        Cursor incorrectAnswerCursor = myDB.rawQuery(incorrectAnswerSQL, new String[]{title} );
        while (incorrectAnswerCursor.moveToNext()) {
            // Add each wrong answer to the list of user options
            options.add(incorrectAnswerCursor.getString(0));
        }
        incorrectAnswerCursor.close();

        // Generate the Question object and return it
        String question = questionTemplate.replace(textToReplace, title);
        Collections.shuffle(options); // Randomize the user options

        return new Question(question, answer, options);
    }

    private Question whichMovieDidTwoStarsAppearTogether() {
        String questionTemplate = "In which movie the stars @STAR1@ and @STAR2@ appear together?";

        String moviesWithTwoActorsSQL = "SELECT DISTINCT M.title as title, SIM1.star_id, SIM2.star_id  " +
                                        "FROM stars_in_movies SIM1, stars_in_movies SIM2, movies M " +
                                        "WHERE SIM1.movie_id = SIM2.movie_id " +
                                            "AND SIM1.star_id != SIM2.star_id " +
                                            "AND M.id = SIM1.movie_id " +
                                        "ORDER BY RANDOM() " +
                                        "LIMIT 1;";
        Cursor answerCursor = myDB.rawQuery(moviesWithTwoActorsSQL, null);
        answerCursor.moveToFirst();
        String answer = answerCursor.getString(0);
        String star_id1 = answerCursor.getString(1);
        String star_id2 = answerCursor.getString(2);
        answerCursor.close();

        String findStarByID = "SELECT DISTINCT first_name||' '||last_name AS name " +
                              "FROM stars S " +
                              "WHERE S.id = ?;";
        Cursor replacement1Cursor = myDB.rawQuery(findStarByID, new String[]{star_id1});
        replacement1Cursor.moveToFirst();
        String replacement1 = replacement1Cursor.getString(0);

        Cursor replacement2Cursor = myDB.rawQuery(findStarByID, new String[]{star_id2});
        replacement2Cursor.moveToFirst();
        String replacement2 = replacement2Cursor.getString(0);

        // Add the correct answer to the list of user options
        List<String> options = new ArrayList<String>();
        options.add(answer);

        String incorrectAnswerSQL = "SELECT M.title " +
                                    "FROM movies M JOIN stars_in_movies SIM " +
                                    "   ON M.id = SIM.movie_id JOIN stars S " +
                                    "   ON SIM.star_id = S.id " +
                                    "WHERE M.title != '' AND S.first_name != '' AND S.last_name != '' " +
                                    "   AND (SIM.star_id != ? OR SIM.star_id != ?) " +
                                    "ORDER BY RANDOM() " +
                                    "LIMIT 3;";
        Cursor incorrectAnswerCursor = myDB.rawQuery(incorrectAnswerSQL, new String[]{star_id1, star_id1} );
        while (incorrectAnswerCursor.moveToNext()) {
            // Add each wrong answer to the list of user options
            options.add(incorrectAnswerCursor.getString(0));
        }
        incorrectAnswerCursor.close();


        // Generate the Question object and return it
        String question = questionTemplate.replace("@STAR1@", replacement1).replace("@STAR2@", replacement2);
        Collections.shuffle(options); // Randomize the user options

        return new Question(question, answer, options);
    }

    private Question whoDirectedStar() {
        String questionTemplate = "Who directed the star @STAR@?";
        String textToReplace = "@STAR@"; // We want to replace the following string

        String movieWhereDirectorDirectedStar = "SELECT M.director, S.first_name||' '||S.last_name " +
                                                 "FROM movies M JOIN stars_in_movies SIM " +
                                                 "   ON M.id = SIM.movie_id JOIN stars S " +
                                                 "   ON SIM.star_id = S.id " +
                                                 "WHERE S.first_name != '' AND S.last_name != '' AND M.director != '' " +
                                                 "ORDER BY RANDOM() " +
                                                 "LIMIT 1;";
        Cursor answerCursor = myDB.rawQuery(movieWhereDirectorDirectedStar, null);
        answerCursor.moveToFirst();
        String answer = answerCursor.getString(0);
        String starReplacement = answerCursor.getString(1);
        answerCursor.close();

        // Add the correct answer to the list of user options
        List<String> options = new ArrayList<String>();
        options.add(answer);

        String incorrectAnswerSQL = "SELECT M.director " +
                                    "FROM movies M JOIN stars_in_movies SIM " +
                                    "   ON M.id = SIM.movie_id JOIN stars S " +
                                    "   ON SIM.star_id = S.id " +
                                    "WHERE M.director != '' AND S.first_name != '' AND S.last_name != '' " +
                                    "   AND (S.first_name||' '||S.last_name != ? OR M.director != ?) " +
                                    "ORDER BY RANDOM() " +
                                    "LIMIT 3;";
        Cursor incorrectAnswerCursor = myDB.rawQuery(incorrectAnswerSQL, new String[]{starReplacement, answer} );
        while (incorrectAnswerCursor.moveToNext()) {
            // Add each wrong answer to the list of user options
            options.add(incorrectAnswerCursor.getString(0));
        }
        incorrectAnswerCursor.close();


        // Generate the Question object and return it
        String question = questionTemplate.replace(textToReplace, starReplacement);
        Collections.shuffle(options); // Randomize the user options

        return new Question(question, answer, options);
    }

    private Question whoDidNotDirectStar() {
        String questionTemplate = "Who did not direct the star @STAR@?";
        String textToReplace = "@STAR@"; // We want to replace the following string

        String actorsWithThreeOrMoreDirectors = "SELECT DISTINCT S.first_name||' '||S.last_name AS name " +
                                                "FROM movies M JOIN stars_in_movies SIM " +
                                                "ON M.id = SIM.movie_id JOIN stars S " +
                                                "ON SIM.star_id = S.id " +
                                                "WHERE M.director != '' AND S.first_name||' '||S.last_name != '' " +
                                                "GROUP BY name " +
                                                "HAVING COUNT(M.director) > 2 " +
                                                "ORDER BY RANDOM() " +
                                                "LIMIT 1;";

        Cursor cursor = myDB.rawQuery(actorsWithThreeOrMoreDirectors, null);
        cursor.moveToFirst();
        String starReplacement = cursor.getString(0);
        cursor.close();

        String correctAnswerSQL = "SELECT DISTINCT M.director " +
                                  "FROM movies M JOIN stars_in_movies SIM " +
                                  "   ON M.id = SIM.movie_id JOIN stars S " +
                                  "   ON SIM.star_id = S.id " +
                                  "WHERE M.director != '' AND S.first_name||' '||S.last_name != ? " +
                                  "ORDER BY RANDOM() " +
                                  "LIMIT 1;";
        Cursor answerCursor = myDB.rawQuery(correctAnswerSQL, new String[]{starReplacement} );
        answerCursor.moveToFirst();
        String answer = answerCursor.getString(0);
        answerCursor.close();

        // Add the correct answer to the list of user options
        List<String> options = new ArrayList<String>();
        options.add(answer);

        String incorrectAnswerSQL = "SELECT DISTINCT M.director " +
                                    "FROM movies M JOIN stars_in_movies SIM " +
                                    "   ON M.id = SIM.movie_id JOIN stars S " +
                                    "   ON SIM.star_id = S.id " +
                                    "WHERE M.director != '' AND S.first_name||' '||S.last_name = ? " +
                                    "ORDER BY RANDOM() " +
                                    "LIMIT 3;";
        Cursor incorrectAnswerCursor = myDB.rawQuery(incorrectAnswerSQL, new String[]{starReplacement} );
        while (incorrectAnswerCursor.moveToNext()) {
            // Add each wrong answer to the list of user options
            options.add(incorrectAnswerCursor.getString(0));
        }
        incorrectAnswerCursor.close();


        // Generate the Question object and return it
        String question = questionTemplate.replace(textToReplace, starReplacement);
        Collections.shuffle(options); // Randomize the user options

        return new Question(question, answer, options);
    }

    private Question whichStarAppearsInBothMovies() {
        String questionTemplate = "Which star appears in both movies @MOVIE1@ and @MOVIE2@?";

        String actorWithTwoMoviesSQL = "SELECT DISTINCT S.first_name||' '||S.last_name, SIM1.movie_id, SIM2.movie_id  " +
                                       "FROM stars_in_movies SIM1, stars_in_movies SIM2, stars S " +
                                       "WHERE SIM1.star_id = SIM2.star_id " +
                                       "AND SIM1.movie_id != SIM2.movie_id " +
                                       "AND S.id = SIM1.star_id " +
                                       "ORDER BY RANDOM() " +
                                       "LIMIT 1;";
        Cursor answerCursor = myDB.rawQuery(actorWithTwoMoviesSQL, null);
        answerCursor.moveToFirst();
        String answer = answerCursor.getString(0);
        String movie_id1 = answerCursor.getString(1);
        String movie_id2 = answerCursor.getString(2);
        answerCursor.close();

        String findStarByID = "SELECT DISTINCT title AS name " +
                "FROM movies M " +
                "WHERE M.id = ?;";
        Cursor replacement1Cursor = myDB.rawQuery(findStarByID, new String[]{movie_id1});
        replacement1Cursor.moveToFirst();
        String replacement1 = replacement1Cursor.getString(0);

        Cursor replacement2Cursor = myDB.rawQuery(findStarByID, new String[]{movie_id2});
        replacement2Cursor.moveToFirst();
        String replacement2 = replacement2Cursor.getString(0);

        // Add the correct answer to the list of user options
        List<String> options = new ArrayList<String>();
        options.add(answer);

        String incorrectAnswerSQL = "SELECT S.first_name||' '||S.last_name " +
                                    "FROM stars S JOIN stars_in_movies SIM " +
                                    "   ON S.id = SIM.star_id JOIN movies M " +
                                    "   ON SIM.movie_id = M.id " +
                                    "WHERE M.title != '' AND S.first_name != '' AND S.last_name != '' " +
                                    "   AND (SIM.movie_id != ? OR SIM.movie_id != ?) " +
                                    "ORDER BY RANDOM() " +
                                    "LIMIT 3;";
        Cursor incorrectAnswerCursor = myDB.rawQuery(incorrectAnswerSQL, new String[]{movie_id1, movie_id2} );
        while (incorrectAnswerCursor.moveToNext()) {
            // Add each wrong answer to the list of user options
            options.add(incorrectAnswerCursor.getString(0));
        }
        incorrectAnswerCursor.close();


        // Generate the Question object and return it
        String question = questionTemplate.replace("@MOVIE1@", replacement1).replace("@MOVIE2@", replacement2);
        Collections.shuffle(options); // Randomize the user options

        return new Question(question, answer, options);
    }

    private Question whichStarDidNotAppearInMovieWithThisStar() {
        String questionTemplate = "Which star did not appear in the same movie with the star @STAR@?";

        String moviesWithFourActorsSQL = "SELECT DISTINCT M.title, SIM1.star_id, SIM2.star_id, SIM3.star_id, SIM4.star_id " +
                                          "FROM stars_in_movies SIM1, stars_in_movies SIM2, stars_in_movies SIM3, stars_in_movies SIM4, movies M " +
                                          "WHERE SIM1.movie_id = SIM2.movie_id " +
                                          "AND SIM2.movie_id = SIM3.movie_id " +
                                          "AND SIM3.movie_id = SIM4.movie_id " +
                                          "AND SIM1.star_id != SIM2.star_id " +
                                          "AND SIM2.star_id != SIM3.star_id " +
                                          "AND SIM3.star_id != SIM4.star_id " +
                                          "AND M.id = SIM1.movie_id " +
                                          "ORDER BY RANDOM() " +
                                          "LIMIT 1;";
        Cursor cursor = myDB.rawQuery(moviesWithFourActorsSQL, null);
        cursor.moveToFirst();
        String movieTitle = cursor.getString(0);
        String star_id1 = cursor.getString(1);
        String star_id2 = cursor.getString(2);
        String star_id3 = cursor.getString(3);
        String starReplacement_id = cursor.getString(4);
        cursor.close();

        String findStarByID = "SELECT DISTINCT first_name||' '||last_name AS name " +
                "FROM stars S " +
                "WHERE S.id = ?;";
        Cursor replacement1Cursor = myDB.rawQuery(findStarByID, new String[]{star_id1});
        replacement1Cursor.moveToFirst();
        String wrongActor1 = replacement1Cursor.getString(0);
        replacement1Cursor.close();

        Cursor replacement2Cursor = myDB.rawQuery(findStarByID, new String[]{star_id2});
        replacement2Cursor.moveToFirst();
        String wrongActor2 = replacement2Cursor.getString(0);
        replacement2Cursor.close();

        Cursor replacement3Cursor = myDB.rawQuery(findStarByID, new String[]{star_id3});
        replacement3Cursor.moveToFirst();
        String wrongActor3 = replacement3Cursor.getString(0);
        replacement3Cursor.close();

        Cursor replacement4Cursor = myDB.rawQuery(findStarByID, new String[]{starReplacement_id});
        replacement4Cursor.moveToFirst();
        String starReplacement = replacement4Cursor.getString(0);
        replacement4Cursor.close();

        // Add the incorrect answers to the list of user options
        List<String> options = new ArrayList<String>();
        options.add(wrongActor1);
        options.add(wrongActor2);
        options.add(wrongActor3);

        String answerSQL = "SELECT S.first_name||' '||S.last_name " +
                           "FROM movies M JOIN stars_in_movies SIM " +
                           "   ON M.id = SIM.movie_id JOIN stars S " +
                           "   ON SIM.star_id = S.id " +
                           "WHERE S.first_name != '' AND S.last_name != '' " +
                           "   AND M.title != ? " +
                           "ORDER BY RANDOM() " +
                           "LIMIT 1;";
        Cursor answerCursor = myDB.rawQuery(answerSQL, new String[]{movieTitle});
        answerCursor.moveToFirst();
        String answer = answerCursor.getString(0);
        answerCursor.close();

        // Add the correct answer
        options.add(answer);


        // Generate the Question object and return it
        String question = questionTemplate.replace("@STAR@", starReplacement);
        Collections.shuffle(options); // Randomize the user options

        return new Question(question, answer, options);
    }

    private Question whoDirectedStarInYear() {
        String questionTemplate = "Who directed the star @STAR@ in year @YEAR@?";

        String movieWhereDirectorDirectedStar = "SELECT M.director, S.first_name||' '||S.last_name, M.year " +
                                                "FROM movies M JOIN stars_in_movies SIM " +
                                                "   ON M.id = SIM.movie_id JOIN stars S " +
                                                "   ON SIM.star_id = S.id " +
                                                "WHERE S.first_name != '' AND S.last_name != '' " +
                                                    "AND M.director != '' AND year != '' " +
                                                "ORDER BY RANDOM() " +
                                                "LIMIT 1;";
        Cursor answerCursor = myDB.rawQuery(movieWhereDirectorDirectedStar, null);
        answerCursor.moveToFirst();
        String answer = answerCursor.getString(0);
        String starReplacement = answerCursor.getString(1);
        String yearReplacement = answerCursor.getString(2);
        answerCursor.close();

        // Add the correct answer to the list of user options
        List<String> options = new ArrayList<String>();
        options.add(answer);

        String incorrectAnswerSQL = "SELECT M.director " +
                                    "FROM movies M JOIN stars_in_movies SIM " +
                                    "   ON M.id = SIM.movie_id JOIN stars S " +
                                    "   ON SIM.star_id = S.id " +
                                    "WHERE M.director != '' AND S.first_name != '' AND S.last_name != '' " +
                                    "   AND (S.first_name||' '||S.last_name != ? OR M.director != ? OR year != ?) " +
                                    "ORDER BY RANDOM() " +
                                    "LIMIT 3;";
        Cursor incorrectAnswerCursor = myDB.rawQuery(incorrectAnswerSQL, new String[]{starReplacement, answer, yearReplacement} );
        while (incorrectAnswerCursor.moveToNext()) {
            // Add each wrong answer to the list of user options
            options.add(incorrectAnswerCursor.getString(0));
        }
        incorrectAnswerCursor.close();


        // Generate the Question object and return it
        String question = questionTemplate.replace("@STAR@", starReplacement).replace("@YEAR@", yearReplacement);
        Collections.shuffle(options); // Randomize the user options

        return new Question(question, answer, options);
    }

    private Question generateQuestion(String questionTemplate, String correctAnswerSQL, String incorrectAnswerSQL, int correctAnswerIndex,
                                      int stringReplaceIndex, String stringToReplace) {
        // Get the correct answer and movie title
        Cursor answerCursor = myDB.rawQuery(correctAnswerSQL, null);
        answerCursor.moveToFirst();
        String answer = answerCursor.getString(correctAnswerIndex); // This is the answer to the question
        String replacementText = answerCursor.getString(stringReplaceIndex);
        answerCursor.close();

        // Add the correct answer to the list of user options
        List<String> options = new ArrayList<String>();
        options.add(answer);

        // Get the wrong answer options
        Cursor wrongAswersCursor = myDB.rawQuery(incorrectAnswerSQL, new String[]{answer} );
        while (wrongAswersCursor.moveToNext()) {
            // Add each wrong answer to the list of user options
            options.add(wrongAswersCursor.getString(0));
        }
        wrongAswersCursor.close();

        // Generate the Question object and return it
        String question = questionTemplate.replace(stringToReplace, replacementText);
        Collections.shuffle(options); // Randomize the user options

        return new Question(question, answer, options);
    }
}
