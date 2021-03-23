package com.qtt.thebarber.Common;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.qtt.thebarber.Model.MyNotification;

import java.util.List;

public class MyDiffCallBack extends DiffUtil.Callback {

    List<MyNotification> oldList;
    List<MyNotification> newList;

    public MyDiffCallBack(List<MyNotification> oldList, List<MyNotification> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getUid() == newList.get(newItemPosition).getUid();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition) == newList.get(newItemPosition);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}