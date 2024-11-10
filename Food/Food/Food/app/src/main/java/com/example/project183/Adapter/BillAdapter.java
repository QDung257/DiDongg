package com.example.project183.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project183.Activity.BillItem;
import com.example.project183.R;

import java.util.ArrayList;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {

    private ArrayList<BillItem> bills;
    private OnBillDeleteListener deleteListener;

    public BillAdapter(ArrayList<BillItem> bills, OnBillDeleteListener deleteListener) {
        this.bills = bills;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_item, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        BillItem bill = bills.get(position);
        holder.billTextView.setText(bill.getBill());
        // Giả sử bạn có phương thức getStatus() trong BillItem để lấy trạng thái
        holder.statusTextView.setText(bill.getStatus()); // Cập nhật trạng thái

        holder.deleteButton.setOnClickListener(v -> {
            deleteListener.onDeleteBill(bill.getBillId());
        });
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    public static class BillViewHolder extends RecyclerView.ViewHolder {
        TextView billTextView;
        TextView statusTextView; // Thêm TextView cho trạng thái
        Button deleteButton;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            billTextView = itemView.findViewById(R.id.billTextView);
            statusTextView = itemView.findViewById(R.id.statusTextView); // Khởi tạo statusTextView
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    public interface OnBillDeleteListener {
        void onDeleteBill(String billId);
    }
}
