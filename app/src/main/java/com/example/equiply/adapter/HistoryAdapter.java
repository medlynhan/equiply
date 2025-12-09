package com.example.equiply.adapter;

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

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private Context context;
    private List<History> historyList;
    private List<History> historyListFull;
//    private OnHistoryClickListener listener;
//
//    public interface OnHistoryClickListener {
//        void onHistoryClick(History history);
//    }

    public HistoryAdapter(Context context, List<History> historyList) {
        this.context = context;
        this.historyList = historyList;
        this.historyListFull = new ArrayList<>(historyList);
//        this.listener = listener;
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
        holder.tvItemStatus.setText(history.getStatus());

        if (history.getStatus().equals("Dipinjam")) {
            holder.tvDate.setText("Sedang dipinjam");
            holder.tvDate.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else if (history.getStatus().equals("Dikembalikan")) {
            holder.tvDate.setText(history.getReturnDate());
        }

        Glide.with(context)
                .load(history.getImageUrl())
                .placeholder(R.drawable.ic_img_placeholder)
                .error(R.drawable.ic_img_placeholder)
                .centerInside()
                .into(holder.ivToolImage);
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