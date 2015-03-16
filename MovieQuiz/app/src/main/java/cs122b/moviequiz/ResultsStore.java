package cs122b.moviequiz;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

/**
 * Created by john on 3/15/2015.
 */
public class ResultsStore {

    private static String DB_PATH = "/data/data/cs122b.moviequiz/databases/";
    private static String DB_NAME = "mydb";
    private SQLiteDatabase myDB;

    public ResultsStore(){
        String myPath = DB_PATH + DB_NAME;
        myDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public int getNextQuizId(){
        String query = "SELECT max(quiz_id) " +
                "FROM quiz_results;";
        Cursor c = myDB.rawQuery(query, new String[]{});
        if(c.moveToNext()){
            return c.getInt(0)+1;
        }
        return 0;
    }

    public int getQuizCount(){
        String query = "SELECT COUNT(DISTINCT quiz_id)" +
                "FROM quiz_results;";
        Cursor c = myDB.rawQuery(query, new String[]{});
        if(c.moveToNext()){
            return c.getInt(0);
        }
        return 0;
    }

    public int getAverageQuizTime(){
        String query = "SELECT AVG(time) " +
                "FROM quiz_results;";
        Cursor c = myDB.rawQuery(query, new String[]{});
        if(c.moveToNext()){
            return c.getInt(0);
        }
        return 0;
    }

    public int getTotalQuizTime(){
        String query = "SELECT SUM(time) " +
                "FROM quiz_results;";
        Cursor c = myDB.rawQuery(query, new String[]{});
        if(c.moveToNext()){
            return c.getInt(0);
        }
        return 0;
    }

    public int getQuestionsTaken(){
        String query = "SELECT COUNT(*) " +
                "FROM quiz_results;";
        Cursor c = myDB.rawQuery(query, new String[]{});
        if(c.moveToNext()){
            return c.getInt(0);
        }
        return 0;
    }

    public int getCorrectAnswers(){
        String query = "SELECT COUNT(*) " +
                "FROM quiz_results " +
                "WHERE correct='1';";
        Cursor c = myDB.rawQuery(query, new String[]{});
        if(c.moveToNext()){
            return c.getInt(0);
        }
        return 0;
    }

    public int getWrongAnswers(){
        String query = "SELECT COUNT(*) " +
                "FROM quiz_results " +
                "WHERE correct='0';";
        Cursor c = myDB.rawQuery(query, new String[]{});
        if(c.moveToNext()){
            return c.getInt(0);
        }
        return 0;
    }

    public void addResult(int quizId, int questionId, boolean result, long time){
        String insertQuery = "INSERT INTO quiz_results " +
                "(quiz_id, question_id, correct, time) " +
                "VALUES " +
                "( '" + quizId + "', '" + questionId + "', '" + (result ? "1" : "0") + "', '" +  time + "');";
        myDB.execSQL(insertQuery);
    }

}
