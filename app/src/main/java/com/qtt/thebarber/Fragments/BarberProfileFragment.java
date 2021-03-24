package com.qtt.thebarber.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.qtt.thebarber.Adapter.MyLookBookAdapter;
import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.Common.SpacesItemDecoration;
import com.qtt.thebarber.Model.LookBook;
import com.qtt.thebarber.R;
import com.qtt.thebarber.databinding.FragmentBarberProfileBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class BarberProfileFragment extends BottomSheetDialogFragment {

    FragmentBarberProfileBinding binding;


    private  static BarberProfileFragment instance;

    public static BarberProfileFragment getInstance() {
        if (instance == null)
            instance = new BarberProfileFragment();
        return instance;
    }

    private BarberProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBarberProfileBinding.inflate(getLayoutInflater());
        View itemView =  binding.getRoot();

        initView();

        return itemView;
    }

    private void initView() {
        binding.tvBarberName.setText(Common.currentBarber.getName());
        binding.tvBarberAddress.setText(Common.currentBarber.getAddress());
        binding.tvBarberPhone.setText(Common.currentBarber.getPhone());

        binding.recyclerLookBook.setHasFixedSize(true);
        binding.recyclerLookBook.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false));
        binding.recyclerLookBook.addItemDecoration(new SpacesItemDecoration(16));

        if (!Common.currentBarber.getAvatar().isEmpty()) {
            Picasso.get().load(Common.currentBarber.getAvatar()).error(R.drawable.user_avatar).into(binding.imgUserAvatar);
        }


        FirebaseFirestore.getInstance()
                .collection("AllSalon")
                .document(Common.cityName)
                .collection("Branch")
                .document(Common.currentSalon.getId())
                .collection("Barbers")
                .document(Common.currentBarber.getBarberId())
                .collection("LookBook")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<LookBook> lookBookList = new ArrayList<>();

                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            LookBook lookBook = documentSnapshot.toObject(LookBook.class);
                            lookBookList.add(lookBook);
                        }

                        MyLookBookAdapter myLookBookAdapter = new MyLookBookAdapter(getContext(), lookBookList);
                        binding.recyclerLookBook.setAdapter(myLookBookAdapter);
                    }


                }).addOnFailureListener(e -> Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}