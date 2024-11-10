package com.example.project183.Admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project183.Activity.Bill;
import com.example.project183.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Admin_donhang extends AppCompatActivity {

    private RecyclerView adminBillRecyclerView;
    private AdminBillAdapter adminBillAdapter;
    private ArrayList<BillItemAdmin> adminBills;
    private DatabaseReference databaseReference;
    private ImageView imgbackadm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_donhang);

        adminBillRecyclerView = findViewById(R.id.adminRecyclerView);

        adminBills = new ArrayList<>();
        adminBillAdapter = new AdminBillAdapter(adminBills, this::confirmBill,this::deleteBill);

        adminBillRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adminBillRecyclerView.setAdapter(adminBillAdapter);

        // Khởi tạo Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Bills");
        getAdminBillsFromFirebase();

        imgbackadm = findViewById(R.id.backbilladm);
        imgbackadm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void getAdminBillsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                adminBills.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String billId = snapshot.getKey(); // Lấy ID hóa đơn
                    String bill = snapshot.child("bill").getValue(String.class); // Lấy giá trị hóa đơn
                    String status = snapshot.child("status").getValue(String.class); // Lấy trạng thái
                    // Kiểm tra xem giá trị có null không
                    if (bill != null && status != null) {
                        adminBills.add(new BillItemAdmin(billId, bill, status)); // Thêm hóa đơn vào danh sách
                    }
                }
                adminBillAdapter.notifyDataSetChanged(); // Cập nhật adapter
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Admin_donhang.this, "Không thể tải hóa đơn.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmBill(String billId) {
        // Hiển thị hộp thoại xác nhận trước khi cập nhật trạng thái
        new AlertDialog.Builder(this)
        .setTitle("Xác nhận đơn hàng")
        .setMessage("Bạn có chắc muốn xác nhận đơn hàng này không?")
        .setPositiveButton("Có", (dialog, which) -> {
            // Cập nhật trạng thái đơn hàng thành "Đã xác nhận"
            databaseReference.child(billId).child("status").setValue("Đã xác nhận")
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Admin_donhang.this, "Xác nhận đơn hàng thành công!.", Toast.LENGTH_SHORT).show();
                            getAdminBillsFromFirebase(); // Tải lại danh sách hóa đơn
                        } else {
                            Toast.makeText(Admin_donhang.this, "Không thể cập nhật đơn hàng!.", Toast.LENGTH_SHORT).show();
                        }
                    });
        })
        .setNegativeButton("Không", null) // Nếu người dùng chọn không xác nhận
        .show();
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
                    Toast.makeText(Admin_donhang.this, "Đơn hàng đã được xóa.", Toast.LENGTH_SHORT).show();
                    getAdminBillsFromFirebase(); // Tải lại danh sách hóa đơn
                } else {
                    Toast.makeText(Admin_donhang.this, "Lỗi khi xóa đơn hàng.", Toast.LENGTH_SHORT).show();
                }
            });
        })
        .setNegativeButton("Không", null) // Người dùng chọn không xóa
        .show();
    }

}
