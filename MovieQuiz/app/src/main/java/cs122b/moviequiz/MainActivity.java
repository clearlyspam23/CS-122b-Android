package cs122b.moviequiz;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabase myDB= null;

        try {
            myDB = this.openOrCreateDatabase("mydb", MODE_PRIVATE, null);

            /* CREATE tables and IMPORT data */
            importMoviesTable(myDB); // Create 'movies' table and import all data
            importStarsTable(myDB); // Create 'stars' table and import all data
            importStarsInMoviesTable(myDB); // Create 'stars_in_movies' table and import all data

            Cursor movieCursor = myDB.rawQuery("select count(*) from movies", null);
            movieCursor.moveToFirst();
            int movieCount = movieCursor.getInt(0);
            movieCursor.close();

            Cursor starsCursor = myDB.rawQuery("select count(*) from stars", null);
            starsCursor.moveToFirst();
            int starsCount = starsCursor.getInt(0);
            starsCursor.close();

            Cursor simCursor = myDB.rawQuery("select count(*) from stars_in_movies", null);
            simCursor.moveToFirst();
            int simCount = simCursor.getInt(0);
            simCursor.close();

            LinearLayout lView = new LinearLayout(this);

            TextView myText = new TextView(this);

            String dbcount = "Movies: " + movieCount + " Stars: " + starsCount + " SIM:" +simCount;
            myText.setText(dbcount);

            lView.addView(myText);

            setContentView(lView);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
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
}





