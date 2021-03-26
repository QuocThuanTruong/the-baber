package com.qtt.thebarber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.qtt.thebarber.Adapter.MyNotificationAdapter;
import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.Interface.INotificationLoadListener;
import com.qtt.thebarber.Model.MyNotification;
import com.qtt.thebarber.databinding.ActivityNotificationBinding;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends AppCompatActivity implements INotificationLoadListener {

    ActivityNotificationBinding binding;

    INotificationLoadListener iNotificationLoadListener;

    CollectionReference notificationCol;

    int totalItem = 0, lastVisibleItem;
    boolean isLoading = false, isMaxData = false;

    DocumentSnapshot finalDoc;

    MyNotificationAdapter myNotificationAdapter = null;
    List<MyNotification> firstList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorBackground));
        }

        init();
        initView();
        loadNotification(null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        binding.recyclerNotification.setHasFixedSize(true);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerNotification.setLayoutManager(layoutManager);

        binding.recyclerNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItem = layoutManager.getItemCount();
                lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItem <= (lastVisibleItem + Common.MAX_NOTI_PER_LOAD)) {
                    loadNotification(finalDoc);
                    isLoading = true;
                }
            }
        });

        binding.imgBack.setOnClickListener(v -> finish());
    }

    private void loadNotification(DocumentSnapshot lastDoc) {
        ///AllSalon/Florida/Branch/0n7ikrtgQXW4EXhuJ0qy/Barbers/Nsa4hBFukd8UZYMiRe5y/Notifications
        notificationCol = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Notifications");
        if (lastDoc == null) {
            notificationCol.orderBy("serverTimeStamp", Query.Direction.DESCENDING)
                    .limit(Common.MAX_NOTI_PER_LOAD)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<MyNotification> myNotifications = new ArrayList<>();
                            DocumentSnapshot finalDoc = null;

                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                MyNotification myNotification = documentSnapshot.toObject(MyNotification.class);
                                myNotifications.add(myNotification);
                                finalDoc = documentSnapshot;
                            }

                            iNotificationLoadListener.onNotificationLoadSuccess(myNotifications, finalDoc);
                        }
                    }).addOnFailureListener(e -> iNotificationLoadListener.onNotificationLoadFailed(e.getMessage()));
        } else {
            if (!isMaxData) {
                notificationCol.orderBy("serverTimeStamp", Query.Direction.DESCENDING)
                        .startAfter(lastDoc)
                        .limit(Common.MAX_NOTI_PER_LOAD)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                List<MyNotification> myNotifications = new ArrayList<>();
                                DocumentSnapshot finalDoc = null;

                                for (DocumentSnapshot documentSnapshot : task.getResult()) {
                                    MyNotification myNotification = documentSnapshot.toObject(MyNotification.class);
                                    myNotifications.add(myNotification);
                                    finalDoc = documentSnapshot;
                                }

                                iNotificationLoadListener.onNotificationLoadSuccess(myNotifications, finalDoc);
                            }
                        }).addOnFailureListener(e -> iNotificationLoadListener.onNotificationLoadFailed(e.getMessage()));
            }
        }

    }

    private void init() {
        iNotificationLoadListener = this;
    }

    @Override
    public void onNotificationLoadSuccess(List<MyNotification> notificationList, DocumentSnapshot lastDocument) {
        if (lastDocument != null) {
            if (lastDocument.equals(finalDoc)) {
                isMaxData = true;
            } else {
                finalDoc = lastDocument;
                isMaxData = false;
            }

            if (myNotificationAdapter == null || notificationList.size() == 0) {
                myNotificationAdapter = new MyNotificationAdapter(this, notificationList);
                firstList = notificationList;
            } else {
                if (!notificationList.equals(firstList)) {
                    myNotificationAdapter.updateList(notificationList);
                }
            }

            binding.recyclerNotification.setAdapter(myNotificationAdapter);
            myNotificationAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNotificationLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
