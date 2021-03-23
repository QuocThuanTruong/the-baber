package com.qtt.thebarber.EventBus;

import com.qtt.thebarber.Model.Barber;

import java.util.List;

public class BarberDoneEvent {
    private List<Barber> barbers;

    public BarberDoneEvent(List<Barber> barbers) {
        this.barbers = barbers;
    }

    public List<Barber> getBarbers() {
        return barbers;
    }

    public void setBarbers(List<Barber> barbers) {
        this.barbers = barbers;
    }
}
