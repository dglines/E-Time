package edu.tacoma.uw.css.group7.e_time.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import edu.tacoma.uw.css.group7.e_time.R;
import edu.tacoma.uw.css.group7.e_time.video.Video;

public class RecentDB {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "Recent.db";
    private static final String RECENT_TABLE = "Recent";

    private RecentDBHelper mRecentDBHelper;
    private SQLiteDatabase mSQLiteDatabase;

    public RecentDB(Context context) {
        mRecentDBHelper = new RecentDBHelper(
                context, DB_NAME, null, DB_VERSION);
        mSQLiteDatabase = mRecentDBHelper.getWritableDatabase();
    }

    /**
     * Inserts the recent timer into the local sqlite table. Returns true if successful, false otherwise.
     * @param vidId
     * @param title
     * @param duration
     * @param currentTime
     * @return true or false
     */
    public boolean insertRecent(String vidId, String title, String duration, String currentTime) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("vidId", vidId);
        contentValues.put("title", title);
        contentValues.put("duration", duration);
        contentValues.put("currentTime", currentTime);

        long rowId = mSQLiteDatabase.insert("Recent", null, contentValues);
        return rowId != -1;
    }

    /**
     * Delete all the data from the RECENT_TABLE
     */
    public void deleteRecents() {
        if (mSQLiteDatabase == null) return;
        mSQLiteDatabase.delete(RECENT_TABLE, null, null);
    }

    /**
     * Returns the list of courses from the local Course table.
     * @return list
     */
    public List<Video> getRecents() {

        String[] columns = {
                "vidId", "title", "duration", "currentTime"
        };

        Cursor c = mSQLiteDatabase.query(
                RECENT_TABLE,  // The table to query
                columns,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        c.moveToFirst();
        List<Video> list = new ArrayList<Video>();
        for (int i=0; i<c.getCount(); i++) {
            String id = c.getString(0);
            String title = c.getString(1);
            String duration = c.getString(2);
            String currentTime = c.getString(3);
            String searchTerm = "";
            Video video = new Video(id, title, duration, currentTime, searchTerm);
            list.add(video);
            c.moveToNext();
        }

        return list;
    }

    /**
     * Helper class for RecentDB
     */
    class RecentDBHelper extends SQLiteOpenHelper {
        private final String CREATE_RECENT_SQL;
        private final String DROP_RECENT_SQL;

        public RecentDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);

            CREATE_RECENT_SQL = context.getString(R.string.CREATE_RECENT_SQL);
            DROP_RECENT_SQL = context.getString(R.string.DROP_RECENT_SQL);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_RECENT_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(DROP_RECENT_SQL);
            onCreate(sqLiteDatabase);
        }

    }
}
