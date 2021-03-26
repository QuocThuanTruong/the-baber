package com.qtt.thebarber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.qtt.thebarber.Adapter.MyHistoryAdapter;
import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.EventBus.HistoryLoadEvent;
import com.qtt.thebarber.Model.BookingInformation;
import com.qtt.thebarber.databinding.ActivityHistoryBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class HistoryActivity extends AppCompatActivity {

    ActivityHistoryBinding binding;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorBackground));
        }

        initView();
        loadUserBookingInformation();
    }

    private void loadUserBookingInformation() {
        dialog.show();
        ///User/+841689294631/Booking
        CollectionReference userBookingRef = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");


        userBookingRef.whereEqualTo("done", true)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<BookingInformation> bookingInformations = new ArrayList<>();

                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            BookingInformation bookingInformation = documentSnapshot.toObject(BookingInformation.class);
                            bookingInformations.add(bookingInformation);
                        }

                        EventBus.getDefault().postSticky(new HistoryLoadEvent(true, bookingInformations));
                    }
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> {
                    EventBus.getDefault().postSticky(new HistoryLoadEvent(false, e.getMessage()));
                    dialog.dismiss();
                });
    }

    private void initView() {
        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        binding.recyclerHistory.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerHistory.setLayoutManager(layoutManager);

        binding.imgBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onHistoryLoadd(HistoryLoadEvent event) {
        if (event.getSuccess()) {
            MyHistoryAdapter myHistoryAdapter = new MyHistoryAdapter(this, event.getBookingInformationList());
            binding.recyclerHistory.setAdapter(myHistoryAdapter);
        } else {
            Toast.makeText(this, event.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}