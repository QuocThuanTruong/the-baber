package com.qtt.thebarber.Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;


@Dao
public interface CartDAO {

    @Query("SELECT * FROM Cart WHERE userPhone = :userPhone")
    Flowable<List<CartItem>> getAllItemFromCart(String userPhone);

    @Query("SELECT SUM(productQuantity) FROM Cart WHERE userPhone = :userPhone")
    Single<Integer> countItemCart(String userPhone);

    @Query("SELECT * FROM Cart WHERE productId = :productId AND userPhone = :userPhone")
    Single<CartItem> getProductInCart(String productId, String userPhone);

    @Query("SELECT SUM(productPrice * productQuantity) FROM Cart WHERE userPhone = :userPhone")
    Single<Long> sumPrice(String userPhone);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(CartItem... cartItems);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> update(CartItem cartItem);

    @Delete
    Single<Integer> delete(CartItem cartItem);

    @Query("DELETE FROM Cart WHERE userPhone = :userPhone")
    Single<Integer> clearCart(String userPhone);

}
