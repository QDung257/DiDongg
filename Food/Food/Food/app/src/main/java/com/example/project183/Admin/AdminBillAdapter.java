package com.example.project183.Admin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project183.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AdminBillAdapter extends RecyclerView.Adapter<AdminBillAdapter.ViewHolder> {

    private ArrayList<BillItemAdmin> adminBills;
    private OnConfirmClickListener onConfirmClickListener;
    private OnDeleteClickListener onDeleteClickListener;

    public AdminBillAdapter(ArrayList<BillItemAdmin> adminBills, OnConfirmClickListener onConfirmClickListener, OnDeleteClickListener onDeleteClickListener) {
        this.adminBills = adminBills;
        this.onConfirmClickListener = onConfirmClickListener;
        this.onDeleteClickListener = onDeleteClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item_admin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BillItemAdmin billItem = adminBills.get(position);
        holder.billTextView.setText(billItem.getItemName());
        holder.statusTextView.setText(billItem.getStatus());

        // Xử lý sự kiện khi nhấn nút xác nhận
        holder.confirmButton.setOnClickListener(v -> onConfirmClickListener.onConfirmClick(billItem.getId()));

        // Xử lý sự kiện khi nhấn nút xóa
        holder.deleteButton.setOnClickListener(v -> onDeleteClickListener.onDeleteClick(billItem.getId()));
    }

    @Override
    public int getItemCount() {
        return adminBills.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView billTextView;
        TextView statusTextView;
        Button confirmButton;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            billTextView = itemView.findViewById(R.id.billTextViewadm);
            statusTextView = itemView.findViewById(R.id.statusTextViewadm);
            confirmButton = itemView.findViewById(R.id.confirmButtonadm);
            deleteButton = itemView.findViewById(R.id.deleteButtonadm);
        }
    }

    // Giao diện để xử lý sự kiện click xác nhận
    public interface OnConfirmClickListener {
        void onConfirmClick(String billId);
    }

    // Giao diện để xử lý sự kiện click xóa
    public interface OnDeleteClickListener {
        void onDeleteClick(String billId);
    }
}
