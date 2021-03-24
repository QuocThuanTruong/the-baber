package com.qtt.thebarber.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.qtt.thebarber.EventBus.EnableNextButtonEvent;
import com.qtt.thebarber.EventBus.ShowBarberProfileEvent;
import com.qtt.thebarber.Fragments.BarberProfileFragment;
import com.qtt.thebarber.Interface.IRecyclerItemSelectedListener;
import com.qtt.thebarber.Model.Barber;
import com.qtt.thebarber.R;
import com.qtt.thebarber.databinding.LayoutBarberBinding;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class MyBarberAdapter extends RecyclerView.Adapter<MyBarberAdapter.BarberViewHolder> {

    Context context;
    List<Barber> barberList;
    List<CardView> cardViewList;
    LayoutBarberBinding binding;

    public MyBarberAdapter(Context context, List<Barber> barberList) {
        this.context = context;
        this.barberList = barberList;
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public BarberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = LayoutBarberBinding.inflate(LayoutInflater.from(context), parent, false);

        return new BarberViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull BarberViewHolder holder, int position) {
        holder.binding.tvBarberName.setText(barberList.get(position).getName());
        holder.binding.rtbBarber.setRating((float) (barberList.get(position).getRating() / ( barberList.get(position).getRatingTimes() == 0 ? 1 : barberList.get(position).getRatingTimes())));
        holder.binding.tvRatingTimes.setText(new StringBuilder(barberList.get(position).getRatingTimes() + "").append(" ratings").toString());

        if (!barberList.get(position).getAvatar().isEmpty()) {
            Picasso.get().load(barberList.get(position).getAvatar()).error(R.drawable.user_avatar).into(holder.binding.imgBarberAvt);
        }

        if (!cardViewList.contains(holder.binding.cardViewBarber))
            cardViewList.add(holder.binding.cardViewBarber);

        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int position) {
                for (CardView cardView : cardViewList)
                    cardView.setCardBackgroundColor(context.getResources().getColor(android.R.color.white));

                cardViewList.get(position).setCardBackgroundColor(context.getResources().getColor(R.color.colorCardSelected));

                //send flag to Booking Activity
                EventBus.getDefault().postSticky(new EnableNextButtonEvent(3, barberList.get(position)));
            }
        });
    }

    @Override
    public int getItemCount() {
        return barberList.size();
    }

    public class BarberViewHolder  extends  RecyclerView.ViewHolder implements View.OnClickListener {
        private LayoutBarberBinding binding;
        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public BarberViewHolder(@NonNull View itemView) {
            super(itemView);

            binding = LayoutBarberBinding.bind(itemView);

            binding.btnViewBarberProfile.setOnClickListener(v -> {
                EventBus.getDefault().postSticky(new ShowBarberProfileEvent(barberList.get(getAdapterPosition())));
            });

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}
