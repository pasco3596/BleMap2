package jp.ac.hal.blemap;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by pasuco on 2016/06/03.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "beacons";

    public MyDatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    public void onCreate(SQLiteDatabase db) {
        try {
            String sql = "create table  beacons (uuid text, major integer NOT NULL, " +
                    "minor integer  NOT NULL, x integer NOT NULL, y integer NOT NULL, " +
                    "primary key(uuid, major, minor))";
            db.execSQL(sql);
            sql = "insert into beacons values ('b0fc4601-14a6-43a1-abcd-cb9cfddb4013', 3, 13153, -4, -4)";//1
            db.execSQL(sql);
            sql = "insert into beacons values ('b0fc4601-14a6-43a1-abcd-cb9cfddb4013', 3, 13154, -4, 0)";//1
            db.execSQL(sql);
            sql = "insert into beacons values ('b0fc4601-14a6-43a1-abcd-cb9cfddb4013', 3, 13156, 0, 0)";//1
            db.execSQL(sql);
            sql = "insert into beacons values ('b0fc4601-14a6-43a1-abcd-cb9cfddb4013', 3, 13157, 4, 0)";//2
            db.execSQL(sql);
            sql = "insert into beacons values ('b0fc4601-14a6-43a1-abcd-cb9cfddb4013', 3, 13159, 0, -4)";//2
            db.execSQL(sql);
            sql = "insert into beacons values ('b0fc4601-14a6-43a1-abcd-cb9cfddb4013', 3, 13160, 0, 4)";//3
            db.execSQL(sql);
            sql = "insert into beacons values ('b0fc4601-14a6-43a1-abcd-cb9cfddb4013', 3, 13161, -4, 4)";//4
            db.execSQL(sql);
            sql = "insert into beacons values ('b0fc4601-14a6-43a1-abcd-cb9cfddb4013', 3, 13162, 4, 4)";//4
            db.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            String sql = "drop table beacons";
            db.execSQL(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        onCreate(db);
    }
}