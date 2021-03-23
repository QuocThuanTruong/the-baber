package com.qtt.thebarber.EventBus;

public class LoadTimeSlotEvent {
    private Boolean isLoaded;

    public LoadTimeSlotEvent(Boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    public Boolean getLoaded() {
        return isLoaded;
    }

    public void setLoaded(Boolean loaded) {
        isLoaded = loaded;
    }
}
