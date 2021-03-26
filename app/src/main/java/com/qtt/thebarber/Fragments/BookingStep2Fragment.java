package com.qtt.thebarber.Fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qtt.thebarber.Adapter.MyBarberAdapter;
import com.qtt.thebarber.Common.SpacesItemDecoration;
import com.qtt.thebarber.EventBus.BarberDoneEvent;
import com.qtt.thebarber.R;
import com.qtt.thebarber.databinding.FragmentBookingStep2Binding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookingStep2Fragment extends Fragment {

    FragmentBookingStep2Binding binding;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void barbersReceiver(BarberDoneEvent event) {
            MyBarberAdapter myBarberAdapter = new MyBarberAdapter(getActivity(), event.getBarbers());
            binding.recyclerBarber.setAdapter(myBarberAdapter);
    };

    private static BookingStep2Fragment instance;

    public static BookingStep2Fragment getInstance() {
        if (instance == null)
            instance = new BookingStep2Fragment();
        return instance;
    }

    private BookingStep2Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookingStep2Binding.inflate(inflater, container, false);
        View view =  binding.getRoot();

        initView();

        return view;
    }

    private void initView() {
        binding.recyclerBarber.setHasFixedSize(true);
        binding.recyclerBarber.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        binding.recyclerBarber.addItemDecoration(new SpacesItemDecoration(8));

    }

}
