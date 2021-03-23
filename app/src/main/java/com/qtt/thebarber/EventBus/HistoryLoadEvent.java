package com.qtt.thebarber.EventBus;

import com.qtt.thebarber.Model.BookingInformation;

import java.util.List;

public class HistoryLoadEvent {
    private Boolean isSuccess;
    private String message;
    private List<BookingInformation> bookingInformationList;

    public HistoryLoadEvent(Boolean isSuccess, List<BookingInformation> bookingInformationList) {
        this.isSuccess = isSuccess;
        this.bookingInformationList = bookingInformationList;
    }

    public HistoryLoadEvent(Boolean isSuccess, String message) {
        this.isSuccess = isSuccess;
        this.message = message;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<BookingInformation> getBookingInformationList() {
        return bookingInformationList;
    }

    public void setBookingInformationList(List<BookingInformation> bookingInformationList) {
        this.bookingInformationList = bookingInformationList;
    }
}
