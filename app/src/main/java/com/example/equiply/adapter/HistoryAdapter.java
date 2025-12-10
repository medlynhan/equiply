package com.example.equiply.adapter;

import android.content.Intent;
import android.content.Context;
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
import com.example.equiply.model.History;
import com.example.equiply.student_activity.HistoryDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<History> historyList;
    private List<History> historyListFull;

    public HistoryAdapter(Context context, List<History> historyList) {
        this.context = context;
        this.historyList = historyList;
        this.historyListFull = new ArrayList<>(historyList);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_layout, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.HistoryViewHolder holder, int position) {
        History history = historyList.get(position);

        holder.tvItemTitle.setText(history.getToolName());

        String status = history.getStatus();

        if (history.getStatus().equals("Dipinjam")) {
            holder.tvItemStatus.setText("Sedang Dipinjam");
            holder.tvItemStatus.setTextColor(ContextCompat.getColor(context, R.color.black_modif));

            holder.tvDate.setText(" - ");
//            holder.tvDate.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else if (history.getStatus().equals("Menunggu Konfirmasi")) {
            holder.tvItemStatus.setText("Menunggu Konfirmasi");
            holder.tvItemStatus.setTextColor(ContextCompat.getColor(context, R.color.black_modif));

            holder.tvDate.setText(" - ");
//            holder.tvDate.setTextColor(ContextCompat.getColor(context, R.color.yellow));
        } else if (history.getStatus().equals("Dikembalikan")) {
            holder.tvItemStatus.setText("Telah Dikembalikan");
            holder.tvItemStatus.setTextColor(ContextCompat.getColor(context, R.color.black_modif));

            holder.tvDate.setText(history.getReturnDate());
            holder.tvDate.setTextColor(ContextCompat.getColor(context, R.color.black_modif));
        }

        Glide.with(context)
                .load(history.getImageUrl())
                .placeholder(R.drawable.ic_img_placeholder)
                .error(R.drawable.ic_img_placeholder)
                .centerInside()
                .into(holder.ivToolImage);

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, HistoryDetailActivity.class);
            // Pass all data needed for the detail screen
            intent.putExtra("TOOL_ID", history.getToolId());
            intent.putExtra("TOOL_NAME", history.getToolName());
            intent.putExtra("TOOL_IMAGE", history.getImageUrl());
            intent.putExtra("USER_ID", history.getUserId());
            intent.putExtra("STATUS", history.getStatus());
            intent.putExtra("BORROW_DATE", history.getBorrowDate());
            intent.putExtra("RETURN_DATE", history.getReturnDate());
            intent.putExtra("REASON", history.getReason());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    // filter buat search bar
    public void filter(String query) {
        historyList.clear();
        if (query.isEmpty()) {
            historyList.addAll(historyListFull);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (History item : historyListFull) {
                if (item.getToolName().toLowerCase().contains(lowerCaseQuery)) {
                    historyList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    // update data from activity
    public void updateData(List<History> newHistoryList) {
        this.historyList.clear();
        this.historyList.addAll(newHistoryList);
        this.historyListFull.clear();
        this.historyListFull.addAll(newHistoryList);
        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvItemTitle;
        TextView tvItemStatus;
        TextView tvDate;
        ImageView ivToolImage;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemStatus = itemView.findViewById(R.id.tvItemStatus);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivToolImage = itemView.findViewById(R.id.ivToolImage);
        }
    }
}