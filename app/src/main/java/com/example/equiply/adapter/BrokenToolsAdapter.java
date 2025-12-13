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

public class BrokenToolsAdapter extends RecyclerView.Adapter<BrokenToolsAdapter.ViewHolder> {

    private final ArrayList<Tool> tools;

    public BrokenToolsAdapter(ArrayList<Tool> tools) {
        this.tools = tools;
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
        holder.tvStatus.setText("Rusak");

        // image alat
        Glide.with(holder.itemView.getContext())
                .load(tool.getImageUrl())
                .placeholder(R.drawable.ic_img_placeholder)
                .into(holder.ivTool);
    }

    @Override
    public int getItemCount() {
        return tools.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivTool;
        TextView tvName, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTool = itemView.findViewById(R.id.ivToolImage);
            tvName = itemView.findViewById(R.id.tvToolName);
            tvStatus = itemView.findViewById(R.id.tvToolStatus);
        }
    }
}
