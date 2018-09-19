package ch.devtadel.luuser.DAL.local;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import ch.devtadel.luuser.DAL.local_database.CantonSqlTable;

public class CantonDao extends BaseDao{
    private static CantonDao instance;

    private CantonDao(Context context) {
        super(context);
    }

    public static synchronized CantonDao getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (instance == null) {
            instance = new CantonDao(context.getApplicationContext());
        }

        // If a db.close() was made before, we need to reopen the database
        if (!instance.db.isOpen()) {
            instance.open();
        }
        return instance;
    }

    /**
     * Funktion um Alle Orte azurückzugeben
     */
    public List<String> getCantons(){
        final List<String> returnList = new ArrayList<>();

        //Spalten die ausgegeben werden sollen
        String[] projection = {
                CantonSqlTable.COLUMN_CANTON_1,
                CantonSqlTable.COLUMN_CANTON_2,
        };

        //Query für die Abfrage erstellen
        Cursor cursor = db.query(CantonSqlTable.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            returnList.add(cursor.getString(0));
            returnList.add(cursor.getString(1));
            returnList.add(cursor.getString(2));
        }

        cursor.close();

        return returnList;
    }
}
