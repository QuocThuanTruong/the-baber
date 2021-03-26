package com.qtt.thebarber.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qtt.thebarber.Adapter.MyServiceAdapter;
import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.Common.SpacesItemDecoration;
import com.qtt.thebarber.EventBus.ServicesLoadDoneEvent;
import com.qtt.thebarber.R;
import com.qtt.thebarber.databinding.FragmentBookingStep11Binding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class BookingStep1_1Fragment extends Fragment {

    FragmentBookingStep11Binding binding;
    MyServiceAdapter myServiceAdapter;

    private static BookingStep1_1Fragment instance;

    public static BookingStep1_1Fragment getInstance() {
        if (instance == null)
            instance = new BookingStep1_1Fragment();
        return instance;
    }

    private BookingStep1_1Fragment() {
        // Required empty public constructor
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void loadServiceDone(ServicesLoadDoneEvent event) {
        myServiceAdapter = new MyServiceAdapter(getContext(), event.getBarberServiceList());
        binding.recyclerServices.setAdapter(myServiceAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBookingStep11Binding.inflate(inflater, container, false);

        initView();

        return binding.getRoot();
    }

    private void initView() {
        binding.recyclerServices.setHasFixedSize(true);
        binding.recyclerServices.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.recyclerServices.addItemDecoration(new SpacesItemDecoration(16));
    }
}