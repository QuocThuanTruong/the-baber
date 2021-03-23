package com.qtt.thebarber.Interface;

import com.qtt.thebarber.Model.Banner;

import java.util.List;

public interface IBannerLoadListener {
    void onBannerLoadSuccess(List<Banner> bannerList);
    void onBannerLoadFailed(String message);
}
