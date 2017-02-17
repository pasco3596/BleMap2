package jp.ac.hal.blemap;

import java.util.Comparator;

/**
 * Created by pasuco on 2016/11/25.
 */
public class MyBeaconSort implements Comparator<MyBeacon> {
    public int compare(MyBeacon myBeacon1, MyBeacon myBeacon2) {
//        double no1 = myBeacon1.getDistance();
//        double no2 = myBeacon2.getDistance();

        double x1 = myBeacon1.getX();
        double x2 = myBeacon2.getX();
        double y1 = myBeacon1.getY();
        double y2 = myBeacon2.getY();

        double no1 = Math.sqrt(x1 * x1 + y1 + y1);
        double no2 = Math.sqrt(x2 * x2 + y2 + y2);

//        double no1 = myBeacon1.getRSSI();
//        double no2 = myBeacon2.getRSSI();

        return Double.compare(no1, no2);


//        return Double.compare(no2,no1);
    }
}
