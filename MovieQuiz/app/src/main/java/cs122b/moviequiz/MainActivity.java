package cs122b.moviequiz;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class MainActivity extends ActionBarActivity {

    private ArrayList<Controller> activeControllers = new ArrayList<>();

    private MainController mainController;
    private ResultsStore results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabase myDB= null;

        try {

            myDB = this.openOrCreateDatabase("mydb", MODE_PRIVATE, null);

            /* CREATE tables and IMPORT data */
            createResultsTable(myDB);
            importMoviesTable(myDB); // Create 'movies' table and import all data
            importStarsTable(myDB); // Create 'stars' table and import all data
            importStarsInMoviesTable(myDB); // Create 'stars_in_movies' table and import all data
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        results = new ResultsStore();
        setCurrentController(mainController = new MainController());
    }

    protected void onPause(){
        super.onPause();
        getCurrentController().onPause();
    }

    protected void onResume(){
        super.onResume();
        getCurrentController().onResume();
    }

    protected void onSaveInstanceState(Bundle outBundle){
        super.onSaveInstanceState(outBundle);
        ArrayList<Integer> controlStackIds = new ArrayList<Integer>();
        for(Controller c : activeControllers){
            c.onSaveInstanceState(outBundle);
            if(c instanceof MainController){
                controlStackIds.add(0);
            }
            else if(c instanceof QuizController){
                controlStackIds.add(1);
            }
            else if(c instanceof ResultsController)
                controlStackIds.add(2);
        }
        outBundle.putIntegerArrayList("controlStack", controlStackIds);
    }

    protected void onRestoreInstanceState(Bundle inBundle){
        super.onRestoreInstanceState(inBundle);
        activeControllers.clear();
        mainController = new MainController();
        ArrayList<Integer> controlStackIds = inBundle.getIntegerArrayList("controlStack");
        for(Integer i : controlStackIds){
            if(i.intValue()==0) {
                mainController.onRestoreInstanceState(inBundle);
                mainController.setActivity(this);
                activeControllers.add(mainController);
            }
            else if(i.intValue()==1) {
                mainController.quizController.onRestoreInstanceState(inBundle);
                mainController.quizController.setActivity(this);
                activeControllers.add(mainController.quizController);
            }
            else if(i.intValue()==2) {
                mainController.resultsController.onRestoreInstanceState(inBundle);
                mainController.resultsController.setActivity(this);
                activeControllers.add(mainController.resultsController);
            }
        }
        if(hasController())
            setContentView(getCurrentController().getView());
    }

    public void back(){
        if(hasController()) {
            getCurrentController().hide(this);
            activeControllers.remove(activeControllers.size()-1);
            if(hasController())
                getCurrentController().show(this);
        }
    }

    public Controller getCurrentController(){
        return activeControllers.get(activeControllers.size()-1);
    }

    public boolean hasController(){
        return !activeControllers.isEmpty();
    }

    public void setCurrentController(Controller controller){
        if(hasController())
            getCurrentController().hide(this);
        activeControllers.add(controller);
        controller.show(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createResultsTable(SQLiteDatabase myDB){
        String createTableSQL = "CREATE TABLE IF NOT EXISTS quiz_results( " +
                "quiz_id INTEGER NOT NULL, " +
                "question_id INTEGER NOT NULL, " +
                "correct INTEGER NOT NULL, " +
                "time INTEGER NOT NULL, " +
                "PRIMARY KEY(quiz_id, question_id)" +
                ");";
        myDB.execSQL(createTableSQL);
    }

    private void importStarsTable(SQLiteDatabase myDB) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS stars( " +
                                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                    "first_name VARCHAR NOT NULL, " +
                                    "last_name VARCHAR NOT NULL, " +
                                    "dob VARCHAR" +
                                ");";

        String insertRowTemplate = "INSERT INTO stars (id, first_name, last_name, dob) VALUES (@TABLE_VALUES@);";

        String fileName = "stars.csv";

        String tableName = "stars";

        createAndImportTable(createTableSQL, insertRowTemplate, fileName, tableName, myDB);
    }

    private void importMoviesTable(SQLiteDatabase myDB) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS movies( " +
                                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                    "title VARCHAR NOT NULL, " +
                                    "year INTEGER NOT NULL, " +
                                    "director VARCHAR NOT NULL" +
                                ");";

        String insertRowTemplate = "INSERT INTO movies (id, title, year, director) VALUES (@TABLE_VALUES@);";

        String fileName = "movies.csv";

        String tableName = "movies";

        createAndImportTable(createTableSQL, insertRowTemplate, fileName, tableName, myDB);
    }

    private void importStarsInMoviesTable(SQLiteDatabase myDB) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS stars_in_movies( " +
                                    "star_id INTEGER NOT NULL, " +
                                    "movie_id INTEGER NOT NULL," +
                                    "FOREIGN KEY(star_id) REFERENCES stars(id)," +
                                    "FOREIGN KEY(movie_id) REFERENCES movies(id)" +
                                ");";

        String insertRowTemplate = "INSERT INTO stars_in_movies (star_id, movie_id) VALUES (@TABLE_VALUES@);";

        String fileName = "stars_in_movies.csv";

        String tableName = "stars_in_movies";

        createAndImportTable(createTableSQL, insertRowTemplate, fileName, tableName, myDB);
    }

    private void createAndImportTable(String createSQL, String importTemplateSQL, String fileName, String tableName, SQLiteDatabase myDB) {
        try {
            /* Create movie table in the Database. */
            myDB.execSQL(createSQL);

            /* Check if data has previously been imported in onCreate() */
            Cursor cursor = myDB.rawQuery("select count(*) from " + tableName, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
            cursor.close();

            // Only insert the rows if they are not already in the db
            if(count == 0) {
                /* Insert data into 'movies' Table*/
                StringBuffer movieImport = new StringBuffer();

                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(
                            new InputStreamReader(getAssets().open(fileName)));

                    String mLine = reader.readLine();
                    while (mLine != null) {
                        //insert row into db
                        myDB.execSQL(importTemplateSQL.replace("@TABLE_VALUES@", mLine));

                        mLine = reader.readLine();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ResultsStore getResults(){
        return results;
    }
}





