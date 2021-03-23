package com.qtt.thebarber.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.qtt.thebarber.Common.Common;
import com.qtt.thebarber.Common.MyDiffCallBack;
import com.qtt.thebarber.Interface.IRecyclerItemSelectedListener;
import com.qtt.thebarber.Model.MyNotification;
import com.qtt.thebarber.databinding.LayoutNotificationItemBinding;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MyNotificationAdapter extends RecyclerView.Adapter<MyNotificationAdapter.MyViewHolder> {

    Context context;
    List<MyNotification> notificationList;
    LayoutNotificationItemBinding binding;

    public MyNotificationAdapter(Context context, List<MyNotification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = LayoutNotificationItemBinding.inflate(LayoutInflater.from(context), parent, false);
        View itemView = binding.getRoot();
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.binding.tvNotificationTitle.setText(notificationList.get(position).getTitle());
        holder.binding.tvNotificationContent.setText(notificationList.get(position).getContent());
        holder.binding.tvTime.setText(notificationList.get(position).getServerTimeStamp().toDate().toString());
        if (!notificationList.get(position).getAvatar().isEmpty()) {
            Picasso.get().load(notificationList.get(position).getAvatar()).into(holder.binding.imgAvatar);
        }

        if (notificationList.get(position).isRead()) {
            holder.binding.cardBadge.setVisibility(View.GONE);
        } else {
            holder.binding.cardBadge.setVisibility(View.VISIBLE);
        }


        holder.setiRecyclerItemSelectedListener((view, position1) -> {
            FirebaseFirestore.getInstance()
                    .collection("User")
                    .document(Common.currentUser.getPhoneNumber())
                    .collection("Notifications")
                    .document(notificationList.get(position1).getUid())
                    .update("read", true)
                    .addOnCompleteListener(task -> {
                        holder.binding.cardBadge.setVisibility(View.GONE);
                    }).addOnFailureListener(e -> Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public void updateList(List<MyNotification> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MyDiffCallBack(this.notificationList, newList));
        notificationList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        LayoutNotificationItemBinding binding;
        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = LayoutNotificationItemBinding.bind(itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            iRecyclerItemSelectedListener.onItemSelectedListener(v, getAdapterPosition());
        }
    }
}

