package com.qtt.thebarber.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface CartDataSource {

    Flowable<List<CartItem>> getAllItemFromCart(String userPhone);


    Single<Integer> countItemCart(String userPhone);


    Single<CartItem> getProductInCart(String productId, String userPhone);


    Single<Long> sumPrice(String userPhone);


    Completable insert(CartItem... cartItems);


    Single<Integer> update(CartItem cartItem);


    Single<Integer> delete(CartItem cartItem);


    Single<Integer> clearCart(String userPhone);
}
