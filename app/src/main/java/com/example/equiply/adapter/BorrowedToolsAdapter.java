package com.example.equiply.adapter;

import static com.example.equiply.R.id.ivStatusIcon;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equiply.R;

import java.util.ArrayList;

public class BorrowedToolsAdapter extends RecyclerView.Adapter<BorrowedToolsAdapter.ViewHolder> {

    private final ArrayList<String> list;


    public BorrowedToolsAdapter(ArrayList<String> list) {
        this.list = list;
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
        String item = list.get(position);

        holder.tvName.setText(item);
        holder.tvStatus.setText("Borrowed");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivTool, ivStatusIcon;
        TextView tvName, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTool = itemView.findViewById(R.id.ivToolImage);
            tvName = itemView.findViewById(R.id.tvToolName);
            tvStatus = itemView.findViewById(R.id.ivStatusIcon);
        }
    }
}
