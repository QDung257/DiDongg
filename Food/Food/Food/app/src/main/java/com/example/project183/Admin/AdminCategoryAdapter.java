package com.example.project183.Admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project183.Activity.ListFoodActivity;
import com.example.project183.Domain.Category;
import com.example.project183.R;

import java.util.ArrayList;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.viewholder> {
    ArrayList<Category> items;
    Context context;

    public AdminCategoryAdapter(ArrayList<Category> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public AdminCategoryAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context=parent.getContext();
        View inflate= LayoutInflater.from(parent.getContext()).inflate(R.layout.viewholder_category,parent,false);
        return new viewholder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminCategoryAdapter.viewholder holder, int position) {
        holder.titleTxt.setText(items.get(position).getName());

        Glide.with(context)
                .load(items.get(position).getImagePath())
                .into(holder.pic);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdminListFood.class);
            intent.putExtra("CategoryId",items.get(position).getId());
            intent.putExtra("CategoryName",items.get(position).getName());
            context.startActivities(new Intent[]{intent});
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView titleTxt;
        ImageView pic;

        public viewholder(@NonNull View itemView) {
            super(itemView);
            titleTxt=itemView.findViewById(R.id.catNameTxt);
            pic=itemView.findViewById(R.id.imgCat);
        }
    }
}
