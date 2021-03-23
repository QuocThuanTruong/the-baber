package com.qtt.thebarber.Interface;

import com.qtt.thebarber.Model.Banner;

import java.util.List;

public interface ILookBookLoadListener {
    void onLookBookLoadSuccess(List<Banner> lookBookList);
    void onLookBookLoadFailed(String message);
}
