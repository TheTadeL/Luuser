package ch.devtadel.luuser.DAL.local_database;

public class CantonSqlTable extends BaseSqlTable{
    public static final String TABLE_NAME = "Places";
    public static final String COLUMN_CANTON_1 = "canton 1";
    public static final String COLUMN_CANTON_2 = "canton 2";

    static String getSqlQueryForCreateTable()
    {
        return "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + INTEGER_TYPE + PK_CONSTRAINT + COMMA_SEP +
                COLUMN_CANTON_1 + TEXT_TYPE + COMMA_SEP +
                COLUMN_CANTON_2 + TEXT_TYPE + " )";
    }

    static String getSqlQueryForDropTable()
    {
        return "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
