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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private BeaconManager beaconManager;
    private static final String UUID = "00000001-1114-2345-6789-123456789121";
    // iBeaconのデータを認識するためのParserフォーマット
    public static final String IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int BLUETOOTH_ENABLE = 1;

    private BluetoothAdapter bluetoothAdapter;

    ListView lv;
    List<MyBeacon> allList;
    List<MyBeacon> alist;
    TextView tv;
    Button bt;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        tv =(TextView)findViewById(R.id.textView);

        bt = (Button)findViewById(R.id.button);
        bt.setOnClickListener(view -> tv.setText("あああああ"));

        MyDatabaseHelper mh = new MyDatabaseHelper(this);
        SQLiteDatabase db = mh.getWritableDatabase();
        DAO dao = new DAO(db);
        allList  = dao.selectAll();
        db.close();
        lv = (ListView) findViewById(R.id.listView);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_FORMAT));
    }

    @Override
    public void onBeaconServiceConnect() {
        new Thread(() -> {
            beaconManager.addRangeNotifier((beacons,region) -> {

                //beacons.stream().filter(s -> s.getId1().toString() == UUID ).forEach(s -> {
                alist = new ArrayList<>();
                Log.d("beaconsbeacons",String.valueOf(beacons.size()));

                for (Beacon s : beacons) {
                    Log.d("MyActivity", "UUID:" + s.getId1().toString() + ", major:" + s.getId2() +
                    ", minor:" + s.getId3() + ", Distance:" + s.getDistance() + ", RSSI:" + s.getRssi());
                    //
                    //                      MyBeacon b = allList.stream().filter(m -> m.getMajor().equals(s.getId2().toString())
                    //                              && m.getMinor().equals(s.getId3())).findFirst().orElse(null);
                    MyBeacon b;
                    int i = 0;
                    while (true) {
                        MyBeacon myBeacon = allList.get(i);
                        String major = myBeacon.getMajor();
                        String minor = myBeacon.getMinor();
                        if (major.equals(s.getId2().toString()) && minor.equals(s.getId3().toString())) {
                            b = myBeacon;
                            break;
                        }
                     i++;
                    }

                    b.setDistance(s.getDistance());
                    b.setRSSI(s.getRssi());
                    alist.add(b);
                    getPosition(alist);
                }
       //         });


                handler.post(() -> {
                        ArrayAdapter<MyBeacon> arrayAdapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,alist);
                        lv.setAdapter(arrayAdapter);
                });
            });
            try {
                beaconManager.startRangingBeaconsInRegion(new Region("unique-ranging-region-id", null, null, null));
                Log.e("START BEACON","START BEACON");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }).start();

    }

    public void getPosition(List<MyBeacon> list) {

        for (int i = 0; i < list.size()-1; i++) {
            MyBeacon myBeacon1 = list.get(i);
            MyBeacon myBeacon2 = list.get(i+1);

            double x1 = myBeacon1.getX();
            double y1 = myBeacon1.getY();
            double x2 = myBeacon2.getX();
            double y2 = myBeacon2.getY();

            double R1 = myBeacon1.getDistance();
            double R2 = myBeacon2.getDistance();

            double L =  Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
            double θ = Math.atan2((y2 - y1), (y2 - y1));

            double cosA = (L * L + R1 * R1 - R2 * R2)/ (2 * L * R1);
            double a = Math.acos(cosA);
            double xp1 = x1 + R1 * (Math.cos(θ + a));
            double yp1 = y1 + R1 * (Math.sin(θ + a));

            double xp2 = x1 + R1 * (Math.cos(θ - a));
            double yp2 = y1 + R1 * (Math.sin(θ - a));


            Log.e("beacon1:","("+x1+","+y1+")");
            Log.e("beacon2:","("+x2+","+y2+")");
            Log.e("P1:","("+xp1+","+yp1+")");
            Log.e("P2:","("+xp2+","+yp2+")");
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        requestPermission();
       // bluetoothOn();
        beaconManager.bind(this);
    }

    @Override
    protected void onPause() {
        beaconManager.unbind(this);
        super.onPause();
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
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    public void bluetoothOn() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            Intent btOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(btOn, BLUETOOTH_ENABLE);
        }
    }

    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            if (requestCode == BLUETOOTH_ENABLE) {
                Toast.makeText(MainActivity.this, R.string.aa, Toast.LENGTH_SHORT).show();
            }
        }
    }

}
