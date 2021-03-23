package com.qtt.thebarber.EventBus;

import com.qtt.thebarber.Model.Barber;
import com.qtt.thebarber.Model.BarberService;
import com.qtt.thebarber.Model.Salon;

public class EnableNextButtonEvent {
    private int step;
    private Barber barber;
    private Salon salon;
    private int timeSlot;
    private BarberService barberService;

    public EnableNextButtonEvent(int step, Salon salon) {
        this.step = step;
        this.salon = salon;
    }

    public EnableNextButtonEvent(int step, BarberService barberService) {
        this.step = step;
        this.barberService = barberService;
    }

    public BarberService getBarberService() {
        return barberService;
    }

    public void setBarberService(BarberService barberService) {
        this.barberService = barberService;
    }

    public EnableNextButtonEvent(int step, Barber barber) {
        this.step = step;
        this.barber = barber;
    }

    public EnableNextButtonEvent(int step, int timeSlot) {
        this.step = step;
        this.timeSlot = timeSlot;
    }

    public int getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(int timeSlot) {
        this.timeSlot = timeSlot;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public Barber getBarber() {
        return barber;
    }

    public void setBarber(Barber barber) {
        this.barber = barber;
    }

    public Salon getSalon() {
        return salon;
    }

    public void setSalon(Salon salon) {
        this.salon = salon;
    }
}
