package jp.ac.hal.blemap;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
            String sql = "create table  beacons (uuid text primary key , major integer NOT NULL" +
                    " , minor integer  NOT NULL , x integer NOT NULL, y integer NOT NULL)";
            db.execSQL(sql);
            sql = "insert into beacons values ('05f62a3d-f60f-44bc-b36e-2b80fd6c9679',1 , 1, 10, 10)";
            db.execSQL(sql);
            sql = "insert into beacons values ('00000001-1114-2345-6789-123456789121', 1, 1, 11, 10)";
            db.execSQL(sql);
            sql = "insert into beacons values ('00000000-0000-0000-0000-000000000002', 65535, 65535, 10, 11)";
            db.execSQL(sql);
            sql = "insert into beacons values ('00000000-0000-0000-0000-000000000001', 65535, 65535, 11, 11)";
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