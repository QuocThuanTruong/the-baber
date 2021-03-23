package com.qtt.thebarber.EventBus;

public class ConfirmBookingEvent {
    private Boolean isConfirm;

    public ConfirmBookingEvent(Boolean isConfirm) {
        this.isConfirm = isConfirm;
    }

    public Boolean getConfirm() {
        return isConfirm;
    }

    public void setConfirm(Boolean confirm) {
        isConfirm = confirm;
    }
}
