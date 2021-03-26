package com.qtt.thebarber.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.Database.CartDataSource;
import com.qtt.thebarber.Database.CartDatabase;
import com.qtt.thebarber.Database.CartItem;
import com.qtt.thebarber.Database.LocalCartDataSource;
import com.qtt.thebarber.EventBus.CountCartEvent;
import com.qtt.thebarber.Interface.IRecyclerItemSelectedListener;
import com.qtt.thebarber.Model.ShoppingItem;
import com.qtt.thebarber.R;
import com.qtt.thebarber.databinding.LayoutShoppingItemBinding;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MyShoppingItemAdapter extends RecyclerView.Adapter<MyShoppingItemAdapter.MyShoppingItemViewHolder> {

    private Context context;
    private List<ShoppingItem> shoppingItemList;
    private CartDataSource cartDataSource;
    private CompositeDisposable compositeDisposable;
    LayoutShoppingItemBinding binding;

    public MyShoppingItemAdapter(Context context, List<ShoppingItem> shoppingItemList) {
        this.context = context;
        this.shoppingItemList = shoppingItemList;
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
        compositeDisposable = new CompositeDisposable();
    }

    public void onDestroy() {
        compositeDisposable.clear();
    }

    @NonNull
    @Override
    public MyShoppingItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = LayoutShoppingItemBinding.inflate(LayoutInflater.from(context), parent, false);
        View itemView = binding.getRoot();
        return new MyShoppingItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyShoppingItemViewHolder holder, int position) {
        Picasso.get().load(shoppingItemList.get(position).getImage()).error(R.drawable.img_not_found).into(holder.binding.imgShoppingItem);
        holder.binding.tvShoppingItemName.setText(Common.formatShoppingItemName(shoppingItemList.get(position).getName()));
        holder.binding.tvShoppingItemPrice.setText(new StringBuilder("$").append(shoppingItemList.get(position).getPrice()).toString());

        holder.setiRecyclerItemSelectedListener((view, position1) -> {
            CartItem cartItem = new CartItem();
            cartItem.setProductId(shoppingItemList.get(position1).getId());
            cartItem.setProductName(shoppingItemList.get(position1).getName());
            cartItem.setProductImage(shoppingItemList.get(position1).getImage());
            cartItem.setProductPrice(shoppingItemList.get(position1).getPrice());
            cartItem.setUserPhone(Common.currentUser.getPhoneNumber());
            cartItem.setProductQuantity(1);

            //Insert to db
            //DatabaseUtils.insertItemToCart(cartDatabase, cartItem);
            cartDataSource.getProductInCart(cartItem.getProductId(), Common.currentUser.getPhoneNumber())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<CartItem>() {
                        @Override
                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                        }

                        @Override
                        public void onSuccess(@io.reactivex.annotations.NonNull CartItem t) {
                            if (t.equals(cartItem)) {

                                t.setProductQuantity(t.getProductQuantity() + 1);

                                cartDataSource.update(t)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new SingleObserver<Integer>() {
                                            @Override
                                            public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                            }

                                            @Override
                                            public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                                Toast.makeText(context, "Added to Cart!", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });


                            } else {
                                compositeDisposable.add(cartDataSource.insert(cartItem)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            Toast.makeText(context, "Added to Cart!", Toast.LENGTH_SHORT).show();
                                        }, throwable -> {
                                            Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }));
                            }

                            EventBus.getDefault().post(new CountCartEvent(true));
                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            if (!e.getMessage().contains("empty")) {
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                compositeDisposable.add(cartDataSource.insert(cartItem)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(() -> {
                                            Toast.makeText(context, "Added to Cart!", Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().post(new CountCartEvent(true));
                                        }, throwable -> {
                                            Toast.makeText(context, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        }));
                            }
                        }
                    });


        });
    }

    @Override
    public int getItemCount() {
        return shoppingItemList.size();
    }

    public class MyShoppingItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LayoutShoppingItemBinding binding;
        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyShoppingItemViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = LayoutShoppingItemBinding.bind(itemView);

            binding.tvAddToCart.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
