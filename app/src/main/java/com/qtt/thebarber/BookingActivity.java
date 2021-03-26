package com.qtt.thebarber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.qtt.thebarber.Adapter.MyViewPagerAdapter;
import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.Common.NonSwipeableViewPager;
import com.qtt.thebarber.EventBus.BarberDoneEvent;
import com.qtt.thebarber.EventBus.ConfirmBookingEvent;
import com.qtt.thebarber.EventBus.EnableNextButtonEvent;
import com.qtt.thebarber.EventBus.LoadTimeSlotEvent;
import com.qtt.thebarber.EventBus.ServicesLoadDoneEvent;
import com.qtt.thebarber.EventBus.ShowBarberProfileEvent;
import com.qtt.thebarber.Fragments.BarberProfileFragment;
import com.qtt.thebarber.Interface.IBarberServicesLoadListener;
import com.qtt.thebarber.Model.Barber;
import com.qtt.thebarber.Model.BarberService;
import com.qtt.thebarber.databinding.ActivityBookingBinding;
import com.qtt.thebarber.databinding.ActivityHomeBinding;
import com.shuhart.stepview.StepView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

public class BookingActivity extends AppCompatActivity implements IBarberServicesLoadListener {

    AlertDialog alertDialog;
    CollectionReference barbersRef;
    ActivityBookingBinding binding;
    IBarberServicesLoadListener iBarberServicesLoadListener;

    void previousStep() {
        if (Common.currentStep > 0 || Common.currentStep == 4) {
            Common.currentStep--;
            binding.viewPager.setCurrentItem(Common.currentStep);
            if (Common.currentStep < 4) {
                binding.btnNextStep.setEnabled(true);
                setColorButton();
            }
        }
    }

    void nextStep() {
        if (Common.currentStep == 0 || Common.currentStep < 4) {
            Common.currentStep++;
            Log.d("BService", "nextStep: " + Common.currentStep);
             if (Common.currentStep == 1)
                loadBarberServices();
            else if (Common.currentStep == 2)
                loadBarbersBySalon(Common.currentSalon.getId());
            else if (Common.currentStep == 3)
                loadSlotTimeOfBarber(Common.currentBarber.getBarberId());
            else if (Common.currentStep == 4)
                if (Common.currentTimeSlot != -1)
                    confirmBooking();

        }

        binding.viewPager.setCurrentItem(Common.currentStep);
    }

