package chupaj.journal;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import chupaj.journal.data.JournalContract;




import static android.R.attr.name;
import static chupaj.journal.data.JournalContract.StoriesEntry.COLUMN_JOURNAL_CATEGORY;
import static chupaj.journal.data.JournalContract.StoriesEntry.COLUMN_JOURNAL_DATE;
import static chupaj.journal.data.JournalContract.StoriesEntry.COLUMN_JOURNAL_NAME;
import static chupaj.journal.data.JournalContract.StoriesEntry.COLUMN_JOURNAL_STORYLINE;
import static chupaj.journal.data.JournalContract.StoriesEntry._ID;

/**
 * Created by AGUELE OSEKUEMEN JOE on 6/29/2018.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mNameEditText;
    private EditText mCategoryEditText;
    private EditText mStoryEditText;
    private EditText mDateEditText;
    private Uri mCurrentJournalUri;

    // Identifier for the journal data loader
    private static final int EXISTING_JOURNAL_LOADER = 0;

    // Boolean flag that keeps track of whether the journal has been edited (true) or not (false) */
    private boolean mJournalHasChanged = false;




    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying the view, and
     * we change the mJournalHasChanged boolean to true
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mJournalHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);



        // Examine the intent that was used to launch this activity
        // in order to figure otu if we're creating a new journal or editing an existing one
        Intent intent = getIntent();
        mCurrentJournalUri = intent.getData();

        // If the intent DOES NOT contain a journal content URI, then we know that we are
        // creating a new journal
        if (mCurrentJournalUri == null) {
            // This is a new journal, so change the app bar to say "Add a journal"
            setTitle("Add a journal");

            //Invalidate the options menu, so the "Delete menu option can be hidden.
            // It doesn't make sense to delete a journal that hasn't been created yet. }
            invalidateOptionsMenu();
        } else {
            // Otherwise this is an existing journal, so change app bar to say "Edit journal"
            setTitle("Edit journal");

            //Initialize a loader to read the journal data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_JOURNAL_LOADER, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_title);
        mCategoryEditText = (EditText) findViewById(R.id.edit_category);
        mDateEditText = (EditText) findViewById(R.id.edit_date);
        mStoryEditText = (EditText) findViewById(R.id.edit_story);


        mCategoryEditText.setOnTouchListener(mTouchListener);
        mNameEditText.setOnTouchListener(mTouchListener);
        mDateEditText.setOnTouchListener(mTouchListener);
        mStoryEditText.setOnTouchListener(mTouchListener);

    }


    private void share(){
        EditText categoryTextField = (EditText) findViewById(R.id.edit_category);
        Editable category = categoryTextField.getText();

        EditText dateTextField = (EditText) findViewById(R.id.edit_date);
        Editable date = dateTextField.getText();

        EditText titleTextField = (EditText) findViewById(R.id.edit_title);
        Editable title = titleTextField.getText();

        EditText storyTextField = (EditText) findViewById(R.id.edit_story);
        Editable story = storyTextField.getText();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "MY JOURNAL" +"\n"
                + "Title is: " + title + "\n"
                + "Category: " + category + "\n"
                + "Notes" + story + "\n\n"
                + "Date: " + date + "\n"
        );
        startActivity(shareIntent);
    }
    private void saveJournal() {
        //Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String categoryString = mCategoryEditText.getText().toString().trim();
        String dateString = mDateEditText.getText().toString().trim();
        String storyString = mStoryEditText.getText().toString().trim();

        if (mCurrentJournalUri == null
                && TextUtils.isEmpty(nameString)
                && TextUtils.isEmpty(dateString)
                && TextUtils.isEmpty(storyString)
                && TextUtils.isEmpty(categoryString)) {
            return;
        }

        //Create a ContentValues object where column names are the keys,
        // and journal attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(COLUMN_JOURNAL_NAME, nameString);
        values.put(COLUMN_JOURNAL_CATEGORY, categoryString);
        values.put(COLUMN_JOURNAL_DATE, dateString);
        values.put(COLUMN_JOURNAL_STORYLINE,storyString);


        // Determine if this is a new or existing journal by checking if mCurrentJournalUri is null or not
        if (mCurrentJournalUri == null) {
            //This is a NEW journal, so insert a new journal into the provider.
            // returning the content URI for the new journal.
            Uri newUri = getContentResolver().insert(JournalContract.StoriesEntry.CONTENT_URI, values);

            //Show a toast message depending on whether or not the insertion was successful
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Error with saving journal details ", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
               // Toast.makeText(this, "journal saved" + newUri, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "journal saved" , Toast.LENGTH_SHORT).show();

            }
        } else {
            // Otherwise this is an EXISTING journal, so update the journal with content URI: mCurrentjournalUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentjournalUri will already identify the correct row in the database that
            // we want to modify
            int rowsAffected = getContentResolver().update(mCurrentJournalUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful
            if (rowsAffected == 0) {
                // if no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Error with updating journal", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast
                Toast.makeText(this, "journal updated", Toast.LENGTH_SHORT).show();
            }
        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /*
    * This method is called after invalidateOptionsMenu(), so that the
    * menu can be updated ( some menu items can be hidden or made visible)
    * */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new journal, hide the "Delete" menu item.
        if (mCurrentJournalUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //Save journal to the database
                saveJournal();
                //Exit activity
                finish();
                return true;

            // RESPOND TO A CLICK ON THE "SHARE" MENU OPTION
            case R.id.action_share:
                share();
                return true;

            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                //If the journal hasn't changed, continue with navigating up to a parent activity
                // which is the {@link CatalogActivity}
                if (!mJournalHasChanged) {
                    // Navigate back to parent activity (CatalogActivity)
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                //Otherwise if there are unsaved changes, set up a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }



    // This method is called when the back button is pressed.
    @Override
    public void onBackPressed() {
        // If the journal hasn't changed, continue with handling bakc button press
        if (!mJournalHasChanged) {
            super.onBackPressed();
            return;
        }

        //Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
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
                mCurrentJournalUri,       // Query the content URI for the current journal
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

            String date= cursor.getString(dateColumnIndex);

            String story = cursor.getString(storyColumnIndex);


            //Update the views on the screen with the values from the database
            mNameEditText.setText(name);
            mCategoryEditText.setText(category);
            mStoryEditText.setText(story);
            mDateEditText.setText(date);

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");

        mCategoryEditText.setText("");

        mStoryEditText.setText("");
        mDateEditText.setText("");
    }


    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when the user
     *                                   confirms they want to discard their changes
     */
    private void showUnsavedChangesDialog
    (DialogInterface.OnClickListener discardButtonClickListener) {
        //Create an ALertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // USER clicked the "Keep Editing" button, so dismiss this dialog
                // and continue editing the journal.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /*
    * Prompt the user to confirm that they want to delete this journal
    * */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this journal?");
        builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete button, so delete the journal.
                deleteJournal();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the journal.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the ALertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


      /*
    * Perform the deletion of the journal in the database
    * */


    private void deleteJournal() {
        // Only perform the delete if this is an existing journal.
        if (mCurrentJournalUri != null) {
            // Call the ContentResolver to delete the journal at the given content URI
            // Pass in null for the selection adn selection args because the mCurrentjournalUri
            // content URI already identifies the journal that we want
            int rowsDeleted = getContentResolver().delete(mCurrentJournalUri, null, null);

            // Show a toast message depending on whether or not the delete was successful
            if (rowsDeleted == 0) {
                // if no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, "Error with deleting journal", Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, "journal deleted", Toast.LENGTH_SHORT).show();
            }
        }

        // CLose the activity
        finish();
    }




}
