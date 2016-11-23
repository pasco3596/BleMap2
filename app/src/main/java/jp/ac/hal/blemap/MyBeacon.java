package jp.ac.hal.blemap;

/**
 * Created by pasuco on 2016/06/13.
 */
public class MyBeacon {

    private String UUID;
    private int  RSSI;
    private String major;
    private String minor;
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

    public String getMajor() {
        return major;
    }

    public String getMinor() {
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

    public MyBeacon(String UUID, int RSSI, String major, String minor , int x , int y){
        this.UUID = UUID;
        this.RSSI = RSSI;
        this.major= major;
        this.minor=minor;
        this.x = x;
        this.y = y;
    }
    public MyBeacon(String UUID, String major, String minor , int x , int y){
        this.UUID = UUID;
        this.major= major;
        this.minor=minor;
        this.x = x;
        this.y = y;
    }
    public String toString(){

        return String.format("%s\n%s\nXç¿ïW:%sYç¿ïW:%s", UUID,RSSI,x,y);
    }
}
