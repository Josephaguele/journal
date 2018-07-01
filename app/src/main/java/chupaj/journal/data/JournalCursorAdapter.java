package chupaj.journal.data;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import chupaj.journal.R;


import static chupaj.journal.data.JournalContract.StoriesEntry.COLUMN_JOURNAL_CATEGORY;
import static chupaj.journal.data.JournalContract.StoriesEntry.COLUMN_JOURNAL_DATE;
import static chupaj.journal.data.JournalContract.StoriesEntry.COLUMN_JOURNAL_NAME;
import static chupaj.journal.data.JournalContract.StoriesEntry.COLUMN_JOURNAL_STORYLINE;

/**
 * Created by AGUELE OSEKUEMEN JOE on 6/29/2018.
 */

public class JournalCursorAdapter extends CursorAdapter {

    public JournalCursorAdapter(Context context, Cursor c){
        super(context,c, 0);
    }

    // Makes a new blank list item view. No data is set (or bound) to the views yet
    // @param context app context
    // @param cursor The cursor from which to get the data. The cursor is already
    //  moved to the correct position
    // @param parent The parent to which the new view is attached to
    // @return the newly created list item view
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // The bindView method is used to bind all data to a given view
        // such as setting the text on a TextView

        /* Find fields to populate in inflated template*/
        TextView nameTextView = (TextView) view.findViewById(R.id.title);
        TextView categoryTextView = (TextView) view.findViewById(R.id.category);
        TextView dateTextView = (TextView)view.findViewById(R.id.date);



        // Find the columns of JOURNAL attributes we are interested in
        int nameColumnIndex = cursor.getColumnIndex(COLUMN_JOURNAL_NAME);
        int categoryColumnIndex = cursor.getColumnIndex(COLUMN_JOURNAL_CATEGORY);
        int dateColumnIndex = cursor.getColumnIndex(COLUMN_JOURNAL_DATE);





        // Extract client attributes from the cursor for the current journal
        String journalName = cursor.getString(nameColumnIndex);
        String journalCategory = cursor.getString(categoryColumnIndex);
        String date = cursor.getString(dateColumnIndex);


        // Populate fields with extracted properties for the current journal
        nameTextView.setText(journalName);
        categoryTextView.setText(journalCategory);
        dateTextView.setText(date);


    }
}
