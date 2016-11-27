package jp.ac.hal.blemap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pasuco on 2016/06/20.
 */
public class DAO {
    private SQLiteDatabase db;

    public DAO(SQLiteDatabase db){
        this.db=db;
    }

    public MyBeacon select(String UUID,int RSSI) {
        MyBeacon mbeacon = null;
        String sql = "select * from beacons where uuid = ?";
        Cursor c = db.rawQuery(sql,new String[]{UUID});
        boolean flg = c.moveToFirst();
        if(flg){
            int major = c.getInt(c.getColumnIndex("major"));
            int minor = c.getInt(c.getColumnIndex("minor"));
            int  x = c.getInt(c.getColumnIndex("x"));
            int  y = c.getInt(c.getColumnIndex("y"));
            mbeacon = new MyBeacon(UUID,RSSI,major,minor,x,y);

        }
        return mbeacon;
    }

    public List<MyBeacon> selectAll() {
        List<MyBeacon> list = new ArrayList<>();
        boolean flg = false;
        String sql = "select * from beacons";

        Cursor c = db.rawQuery(sql,null);

        if (null != c) {
            flg = c.moveToFirst();
        }
        while (flg) {
            String UUID = c.getString(c.getColumnIndex("uuid"));
            int major = c.getInt(c.getColumnIndex("major"));
            int minor = c.getInt(c.getColumnIndex("minor"));
            int  x = c.getInt(c.getColumnIndex("x"));
            int  y = c.getInt(c.getColumnIndex("y"));
            MyBeacon myBeacon = new MyBeacon(UUID, major, minor, x, y);
            list.add(myBeacon);
            flg = c.moveToNext();
        }


        return list;
    }

}
