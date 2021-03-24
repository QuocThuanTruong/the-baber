package com.qtt.thebarber.Interface;

import com.qtt.thebarber.Model.LookBook;

import java.util.List;

public interface IBannerLoadListener {
    void onBannerLoadSuccess(List<LookBook> bannerList);
    void onBannerLoadFailed(String message);
}
