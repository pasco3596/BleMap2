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
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private BeaconManager beaconManager;
    private static final String UUID = "00000001-1114-2345-6789-123456789121";
    // iBeacon�̃f�[�^��F�����邽�߂�Parser�t�H�[�}�b�g
    public static final String IBEACON_FORMAT = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;
    private static final int BLUETOOTH_ENABLE = 1;

    private BluetoothAdapter bluetoothAdapter;

    ListView lv;
    List<MyBeacon> allBeacons;
    List<MyBeacon> beaconList;
    TextView tv;
    Button bt;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermission();

        tv =(TextView)findViewById(R.id.textView);
        bt = (Button)findViewById(R.id.button);
        bt.setOnClickListener(view -> tv.setText("����������"));

        MyDatabaseHelper mh = new MyDatabaseHelper(this);
        SQLiteDatabase db = mh.getWritableDatabase();
        DAO dao = new DAO(db);
        allBeacons  = dao.selectAll();
        db.close();
        lv = (ListView) findViewById(R.id.listView);

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(IBEACON_FORMAT));
    }
    double aaa;
    @Override
    public void onBeaconServiceConnect() {
        new Thread(() -> {
            beaconManager.addRangeNotifier((beacons,region) -> {

                //beacons.stream().filter(s -> s.getId1().toString() == UUID ).forEach(s -> {
                beaconList = new ArrayList<>();

                for (Beacon beacon : beacons) {
                    Log.d("MyActivity", "UUID:" + beacon.getId1().toString() + ", major:" + beacon.getId2() +
                            ", minor:" + beacon.getId3() + ", Distance:"
                            + beacon.getDistance() + ", RSSI:" + beacon.getRssi()
                            + "txpower:" + beacon.getTxPower());
                     /*
                     n = 2.0 ��Q���̂Ȃ����
                     n < 2.0 : �d�g�����˂��Ȃ���`��������
                     n > 2.0  : ��Q���ɋz�����ꌸ�����Ȃ���`��������
                     ���̓e�L�g�[�Ȓl
                      */
                    double n = 2.1;
                    aaa = Math.pow(10.0, (beacon.getTxPower() - beacon.getRssi()) / (10.0 * n));
                    Log.e("aaaaaaaaaaaaaa",String.valueOf(aaa));
                    //
                    //                      MyBeacon b = allList.stream().filter(m ->  m.getMajor().equals(s.getId2().toString())
                    //
                    MyBeacon myBeacon = null;
                    for (int i = 0; i < allBeacons.size(); i++){
                        MyBeacon mb = allBeacons.get(i);

                        if(mb.getUUID().equals(beacon.getId1().toString())){
                            int x = mb.getX();
                            int y = mb.getY();
                            myBeacon = new MyBeacon(beacon, x, y);
                            beaconList.add(myBeacon);
                        }
                    }
                }

                if (2 < beaconList.size()) {
                    //RSSI�̋������Ƀ\�[�g
                    Collections.sort(beaconList,new MyBeaconSort());
                    getPosition2(beaconList);
                }
                       //         });
                handler.post(() -> {
                    tv.setText(String .valueOf(aaa));
                        ArrayAdapter<MyBeacon> arrayAdapter = new ArrayAdapter<>(MainActivity.this,android.R.layout.simple_list_item_1,beaconList);
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
            double �� = Math.atan2((y2 - y1), (y2 - y1));

            double cosA = (L * L + R1 * R1 - R2 * R2) / (2 * L * R1);
            double a = Math.acos(cosA);
            double xp1 = x1 + R1 * (Math.cos(�� + a));
            double yp1 = y1 + R1 * (Math.sin(�� + a));

            double xp2 = x1 + R1 * (Math.cos(�� - a));
            double yp2 = y1 + R1 * (Math.sin(�� - a));

            Log.e("beacon1:","("+x1+","+y1+")");
            Log.e("beacon2:","("+x2+","+y2+")");
            Log.e("P1:","("+xp1+","+yp1+")");
            Log.e("P2:","("+xp2+","+yp2+")");
        }
    }

    public void getPosition2(List<MyBeacon> list) {

        for (int i = 0; i < list.size()-1 || i < 3; i++){
            MyBeacon myBeacon1 = list.get(i);
            for (int j = i+1; j < list.size()-1|| j < 3; j++){
                MyBeacon myBeacon2 = list.get(j);

                double x1 = myBeacon1.getX();
                double y1 = myBeacon1.getY();
                double x2 = myBeacon2.getX();
                double y2 = myBeacon2.getY();

                double R1 = myBeacon1.getDistance();
                double R2 = myBeacon2.getDistance();

                double L =  Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
                double �� = Math.atan2((y2 - y1), (y2 - y1));

                double cosA = (L * L + R1 * R1 - R2 * R2) / (2 * L * R1);
                double a = Math.acos(cosA);
                double xp1 = x1 + R1 * (Math.cos(�� + a));
                double yp1 = y1 + R1 * (Math.sin(�� + a));

                double xp2 = x1 + R1 * (Math.cos(�� - a));
                double yp2 = y1 + R1 * (Math.sin(�� - a));

                Log.e("beacon1:","("+x1+","+y1+")");
                Log.e("beacon2:","("+x2+","+y2+")");
                Log.e("P1:","("+xp1+","+yp1+")");
                Log.e("P2:","("+xp2+","+yp2+")");
            }
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

    // �ʒu���̋���marshmallow�ȍ~�͎��Ȃ��Ⴂ���Ȃ�
    public void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},PERMISSION_REQUEST_COARSE_LOCATION);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_COARSE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "��������", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "������Ƃł����", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }
    //bluetooth��ON�ɂ���
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
                Toast.makeText(MainActivity.this, "Bluetooth�t���Ȃ������", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
