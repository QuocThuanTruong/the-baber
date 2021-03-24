package com.qtt.thebarber.Interface;

import com.qtt.thebarber.Model.LookBook;

import java.util.List;

public interface ILookBookLoadListener {
    void onLookBookLoadSuccess(List<LookBook> lookBookList);
    void onLookBookLoadFailed(String message);
}
