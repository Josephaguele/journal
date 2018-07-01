package chupaj.journal.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by AGUELE OSEKUEMEN JOE on 6/28/2018.
 */

public final class JournalContract {

    /**
     * The "Content Authority" is a name for the entire content provider similar to the
     * relationship between a domain name and its website. A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "chupaj.journal";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider


    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    /*Possible path (appended to base content URI for possible URI's)
    * For instance, content://chupaj.journal/journal/ is a valid path for
    * looking at journal data. content://chupaj.journal/staff/ will fail.
    * as the ContentProvider hasn't been given any information on what to do with staff
    * */

    public static final String PATH_STORIES = "stories";

    // To prevent someone from accidentally instantiating the contract class.
    // give it an empty constructor


    public JournalContract(){}


    public static final class StoriesEntry implements BaseColumns {

        // The content URI TO access the journal data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STORIES);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +"/" + CONTENT_AUTHORITY +"/" + PATH_STORIES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +CONTENT_AUTHORITY + "/" + PATH_STORIES;

        // Name of database table for journal notes
        public static final String TABLE_NAME = "notes";

        // Unique ID number for the journal ( only for use in the database table)
        // Type: INTEGER
        public static final String _ID = BaseColumns._ID;

        // name of the journal note
        // Type: TEXT
        public static final String COLUMN_JOURNAL_NAME = "title";

        // details of the journal
        // TYPE: TEXT
        public static final String COLUMN_JOURNAL_STORYLINE = "storyline";


        public static final String COLUMN_JOURNAL_CATEGORY = "category";
        // date of the journal
        // Type: String
        public static final String COLUMN_JOURNAL_DATE = "date";




    }
}
