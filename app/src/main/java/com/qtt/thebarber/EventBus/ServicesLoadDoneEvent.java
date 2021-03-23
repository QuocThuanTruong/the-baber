package com.qtt.thebarber.EventBus;

import com.qtt.thebarber.Model.BarberService;

import java.util.List;

public class ServicesLoadDoneEvent {
    List<BarberService> barberServiceList;

    public List<BarberService> getBarberServiceList() {
        return barberServiceList;
    }

    public void setBarberServiceList(List<BarberService> barberServiceList) {
        this.barberServiceList = barberServiceList;
    }

    public ServicesLoadDoneEvent(List<BarberService> barberServiceList) {
        this.barberServiceList = barberServiceList;
    }
}
