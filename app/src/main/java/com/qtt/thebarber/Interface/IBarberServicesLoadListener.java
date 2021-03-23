package com.qtt.thebarber.Interface;

import com.qtt.thebarber.Model.BarberService;

import java.util.List;

public interface IBarberServicesLoadListener {
    void onBarberServicesLoadSuccess(List<BarberService> barberServicesList);

    void onBarberServicesLoadFailed(String message);
}
