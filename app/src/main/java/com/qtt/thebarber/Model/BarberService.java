package com.qtt.thebarber.Model;

public class BarberService {
    private String uid;
    private String name;
    private int price;
    private String avatar;

    public BarberService() {
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public BarberService(String uid, String name, int price, String avatar) {
        this.uid = uid;
        this.name = name;
        this.price = price;
        this.avatar = avatar;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}