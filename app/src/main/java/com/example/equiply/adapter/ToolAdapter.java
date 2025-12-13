package com.example.equiply.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.equiply.R;
import com.example.equiply.model.Tool;

import java.util.ArrayList;
import java.util.List;

public class ToolAdapter extends RecyclerView.Adapter<ToolAdapter.ToolViewHolder> {

    private Context context;
    private List<Tool> toolList;
    private List<Tool> toolListFull;
    private OnToolClickListener listener;

    public interface OnToolClickListener {
        void onToolClick(Tool tool);
    }

    public ToolAdapter(Context context, List<Tool> toolList, OnToolClickListener listener) {
        this.context = context;
        this.toolList = toolList;
        this.toolListFull = new ArrayList<>(toolList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ToolViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.tool_layout, parent, false);
        return new ToolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToolViewHolder holder, int position) {
        Tool tool = toolList.get(position);

        holder.tvItemTitle.setText(tool.getName());
        holder.tvItemStatus.setText(tool.getStatus());

        Glide.with(context)
                .load(tool.getImageUrl())
                .placeholder(R.drawable.ic_img_placeholder)
                .error(R.drawable.ic_img_placeholder)
                .centerInside()
                .into(holder.ivToolImage);

        holder.cardView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onToolClick(tool);
            }
        });
    }

    @Override
    public int getItemCount() {
        return toolList.size();
    }

    public void filter(String query) {
        toolList.clear();
        if (query.isEmpty()) {
            toolList.addAll(toolListFull);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (Tool tool : toolListFull) {
                if (tool.getName().toLowerCase().contains(lowerCaseQuery)) {
                    toolList.add(tool);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void updateData(List<Tool> newToolList) {
        this.toolList.clear();
        this.toolList.addAll(newToolList);
        this.toolListFull.clear();
        this.toolListFull.addAll(newToolList);
        notifyDataSetChanged();
    }

    public static class ToolViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tvItemTitle;
        TextView tvItemStatus;
        ImageView ivToolImage;

        public ToolViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView) itemView;
            tvItemTitle = itemView.findViewById(R.id.tvItemTitle);
            tvItemStatus = itemView.findViewById(R.id.tvItemStatus);
            ivToolImage = itemView.findViewById(R.id.ivToolImage);
        }
    }
}