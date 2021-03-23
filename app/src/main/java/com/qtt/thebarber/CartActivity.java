package com.qtt.thebarber;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.qtt.thebarber.Adapter.MyCartAdapter;
import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.Database.CartDataSource;
import com.qtt.thebarber.Database.CartDatabase;
import com.qtt.thebarber.Database.LocalCartDataSource;
import com.qtt.thebarber.databinding.ActivityCartBinding;
import com.qtt.thebarber.databinding.LayoutCartItemBinding;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CartActivity extends AppCompatActivity {

    CartDataSource cartDataSource;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    ActivityCartBinding binding;

    void onClearCart() {
       clearCart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initView();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(CartActivity.this.getResources().getColor(R.color.colorBackground));
        }

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());

        getAllCart();
    }

    private void sumCart() {
        cartDataSource.sumPrice(Common.currentUser.getPhoneNumber())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Long aLong) {
                        binding.tvTotalPrice.setText(new StringBuilder("$").append(aLong).toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (!e.getMessage().contains("empty")) {
                            Toast.makeText(CartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            binding.tvTotalPrice.setText("");
                        }
                    }
                });
    }

    private void clearCart() {
        cartDataSource.clearCart(Common.currentUser.getPhoneNumber())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NonNull Integer integer) {
                        getAllCart();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (!e.getMessage().contains("empty")) {
                            Toast.makeText(CartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void getAllCart() {
        //DatabaseUtils.getAllItemFromCart(cartDatabase, this);
        compositeDisposable.add(cartDataSource.getAllItemFromCart(Common.currentUser.getPhoneNumber())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    MyCartAdapter myCartAdapter = new MyCartAdapter(cartItems, this);
                    binding.recyclerCart.setAdapter(myCartAdapter);

                    sumCart();
                }, throwable -> {
                    Toast.makeText(CartActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }));
    }

    private void initView() {
        binding.recyclerCart.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        binding.recyclerCart.setLayoutManager(linearLayoutManager);

        binding.btnClearCart.setOnClickListener(v -> clearCart());
        binding.imgBack.setOnClickListener(v -> {
            finish();
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
