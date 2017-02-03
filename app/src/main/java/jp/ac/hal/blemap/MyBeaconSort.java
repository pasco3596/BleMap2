package jp.ac.hal.blemap;

import java.util.Comparator;

/**
 * Created by pasuco on 2016/11/25.
 */
public class MyBeaconSort implements Comparator<MyBeacon> {
    public int compare(MyBeacon myBeacon1, MyBeacon myBeacon2) {
        int no1 = myBeacon1.getRSSI();
        int no2 = myBeacon2.getRSSI();
        if (no1 > no2) {
            return 1;
        } else if (no1 == no2) {
            return 0;
        } else {
            return -1;
        }
    }
}
