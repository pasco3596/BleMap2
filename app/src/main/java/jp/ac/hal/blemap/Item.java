package jp.ac.hal.blemap;

import java.util.UUID;

/**
 * Created by pasuco on 2017/01/27.
 */

public class Item {
    private String uuid;
    private String name;
    private String discription;
    private int major;
    private int minor;
    private int x;
    private int y;

    public int getMajor() {
        return major;
    }

    public void setMajor(int major) {
        this.major = major;
    }

    public int getMinor() {
        return minor;
    }

    public void setMinor(int minor) {
        this.minor = minor;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscription() {
        return discription;
    }

    public void setDiscription(String discription) {
        this.discription = discription;
    }

    public Item(String uuid, int major, int minor, String name, String discription, int x, int y) {
        this.uuid = uuid;
        this.major = major;
        this.minor = minor;
        this.name = name;
        this.discription = discription;
        this.x = x;
        this.y = y;
    }
}
