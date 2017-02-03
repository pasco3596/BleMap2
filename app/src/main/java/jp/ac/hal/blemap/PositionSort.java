package jp.ac.hal.blemap;

import java.util.Comparator;

/**
 * Created by pasuco on 2016/12/01.
 */
public class PositionSort implements Comparator<Position> {
    public int compare(Position p1, Position p2) {
        double xp1 = p1.getPositionX();
        double xp2 = p2.getPositionX();
        double yp1 = p1.getPositionY();
        double yp2 = p2.getPositionY();


        double d1 = (xp1 * xp1) + (yp1 * yp1);
        double d2 = (xp2 * xp2) + (yp2 * yp2);

        d1 = Math.abs(Math.sqrt(d1));
        d2 = Math.abs(Math.sqrt(d2));

        if (d1 > d2) {
            return 1;
        } else if (d1 == d2) {
            return 0;
        } else {
            return -1;
        }

    }
}
