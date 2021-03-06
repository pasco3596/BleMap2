package jp.ac.hal.blemap;

import org.altbeacon.beacon.Beacon;

/**
 * Created by pasuco on 2016/06/13.
 */
public class MyBeacon {

    private String UUID;
    private int RSSI;
    private String name;
    private String discription;
    private int txpower;
    private int major;
    private int minor;
    private int x;
    private int y;
    private double distance;

    public String getUUID() {
        return UUID;
    }

    public int getRSSI() {
        return RSSI;
    }

    public void setRSSI(int RSSI) {
        this.RSSI = RSSI;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getDistance() {
        /*
        n = 2.0 障害物のない空間
        n < 2.0 : 電波が反射しながら伝搬する空間
        n > 2.0  : 障害物に吸収され減衰しながら伝搬する空間
        今はテキトーな値
        */
//        double n = 2.1;
//        double distance = Math.pow(10.0, (getTxpower() - getRSSI()) / (10.0 * n));
//
        return distance;
    }

    public int getTxpower() {
        return txpower;
    }

    public void setTxpower(int txpower) {
        this.txpower = txpower;
    }


    public MyBeacon(String UUID, int RSSI, int major, int minor, int x, int y) {
        this.UUID = UUID;
        this.RSSI = RSSI;
        this.major = major;
        this.minor = minor;
        this.x = x;
        this.y = y;
    }

    public MyBeacon(String UUID, int major, int minor, int x, int y) {
        this.UUID = UUID;
        this.major = major;
        this.minor = minor;
        this.x = x;
        this.y = y;
    }

    public MyBeacon(Beacon beacon, int x, int y) {
        this.UUID = beacon.getId1().toString();
        this.RSSI = beacon.getRssi();
        this.major = beacon.getId2().toInt();
        this.minor = beacon.getId3().toInt();
        this.txpower = beacon.getTxPower();
        this.distance = beacon.getDistance();
        this.x = x;
        this.y = y;
    }

    public MyBeacon(String uuid, int major, int minor, String name, String discription, int x, int y) {
        this.UUID = uuid;
        this.major = major;
        this.minor = minor;
        this.name = name;
        this.discription = discription;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return name;
    }

    public String getDiscription() {
        return discription;
    }
}
