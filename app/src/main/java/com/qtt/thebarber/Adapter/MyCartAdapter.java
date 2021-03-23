package com.qtt.thebarber.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qtt.thebarber.Database.CartDataSource;
import com.qtt.thebarber.Database.CartDatabase;
import com.qtt.thebarber.Database.CartItem;
import com.qtt.thebarber.Database.LocalCartDataSource;
import com.qtt.thebarber.R;
import com.qtt.thebarber.databinding.LayoutCartItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyCartHolder> {

    List<CartItem> cartItemList;
    Context context;
    CartDataSource cartDataSource;
    CompositeDisposable compositeDisposable;
    LayoutCartItemBinding binding;

    public MyCartAdapter(List<CartItem> cartItemList, Context context) {
        this.cartItemList = cartItemList;
        this.context = context;
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
        compositeDisposable = new CompositeDisposable();
    }

    public void onDestroy() {
        compositeDisposable.clear();
    }


    @NonNull
    @Override
    public MyCartHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = LayoutCartItemBinding.inflate(LayoutInflater.from(context), parent, false);
        View itemView = binding.getRoot();
        return new MyCartHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyCartHolder holder, int position) {

        Picasso.get().load(cartItemList.get(position).getProductImage()).error(R.drawable.img_not_found).into(holder.binding.imgCart);
        holder.binding.tvCartName.setText(cartItemList.get(position).getProductName());
        holder.binding.tvCartPrice.setText(new StringBuilder("$").append(cartItemList.get(position).getProductPrice()).toString());
        holder.binding.tvQuantity.setText(cartItemList.get(position).getProductQuantity() + "");

        holder.setListener((view, position1, isDecrease) -> {

            if (isDecrease) {
                if (cartItemList.get(position1).getProductQuantity() == 1) {
                    CartItem cartItem = cartItemList.get(position1);

                    cartDataSource.delete(cartItem)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<Integer>() {
                                @Override
                                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                }

                                @Override
                                public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                    cartItemList.remove(cartItem);
                                    notifyItemRemoved(position1);
                                }

                                @Override
                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }
                else if (cartItemList.get(position1).getProductQuantity() > 1) {
                    cartItemList.get(position1)
                            .setProductQuantity(cartItemList.get(position1).getProductQuantity() - 1);

                    //DatabaseUtils.updateCartItem(cartDatabase, cartItemList.get(position1));
                    cartDataSource.update(cartItemList.get(position1))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<Integer>() {
                                @Override
                                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                }

                                @Override
                                public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                    holder.binding.tvQuantity.setText(cartItemList.get(position1).getProductQuantity() +"");
                                }

                                @Override
                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                }

            } else {
                if (cartItemList.get(position1).getProductQuantity() < 99) {
                    cartItemList.get(position1)
                            .setProductQuantity(cartItemList.get(position1).getProductQuantity() + 1);

                    cartDataSource.update(cartItemList.get(position1))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new SingleObserver<Integer>() {
                                @Override
                                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                }

                                @Override
                                public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                    holder.binding.tvQuantity.setText(cartItemList.get(position1).getProductQuantity() +"");
                                }

                                @Override
                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }


    public class MyCartHolder extends RecyclerView.ViewHolder{
        LayoutCartItemBinding binding;
        IImageViewListener listener;

        public void setListener(IImageViewListener listener) {
            this.listener = listener;
        }

        public MyCartHolder(@NonNull View itemView) {
            super(itemView);

            binding = LayoutCartItemBinding.bind(itemView);

            binding.imgDecrease.setOnClickListener(v -> listener.onImageViewClickListener(v, getAdapterPosition(), true));

            binding.imgIncrease.setOnClickListener(v -> listener.onImageViewClickListener(v, getAdapterPosition(), false));
        }
    }

    private interface IImageViewListener {
        void onImageViewClickListener(View view, int position, boolean isDecrease);
    }
}
