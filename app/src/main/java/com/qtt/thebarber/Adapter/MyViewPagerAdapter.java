package com.qtt.thebarber.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.qtt.thebarber.Fragments.BookingStep1Fragment;
import com.qtt.thebarber.Fragments.BookingStep1_1Fragment;
import com.qtt.thebarber.Fragments.BookingStep2Fragment;
import com.qtt.thebarber.Fragments.BookingStep3Fragment;
import com.qtt.thebarber.Fragments.BookingStep4Fragment;

public class MyViewPagerAdapter extends FragmentPagerAdapter {

    public MyViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return BookingStep1Fragment.getInstance();
            case 1:
                return BookingStep1_1Fragment.getInstance();
            case 2:
                return BookingStep2Fragment.getInstance();
            case 3:
                return BookingStep3Fragment.getInstance();
            case 4:
                return BookingStep4Fragment.getInstance();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }
}
