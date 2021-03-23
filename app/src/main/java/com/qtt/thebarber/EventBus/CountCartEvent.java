package com.qtt.thebarber.EventBus;

public class CountCartEvent {
    boolean isCounting;

    public CountCartEvent(boolean isCounting) {
        this.isCounting = isCounting;
    }

    public boolean isCounting() {
        return isCounting;
    }

    public void setCounting(boolean counting) {
        isCounting = counting;
    }
}
