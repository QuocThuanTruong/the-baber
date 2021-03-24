package com.qtt.thebarber.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.qtt.thebarber.EventBus.EnableNextButtonEvent;
import com.qtt.thebarber.Interface.IRecyclerItemSelectedListener;
import com.qtt.thebarber.Model.Salon;
import com.qtt.thebarber.R;
import com.qtt.thebarber.databinding.LayoutSalonBinding;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MySalonAdapter extends RecyclerView.Adapter<MySalonAdapter.SalonViewHolder> {

    Context context;
    List<Salon> salonList;
    List<CardView> cardViewList;
    LayoutSalonBinding binding;

    public MySalonAdapter(Context context, List<Salon> salonList) {
        this.context = context;
        this.salonList = salonList;

        cardViewList = new ArrayList<>();

    }

    @NonNull
    @Override
    public SalonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = LayoutSalonBinding.inflate(LayoutInflater.from(context), parent, false);
        View itemView = binding.getRoot();
        return new SalonViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SalonViewHolder holder, int position) {
        holder.binding.tvSalonName.setText(salonList.get(position).getName());
        holder.binding.tvSalonAddress.setText(salonList.get(position).getAddress());

        if (!cardViewList.contains(holder.binding.cardViewSalon))
            cardViewList.add(holder.binding.cardViewSalon);

        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int position) {
                for (CardView cardView : cardViewList)
                    cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

                cardViewList.get(position).setCardBackgroundColor(context.getResources().getColor(R.color.colorCardSelected));

                //Send flag to Booking Activity
                EventBus.getDefault().post(new EnableNextButtonEvent(1, salonList.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return salonList.size();
    }

    public class SalonViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LayoutSalonBinding binding;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public SalonViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = LayoutSalonBinding.bind(itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
