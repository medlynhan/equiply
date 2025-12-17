package com.example.equiply.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.equiply.R;
import com.example.equiply.model.BorrowHistory;
import com.example.equiply.student_activity.HistoryDetailActivity;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<BorrowHistory> borrowHistoryList;
    private List<BorrowHistory> borrowHistoryListFull;

    public HistoryAdapter(Context context, List<BorrowHistory> borrowHistoryList) {
        this.context = context;
        this.borrowHistoryList = borrowHistoryList;
        this.borrowHistoryListFull = new ArrayList<>(borrowHistoryList);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_layout, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.HistoryViewHolder holder, int position) {
        BorrowHistory borrowHistory = borrowHistoryList.get(position);

        holder.tvItemTitle.setText(borrowHistory.getToolName());

        String status = borrowHistory.getStatus() != null ? borrowHistory.getStatus().toLowerCase() : "";

        switch (status) {
            case "approved":
            case "dipinjam":
                holder.tvItemStatus.setText("Dipinjam");
                holder.tvItemStatus.setTextColor(ContextCompat.getColor(context, R.color.black_modif));
                holder.statusBadge.setCardBackgroundColor(Color.parseColor("#FF9800")); // Orange
                break;

            case "pending":
                holder.tvItemStatus.setText("Menunggu");
                holder.tvItemStatus.setTextColor(ContextCompat.getColor(context, R.color.black_modif));
                holder.statusBadge.setCardBackgroundColor(Color.GRAY);
                break;

            case "pending_return":
                holder.tvItemStatus.setText("Verifikasi");
                holder.tvItemStatus.setTextColor(ContextCompat.getColor(context, R.color.black_modif));
                holder.statusBadge.setCardBackgroundColor(Color.parseColor("#FFC107")); // Amber/Yellow
                break;

            case "returned":
            case "dikembalikan":
                holder.tvItemStatus.setText("Dikembalikan");
                holder.tvItemStatus.setTextColor(ContextCompat.getColor(context, R.color.black_modif));
                holder.statusBadge.setCardBackgroundColor(Color.parseColor("#4CAF50")); // Green
                break;

            case "rejected":
            case "ditolak":
                holder.tvItemStatus.setText("Ditolak");
                holder.tvItemStatus.setTextColor(Color.WHITE);
                holder.statusBadge.setCardBackgroundColor(Color.parseColor("#D32F2F")); // Red
                break;

            default:
                holder.tvItemStatus.setText(status);
                holder.tvItemStatus.setTextColor(ContextCompat.getColor(context, R.color.black_modif));
                holder.statusBadge.setCardBackgroundColor(Color.LTGRAY);
                break;
        }

        holder.tvDate.setText(borrowHistory.getRequestDate());

        Glide.with(context)
                .load(borrowHistory.getImageUrl())
                .placeholder(R.drawable.ic_img_placeholder)
                .error(R.drawable.ic_img_placeholder)
                .centerInside()
                .into(holder.ivToolImage);

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HistoryDetailActivity.class);
            // Pass all data needed for the detail screen
            intent.putExtra("TOOL_ID", borrowHistory.getToolId());
            intent.putExtra("TOOL_NAME", borrowHistory.getToolName());
            intent.putExtra("TOOL_IMAGE", borrowHistory.getImageUrl());
            intent.putExtra("USER_ID", borrowHistory.getUserId());
            intent.putExtra("STATUS", borrowHistory.getStatus());
            intent.putExtra("BORROW_DATE", borrowHistory.getBorrowDate());
            intent.putExtra("RETURN_DATE", borrowHistory.getReturnDate());
            intent.putExtra("REASON", borrowHistory.getReason());

            context.startActivity(intent);
            if (context instanceof Activity) {
                ((Activity) context).overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return borrowHistoryList.size();
    }

    // filter buat search bar
    public void filter(String query) {
        borrowHistoryList.clear();
        if (query.isEmpty()) {
            borrowHistoryList.addAll(borrowHistoryListFull);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (BorrowHistory item : borrowHistoryListFull) {
                if (item.getToolName().toLowerCase().contains(lowerCaseQuery)) {
                    borrowHistoryList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    // update data from activity
    public void updateData(List<BorrowHistory> newBorrowHistoryList) {
        this.borrowHistoryList.clear();
        this.borrowHistoryList.addAll(newBorrowHistoryList);
        this.borrowHistoryListFull.clear();
        this.borrowHistoryListFull.addAll(newBorrowHistoryList);
        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvItemTitle;
        TextView tvItemStatus;
        TextView tvDate;
        ImageView ivToolImage;
        MaterialCardView statusBadge;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemStatus = itemView.findViewById(R.id.tvItemStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivToolImage = itemView.findViewById(R.id.ivToolImage);
            statusBadge = itemView.findViewById(R.id.statusBadge);
        }
    }
}