package com.qtt.thebarber.Model;

import com.google.firebase.Timestamp;
import com.qtt.thebarber.Database.CartItem;

import java.util.List;

public class BookingInformation {
    private String cityBooking, customerName, customerPhone, barberId, barberName, salonId, salonName, salonAddress, time;
    private int timeSlot;
    private Timestamp timestamp;
    private boolean done;
    private List<CartItem> cartItemList;
    private List<String> barberServiceList;

    public BookingInformation() {
    }

    public BookingInformation(String cityBooking, String customerName, String customerPhone, String barberId, String barberName, String salonId, String salonName, String salonAddress, String time, int timeSlot, Timestamp timestamp, boolean done, List<CartItem> cartItemList, List<String> barberServiceList) {
        this.cityBooking = cityBooking;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.barberId = barberId;
        this.barberName = barberName;
        this.salonId = salonId;
        this.salonName = salonName;
        this.salonAddress = salonAddress;
        this.time = time;
        this.timeSlot = timeSlot;
        this.timestamp = timestamp;
        this.done = done;
        this.cartItemList = cartItemList;
        this.barberServiceList = barberServiceList;
    }

    public String getCityBooking() {
        return cityBooking;
    }

    public void setCityBooking(String cityBooking) {
        this.cityBooking = cityBooking;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getBarberId() {
        return barberId;
    }

    public void setBarberId(String barberId) {
        this.barberId = barberId;
    }

    public String getBarberName() {
        return barberName;
    }

    public void setBarberName(String barberName) {
        this.barberName = barberName;
    }

    public String getSalonId() {
        return salonId;
    }

    public void setSalonId(String salonId) {
        this.salonId = salonId;
    }

    public String getSalonName() {
        return salonName;
    }

    public void setSalonName(String salonName) {
        this.salonName = salonName;
    }

    public String getSalonAddress() {
        return salonAddress;
    }

    public void setSalonAddress(String salonAddress) {
        this.salonAddress = salonAddress;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(int timeSlot) {
        this.timeSlot = timeSlot;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public List<CartItem> getCartItemList() {
        return cartItemList;
    }

    public void setCartItemList(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    public List<String> getBarberServiceList() {
        return barberServiceList;
    }

    public void setBarberServiceList(List<String> barberServiceList) {
        this.barberServiceList = barberServiceList;
    }
}
