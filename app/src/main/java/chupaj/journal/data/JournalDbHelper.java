package chupaj.journal.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by AGUELE OSEKUEMEN JOE on 6/28/2018.
 */

public class JournalDbHelper extends SQLiteOpenHelper {

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    public static final int DATABASE_VERSION = 1;

    /**
     * Name of the database file
     */
    public static final String DATABASE_NAME = "journals.db";

    /**
     * Constructs a new instance of {@link JournalDbHelper}.
     *
     * @param context of the app
     */


    public JournalDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //This is called when the database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase database) {
        // Create a String that contains the SQL statement to create the JOURNALS table
        String SQL_CREATE_JOURNALS_TABLE = "CREATE TABLE " + JournalContract.StoriesEntry.TABLE_NAME + "("
                + JournalContract.StoriesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + JournalContract.StoriesEntry.COLUMN_JOURNAL_NAME + " TEXT NOT NULL, "
                + JournalContract.StoriesEntry.COLUMN_JOURNAL_CATEGORY + " TEXT, "
                + JournalContract.StoriesEntry.COLUMN_JOURNAL_STORYLINE + " TEXT, "
                + JournalContract.StoriesEntry.COLUMN_JOURNAL_DATE + " TEXT NOT NULL );";

        // Execute the SQL statement
        database.execSQL(SQL_CREATE_JOURNALS_TABLE);

    }

    // This is called when the database needs to be upgraded
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // The database is still at version 1, so there's nothing to do be done here.

    }
}
