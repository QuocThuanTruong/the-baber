package com.qtt.thebarber.Interface;

import com.qtt.thebarber.Model.TimeSlot;

import java.util.List;

public interface ITimeSlotLoadListener {
    void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList); //list from server
    void onTimeSlotLoadFailed(String message);
    void onTimeSlotLoadEmpty(); //if empty use default time slot list
}
