package com.qtt.thebarber.Fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.MainActivity;
import com.qtt.thebarber.R;
import com.qtt.thebarber.UpdateProfileActivity;
import com.qtt.thebarber.databinding.FragmentProfileBinding;
import com.squareup.picasso.Picasso;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        initView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Common.currentUser.getAvatar().isEmpty()) {
            binding.imgUserAvatar.setImageDrawable(getContext().getResources().getDrawable(R.drawable.user_avatar));
        } else {
            Picasso.get().load(Common.currentUser.getAvatar()).error(R.drawable.user_avatar).into(binding.imgUserAvatar);
        }

        binding.tvUserName.setText(Common.currentUser.getName());
        binding.tvUserPhone.setText(Common.currentUser.getAddress());
    }

    private void initView() {
        if (Common.currentUser.getAvatar().isEmpty()) {
            binding.imgUserAvatar.setImageDrawable(getContext().getResources().getDrawable(R.drawable.user_avatar));
        } else {
            Picasso.get().load(Common.currentUser.getAvatar()).error(R.drawable.user_avatar).into(binding.imgUserAvatar);
        }

        binding.tvUserName.setText(Common.currentUser.getName());
        binding.tvUserPhone.setText(Common.currentUser.getAddress());

        binding.btnEdtProfile.setOnClickListener(v -> startActivity(new Intent(getActivity(), UpdateProfileActivity.class)));

        binding.btnLogOut.setOnClickListener(v -> logOut());
    }

    void logOut() {
        AlertDialog.Builder builder  = new AlertDialog.Builder(getContext());
        builder.setTitle("Sign out")
                .setMessage("Do you want really sign out?")
                .setNegativeButton("Cancel", (dialoginterface, i) -> {
                    dialoginterface.dismiss();
                })
                .setPositiveButton("OK", (dialog1, which) -> {
                    FirebaseAuth.getInstance().signOut();

                    Common.currentBarber = null;
                    Common.currentBookingId = "";
                    Common.currentBookingInfo = null;
                    Common.currentSalon = null;
                    Common.currentTimeSlot = -1;
                    Common.currentUser = null;

                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}