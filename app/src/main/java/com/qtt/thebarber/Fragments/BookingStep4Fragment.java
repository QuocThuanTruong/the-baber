package com.qtt.thebarber.Fragments;


import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.Database.CartDataSource;
import com.qtt.thebarber.Database.CartDatabase;
import com.qtt.thebarber.Database.LocalCartDataSource;
import com.qtt.thebarber.EventBus.ClearCartEvent;
import com.qtt.thebarber.EventBus.ConfirmBookingEvent;
import com.qtt.thebarber.Interface.INotificationSendListener;
import com.qtt.thebarber.Model.BarberService;
import com.qtt.thebarber.Model.BookingInformation;
import com.qtt.thebarber.Model.FCMSendData;
import com.qtt.thebarber.Model.MyNotification;
import com.qtt.thebarber.Model.MyToken;
import com.qtt.thebarber.R;
import com.qtt.thebarber.Retrofit.IFCMApi;
import com.qtt.thebarber.Retrofit.RetrofitClient;
import com.qtt.thebarber.databinding.FragmentBookingStep4Binding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class BookingStep4Fragment extends Fragment implements INotificationSendListener {

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    CartDataSource cartDataSource;
    FragmentBookingStep4Binding binding;
    Unbinder unbinder;
    SimpleDateFormat simpleDateFormat;

    IFCMApi ifcmApi;

    AlertDialog dialog;

    INotificationSendListener iNotificationSendListener;


    void confirmBooking() {
        dialog.show();

       getAllCart();
    }

    private void getAllCart() {
        //DatabaseUtils.getAllItemFromCart(cartDatabase, this);
        compositeDisposable.add(cartDataSource.getAllItemFromCart(Common.currentUser.getPhoneNumber())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    final BookingInformation bookingInformation = new BookingInformation();

                    //use timestamp to filter all days > today
                    String startTime = Common.convertTimeSlotToString(Common.currentTimeSlot);
                    String[] convertTime = startTime.split("-"); //Ex: hh:mm - hh:mm
                    String[] startConvertTime = convertTime[0].split(":");
                    int startHourInt = Integer.parseInt(startConvertTime[0].trim());
                    int startMinInt = Integer.parseInt(startConvertTime[1].trim());

                    Calendar bookingDateWithOurHouse = Calendar.getInstance();
                    bookingDateWithOurHouse.setTimeInMillis(Common.bookingDate.getTimeInMillis());
                    bookingDateWithOurHouse.set(Calendar.HOUR_OF_DAY, startHourInt);
                    bookingDateWithOurHouse.set(Calendar.MINUTE, startMinInt);

                    //Toast.makeText(getActivity(), bookingDateWithOurHouse.getTime().toString(), Toast.LENGTH_SHORT).show();

                    Timestamp timestamp = new Timestamp(bookingDateWithOurHouse.getTime());
                    bookingInformation.setTimestamp(timestamp);

                    bookingInformation.setDone(false);

                    bookingInformation.setCityBooking(Common.cityName);
                    bookingInformation.setBarberId(Common.currentBarber.getBarberId());
                    bookingInformation.setBarberName(Common.currentBarber.getName());

                    bookingInformation.setCustomerName(Common.currentUser.getName());
                    bookingInformation.setCustomerPhone(Common.currentUser.getPhoneNumber());

                    bookingInformation.setSalonName(Common.currentSalon.getName());
                    bookingInformation.setSalonAddress(Common.currentSalon.getAddress());
                    bookingInformation.setSalonId(Common.currentSalon.getId());

                    bookingInformation.setTime(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot)).append(" at ")
                            .append(simpleDateFormat.format(Common.bookingDate.getTime())).toString());
                    bookingInformation.setTimeSlot(Common.currentTimeSlot);
                    bookingInformation.setCartItemList(cartItems);


                    List<String> barberServiceList = new ArrayList<>();
                    barberServiceList.add(Common.selectedService.getUid());
                    bookingInformation.setBarberServiceList(barberServiceList);

                    ///AllSalon/Florida/Branch/0n7ikrtgQXW4EXhuJ0qy/Barbers/Nsa4hBFukd8UZYMiRe5y/27_03_2021/13
                    DocumentReference bookingDoc = FirebaseFirestore.getInstance()
                            .collection("AllSalon")
                            .document(Common.cityName)
                            .collection("Branch")
                            .document(Common.currentSalon.getId())
                            .collection("Barbers")
                            .document(Common.currentBarber.getBarberId())
                            .collection(Common.simpleDateFormat.format(Common.bookingDate.getTime()))
                            .document(String.valueOf(Common.currentTimeSlot));

                    Log.d("Booking", "getAllCart: " +bookingInformation.getCartItemList().size());
                    Log.d("Booking", "getAllCart: " +bookingInformation.getTimeSlot());
                    Log.d("Booking", "getAllCart: " +bookingInformation.getTime());

                    bookingDoc.set(bookingInformation)
                            .addOnCompleteListener(task -> {
                                addUserBooking(bookingInformation);
                            }).
                            addOnFailureListener(e -> Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show());
                }, throwable -> {
                    Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }


    private void addUserBooking(final BookingInformation bookingInformation) {

        final CollectionReference userBooking = FirebaseFirestore.getInstance()
                .collection("User")
                .document(Common.currentUser.getPhoneNumber())
                .collection("Booking");

        Calendar today = Calendar.getInstance();
        today.add(Calendar.DATE, 0);
        today.add(Calendar.HOUR_OF_DAY, 0);
        today.add(Calendar.MINUTE, 0);

        Timestamp todayTimeStamp = new Timestamp(today.getTime());

        userBooking.whereGreaterThanOrEqualTo("timestamp", todayTimeStamp)
                .whereEqualTo("done", false)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.getResult().isEmpty()) {
                        userBooking.document()
                                .set(bookingInformation)
                                .addOnSuccessListener(aVoid -> {
                                    //Create notification
                                    final MyNotification myNotification = new MyNotification();
                                    myNotification.setTitle(Common.selectedService.getName());
                                    myNotification.setContent(new StringBuilder("You have new booking at ").append(Common.convertTimeSlotToString(Common.currentTimeSlot))
                                            .append(" on ")
                                            .append(simpleDateFormat.format(Common.bookingDate.getTime())).append(" from user: ").append(Common.currentUser.getName()).toString());
                                    myNotification.setUid(UUID.randomUUID().toString());
                                    myNotification.setRead(false);
                                    myNotification.setServerTimeStamp(Timestamp.now());
                                    myNotification.setAvatar(Common.selectedService.getAvatar());

                                    //
                                    Paper.init(getContext());
                                    Paper.book().write("UUID_NOTIFICATION", myNotification.getUid());

                                    FirebaseFirestore.getInstance()
                                            .collection("AllSalon")
                                            .document(Common.cityName)
                                            .collection("Branch")
                                            .document(Common.currentSalon.getId())
                                            .collection("Barbers")
                                            .document(Common.currentBarber.getBarberId())
                                            .collection("Notifications")
                                            .document(myNotification.getUid())
                                            .set(myNotification)
                                            .addOnSuccessListener(aVoid1 -> FirebaseFirestore.getInstance()
                                                    .collection("Tokens")
                                                    .document(Common.currentBarber.getBarberId())
                                                    //.whereEqualTo("userPhone", Common.currentBarber.getName())
                                                   // .limit(1)
                                                    .get()

                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            MyToken myToken = task1.getResult().toObject(MyToken.class);
                                                           // for (DocumentSnapshot documentSnapshot : task.getResult())

                                                            Log.d("AAAAA", myToken.getToken());

                                                            FCMSendData sendRequest = new FCMSendData();
                                                            Map<String, String> dataSend = new HashMap<>();
                                                            dataSend.put(Common.TITLE_KEY, "New Booking");
                                                            dataSend.put(Common.CONTENT_KEY, "You have a new booking: " + Common.selectedService.getName() +", from user: " + Common.currentUser.getName());

                                                            sendRequest.setData(dataSend);
                                                            sendRequest.setTo(myToken.getToken());

                                                            compositeDisposable.add(ifcmApi.sendNotification(sendRequest)
                                                                    .subscribeOn(Schedulers.io())
                                                                    .observeOn(AndroidSchedulers.mainThread()) //mainThread()
                                                                    .subscribe(fcmResponse -> {
                                                                        dialog.dismiss();

                                                                        iNotificationSendListener.onNotificationSend(true);
                                                                    }));
                                                        }
                                                    }));
                                }).addOnFailureListener(e -> {
                                    if (dialog.isShowing())
                                        dialog.dismiss();
                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        if (dialog.isShowing())
                            dialog.dismiss();
                        getActivity().finish();
                        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addToCalendar(Calendar bookingDate, String startDate) {
        String[] convertTime = startDate.split("-"); //Ex: hh:mm - hh:mm
        String[] startConvertTime = convertTime[0].split(":");
        int startHourInt = Integer.parseInt(startConvertTime[0].trim());
        int startMinInt = Integer.parseInt(startConvertTime[1].trim());

        String[] endConvertTime = convertTime[1].split(":");
        int endHourInt = Integer.parseInt(endConvertTime[0].trim());
        int endMinInt = Integer.parseInt(endConvertTime[1].trim());

        Calendar startEvent = Calendar.getInstance();
        startEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        startEvent.set(Calendar.HOUR_OF_DAY, startHourInt);
        startEvent.set(Calendar.MINUTE, startMinInt);

        Calendar endEvent = Calendar.getInstance();
        endEvent.setTimeInMillis(bookingDate.getTimeInMillis());
        endEvent.set(Calendar.HOUR_OF_DAY, endHourInt);
        endEvent.set(Calendar.MINUTE, endMinInt);

        //convert to format string
        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String startEventTime = calendarDateFormat.format(startEvent.getTime());
        String endEventTime = calendarDateFormat.format(endEvent.getTime());

        addToDeviceCalendar(startEventTime, endEventTime, "Haircut Booking",
                new StringBuilder("Haircut from ").append(startDate).append(" with ")
                        .append(Common.currentBarber.getName())
                        .append(" at ").append(Common.currentSalon.getName()).toString(),
                new StringBuilder("Address: ").append(Common.currentSalon.getAddress()).toString());
    }

    private void addToDeviceCalendar(String startEventTime, String endEventTime, String title,
                                     String description, String location) {
        SimpleDateFormat calendarDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        try {
            Date start = calendarDateFormat.parse(startEventTime);
            Date end = calendarDateFormat.parse(endEventTime);

            ContentValues eventValue = new ContentValues();
            eventValue.put(CalendarContract.Events.CALENDAR_ID, new Random().nextInt());
            eventValue.put(CalendarContract.Events.TITLE, title);
            eventValue.put(CalendarContract.Events.DESCRIPTION, description);
            eventValue.put(CalendarContract.Events.EVENT_LOCATION, location);
            eventValue.put(CalendarContract.Events.DTSTART, start.getTime());
            eventValue.put(CalendarContract.Events.DTEND, end.getTime());
            eventValue.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());

            Uri calendarUri;
            if (Build.VERSION.SDK_INT >= 8)
                calendarUri= Uri.parse("content://com.android.calendar/events");
            else
                calendarUri= Uri.parse("content://calendar/events");

            Uri saveUri = getActivity().getContentResolver().insert(calendarUri, eventValue);
            Paper.init(getContext());
            Paper.book().write(Common.EVENT_URI_SAVE, saveUri.toString()); //save uri of the event just added

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String getCalendar(Context context) {
        //get ID of Gmail's Calendar
        String gmailIDCalendar = "";
        String projection[] = {"_id", "calendar_displayName"};
        Uri calendarUri = Uri.parse("content://com.android.calendar/calendars");

        ContentResolver contentResolver = context.getContentResolver();
        Cursor managedCursor = contentResolver.query(calendarUri, projection, null, null, null);

        if (managedCursor.moveToFirst()) {
            String calName;
            int idCol = managedCursor.getColumnIndex(projection[0]);
            int nameCol = managedCursor.getColumnIndex(projection[1]);

            do {
                calName = managedCursor.getString(nameCol);
                if (calName.contains("@gmail.com")) {
                    gmailIDCalendar = managedCursor.getString(idCol);
                    break;
                }
            } while (managedCursor.moveToNext());
            managedCursor.close();
        }
        return gmailIDCalendar;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void confirmBookingReceiver(ConfirmBookingEvent event){
        if (event.getConfirm()) {
            setData();
        }
    }

    private void setData() {
        binding.tvBookingTime.setText(new StringBuilder(Common.convertTimeSlotToString(Common.currentTimeSlot))
                .append(" on ")
                .append(simpleDateFormat.format(Common.bookingDate.getTime())).toString());
        binding.tvBarber.setText(Common.currentBarber.getName());

        binding.tvSalonName.setText(Common.currentSalon.getName());
        binding.tvSalonAddress.setText(Common.currentSalon.getAddress());
        binding.tvSalonPhone.setText(Common.currentSalon.getPhone());
        binding.tvSalonWebsite.setText(Common.currentSalon.getWebsite());
        binding.tvSalonOpenHours.setText(Common.currentSalon.getOpenHours());
        binding.tvService.setText(Common.selectedService.getName());
    }

    private static BookingStep4Fragment instance;

    public static BookingStep4Fragment getInstance() {
        if (instance == null)
            instance = new BookingStep4Fragment();
        return instance;
    }

    private BookingStep4Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ifcmApi = RetrofitClient.getInstance().create(IFCMApi.class);

        iNotificationSendListener = this;

        simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());
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
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookingStep4Binding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.btnConfirm.setOnClickListener(v -> confirmBooking());

        return view;
    }

    @Override
    public void onNotificationSend(boolean isSent) {
        if (isSent) {
            addToCalendar(Common.bookingDate, Common.convertTimeSlotToString(Common.currentTimeSlot));
            Toast.makeText(getActivity(), "Success!", Toast.LENGTH_SHORT).show();
            getActivity().finish();
            clearCart();
            compositeDisposable.clear();
        }
    }

    private void clearCart() {
        cartDataSource.clearCart(Common.currentUser.getPhoneNumber())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {

                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
