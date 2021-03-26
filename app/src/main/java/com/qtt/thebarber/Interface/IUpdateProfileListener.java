package com.qtt.thebarber.Interface;

public interface IUpdateProfileListener {
    void onUpdateProfileSuccess(boolean isSuccess);

    void OnUpdateProfileFailed(String message);
}
