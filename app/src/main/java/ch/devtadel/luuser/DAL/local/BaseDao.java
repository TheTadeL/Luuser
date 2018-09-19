package ch.devtadel.luuser.DAL.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import ch.devtadel.luuser.DAL.local_database.CantonDatabaseHelper;

public abstract class BaseDao {
    private CantonDatabaseHelper dbHelper;
    protected SQLiteDatabase db;

    BaseDao(Context context){
        dbHelper = CantonDatabaseHelper.getInstance(context);
        open();
    }

    void open() throws SQLiteException {
        db = dbHelper.getWritableDatabase();
    }

//    public void close() {
//        dbHelper.close();
//    }
}