package jp.ac.hal.blemap;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    // iBeaconのデータを認識するためのParserフォーマット
    public static final String IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int BLUETOOTH_ENABLE = 1;
    private static final int DETAIL_CODE = 2;

    private BeaconManager beaconManager;
    private BluetoothAdapter bluetoothAdapter;

    private GestureDetector gestureDetector;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;

    static List<Item> items;
    static List<MyBeacon> allBeacons;
    List<MyBeacon> beaconList;

    Handler handler = new Handler();
    List<Position> positions;
    ArrayAdapter<MyBeacon> arrayAdapter;
    MyMapView mmv;
    int finish = 0;
    Identifier identifier = Identifier.parse("b0fc4601-14a6-43a1-abcd-cb9cfddb4013");
    Global global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();
        mmv = (MyMapView) findViewById(R.id.mapview);
        allBeacons = new ArrayList<>();
        global = (Global) getApplication();
        global.init();
        allBeacons = global.allbeacons;
        items = global.items;


        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                createDialog();
                dialog.show();

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }
        });

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
                        if (mb.getMinor() == beacon.getId3().toInt()) {
                            int x = mb.getX();
                            int y = mb.getY();
                            myBeacon = new MyBeacon(beacon, x, y);
                            beaconList.add(myBeacon);
                        }
                    }

                }
                handler.post(() -> {

                    if (2 < beaconList.size()) {
                        Collections.sort(beaconList, new MyBeaconSort());
                        getPosition(beaconList);
                    }
                });
            });
            try {
                beaconManager.startRangingBeaconsInRegion(new Region("unique-ranging-region-id", identifier, null, null));
                Log.e("START BEACON", "START BEACON");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // ユーザーの現在地点
    public void getPosition(List<MyBeacon> list) {
        setPositions2(list);
        positions = new ArrayList<>();
        for (int i = 0; i < list.size() - 2; i++) {
            MyBeacon myBeacon1 = list.get(i);
            double x1 = myBeacon1.getX();
            double y1 = myBeacon1.getY();
            for (int j = i + 1; j < list.size() - 1; j++) {

                MyBeacon myBeacon2 = list.get(j);
                double x2 = myBeacon2.getX();
                double y2 = myBeacon2.getY();

                for (int k = i + 2; k < list.size(); k++) {

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

                    double targetX = (y * Math.sin(rad)) - (x * Math.cos(rad));
                    double targetY = (x * Math.sin(rad)) + (y * Math.cos(rad));

                    targetX += x1;
                    targetY += y1;

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
            mmv.setMapPosition(ax, by);

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

        switch (requestCode) {
            case BLUETOOTH_ENABLE:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(MainActivity.this, "Bluetooth ON", Toast.LENGTH_SHORT).show();
                } else {
                    finish++;
                    if (finish == 2) {
                        finish = 0;
                        Toast.makeText(MainActivity.this, "Bluetooth ONじゃないと使えないよ", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
                break;
            case DETAIL_CODE:
                if (resultCode == RESULT_OK) {
                }
                dialog.cancel();
                break;
        }


    }

    public void createDialog() {
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final View dialogView = inflater.inflate(R.layout.dialog, (ViewGroup) findViewById(R.id.layout_root));

        builder = new AlertDialog.Builder(this);

        builder.setTitle("展示物一覧");
        builder.setView(dialogView);
        ListView lv = (ListView) dialogView.findViewById(R.id.listview);
        lv.setOnItemClickListener((parent, view, position, id) -> {
            ListView listView = (ListView) parent;
            Item item = (Item) listView.getItemAtPosition(position);
            String name = item.getName();
            Intent intent = new Intent(MainActivity.this, ItemDetailActivity.class);
            intent.putExtra("name", name);
            startActivityForResult(intent, DETAIL_CODE);
        });

        ArrayAdapter<Item> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, global.items);

        lv.setAdapter(arrayAdapter);

        dialog = builder.create();


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
//        return super.onTouchEvent(event);
    }

    public void setPositions2(List<MyBeacon> list) {
        double a = 0;
        double b = 0;
        double c = 0;
        double d = 0;
        for (int i = 0; i < list.size(); i++) {
            MyBeacon myBeacon = list.get(i);
            double w = 1 / Math.pow(10, myBeacon.getRSSI());
            double x = myBeacon.getX();
            a += x / w;
            double y = myBeacon.getY();
            c += y / w;

            b += 1 / w;
            d = b;
        }
        double xx = a / b;
        double yy = c / d;
        Log.e("xxyyxxyy", "XX:" + xx + "YY:" + yy);
    }
}
