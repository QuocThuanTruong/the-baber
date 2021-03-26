package com.qtt.thebarber.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.EventBus.EnableNextButtonEvent;
import com.qtt.thebarber.Interface.IRecyclerItemSelectedListener;
import com.qtt.thebarber.Model.TimeSlot;
import com.qtt.thebarber.R;
import com.qtt.thebarber.databinding.LayoutTimeSlotBinding;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MyTimeSlotAdapter extends RecyclerView.Adapter<MyTimeSlotAdapter.TimeSlotViewHolder> {

    private Context context;
    private List<TimeSlot> timeSlotList; //stored slot is booked
    private List<CardView> cardViewList;
    LayoutTimeSlotBinding binding;

    public MyTimeSlotAdapter(Context context) {
        this.context = context;
        timeSlotList = new ArrayList<>();
        cardViewList = new ArrayList<>();
    }

    public MyTimeSlotAdapter(Context context, List<TimeSlot> timeSlotList) {
        this.context = context;
        this.timeSlotList = timeSlotList;
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public TimeSlotViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = LayoutTimeSlotBinding.inflate(LayoutInflater.from(context), parent, false);
        View itemView = binding.getRoot();
        return new TimeSlotViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final TimeSlotViewHolder holder, int position) {
        holder.binding.tvTimeSlot.setText(new StringBuilder(Common.convertTimeSlotToString(position)).toString());

        if (timeSlotList.size() == 0) { //all are available
            holder.binding.tvTimeSlotDesc.setText("Available");
            holder.binding.tvTimeSlotDesc.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.binding.tvTimeSlot.setTextColor(context.getResources().getColor(android.R.color.black));
            holder.binding.cardViewTimeSlot.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
        } else { //at least 1 slot full
            //get time slot from server
            for (TimeSlot timeSlot : timeSlotList) {
                int slot = timeSlot.getTimeSlot();
                if (slot == position) {//is booked
                    holder.binding.cardViewTimeSlot.setTag(Common.KEY_DISABLE);
                    holder.binding.tvTimeSlotDesc.setText("Full");
                    holder.binding.tvTimeSlotDesc.setTextColor(context.getResources().getColor(android.R.color.white));
                    holder.binding.tvTimeSlot.setTextColor(context.getResources().getColor(android.R.color.white));
                    holder.binding.cardViewTimeSlot.setCardBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
                }
            }
        }

        if (!cardViewList.contains(holder.binding.cardViewTimeSlot))
            cardViewList.add(holder.binding.cardViewTimeSlot);

        holder.setiRecyclerItemSelectedListener((view, position1) -> {
            for (CardView cardView : cardViewList) {
                if (cardView.getTag() == null)
                    cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));
            }
            if (cardViewList.get(position1).getTag() == null)
                cardViewList.get(position1).setCardBackgroundColor(context.getResources().getColor(R.color.colorCardSelected));

            EventBus.getDefault().post(new EnableNextButtonEvent(4, position1));
        });
    }


    @Override
    public int getItemCount() {
        return Common.TIME_SLOT_TOTAL;
    }

    public class TimeSlotViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LayoutTimeSlotBinding binding;
        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public TimeSlotViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LayoutTimeSlotBinding.bind(itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
