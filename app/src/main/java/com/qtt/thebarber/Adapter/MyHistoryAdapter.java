package com.qtt.thebarber.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qtt.thebarber.Model.BookingInformation;
import com.qtt.thebarber.databinding.LayoutHistoryBinding;

import java.util.List;

public class MyHistoryAdapter extends RecyclerView.Adapter<MyHistoryAdapter.MyViewHolder> {
    private LayoutHistoryBinding binding;

    Context context;
    List<BookingInformation> bookingInformationList;

    public MyHistoryAdapter(Context context, List<BookingInformation> bookingInformationList) {
        this.context = context;
        this.bookingInformationList = bookingInformationList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LayoutHistoryBinding binding;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LayoutHistoryBinding.bind(itemView);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = LayoutHistoryBinding.inflate(LayoutInflater.from(context), parent, false);
        return new MyViewHolder((binding.getRoot()));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvBookingBarber.setText(bookingInformationList.get(position).getBarberName());
        holder.binding.tvBookingTimeText.setText(bookingInformationList.get(position).getTime());
        holder.binding.tvSalonAddress.setText(bookingInformationList.get(position).getSalonAddress());
        holder.binding.tvSalonName.setText(bookingInformationList.get(position).getSalonName());
        holder.binding.tvBookingDate.setText(bookingInformationList.get(position).getTimestamp().toDate().toString());
    }

    @Override
    public int getItemCount() {
        return bookingInformationList.size();
    }


}
