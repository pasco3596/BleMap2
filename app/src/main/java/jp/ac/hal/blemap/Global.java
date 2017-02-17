package jp.ac.hal.blemap;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pasuco on 2017/02/03.
 */

public class Global extends Application {
    List<MyBeacon> allbeacons;
    List<Item> items;
    public static final String URL = "http://pasuco234lab.xyz/user/getitem";

    public void init() {
        MyDatabaseHelper mh = new MyDatabaseHelper(this);
        SQLiteDatabase db = mh.getWritableDatabase();
        DAO dao = new DAO(db);
        allbeacons = dao.selectAll();
        db.close();
        items = new ArrayList<>();
        getJson();
    }

    public void getJson() {
        AsyncJsonLoader asyncJsonLoader = new AsyncJsonLoader(new AsyncJsonLoader.AsyncCallback() {
            @Override
            public void preExecute() {

            }

            @Override
            public void postExecute(JSONObject result) {
                if (result == null) {
                    return;
                }
                try {
                    JSONArray array = result.getJSONArray("items");
                    Log.e("Object", array.toString());
                    Log.e("Object", array.length() + "");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        Item item = new Item(object.getString("uuid"), object.getInt("major"), object.getInt("minor"),
                                object.getString("name"), object.getString("description"), object.getInt("x"), object.getInt("y"));
                        items.add(item);
                        Log.e("Item", item.getUuid());
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void progressUpdate(int progress) {

            }

            @Override
            public void cancel() {

            }


        });
        asyncJsonLoader.execute(URL);
    }

}
