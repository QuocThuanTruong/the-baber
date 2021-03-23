package com.qtt.thebarber.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qtt.thebarber.Model.LookBook;
import com.qtt.thebarber.R;
import com.qtt.thebarber.databinding.LayoutLookBookItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyLookBookAdapter extends RecyclerView.Adapter<MyLookBookAdapter.MyViewHolder> {
    Context context;
    List<LookBook> lookBookList;
    LayoutLookBookItemBinding binding;

    public MyLookBookAdapter(Context context, List<LookBook> lookBookList) {
        this.context = context;
        this.lookBookList = lookBookList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = LayoutLookBookItemBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(lookBookList.get(position).getUrl()).error(R.drawable.img_not_found).into(holder.binding.imgLookBook);
    }

    @Override
    public int getItemCount() {
        return lookBookList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        LayoutLookBookItemBinding binding;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = LayoutLookBookItemBinding.bind(itemView);
        }
    }
}
