package com.qtt.thebarber.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.EventBus.EnableNextButtonEvent;
import com.qtt.thebarber.Interface.IRecyclerItemSelectedListener;
import com.qtt.thebarber.Model.BarberService;
import com.qtt.thebarber.R;
import com.qtt.thebarber.databinding.LayoutServiceItemBinding;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MyServiceAdapter extends RecyclerView.Adapter<MyServiceAdapter.MyViewHolder> {
    private Context context;
    private List<BarberService> servicesList;
    List<CardView> cardViewList;
    LayoutServiceItemBinding binding;

    public MyServiceAdapter(Context context, List<BarberService> servicesList) {
        this.context = context;
        this.servicesList = servicesList;
        cardViewList = new ArrayList<>();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;
        LayoutServiceItemBinding binding;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LayoutServiceItemBinding.bind(itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = LayoutServiceItemBinding.inflate(LayoutInflater.from(context), parent, false);
        View itemView = binding.getRoot();
        return new MyViewHolder((itemView));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(servicesList.get(position).getAvatar()).into(holder.binding.imgService);
        holder.binding.tvServiceName.setText(servicesList.get(position).getName());
        holder.binding.tvServicePrice.setText(new StringBuilder("$").append(servicesList.get(position).getPrice()).toString());

        if (!cardViewList.contains(holder.binding.cardViewService))
            cardViewList.add(holder.binding.cardViewService);



        holder.setiRecyclerItemSelectedListener((view, position1) -> {
            for (CardView cardView : cardViewList)
                cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

            cardViewList.get(position).setCardBackgroundColor(context.getResources().getColor(R.color.colorCardSelected));

            //call event bus calc total price
            Common.selectedService = servicesList.get(position);
            EventBus.getDefault().post(new EnableNextButtonEvent(2, servicesList.get(position)));
        });
    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }

}
