package com.qtt.thebarber.EventBus;

public class ClearCartEvent {
    boolean isClear;

    public boolean isClear() {
        return isClear;
    }

    public void setClear(boolean clear) {
        isClear = clear;
    }

    public ClearCartEvent(boolean isClear) {
        this.isClear = isClear;
    }
}
