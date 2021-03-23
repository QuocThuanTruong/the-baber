package com.qtt.thebarber.EventBus;

import com.qtt.thebarber.Model.Barber;

public class ShowBarberProfileEvent {
    Barber barber;

    public Barber getBarber() {
        return barber;
    }

    public void setBarber(Barber barber) {
        this.barber = barber;
    }

    public ShowBarberProfileEvent(Barber barber) {
        this.barber = barber;
    }
}
