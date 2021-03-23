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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.qtt.thebarber.Adapter.MySalonAdapter;
import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.Common.SpacesItemDecoration;
import com.qtt.thebarber.Interface.IAllSalonLoadListener;
import com.qtt.thebarber.Interface.IBranchLoadListener;
import com.qtt.thebarber.Model.Salon;
import com.qtt.thebarber.R;
import com.qtt.thebarber.databinding.FragmentBookingStep1Binding;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookingStep1Fragment extends Fragment implements IAllSalonLoadListener, IBranchLoadListener {

    //Ref
    CollectionReference allSalonRef, branchRef;

    IAllSalonLoadListener iAllSalonLoadListener;
    IBranchLoadListener iBranchLoadListener;

    FragmentBookingStep1Binding binding;

    AlertDialog alertDialog;

    private static BookingStep1Fragment instance;

    public static BookingStep1Fragment getInstance() {
        if (instance == null)
            instance = new BookingStep1Fragment();
        return instance;
    }

    private BookingStep1Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //loadCollection
        allSalonRef = FirebaseFirestore.getInstance().collection("AllSalon");

        iAllSalonLoadListener = this;
        iBranchLoadListener = this;

        alertDialog = new SpotsDialog.Builder().setContext(getActivity()).setCancelable(false).build();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBookingStep1Binding.inflate(inflater, container, false);
        View view =  binding.getRoot();

        initRecyclerView();
        loadAllSalon();

        return view;
    }

    private void initRecyclerView() {
        binding.recyclerSalon.setHasFixedSize(true);
        binding.recyclerSalon.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        binding.recyclerSalon.addItemDecoration(new SpacesItemDecoration(8));
    }

    private void loadAllSalon() {
        allSalonRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> nameList = new ArrayList<>();
                        nameList.add("Please choose an area");

                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult())
                            nameList.add(queryDocumentSnapshot.getId());

                        iAllSalonLoadListener.onAllSalonLoadSuccess(nameList);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iAllSalonLoadListener.onAllSalonLoadFailed(e.getMessage());
            }
        });
    }

    @Override
    public void onAllSalonLoadSuccess(List<String> areaNameList) {
        binding.spinner.setItems(areaNameList);

        binding.spinner.setOnItemSelectedListener((view, position, id, item) -> {
            if (position > 0)
                loadBranchOfArea(item.toString());
            else
                binding.recyclerSalon.setVisibility(View.GONE);
        });
    }

    private void loadBranchOfArea(String areaName) {
        alertDialog.show();

        Common.cityName = areaName;

        branchRef = FirebaseFirestore.getInstance().collection("AllSalon")
                .document(areaName)
                .collection("Branch");

        branchRef.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Salon> salons = new ArrayList<>();

                        for (QueryDocumentSnapshot salonSnapShot : task.getResult()) {
                            Salon salon = salonSnapShot.toObject(Salon.class);
                            salon.setId(salonSnapShot.getId());
                            salons.add(salon);
                        }

                        iBranchLoadListener.onBranchLoadSuccess(salons);
                    }
                }).addOnFailureListener(e -> iBranchLoadListener.onBranchLoadFailed(e.getMessage()));
    }

    @Override
    public void onAllSalonLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBranchLoadSuccess(List<Salon> salonList) {
        MySalonAdapter mySalonAdapter = new MySalonAdapter(getContext(), salonList);
        binding.recyclerSalon.setAdapter(mySalonAdapter);
        binding.recyclerSalon.setVisibility(View.VISIBLE);

        alertDialog.dismiss();
    }

    @Override
    public void onBranchLoadFailed(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        alertDialog.dismiss();
    }
}
