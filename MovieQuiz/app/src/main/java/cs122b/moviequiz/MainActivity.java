package cs122b.moviequiz;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteDatabase myDB= null;

        try {
            myDB = this.openOrCreateDatabase("mydb", MODE_PRIVATE, null);

            /* Create a Table in the Database. */
            myDB.execSQL("CREATE TABLE IF NOT EXISTS "
                    + "movies"
                    + " ( id INTEGER PRIMARY KEY AUTOINCREMENT, title VARCHAR, year INTEGER, director VARCHAR);");

            /* Insert data to a Table*/
            myDB.execSQL("INSERT INTO "
                    + "movies"
                    + " (title, year, director)"
                    + " VALUES ('Moonrise Kingdom', 2012, 'Wes Anderson');");

            /*retrieve data from database */
            Cursor c = myDB.rawQuery("SELECT * FROM movies", null);

            int Column1 = c.getColumnIndex("title");
            int Column2 = c.getColumnIndex("year");
            int Column3 = c.getColumnIndex("director");

            // Check if our result was valid.
            c.moveToFirst();
            if (c != null) {
                // Loop through all Results
                do {
                    String title = c.getString(Column1);
                    int year = c.getInt(Column2);
                    String director = c.getString(Column3);
                    String data = title + "/" + year + "/" + director + "\n";

                    LinearLayout lView = new LinearLayout(this);

                    TextView myText = new TextView(this);
                    myText.setText(data);

                    lView.addView(myText);

                    setContentView(lView);

                } while (c.moveToNext());
            }
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
}




