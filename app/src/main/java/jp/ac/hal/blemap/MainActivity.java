package jp.ac.hal.blemap;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import android.widget.ArrayAdapter;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    // iBeaconのデータを認識するためのParserフォーマット
    public static final String IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    public static final String URL = "http://pasuco234lab.xyz/user/getitem";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int BLUETOOTH_ENABLE = 1;

    private BeaconManager beaconManager;
    private BluetoothAdapter bluetoothAdapter;

    List<MyBeacon> allBeacons;
    List<MyBeacon> beaconList;

    Handler handler = new Handler();
    List<Position> positions;
    ArrayAdapter<MyBeacon> arrayAdapter;
    MyMapView mmv;
    int finish = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        mmv = (MyMapView) findViewById(R.id.mapview);
        allBeacons = new ArrayList<>();
        getJson();
        MyDatabaseHelper mh = new MyDatabaseHelper(this);
        SQLiteDatabase db = mh.getWritableDatabase();
        DAO dao = new DAO(db);
        allBeacons = dao.selectAll();
        db.close();

//        lv = (ListView) findViewById(R.id.listView);
//
//        lv.setOnItemClickListener((parent, view, position, id) -> {
//            ListView listView = (ListView) parent;
//            MyBeacon myBeacon = (MyBeacon) listView.getItemAtPosition(position);
//            String targetUUID = myBeacon.getUUID();
//            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
//            intent.putExtra("uuid", targetUUID);
//            startActivity(intent);
//        });

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_FORMAT));
    }

    @Override
    public void onBeaconServiceConnect() {
        new Thread(() -> {
            beaconManager.addRangeNotifier((beacons, region) -> {
                beaconList = new ArrayList<>();
                for (Beacon beacon : beacons) {
                    Log.d("MyActivity", "UUID:" + beacon.getId1().toString() + ", major:" + beacon.getId2() +
                            ", minor:" + beacon.getId3() + ", Distance:"
                            + beacon.getDistance() + ", RSSI:" + beacon.getRssi()
                            + "txpower:" + beacon.getTxPower());

                    MyBeacon myBeacon = null;
                    for (int i = 0; i < allBeacons.size(); i++) {
                        MyBeacon mb = allBeacons.get(i);
                        if (mb.getUUID().equals(beacon.getId1().toString())) {
                            int x = mb.getX();
                            int y = mb.getY();
                            myBeacon = new MyBeacon(beacon, x, y);
                            beaconList.add(myBeacon);
                        }
                    }

                }
                handler.post(() -> {
                    if (2 <= beaconList.size()) {
                        //RSSIの強い順にソート

                        Collections.sort(beaconList, new MyBeaconSort());

                        getPosition(beaconList);
                    }
                    arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, beaconList);
                });
            });
            try {
                beaconManager.startRangingBeaconsInRegion(new Region("unique-ranging-region-id", null, null, null));
                Log.e("START BEACON", "START BEACON");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ユーザーの現在地点
    public void getPosition(List<MyBeacon> list) {

        positions = new ArrayList<>();
        for (int i = 0; i < list.size() - 2; i++) {
            MyBeacon myBeacon1 = list.get(i);
            Log.e("start", "1");
            double x1 = myBeacon1.getX();
            double y1 = myBeacon1.getY();
            for (int j = i + 1; j < list.size() - 1; j++) {

                MyBeacon myBeacon2 = list.get(j);
                double x2 = myBeacon2.getX();
                double y2 = myBeacon2.getY();

                Log.e("start", "2");
                for (int k = i + 2; k < list.size(); k++) {
                    Log.e("start", "3");
                    MyBeacon myBeacon3 = list.get(k);
                    double x3 = myBeacon3.getX();
                    double y3 = myBeacon3.getY();

                    double r1 = myBeacon1.getDistance();
                    double r2 = myBeacon2.getDistance();
                    double r3 = myBeacon3.getDistance();

                    double rad = Math.atan2((y2 - y1), (x2 - x1));

                    double xb2 = x2 - x1;
                    double yb2 = y2 - y1;

                    double xc2 = x3 - x1;
                    double yc2 = y3 - x1;

                    double xx = (xb2 * Math.cos(rad)) + (yb2 * Math.sin(rad));
                    double xx2 = (xc2 * Math.cos(rad)) + (yc2 * Math.sin(rad));
                    double yy2 = (yc2 * Math.cos(rad)) - (xc2 * Math.sin(rad));
                    double rra = r1 * r1;
                    double rrb = r2 * r2;
                    double rrc = r3 * r3;
                    double xxxx = xx * xx;
                    double xxxx2 = xx2 * xx2;
                    double yyyy2 = yy2 * yy2;

                    double x = (rra - rrb + xxxx) / (2 * xx);
                    double y = (rra - rrc + xxxx2 + yyyy2) / (2 * yy2) - ((xx2 / yy2) * x);

                    double targetX = (y * Math.sin(rad)) + (x * Math.cos(rad));
                    double targetY = (x * Math.sin(rad)) + (y * Math.cos(rad));

                    targetX += x1;
                    targetY += y1;

                    Log.e("beacon::", String.valueOf(r1));
                    Log.e("beacon::", String.valueOf(r2));
                    Log.e("beacinDis::", String.valueOf(r3));

                    if (!Double.isNaN(targetX) && !Double.isNaN(targetY) && !Double.isInfinite(targetX) && !Double.isInfinite(targetY)) {
                        positions.add(new Position(targetX, targetY));
                    }
                }
            }
        }
        if (0 < positions.size()) {
            double dx = 0;
            double dy = 0;
            for (Position position : positions) {
                dx += position.getPositionX();
                dy += position.getPositionY();
            }
            float ax = (float) (dx / positions.size());
            float by = (float) (dy / positions.size());
            Log.e("PositonSize",positions.size()+"");
            mmv.setMapPositon(ax, by);
            Log.e("targetX::", String.valueOf(ax));
            Log.e("targetY::", String.valueOf(by));

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bluetoothOn();
        beaconManager.bind(this);
    }

    @Override
    protected void onPause() {
        beaconManager.unbind(this);
        super.onPause();
    }

    // 位置情報の許可をmarshmallow以降は取らなきゃいけない
    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_COARSE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "許可したで", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "許可せんとできんで", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //bluetoothをONにする
    public void bluetoothOn() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent btOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(btOn, BLUETOOTH_ENABLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            if (requestCode == BLUETOOTH_ENABLE) {
                Toast.makeText(MainActivity.this, "Bluetooth ON", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Bluetooth failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            finish++;
            if (finish == 2) {
                finish = 0;
                Toast.makeText(MainActivity.this, "Bluetooth ONじゃないと使えないよ", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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
                    Log.e("Object",array.toString());
                    Log.e("Object",array.length()+"");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        MyBeacon myBeacon3 = new MyBeacon(object.getString("uuid"), object.getInt("major"), object.getInt("minor"),
                                object.getString("name"), object.getString("description"), object.getInt("x"), object.getInt("y"));

                        Log.e("Beacon",myBeacon3.getUUID());
                    }
                    Log.e("aaasasasafafsdvcsd", allBeacons.size() + "");

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
