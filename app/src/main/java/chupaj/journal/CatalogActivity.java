package chupaj.journal;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;


import chupaj.journal.data.JournalCursorAdapter;


import static chupaj.journal.data.JournalContract.StoriesEntry.COLUMN_JOURNAL_CATEGORY;
import static chupaj.journal.data.JournalContract.StoriesEntry.COLUMN_JOURNAL_DATE;
import static chupaj.journal.data.JournalContract.StoriesEntry.COLUMN_JOURNAL_NAME;
import static chupaj.journal.data.JournalContract.StoriesEntry.COLUMN_JOURNAL_STORYLINE;
import static chupaj.journal.data.JournalContract.StoriesEntry.CONTENT_URI;
import static chupaj.journal.data.JournalContract.StoriesEntry._ID;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the journal data loader
    private static final int JOURNAL_LOADER = 0;
    JournalCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        ImageButton fab = (ImageButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
              startActivity(intent);
            }
        });


        // FInd the listView which will be populated with the Journal data
        GridView journalListView = (GridView) findViewById(R.id.list);

        //Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        journalListView.setEmptyView(emptyView);

        //Set up an Adapter to create a list item for each row of JOURNAL data in the Cursor.
        // There is no journal data yet (until the loader finishes ) so pass in null for the Cursor.
        mCursorAdapter = new JournalCursorAdapter(this, null);

        journalListView.setAdapter(mCursorAdapter);


        // Setup the item click listener
        journalListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, JournalDetails.class);

                // Form the content URI that represents the specific JOURNAL that was clicked on,
                // by appending the "id" ( passed as input to this method) onto the
                // {@link StoriesEntry#CONTENT_URI}
                // For example, the URI would be "content://com.example.android.JOURNALs/JOURNALs/2"
                // if the JOURNAL with ID 2 was clicked on.
                Uri currentJournalUri = ContentUris.withAppendedId(CONTENT_URI, id);

                //Set the URI on the data field of the intent
                intent.setData(currentJournalUri);

                //Launch the {@link EditorActivity} to display the data for the current JOURNAL.
                startActivity(intent);
            }
        });

        //set up the item long click listener to listen open the editor activity
        journalListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // Create new intent to go to {@link EditorActivity}
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

                // Form the content URI that represents the specific JOURNAL that was clicked on,
                // by appending the "id" ( passed as input to this method) onto the
                // {@link StoriesEntry#CONTENT_URI}
                // For example, the URI would be "content://com.example.android.JOURNALs/JOURNALs/2"
                // if the JOURNAL with ID 2 was clicked on.
                Uri currentJournalUri = ContentUris.withAppendedId(CONTENT_URI, id);

                //Set the URI on the data field of the intent
                intent.setData(currentJournalUri);

                //Launch the {@link EditorActivity} to display the data for the current JOURNAL.
                startActivity(intent);

                return true;
            }
        });

        // Kick off the Loader
        getLoaderManager().initLoader(JOURNAL_LOADER, null, this);
    }
// Helper method to insert hardcoded JOURNAL data into the database. For debugging purposes only
        private void insertJournal() {

            ContentValues values = new ContentValues();
            values.put(COLUMN_JOURNAL_NAME, "Dubai tour");
            values.put(COLUMN_JOURNAL_CATEGORY, "Tours");
            values.put(COLUMN_JOURNAL_STORYLINE,"Normal");
            values.put(COLUMN_JOURNAL_DATE,"1st of July, 2018");


            // insert a new row for Toto into the provider using the ContentResolver
            // Use the {@link StoriesEntry#CONTENT_URI} to indicate that we want to insert
            // into the JOURNALs database table.
            // Receive the new content URI that will allow us to access Toto's data in the future.
            Uri newUri = getContentResolver().insert(CONTENT_URI, values);
        }

    // Helper method to delete all journals in the database
    private void deleteAllJournals(){
        int rowsDeleted = getContentResolver().delete(CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from journal database");
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertJournal();
                return true;

            case R.id.menu_search:
                onSearchRequested();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                _ID,
                COLUMN_JOURNAL_NAME,
                COLUMN_JOURNAL_CATEGORY,
                COLUMN_JOURNAL_STORYLINE,
                COLUMN_JOURNAL_DATE
        };


        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                CONTENT_URI,    // Provider content URI to query
                projection,     // Columns to include in the resulting Cursor
                null,           // No selection clause
                null,           // No selection arguments
                null);           // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link JournalCursorAdapter} with this new cursor containing updated journal data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }



}




