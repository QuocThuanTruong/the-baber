package com.qtt.thebarber.Fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.qtt.thebarber.Adapter.HomeSliderAdapter;
import com.qtt.thebarber.Adapter.ServerLookBookAdapter;
import com.qtt.thebarber.BookingActivity;
import com.qtt.thebarber.CartActivity;
import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.Database.CartDataSource;
import com.qtt.thebarber.Database.CartDatabase;
import com.qtt.thebarber.Database.LocalCartDataSource;
import com.qtt.thebarber.HistoryActivity;
import com.qtt.thebarber.Interface.IBannerLoadListener;
import com.qtt.thebarber.Interface.IBookingInfoChangeListener;
import com.qtt.thebarber.Interface.IBookingInfoLoadListener;
import com.qtt.thebarber.Interface.ILookBookLoadListener;
import com.qtt.thebarber.Interface.INotificationCountListener;
import com.qtt.thebarber.Model.BarberService;
import com.qtt.thebarber.Model.BookingInformation;
import com.qtt.thebarber.Model.LookBook;
import com.qtt.thebarber.NotificationActivity;
import com.qtt.thebarber.Service.PicassoImageLoadingService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ss.com.bannerslider.Slider;

public class HomeFragment extends Fragment implements IBannerLoadListener, ILookBookLoadListener, IBookingInfoLoadListener, IBookingInfoChangeListener, INotificationCountListener {

    com.qtt.thebarber.databinding.FragmentHomeBinding binding;
    CollectionReference notificationCol;
    EventListener<QuerySnapshot> notificationEvent;
    ListenerRegistration notificationListener;
    INotificationCountListener iNotificationCountListener;
    AlertDialog dialog;

    CartDataSource cartDataSource;


    void booking() {

        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, 0);
        today.add(Calendar.HOUR_OF_DAY, 0);
        today.add(Calendar.MINUTE, 0);

        Timestamp todayTimeStamp = new Timestamp(today.getTime());

        FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking")
                .whereGreaterThanOrEqualTo("timestamp", todayTimeStamp)
                .whereEqualTo("done", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                Toast.makeText(getActivity(), "You have booked before!", Toast.LENGTH_SHORT).show();
                            } else {
                                //not booked before
                                startActivity(new Intent(getActivity(), BookingActivity.class));
                            }
                        }
                        
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                startActivity(new Intent(getActivity(), BookingActivity.class));
            }
        });

    }


    void openCart() {
        startActivity(new Intent(getActivity(), CartActivity.class));
    }


    void openHistory() {
        startActivity(new Intent(getActivity(), HistoryActivity.class));
    }

    void changeBooking() {
        changeBookingFromUser();
    }

    private void changeBookingFromUser() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())
                .setCancelable(false)
                .setTitle("CAUTION")
                .setMessage("If you change booking information, you will delete your old booking\nLet's confirm!")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteBookingFromBarber(true);
                    }
                });

        alertDialog.show();
    }


    void deleteBooking() {
        deleteBookingFromBarber(false);
    }

    private void deleteBookingFromBarber(final boolean isChange) {
        /*- Delete booking in barber collection
         * - Delete booking form user booking
         * - Delete event in calendar*/
        if (Common.currentBookingInfo != null) {
            dialog.show();
            //AllSalon/Florida/Branch/0n7ikrtgQXW4EXhuJ0qy/Barbers/Nsa4hBFukd8UZYMiRe5y/07_08_2019
            DocumentReference barberBookingRef = FirebaseFirestore.getInstance()
                    .collection("AllSalon")
                    .document(Common.currentBookingInfo.getCityBooking())
                    .collection("Branch")
                    .document(Common.currentBookingInfo.getSalonId())
                    .collection("Barbers")
                    .document(Common.currentBookingInfo.getBarberId())
                    .collection(Common.convertTimeStampToStringKey(Common.currentBookingInfo.getTimestamp()))
                    .document(String.valueOf(Common.currentBookingInfo.getTimeSlot()));

            barberBookingRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //continue delete from user booking
                            deleteBookingFromUser(isChange);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(getActivity(), "Current booking must not be empty!", Toast.LENGTH_SHORT).show();
        }
    }


    private void deleteBookingFromUser(final boolean isChange) {
        if (Common.currentBookingId != null) {
            ///User/+84389294631/Booking/N04t8G3zLaMPApCMUVBU
            DocumentReference documentReference = FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(Common.currentUser.getPhoneNumber())
                    .collection("Booking")
                    .document(Common.currentBookingId);
            documentReference.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //delete from calendar
                            Paper.init(getContext());
                            if (Paper.book().read(Common.EVENT_URI_SAVE).toString() != null) {
                                Uri eventUri = null;
                                String eventString = Paper.book().read(Common.EVENT_URI_SAVE).toString();

                                if (eventString != null && !TextUtils.isEmpty(eventString)) {
                                    eventUri = Uri.parse(eventString);
                                }
                                if (eventUri != null)
                                    getActivity().getContentResolver().delete(eventUri, null, null);
                            }

                            Toast.makeText(getContext(), "Deleting booking successfully!", Toast.LENGTH_SHORT).show();

                            //refresh
                            loadUserBooking();
                            if (isChange) {
                                iBookingInfoChangeListener.onBookingInfoChange();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getActivity(), "Booking information must not be null!", Toast.LENGTH_SHORT).show();
            Log.d("HOME_FRAGMENT", "deleteBookingFromUser: Id is null");
        }
        if (dialog.isShowing())
            dialog.dismiss();
    }

    //FireStore
    CollectionReference bannerRef, looBookRef;

    //Interface
    IBannerLoadListener iBannerLoadListener;
    ILookBookLoadListener iLookBookLoadListener;
    IBookingInfoLoadListener iBookingInfoLoadListener;
    IBookingInfoChangeListener iBookingInfoChangeListener;

    ListenerRegistration userBookingListener = null;
    EventListener<QuerySnapshot> userBookingEvent = null;

    public HomeFragment() {
        bannerRef = FirebaseFirestore.getInstance().collection("Banner");
        looBookRef = FirebaseFirestore.getInstance().collection("LookBook");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = com.qtt.thebarber.databinding.FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        //Init slider, interface
        Slider.init(new PicassoImageLoadingService());
        iBannerLoadListener = this;
        iLookBookLoadListener = this;
        iBookingInfoLoadListener = this;
        iBookingInfoChangeListener = this;
        iNotificationCountListener = this;


        //Check account
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            setUserInformation();
            loadBanners();
            loadLookBooks();
            initRealtimeUserBooking();
            loadUserBooking();
            countCartItem();
            initNotificationRealTimeUpdate();
            loadNotification();
        }

        binding.cardViewBooking.setOnClickListener(v -> booking());
        binding.cardViewCart.setOnClickListener(v -> openCart());
        binding.cardViewHistory.setOnClickListener(v -> openHistory());
        binding.btnBookingChange.setOnClickListener(v -> changeBooking());
        binding.btnBookingDelete.setOnClickListener(v -> deleteBooking());

        binding.cardViewBookingInfo.setVisibility(View.GONE);

        binding.cardViewNotification.setOnClickListener(v -> startActivity(new Intent(getActivity(), NotificationActivity.class)));

        return view;
    }

    private void initNotificationRealTimeUpdate() {
        notificationCol = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Notifications");

        notificationEvent = (queryDocumentSnapshot, e) -> loadNotification();

        notificationListener = notificationCol.whereEqualTo("read", false)
                .addSnapshotListener(notificationEvent);
    }

    private void loadNotification() {
        notificationCol.whereEqualTo("read", false)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        iNotificationCountListener.onNotificationCountSuccess(task.getResult().size());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void initRealtimeUserBooking() {
        if (userBookingEvent == null) {
            userBookingEvent = new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    loadUserBooking();
                }
            };
        }
    }

    private void countCartItem() {
        //DatabaseUtils.countItemInCart(cartDatabase, this);

        cartDataSource.countItemCart(Common.currentUser.getPhoneNumber())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull Integer count) {
                        if (count != 0) {
                            binding.notificationBadge.setVisibility(View.VISIBLE);
                            binding.notificationBadge.setText(String.valueOf(count));
                        } else {
                            binding.notificationBadge.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        if (!e.getMessage().contains("empty")) {
                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserBooking();
        countCartItem();
        loadNotification();
    }

    private void loadUserBooking() {
        CollectionReference bookingRef = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");

        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, 0);
        today.add(Calendar.HOUR_OF_DAY, 0);
        today.add(Calendar.MINUTE, 0);

        Timestamp todayTimeStamp = new Timestamp(today.getTime());

        Log.d("AAAAA", todayTimeStamp.toDate().toString());

        bookingRef.whereGreaterThanOrEqualTo("timestamp", todayTimeStamp)
                .whereEqualTo("done", false)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (DocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                BookingInformation bookingInformation = queryDocumentSnapshot.toObject(BookingInformation.class);
                                Log.d("AAAAA", "time server: " + bookingInformation.getTimestamp().toDate());
                                iBookingInfoLoadListener.onBookingInfoLoadSuccess(bookingInformation, queryDocumentSnapshot.getId());
                                break;
                            }
                        } else
                            iBookingInfoLoadListener.onBookingInfoLoadEmpty();
                    }
                }).addOnFailureListener(e -> iBookingInfoLoadListener.onBookingInfoLoadFail(e.getMessage()));

        //Listen real time
        if (userBookingEvent != null) {
            if (userBookingListener == null)
             userBookingListener = bookingRef.addSnapshotListener(userBookingEvent);
        }
    }

    private void loadLookBooks() {
        looBookRef.get()
                .addOnCompleteListener(task -> {
                    List<LookBook> lookBookList = new ArrayList<>();

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot lookBookSnapShot : task.getResult()) {
                            LookBook lookBook = lookBookSnapShot.toObject(LookBook.class);
                            lookBookList.add(lookBook);
                        }
                        iLookBookLoadListener.onLookBookLoadSuccess(lookBookList);
                    }
                }).addOnFailureListener(e -> iLookBookLoadListener.onLookBookLoadFailed(e.getMessage()));
    }

    private void loadBanners() {
        bannerRef.get()
                .addOnCompleteListener(task -> {
                    List<LookBook> bannerList = new ArrayList<>();

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot bannerSnapShot : task.getResult()) {
                            LookBook banner = bannerSnapShot.toObject(LookBook.class);
                            bannerList.add(banner);
                        }

                        iBannerLoadListener.onBannerLoadSuccess(bannerList);
                    }
                }).addOnFailureListener(e -> iBannerLoadListener.onBannerLoadFailed(e.getMessage()));
    }

    private void setUserInformation() {
        //binding.llUserInfo.setVisibility(View.VISIBLE);
        binding.tvUserName.setText(new StringBuilder("Hi! ").append(Common.currentUser.getName()).toString());
    }

    @Override
    public void onBannerLoadSuccess(List<LookBook> bannerList) {
        binding.bannerSlider.setInterval(5000);
        binding.bannerSlider.setAdapter(new HomeSliderAdapter(bannerList));
    }

    @Override
    public void onBannerLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLookBookLoadSuccess(List<LookBook> lookBookList) {
        binding.recyclerLookBook.setHasFixedSize(true);
        binding.recyclerLookBook.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerLookBook.setAdapter(new ServerLookBookAdapter(getActivity(), lookBookList));
    }

    @Override
    public void onLookBookLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookingInfoLoadEmpty() {
        binding.cardViewBookingInfo.setVisibility(View.GONE);
    }

    @Override
    public void onBookingInfoLoadSuccess(BookingInformation bookingInformation, String documentId) {
        Common.currentBookingInfo = bookingInformation;
        Common.currentBookingId = documentId;

        binding.tvSalonAddress.setText(bookingInformation.getSalonAddress());
        binding.tvTime.setText(bookingInformation.getTime());
        binding.tvSalonBarber.setText(bookingInformation.getBarberName());

        String timeRemain = DateUtils.getRelativeTimeSpanString(bookingInformation.getTimestamp().toDate().getTime()
                , Calendar.getInstance().getTimeInMillis(), 0).toString();
        binding.tvTimeRemain.setText(timeRemain);

        getSelectedService();

        binding.cardViewBookingInfo.setVisibility(View.VISIBLE);
    }

    private void getSelectedService() {
        ///AllSalon/Florida/Branch/0n7ikrtgQXW4EXhuJ0qy/Services
        FirebaseFirestore.getInstance().collection("AllSalon")
                .document(Common.currentBookingInfo.getCityBooking())
                .collection("Branch")
                .document(Common.currentBookingInfo.getSalonId())
                .collection("Services")
                .whereEqualTo("uid", Common.currentBookingInfo.getBarberServiceList().get(0))
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            BarberService barberService = documentSnapshot.toObject(BarberService.class);
                            binding.tvService.setText(barberService.getName());
                        }

                    }

                });
    }

    @Override
    public void onBookingInfoLoadFail(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBookingInfoChange() {
        startActivity(new Intent(getActivity(), BookingActivity.class));
    }


    @Override
    public void onDestroy() {
        if (userBookingListener != null) {
            userBookingListener.remove();
        }
        super.onDestroy();
    }

    @Override
    public void onNotificationCountSuccess(int count) {
        if (count != 0) {
            binding.tvNotificationBadge.setVisibility(View.VISIBLE);
            if (count <= 9)
                binding.tvNotificationBadge.setText(String.valueOf(count));
            else
                binding.tvNotificationBadge.setText("9+");
        } else {
            binding.tvNotificationBadge.setVisibility(View.INVISIBLE);
        }
    }
}
