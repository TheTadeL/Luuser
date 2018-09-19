package ch.devtadel.luuser.DAL.local_database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CantonDatabaseHelper extends SQLiteOpenHelper {
    private static CantonDatabaseHelper instance;

    // Database Info
    private static final String DATABASE_NAME = "PlacesDB";
    private static final int DATABASE_VERSION = 1;

    private CantonDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized CantonDatabaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (instance == null) {
            instance = new CantonDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CantonSqlTable.getSqlQueryForCreateTable());
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLException {
        db.execSQL(CantonSqlTable.getSqlQueryForDropTable());

        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) throws SQLException {
        onUpgrade(db, oldVersion, newVersion);
    }
}
