package com.example.equiply.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.equiply.R;
import com.example.equiply.model.Tool;

import java.util.ArrayList;

public class BorrowedToolsAdapter extends RecyclerView.Adapter<BorrowedToolsAdapter.ViewHolder> {

    private final ArrayList<Tool> tools;
    private final ArrayList<Tool> toolsFull;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Tool tool);
    }

    public BorrowedToolsAdapter(ArrayList<Tool> tools, OnItemClickListener listener) {
        this.tools = tools;
        toolsFull = new ArrayList<>(tools);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tool, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tool tool = tools.get(position);

        holder.tvName.setText(tool.getName());
        holder.tvStatus.setText("Dipinjam");

        if (tool.getLastBorrower() != null && !tool.getLastBorrower().equals("-")) {
            holder.tvLastBorrower.setVisibility(View.VISIBLE);
            holder.tvLastBorrower.setText("Peminjam: " + tool.getLastBorrower());

            holder.tvLastBorrower.setBackgroundResource(R.color.blue_light);
            holder.tvLastBorrower.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.blue_dark));
        } else {
            holder.tvLastBorrower.setVisibility(View.VISIBLE);
            holder.tvLastBorrower.setText("Peminjam: " + "-");

            holder.tvLastBorrower.setBackgroundResource(R.color.blue_light);
            holder.tvLastBorrower.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.blue_dark));
        }

        Glide.with(holder.itemView.getContext())
                .load(tool.getImageUrl())
                .placeholder(R.drawable.ic_img_placeholder)
                .into(holder.ivTool);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(tool));
    }

    @Override
    public int getItemCount() {
        return tools.size();
    }

    public void filter(String text) {
        tools.clear();
        if (text.isEmpty()) {
            tools.addAll(toolsFull);
        } else {
            String query = text.toLowerCase().trim();
            for (Tool item : toolsFull) {
                boolean matchName = item.getName().toLowerCase().contains(query);
                boolean matchBorrower = item.getLastBorrower() != null &&
                        item.getLastBorrower().toLowerCase().contains(query);

                if (matchName || matchBorrower) {
                    tools.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateList(ArrayList<Tool> newList) {
        tools.clear();
        toolsFull.clear();
        tools.addAll(newList);
        toolsFull.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivTool;
        TextView tvName, tvStatus, tvLastBorrower;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTool = itemView.findViewById(R.id.ivToolImage);
            tvName = itemView.findViewById(R.id.tvToolName);
            tvStatus = itemView.findViewById(R.id.tvToolStatus);
            tvLastBorrower = itemView.findViewById(R.id.tvLastBorrower);;
        }
    }
}
