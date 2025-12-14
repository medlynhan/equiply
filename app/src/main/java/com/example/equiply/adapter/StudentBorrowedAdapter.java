package com.example.equiply.adapter;

import android.content.Context;
import android.graphics.Color;
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
import com.google.android.material.card.MaterialCardView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class StudentBorrowedAdapter extends RecyclerView.Adapter<StudentBorrowedAdapter.ViewHolder> {
    private final ArrayList<Tool> borrowedTools;
    private final Context context;

    public StudentBorrowedAdapter(Context context, ArrayList<Tool> borrowedTools) {
        this.borrowedTools = borrowedTools;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_borrowed_tool_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tool tool = borrowedTools.get(position);

        holder.tvToolName.setText(tool.getName());
        holder.tvReturnDate.setText("Kembali: " + tool.getReturnDate());

        if (tool.getImageUrl() != null && !tool.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(tool.getImageUrl())
                    .placeholder(R.drawable.ic_img_placeholder)
                    .error(R.drawable.ic_img_placeholder)
                    .into(holder.ivToolImage);
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date returnDate = sdf.parse(tool.getReturnDate());
            Date today = new Date();

            if (returnDate != null) {
                long diff = returnDate.getTime() - today.getTime();
                long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                if (days < 0) {
                    holder.tvDaysLeft.setText("Terlambat");
                    holder.badgeCard.setCardBackgroundColor(Color.parseColor("#D32F2F")); // RED
                } else if (days == 0) {
                    holder.tvDaysLeft.setText("Hari Ini");
                    holder.badgeCard.setCardBackgroundColor(Color.parseColor("#F57C00")); // ORANGE
                } else {
                    holder.tvDaysLeft.setText(days + " Hari Lagi");
                    holder.badgeCard.setCardBackgroundColor(context.getColor(R.color.blue_medium)); // BLUE
                }
            }
        } catch (ParseException e) {
            holder.tvDaysLeft.setText("-");
        }
    }

    @Override
    public int getItemCount() {
        return borrowedTools.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivToolImage;
        TextView tvDaysLeft, tvToolName, tvReturnDate;
        MaterialCardView badgeCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivToolImage = itemView.findViewById(R.id.ivToolImage);
            tvDaysLeft = itemView.findViewById(R.id.tvDaysLeft);
            tvToolName = itemView.findViewById(R.id.tvToolName);
            tvReturnDate = itemView.findViewById(R.id.tvReturnDate);
            badgeCard = (MaterialCardView) tvDaysLeft.getParent();
        }
    }
}
