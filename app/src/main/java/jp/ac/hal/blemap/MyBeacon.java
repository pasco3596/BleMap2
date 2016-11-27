package jp.ac.hal.blemap;

import org.altbeacon.beacon.Beacon;

/**
 * Created by pasuco on 2016/06/13.
 */
public class MyBeacon {

    private String UUID;
    private int  RSSI;
    private int major;
    private int minor;
    private double distance;
    private int x;
    private int y;

    public String getUUID() {
        return UUID;
    }

    public int  getRSSI() {
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
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public double getDistance(){
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public MyBeacon(String UUID, int RSSI, int major, int minor , int x , int y){
        this.UUID = UUID;
        this.RSSI = RSSI;
        this.major = major;
        this.minor = minor;
        this.x = x;
        this.y = y;
    }
    public MyBeacon(String UUID, int major, int minor , int x , int y){
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
        this.distance = beacon.getDistance();
    }


    public String toString(){

        return String.format("%s\n%s\nXç¿ïW:%sYç¿ïW:%s", UUID,RSSI,x,y);
    }
}
