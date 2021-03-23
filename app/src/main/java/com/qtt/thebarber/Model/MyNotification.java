package com.qtt.thebarber.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

public class MyNotification {
    private String uid, title, content;
    private boolean read;
    private Timestamp serverTimeStamp;
    private String avatar;

    public MyNotification() {
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Timestamp getServerTimeStamp() {
        return serverTimeStamp;
    }

    public void setServerTimeStamp(Timestamp serverTimeStamp) {
        this.serverTimeStamp = serverTimeStamp;
    }
}

