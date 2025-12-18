package com.example.equiply.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equiply.R;
import com.example.equiply.model.LendingRequest;

import java.util.ArrayList;

public class ReturnRequestsAdapter extends RecyclerView.Adapter<ReturnRequestsAdapter.ViewHolder> {

    private ArrayList<LendingRequest> returnList;
    private ReturnActionListener listener;

    public interface ReturnActionListener {
        void onAction(LendingRequest item);
    }

    public ReturnRequestsAdapter(ArrayList<LendingRequest> list, ReturnActionListener listener) {
        this.returnList = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LendingRequest item = returnList.get(position);
        holder.tvTitle.setText(item.getToolName());
        holder.tvDesc.setText("Kondisi: " + item.getCondition() + "\nKembali: " + item.getReturnDate());
        holder.tvDate.setText("Ketuk untuk Verifikasi");

        holder.itemView.setOnClickListener(v -> listener.onAction(item));
    }

    @Override
    public int getItemCount() {
        return returnList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvDesc = itemView.findViewById(R.id.tvNotifMessage);
            tvDate = itemView.findViewById(R.id.tvNotifDate);
        }
    }

}
