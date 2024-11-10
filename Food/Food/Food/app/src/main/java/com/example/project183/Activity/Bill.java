package com.example.project183.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project183.Adapter.BillAdapter;
import com.example.project183.Admin.Admin_donhang;
import com.example.project183.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Bill extends AppCompatActivity {

    private RecyclerView billRecyclerView;
    private BillAdapter billAdapter;
    private ArrayList<BillItem> bills;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        billRecyclerView = findViewById(R.id.billRecyclerView);
        ImageView imgthoat = findViewById(R.id.imgthoat);
        imgthoat.setOnClickListener(view -> {
            Intent i = new Intent(Bill.this, MainActivity.class);
            startActivity(i);
        });

        bills = new ArrayList<>();
        billAdapter = new BillAdapter(bills, this::deleteBill);

        billRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        billRecyclerView.setAdapter(billAdapter);

        // Khởi tạo Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Bills");
        getBillsFromFirebase();
    }

    private void getBillsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                bills.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String billId = snapshot.getKey(); // Lấy ID hóa đơn
                    String bill = snapshot.child("bill").getValue(String.class); // Lấy giá trị hóa đơn
                    String status = snapshot.child("status").getValue(String.class); // Lấy trạng thái
                    // Kiểm tra xem giá trị có null không
                    if (bill != null && status != null) {
                        bills.add(new BillItem(billId, bill, status)); // Thêm hóa đơn vào danh sách
                    }
                }
                billAdapter.notifyDataSetChanged(); // Cập nhật adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Bill.this, "Không thể tải hóa đơn.", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void deleteBill(String billId) {
        // Tạo AlertDialog để hỏi người dùng trước khi xóa
        new AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa không?")
            .setPositiveButton("Có", (dialog, which) -> {
                // Nếu người dùng xác nhận xóa
                databaseReference.child(billId).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Bill.this, "Đơn hàng đã được xóa.", Toast.LENGTH_SHORT).show();
                        getBillsFromFirebase(); // Tải lại danh sách hóa đơn
                    } else {
                        Toast.makeText(Bill.this, "Lỗi khi xóa đơn hàng.", Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .setNegativeButton("Không", null) // Người dùng chọn không xóa
            .show();
    }
}
