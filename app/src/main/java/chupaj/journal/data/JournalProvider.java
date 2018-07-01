package chupaj.journal.data;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.HashMap;

import static android.R.attr.rating;
import static android.app.SearchManager.SUGGEST_URI_PATH_QUERY;
import static chupaj.journal.data.JournalContract.StoriesEntry;
import static chupaj.journal.data.JournalContract.CONTENT_AUTHORITY;
import static chupaj.journal.data.JournalContract.StoriesEntry.COLUMN_JOURNAL_NAME;

/**
 * Created by AGUELE OSEKUEMEN JOE on 6/28/2018.
 * Content Provider for the JOURNAL APP
 */

public class JournalProvider extends ContentProvider {

    //Tag for the log messages
    public static final String LOG_TAG = JournalProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the journal table
     */
    private static final int JOURNALS = 100;

    /**
     * URI matcher code for the content URI for a single journal in the journals table
     */
    private static final int JOURNAL_ID = 101;


    private static final int SEARCH = 3;

    public static final String KEY_SEARCH_COLUMN =COLUMN_JOURNAL_NAME;
    private static final HashMap<String, String> SEARCH_SUGGEST_PROJECTION_MAP;
    static {
        SEARCH_SUGGEST_PROJECTION_MAP = new HashMap<String, String>();
        SEARCH_SUGGEST_PROJECTION_MAP.put(JournalContract.StoriesEntry._ID, JournalContract.StoriesEntry._ID);
        SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_1, KEY_SEARCH_COLUMN + " AS " + SearchManager.SUGGEST_COLUMN_TEXT_1);
        SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_TEXT_2, JournalContract.StoriesEntry.COLUMN_JOURNAL_STORYLINE+ " AS " + SearchManager.SUGGEST_COLUMN_TEXT_2);
        SEARCH_SUGGEST_PROJECTION_MAP.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, JournalContract.StoriesEntry._ID + " AS " + SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
    }

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://chupaj.journal/journals" will map to the
        // integer code {@link #JOURNALS}. This URI is used to provide access to MULTIPLE rows
        // of the journals table.
        sUriMatcher.addURI(CONTENT_AUTHORITY, JournalContract.PATH_STORIES, JOURNALS);


        sUriMatcher.addURI(CONTENT_AUTHORITY, JournalContract.PATH_STORIES + "/#", JOURNAL_ID);
    }



    public static final Uri SEARCH_SUGGEST_URI = Uri.parse("content://" + JournalContract.CONTENT_AUTHORITY + "/" + JOURNAL_ID);
    public JournalProvider(){
        // For searching
        sUriMatcher.addURI(CONTENT_AUTHORITY,SUGGEST_URI_PATH_QUERY,SEARCH);
        sUriMatcher.addURI(CONTENT_AUTHORITY,SUGGEST_URI_PATH_QUERY +"/*", SEARCH);
        // sUriMatcher.addURI(CONTENT_AUTHORITY,SUGGEST_URI_PATH_SHORTCUT,SEARCH);
        // sUriMatcher.addURI(CONTENT_AUTHORITY,SUGGEST_URI_PATH_SHORTCUT+"/*", SEARCH);
    }

    //Database helper object
    private JournalDbHelper mDbHelper;


    @Override
    public boolean onCreate() {
        mDbHelper = new JournalDbHelper(getContext());
        return true;
    }


    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor =null;

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(JournalContract.StoriesEntry.TABLE_NAME);

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case SEARCH:
                selectionArgs = new String[]{ "%" + selectionArgs[0] + "%", "%" + selectionArgs[0] +"%"};
                queryBuilder.setProjectionMap(SEARCH_SUGGEST_PROJECTION_MAP);
                cursor = queryBuilder.query(database,projection,selection,selectionArgs,null,null,sortOrder);
                break;

            case JOURNALS:
                // For the journals code, query the journals table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the journals table.
                cursor = database.query(StoriesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null, null, sortOrder);
                break;
            case JOURNAL_ID:
                // For the JOURNALS_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.fashionhome/journals/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = StoriesEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the journals table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(StoriesEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }



    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case JOURNALS:
                return insertJournal(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a Journal into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertJournal(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(COLUMN_JOURNAL_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Journal requires a title");
        }


        // Check that the address is not null
        String category = values.getAsString(StoriesEntry.COLUMN_JOURNAL_CATEGORY);
        if (category == null) {
            throw new IllegalArgumentException("Please give a category");
        }
        // Check that the story is valid
        String story = values.getAsString(StoriesEntry.COLUMN_JOURNAL_STORYLINE);
            if(story == null){
                throw new IllegalArgumentException("Please give a detail or story line");
        }

        // If the number is provided, check that it's greater than or equal to 0 kg
        String number = values.getAsString(StoriesEntry.COLUMN_JOURNAL_DATE);
        if (number == null) {
            throw new IllegalArgumentException("Journal requires a date");
        }
            // No need to check the date, any value is valid (including null).

            // Get writeable database
            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            // Insert the new Journal with the given values
            long id = database.insert(StoriesEntry.TABLE_NAME, null, values);
            // If the ID is -1, then the insertion failed. Log an error and return null.
            if (id == -1) {
                Log.e(LOG_TAG, "Failed to insert row for " + uri);
                return null;
            }

            // Notify all listeners that the data has changed for the Journal content URI
            getContext().getContentResolver().notifyChange(uri, null);

            // Return the new URI with the ID (of the newly inserted row) appended at the end
            return ContentUris.withAppendedId(uri, id);
        }

        @Override
        public int update(Uri uri, ContentValues contentValues, String selection,
                String[] selectionArgs) {
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case JOURNALS:
                    return updateJournal(uri, contentValues, selection, selectionArgs);
                case JOURNAL_ID:
                    // For the JOURNALS_ID code, extract out the ID from the URI,
                    // so we know which row to update. Selection will be "_id=?" and selection
                    // arguments will be a String array containing the actual ID.
                    selection = StoriesEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    return updateJournal(uri, contentValues, selection, selectionArgs);
                default:
                    throw new IllegalArgumentException("Update is not supported for " + uri);
            }
        }


        /**
         * Update journals in the database with the given content values. Apply the changes to the rows
         * specified in the selection and selection arguments (which could be 0 or 1 or more journals).
         * Return the number of rows that were successfully updated.
         */
        private int updateJournal(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
            // If the {@link StoriesEntry#COLUMN_Journal_NAME} key is present,
            // check that the name value is not null.
            if (values.containsKey(COLUMN_JOURNAL_NAME)){
                String name = values.getAsString(COLUMN_JOURNAL_NAME);
                if (name == null) {
                    throw new IllegalArgumentException("Journal requires a title");
                }
            }

            // check that the address is not  null
            if (values.containsKey(StoriesEntry.COLUMN_JOURNAL_CATEGORY)) {
                String address = values.getAsString(StoriesEntry.COLUMN_JOURNAL_CATEGORY);
                if (address == null) {
                    throw new IllegalArgumentException("Category is important");
                }
            }


            if (values.containsKey(StoriesEntry.COLUMN_JOURNAL_STORYLINE)) {
                String name = values.getAsString(StoriesEntry.COLUMN_JOURNAL_STORYLINE);
                if (name == null) {
                    throw new IllegalArgumentException("Journal requires a story");
                }
            }
            // If the {@link StoriesEntry#COLUMN_Journal_NUMBER} key is present,
            // check that the number value is valid.
            if (values.containsKey(StoriesEntry.COLUMN_JOURNAL_DATE)) {
                // Check that the number is greater than or equal to 0 kg
                String number = values.getAsString(StoriesEntry.COLUMN_JOURNAL_DATE);
                if (number == null) {
                    throw new IllegalArgumentException("Journal requires a valid date");
                }
            }

            // No need to check the date, any value is valid (including null).

            // If there are no values to update, then don't try to update the database
            if (values.size() == 0) {
                return 0;
            }

            // Otherwise, get writeable database to update the data
            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            // Perform the update on the database and get the number of rows affected
            int rowsUpdated = database.update(StoriesEntry.TABLE_NAME, values, selection, selectionArgs);

            // If 1 or more rows were updated, then notify all listeners that the data at the
            // given URI has changed
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

            // Return the number of rows updated
            return rowsUpdated;
        }

        @Override
        public int delete(Uri uri, String selection, String[] selectionArgs) {
            // Get writeable database
            SQLiteDatabase database = mDbHelper.getWritableDatabase();

            // Track the number of rows that were deleted
            int rowsDeleted;

            final int match = sUriMatcher.match(uri);
            switch (match) {
                case JOURNALS:
                    // Delete all rows that match the selection and selection args
                    rowsDeleted = database.delete(StoriesEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                case JOURNAL_ID:
                    // Delete a single row given by the ID in the URI
                    selection = StoriesEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    rowsDeleted = database.delete(StoriesEntry.TABLE_NAME, selection, selectionArgs);
                    break;
                default:
                    throw new IllegalArgumentException("Deletion is not supported for " + uri);
            }

            // If 1 or more rows were deleted, then notify all listeners that the data at the
            // given URI has changed
            if (rowsDeleted != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

            // Return the number of rows deleted
            return rowsDeleted;
        }

        @Override
        public String getType(Uri uri) {
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case JOURNALS:
                    return StoriesEntry.CONTENT_LIST_TYPE;
                case JOURNAL_ID:
                    return StoriesEntry.CONTENT_ITEM_TYPE;
                case SEARCH:
                    return null;
                default:
                    throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
            }
        }

    }
