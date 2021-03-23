package com.qtt.thebarber.Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalCartDataSource implements CartDataSource{

    private CartDAO cartDAO;

    public LocalCartDataSource(CartDAO cartDAO) {
        this.cartDAO = cartDAO;
    }

    @Override
    public Flowable<List<CartItem>> getAllItemFromCart(String userPhone) {
        return cartDAO.getAllItemFromCart(userPhone);
    }

    @Override
    public Single<Integer> countItemCart(String userPhone) {
        return cartDAO.countItemCart(userPhone);
    }

    @Override
    public Single<CartItem> getProductInCart(String productId, String userPhone) {
        return cartDAO.getProductInCart(productId, userPhone);
    }

    @Override
    public Single<Long> sumPrice(String userPhone) {
        return cartDAO.sumPrice(userPhone);
    }

    @Override
    public Completable insert(CartItem... cartItems) {
        return cartDAO.insert(cartItems);
    }

    @Override
    public Single<Integer> update(CartItem cartItem) {
        return cartDAO.update(cartItem);
    }

    @Override
    public Single<Integer> delete(CartItem cartItem) {
        return cartDAO.delete(cartItem);
    }

    @Override
    public Single<Integer> clearCart(String userPhone) {
        return cartDAO.clearCart(userPhone);
    }
}
