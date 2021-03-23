package com.qtt.thebarber.Interface;

import com.qtt.thebarber.Model.ShoppingItem;

import java.util.List;

public interface IShoppingItemsLoadListener {
    void onShoppingItemsLoadSuccess(List<ShoppingItem> shoppingItemList);
    void onShoppingItemsLoadFailed(String message);
}
