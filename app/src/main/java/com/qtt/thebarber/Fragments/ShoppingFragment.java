package com.qtt.thebarber.Fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.qtt.thebarber.Adapter.MyShoppingItemAdapter;
import com.qtt.thebarber.CartActivity;
import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.Common.SpacesItemDecoration;
import com.qtt.thebarber.Database.CartDataSource;
import com.qtt.thebarber.Database.CartDatabase;
import com.qtt.thebarber.Database.LocalCartDataSource;
import com.qtt.thebarber.EventBus.CountCartEvent;
import com.qtt.thebarber.Interface.IShoppingItemsLoadListener;
import com.qtt.thebarber.Model.ShoppingItem;
import com.qtt.thebarber.R;
import com.qtt.thebarber.databinding.FragmentShoppingBinding;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingFragment extends Fragment implements IShoppingItemsLoadListener {

    CollectionReference shoppingItemRef;
    MyShoppingItemAdapter myShoppingItemAdapter;

    IShoppingItemsLoadListener iShoppingItemsLoadListener;
    FragmentShoppingBinding binding;
    CartDataSource cartDataSource;


    void chipWaxClick() {
        setChipSelected(binding.chipWax);
        loadShoppingItems(binding.chipWax.getText().toString());
    }


    void chipSprayClick() {
        setChipSelected(binding.chipSpray);
        loadShoppingItems(binding.chipSpray.getText().toString());
    }


    void chipHairCareClick() {
        setChipSelected(binding.chipHairCare);
    }

    void chipBodyCareClick() {
        setChipSelected(binding.chipBodyCare);
    }

    void chipColorClick() { setChipSelected(binding.chipColor);}

    private void setChipSelected(Chip chipWax) {
        for (int i = 0; i < binding.chipGroup.getChildCount(); i++) {
            Chip chipItem = (Chip) binding.chipGroup.getChildAt(i);
            if (chipItem.getId() != chipWax.getId()) {
                chipItem.setChipBackgroundColorResource(R.color.colorBackground);

                chipItem.setTextColor(getResources().getColor(R.color.colorPrimary));
            } else {
                chipItem.setChipBackgroundColorResource(R.color.colorPrimary);
                chipItem.setTextColor(getResources().getColor(android.R.color.white));
            }
        }
    }

    private void loadShoppingItems(String item) {
        shoppingItemRef = FirebaseFirestore.getInstance()
                .collection("Shopping")
                .document(item)
                .collection("Items");

        shoppingItemRef.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<ShoppingItem> shoppingItems = new ArrayList<>();

                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                ShoppingItem shoppingItem = queryDocumentSnapshot.toObject(ShoppingItem.class);
                                shoppingItem.setId(queryDocumentSnapshot.getId());
                                shoppingItems.add(shoppingItem);
                            }

                            iShoppingItemsLoadListener.onShoppingItemsLoadSuccess(shoppingItems);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iShoppingItemsLoadListener.onShoppingItemsLoadFailed(e.getMessage());
            }
        });
    }

    public ShoppingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentShoppingBinding.inflate(inflater, container, false);
        View view =  binding.getRoot();

        iShoppingItemsLoadListener = this;
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());
        //Default load
        loadShoppingItems(binding.chipWax.getText().toString());

        initView();

        return view;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void countCartItem(CountCartEvent event) {
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

    private void initView() {
        binding.recyclerItems.setHasFixedSize(true);
        binding.recyclerItems.setLayoutManager(new GridLayoutManager(getContext(), 2));
        binding.recyclerItems.addItemDecoration(new SpacesItemDecoration(8));

        binding.chipBodyCare.setOnClickListener(v -> chipBodyCareClick());
        binding.chipColor.setOnClickListener(v -> chipColorClick());
        binding.chipHairCare.setOnClickListener(v -> chipHairCareClick());
        binding.chipWax.setOnClickListener(v -> chipWaxClick());
        binding.chipSpray.setOnClickListener(v -> chipSprayClick());

        binding.layoutCart.setOnClickListener(v -> startActivity(new Intent(getActivity(), CartActivity.class)));
    }

    @Override
    public void onShoppingItemsLoadSuccess(List<ShoppingItem> shoppingItemList) {
         myShoppingItemAdapter = new MyShoppingItemAdapter(getContext(), shoppingItemList);
        binding.recyclerItems.setAdapter(myShoppingItemAdapter);
    }

    @Override
    public void onShoppingItemsLoadFailed(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
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

        if (myShoppingItemAdapter != null) {
            myShoppingItemAdapter.onDestroy();
        }
    }
}
