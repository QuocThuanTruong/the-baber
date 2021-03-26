package com.qtt.thebarber.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.qtt.thebarber.Adapter.MyTimeSlotAdapter;
import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.Common.SpacesItemDecoration;
import com.qtt.thebarber.EventBus.LoadTimeSlotEvent;
import com.qtt.thebarber.Interface.ITimeSlotLoadListener;
import com.qtt.thebarber.Model.TimeSlot;
import com.qtt.thebarber.R;
import com.qtt.thebarber.databinding.FragmentBookingStep3Binding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import dmax.dialog.SpotsDialog;

public class BookingStep3Fragment extends Fragment implements ITimeSlotLoadListener {

    SimpleDateFormat simpleDateFormat;
    ITimeSlotLoadListener iTimeSlotLoadListener;
    DocumentReference barberDoc;
    AlertDialog alertDialog;
    FragmentBookingStep3Binding binding;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayTimeSlot(LoadTimeSlotEvent event){
        if (event.getLoaded()) {
            Calendar date = Calendar.getInstance();
            date.add(Calendar.DATE, 0);
            loadAvailableTimeSlotOfBarber(Common.currentBarber.getBarberId(), simpleDateFormat.format(date.getTime()));
        }

    }

    private void loadAvailableTimeSlotOfBarber(String barberId, final String date) {
        alertDialog.show();

        // /AllSalon/Florida/Branch/0n7ikrtgQXW4EXhuJ0qy/Barbers/Nsa4hBFukd8UZYMiRe5y
        barberDoc = FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(Common.cityName)
                .collection("Branch")
                .document(Common.currentSalon.getId())
                .collection("Barbers")
                .document(Common.currentBarber.getBarberId());

        //Check info of barber
        barberDoc.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot barberSnapShot = task.getResult();
                            if (barberSnapShot.exists()) {
                                CollectionReference dateRef = FirebaseFirestore.getInstance()
                                        .collection("AllSalon")
                                        .document(Common.cityName)
                                        .collection("Branch")
                                        .document(Common.currentSalon.getId())
                                        .collection("Barbers")
                                        .document(Common.currentBarber.getBarberId())
                                        .collection(date);

                                dateRef.get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    QuerySnapshot querySnapshot = task.getResult();

                                                    if (querySnapshot.isEmpty()) {
                                                        iTimeSlotLoadListener.onTimeSlotLoadEmpty(); //load default time slot list
                                                    } else {
                                                        List<TimeSlot> timeSlotList = new ArrayList<>();
                                                        for (QueryDocumentSnapshot timeSlotSnapShot : querySnapshot) {
                                                            TimeSlot slot = timeSlotSnapShot.toObject(TimeSlot.class);
                                                            timeSlotList.add(slot);
                                                        }

                                                        iTimeSlotLoadListener.onTimeSlotLoadSuccess(timeSlotList);
                                                    }
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        iTimeSlotLoadListener.onTimeSlotLoadFailed(e.getMessage());
                                    }
                                });
                            }
                        }
                    }
                });
    }

    private static BookingStep3Fragment instance;

    public static BookingStep3Fragment getInstance() {
        if (instance == null)
            instance = new BookingStep3Fragment();
        return instance;
    }

    private BookingStep3Fragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        iTimeSlotLoadListener = this;

        alertDialog = new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();

        simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
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
        binding = FragmentBookingStep3Binding.inflate(inflater, container, false);
        View view =  binding.getRoot();

        initView(view);

        return view;
    }

    private void initView(View view) {
        binding.recyclerTimeSlot.setHasFixedSize(true);
        binding.recyclerTimeSlot.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.recyclerTimeSlot.addItemDecoration(new SpacesItemDecoration(8));

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE, 0);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 4); //current date + 2 day

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.calendar_view)
                .range(startDate, endDate)
                .datesNumberOnScreen(5)
                .mode(HorizontalCalendar.Mode.DAYS)
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if (date.getTimeInMillis() != Common.bookingDate.getTimeInMillis()) {
                    Common.bookingDate = date;
                    loadAvailableTimeSlotOfBarber(Common.currentBarber.getBarberId(), simpleDateFormat.format(date.getTime()));
                }
            }

            @Override
            public void onCalendarScroll(HorizontalCalendarView calendarView, int dx, int dy) {

            }
        });

    }

    @Override
    public void onTimeSlotLoadSuccess(List<TimeSlot> timeSlotList) {
        MyTimeSlotAdapter myTimeSlotAdapter = new MyTimeSlotAdapter(getContext(), timeSlotList);
        binding.recyclerTimeSlot.setAdapter(myTimeSlotAdapter);

        alertDialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();

        alertDialog.dismiss();
    }

    @Override
    public void onTimeSlotLoadEmpty() {
        MyTimeSlotAdapter myTimeSlotAdapter = new MyTimeSlotAdapter(getContext());
        binding.recyclerTimeSlot.setAdapter(myTimeSlotAdapter);

        alertDialog.dismiss();
    }
}