    private void loadBarberServices() {
        ///AllSalon/Florida/Branch/0n7ikrtgQXW4EXhuJ0qy/Services/
        alertDialog.show();

        FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(Common.cityName)
                .collection("Branch")
                .document(Common.currentSalon.getId())
                .collection("Services")
                .get()
                .addOnFailureListener(e -> iBarberServicesLoadListener.onBarberServicesLoadFailed(e.getMessage()))
                .addOnCompleteListener(task -> {


                    if (task.isSuccessful()) {
                        List<BarberService> barberServices = new ArrayList<>();
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            BarberService barberServices1 = documentSnapshot.toObject(BarberService.class);
                            barberServices.add(barberServices1);
                        }
                        Log.d("BService", "loadBarberServices: " + barberServices.size());
                        iBarberServicesLoadListener.onBarberServicesLoadSuccess(barberServices);
                        alertDialog.dismiss();
                    }
                });
    }

    private void confirmBooking() {
        EventBus.getDefault().post(new ConfirmBookingEvent(true));
    }

    private void loadSlotTimeOfBarber(String barberId) {
        EventBus.getDefault().post(new LoadTimeSlotEvent(true));
    }

    ///AllSalon/Florida/Branch/0n7ikrtgQXW4EXhuJ0qy/Barbers/
    private void loadBarbersBySalon(String id) {
        alertDialog.show();

        if (!TextUtils.isEmpty(Common.cityName)) {
            barbersRef = FirebaseFirestore.getInstance().collection("AllSalon")
                    .document(Common.cityName).collection("Branch")
                    .document(id).collection("Barbers");

            barbersRef.get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<Barber> barberList = new ArrayList<>();

                                for (QueryDocumentSnapshot barberSnapshot : task.getResult()) {
                                    Barber barber = barberSnapshot.toObject(Barber.class);
                                    barber.setPassword(""); //Client app
                                    barber.setBarberId(barberSnapshot.getId());
                                    barberList.add(barber);
                                }

//                                Intent intent = new Intent(Common.KEY_LOAD_BARBERS_DONE);
//                                intent.putParcelableArrayListExtra(Common.KEY_BARBERS_STORED, (ArrayList<? extends Parcelable>) barberList);
//                                localBroadcastManager.sendBroadcast(intent);
                                EventBus.getDefault().post(new BarberDoneEvent(barberList));

                                alertDialog.dismiss();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    alertDialog.dismiss();
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showBarberProfile(ShowBarberProfileEvent event) {
        Common.currentBarber = event.getBarber();
        BarberProfileFragment barberProfileFragment = BarberProfileFragment.getInstance();
        barberProfileFragment.show(getSupportFragmentManager(), "BarberProfile");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void nextButtonReceiver(EnableNextButtonEvent event) {

        int step = event.getStep();

        if (step == 1)
            Common.currentSalon = event.getSalon();
        else if (step == 2)
            Common.selectedService = event.getBarberService();
        else if (step == 3)
            Common.currentBarber = event.getBarber();
        else if (step == 4)
            Common.currentTimeSlot = event.getTimeSlot();


        binding.btnNextStep.setEnabled(true);
        setColorButton();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorBackground));
        }

        alertDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        iBarberServicesLoadListener = this;

        setupStepView();
        setColorButton();

        binding.viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        binding.viewPager.setOffscreenPageLimit(5);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                binding.stepView.go(position, true);

                if (position == 0)
                    binding.btnPreviousStep.setEnabled(false);
                else
                    binding.btnPreviousStep.setEnabled(true);
                if (Common.currentSalon == null || Common.currentBarber == null)
                    binding.btnNextStep.setEnabled(false);

                setColorButton();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        binding.btnNextStep.setOnClickListener(v -> nextStep());
        binding.btnPreviousStep.setOnClickListener(v -> previousStep());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetStaticData();
    }

    private void resetStaticData() {
        Common.bookingDate.add(Calendar.DATE, 0);
        Common.currentSalon = null;
        Common.currentStep = 0;
        Common.currentTimeSlot = -1;
        Common.currentBarber = null;
    }

    private void setColorButton() {
        if (binding.btnNextStep.isEnabled())
            binding.btnNextStep.setBackgroundResource(R.drawable.rounded_button);
        else
            binding.btnNextStep.setBackgroundResource(R.drawable.rounded_button);

        if (binding.btnPreviousStep.isEnabled()) {
            binding.btnPreviousStep.setStrokeColor(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
            binding.btnPreviousStep.setStrokeWidth(5);
            binding.btnPreviousStep.setTextColor(getResources().getColor(R.color.colorPrimary));
        }
        else {
            binding.btnPreviousStep.setStrokeColor(ColorStateList.valueOf(getResources().getColor(android.R.color.darker_gray)));
            binding.btnPreviousStep.setStrokeWidth(5);
            binding.btnPreviousStep.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }
    }

    private void setupStepView() {
        List<String> stepList = new ArrayList<>();

        stepList.add("Salon");
        stepList.add("Service");
        stepList.add("Barber");
        stepList.add("Time");
        stepList.add("Confirm");

        binding.stepView.setSteps(stepList);
    }

    @Override
    public void onBarberServicesLoadSuccess(List<BarberService> barberServicesList) {
        EventBus.getDefault().post(new ServicesLoadDoneEvent(barberServicesList));
    }

    @Override
    public void onBarberServicesLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
