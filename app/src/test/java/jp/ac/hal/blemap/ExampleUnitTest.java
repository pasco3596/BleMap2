package jp.ac.hal.blemap;

import android.graphics.Matrix;

import org.altbeacon.beacon.Beacon;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);

        MyBeacon myBeacon12 = new MyBeacon("1",1,1,"あああ","あああ",0,0);
        MyBeacon myBeacon23 = new MyBeacon("1",1,1,"いいい","いいい",1,0);
        MyBeacon myBeacon34 = new MyBeacon("1",1,1,"ううう","ううう",0,1);
        MyBeacon myBeacon45 = new MyBeacon("1",1,1,"えええ","えええ",1,1);

        List<MyBeacon> list = new ArrayList<>();
        list.add(myBeacon12);
        list.add(myBeacon23);
        list.add(myBeacon34);
        list.add(myBeacon45);

        for(MyBeacon myBeacon: list) {


        }







    }
}