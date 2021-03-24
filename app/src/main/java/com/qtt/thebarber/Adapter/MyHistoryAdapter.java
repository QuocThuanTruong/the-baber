package com.qtt.thebarber.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.Model.BarberService;
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

        ///AllSalon/Florida/Branch/0n7ikrtgQXW4EXhuJ0qy/Services
        FirebaseFirestore.getInstance().collection("AllSalon")
                .document(bookingInformationList.get(position).getCityBooking())
                .collection("Branch")
                .document(bookingInformationList.get(position).getSalonId())
                .collection("Services")
                .whereEqualTo("uid", bookingInformationList.get(position).getBarberServiceList().get(0))
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot documentSnapshot : task.getResult()) {
                            BarberService barberService = documentSnapshot.toObject(BarberService.class);
                            holder.binding.tvServiceName.setText(barberService.getName());
                        }

                    }

                });


    }

    @Override
    public int getItemCount() {
        return bookingInformationList.size();
    }


}
