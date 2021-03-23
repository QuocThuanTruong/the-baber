package com.qtt.thebarber;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.Fragments.HomeFragment;
import com.qtt.thebarber.Fragments.ProfileFragment;
import com.qtt.thebarber.Fragments.ShoppingFragment;
import com.qtt.thebarber.Model.User;
import com.qtt.thebarber.databinding.ActivityHomeBinding;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity {

    ActivityHomeBinding binding;

    BottomSheetDialog bottomSheetDialog;
    Bundle mSavedInstanceState;

    CollectionReference userRef;

    AlertDialog alertDialog;

    @Override
    protected void onResume() {
        super.onResume();

        checkRatingDialog();
    }

    private void checkRatingDialog() {
        Paper.init(this);
        String dataSerialized = Paper.book().read(Common.RATING_INFORMATION_KEY, "");

        if (!TextUtils.isEmpty(dataSerialized)) {
            Map<String, String> dataReceived = new Gson().fromJson(dataSerialized, new TypeToken<Map<String, String>>() {
            }.getType());

            if (dataReceived != null) {
                Common.showRatingDialog(HomeActivity.this, dataReceived.get(Common.RATING_STATE_KEY),
                        dataReceived.get(Common.RATING_SALON_ID),
                                dataReceived.get(Common.RATING_SALON_NAME),
                                        dataReceived.get(Common.RATING_BARBER_ID));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(this.getResources().getColor(R.color.colorAccent2));
        }


        userRef = FirebaseFirestore.getInstance().collection("User");
        alertDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();

        mSavedInstanceState = savedInstanceState;

        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener()
        {
            Fragment fragment = null;
            @Override
            public boolean onNavigationItemSelected (@NonNull MenuItem menuItem){
                if (menuItem.getItemId() == R.id.action_home) {
                    fragment = new HomeFragment();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(HomeActivity.this.getResources().getColor(R.color.colorAccent2));
                    }
                } else if (menuItem.getItemId() == R.id.action_shopping) {
                    fragment = new ShoppingFragment();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(HomeActivity.this.getResources().getColor(R.color.colorBackground));
                    }

                } else if (menuItem.getItemId() == R.id.action_profile) {
                    fragment = new ProfileFragment();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        getWindow().setStatusBarColor(HomeActivity.this.getResources().getColor(R.color.colorAccent2));
                    }
                }

                return loadFragment(fragment);
            }
        });

        if (getIntent() != null) {
            boolean isLogin = getIntent().getBooleanExtra(Common.IS_LOGIN, false);
            if (isLogin) {
                alertDialog.show();
               final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Paper.init(HomeActivity.this);
                Paper.book().write(Common.LOGGED_KEY, user.getPhoneNumber());

                DocumentReference currentUser = userRef.document(user.getPhoneNumber());
                currentUser.get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot userSnapShot = task.getResult();

                                    if (!userSnapShot.exists()) {
                                        showUpdateDialog(user.getPhoneNumber().toString());

                                    } else {
                                        Common.currentUser = userSnapShot.toObject(User.class);
                                        binding.bottomNavigation.setSelectedItemId(R.id.action_home);
                                    }

                                    if (alertDialog.isShowing())
                                        alertDialog.dismiss();

                                }
                            }
                        });
            }
        }

}

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null ) {
           getSupportFragmentManager().beginTransaction().replace(R.id.fg_container, fragment)
                    .commitAllowingStateLoss();
            return true;
        }
        return false;
    }



    private void showUpdateDialog(final String phoneNumber) {

        //init dialog
        bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        bottomSheetDialog.setCancelable(false);

        View sheetView = getLayoutInflater().inflate(R.layout.layout_update_information, null);

        Button btnUpdate = sheetView.findViewById(R.id.btn_update);
        final TextInputEditText edtName = sheetView.findViewById(R.id.edt_name);
        final TextInputEditText edtAddress = sheetView.findViewById(R.id.edt_address);

        btnUpdate.setOnClickListener(v -> {
            if (!alertDialog.isShowing())
                alertDialog.show();

            final User user = new User(edtName.getText().toString(),
                    edtAddress.getText().toString(), phoneNumber, null);

            userRef.document(phoneNumber)
                    .set(user)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            bottomSheetDialog.dismiss();
                            if (alertDialog.isShowing())
                                alertDialog.dismiss();
                            //Update
                            Common.currentUser = user;
                            binding.bottomNavigation.setSelectedItemId(R.id.action_home);

                            Toast.makeText(HomeActivity.this, "Thank you", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    bottomSheetDialog.dismiss();
                    if (alertDialog.isShowing())
                        alertDialog.dismiss();
                    Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

}
