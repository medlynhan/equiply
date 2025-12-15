package com.example.equiply.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equiply.R;
import com.example.equiply.model.BorrowHistory;

import java.util.ArrayList;

public class BorrowRequestsAdapter extends RecyclerView.Adapter<BorrowRequestsAdapter.ViewHolder> {

    private ArrayList<BorrowHistory> historyList;
    private ApprovalActionListener listener;

    public interface ApprovalActionListener {
        void onApprove(BorrowHistory item);
        void onReject(BorrowHistory item);
    }

    public BorrowRequestsAdapter(ArrayList<BorrowHistory> list, ApprovalActionListener listener) {
        this.historyList = list;
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
        BorrowHistory item = historyList.get(position);
        holder.tvTitle.setText(item.getToolName());
        holder.tvDesc.setText("Peminjam ID: " + item.getUserId() + "\nTanggal: " + item.getBorrowDate());

        holder.itemView.setOnClickListener(v -> {
            listener.onApprove(item);
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvNotifTitle);
            tvDesc = itemView.findViewById(R.id.tvNotifMessage);
        }
    }
}
