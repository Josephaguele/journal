package chupaj.journal;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import static chupaj.journal.data.JournalContract.StoriesEntry.COLUMN_JOURNAL_CATEGORY;
import static chupaj.journal.data.JournalContract.StoriesEntry.COLUMN_JOURNAL_DATE;
import static chupaj.journal.data.JournalContract.StoriesEntry.COLUMN_JOURNAL_NAME;
import static chupaj.journal.data.JournalContract.StoriesEntry.COLUMN_JOURNAL_STORYLINE;
import static chupaj.journal.data.JournalContract.StoriesEntry._ID;

/**
 * Created by AGUELE OSEKUEMEN JOE on 6/29/2018.
 */

public class JournalDetails extends AppCompatActivity  implements  LoaderManager.LoaderCallbacks<Cursor>{

    private static final int EXISTING_JOURNAL_LOADER = 0;
    private TextView cName;
    private TextView cCategory;
    private TextView cDate;
    private TextView cStory;
    private Uri currentJournalUri;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        currentJournalUri = intent.getData();

        if (currentJournalUri == null) {
            // This is a new JOURNAL, so change
            Toast.makeText(getApplicationContext(), "No journal data here", Toast.LENGTH_LONG).show();

        } else {

            // launching intent for EditorActivity
            ImageButton sImageButton = (ImageButton) findViewById(R.id.img);
            sImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // the setData makes the EditorActivity to pick the data of the current journal so as to enable editing
                    Intent mIntent = new Intent(getApplicationContext(), EditorActivity.class).setData(currentJournalUri);
                    startActivity(mIntent);
                }
            });
            //Initialize a loader to read the journal data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_JOURNAL_LOADER, null, this);
        }


        cName = (TextView) findViewById(R.id.c_name);
        cCategory = (TextView) findViewById(R.id.c_category);
        cDate = (TextView) findViewById(R.id.c_date);
        cStory = (TextView) findViewById(R.id.c_story);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all journal attributes, define a projection that contains
        // all columns form the journal table

        String[] projection = {_ID,
                COLUMN_JOURNAL_NAME,
                COLUMN_JOURNAL_CATEGORY,
                COLUMN_JOURNAL_STORYLINE,
                COLUMN_JOURNAL_DATE
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this, // Parent activity context
                currentJournalUri,       // Query the content URI for the current journal
                projection,            // Columns to include in the resulting Cursor
                null,                   //No selection clause
                null,                   //No selection arguments
                null);                  //Default sort order
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        //Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            //Find the columns of journal attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(COLUMN_JOURNAL_NAME);

            int categoryColumnIndex = cursor.getColumnIndex(COLUMN_JOURNAL_CATEGORY);

            int dateColumnIndex = cursor.getColumnIndex(COLUMN_JOURNAL_DATE);

            int storyColumnIndex = cursor.getColumnIndex(COLUMN_JOURNAL_STORYLINE);


            // eXTRACT OUT THE VALUE FROM THE cURSOR for the given column index
            String name = cursor.getString(nameColumnIndex);

            String category = cursor.getString(categoryColumnIndex);

            String date = cursor.getString(dateColumnIndex);

            String story = cursor.getString(storyColumnIndex);


            //Update the views on the screen with the values from the database
            cName.setText(name);
            cCategory.setText(category);
            cDate.setText(date);
            cStory.setText(story);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        cName.setText("");

        cCategory.setText("");

        cDate.setText("");

        cStory.setText("");
    }


}
