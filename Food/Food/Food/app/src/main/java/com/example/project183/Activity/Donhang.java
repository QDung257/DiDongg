package com.example.project183.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project183.Domain.Foods;
import com.example.project183.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Donhang extends AppCompatActivity {

    private TextView billDetails;
    private Button confirmOrderButton, confirmOrderButton1;
    private ArrayList<Foods> cartItems;
    private DatabaseReference databaseReference;
    private String generatedBill;
    private ArrayList<String> bills;
    private double totalAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donhang);

        billDetails = findViewById(R.id.billDetails);
        confirmOrderButton = findViewById(R.id.confirmOrderButton);
        confirmOrderButton1 = findViewById(R.id.confirmOrderButton1); // Thêm tham chiếu cho nút mới

        databaseReference = FirebaseDatabase.getInstance().getReference("Bills");
        cartItems = (ArrayList<Foods>) getIntent().getSerializableExtra("cartItems");
        bills = new ArrayList<>();

        generateBill();

        confirmOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Tạo AlertDialog để xác nhận
                new AlertDialog.Builder(Donhang.this)
                        .setTitle("Xác nhận đơn hàng")
                        .setMessage("Bạn có chắc chắn xác nhận không?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            // Nếu người dùng chọn "Có", lưu hóa đơn vào Firebase
                            saveBillToFirebase(generatedBill);
                            bills.add(generatedBill); // Thêm hóa đơn vào danh sách
                            Intent intent = new Intent(Donhang.this, Bill.class);
                            startActivity(intent);
                        })
                        .setNegativeButton("Không", (dialog, which) -> {
                            // Nếu người dùng chọn "Không", chỉ cần đóng dialog
                            dialog.dismiss();
                        })
                        .show();
            }
        });

        // Sự kiện bấm vào nút mới để trừ tiền từ tài khoản
        confirmOrderButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cartItems == null || cartItems.isEmpty()) {
                    Toast.makeText(Donhang.this, "Giỏ hàng của bạn đang trống. Không thể thực hiện thanh toán.", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(Donhang.this)
                        .setTitle("Xác nhận thanh toán")
                        .setMessage("Bạn có chắc chắn muốn thanh toán và trừ tiền từ tài khoản không?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            processAccountDeduction();
                        })
                        .setNegativeButton("Không", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });
    }

    private void processAccountDeduction() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        userRef.child("balance").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long currentBalance = snapshot.getValue(Long.class);
                    long newBalance = currentBalance - (long) totalAmount;

                    if (newBalance >= 0) {
                        userRef.child("balance").setValue(newBalance)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        saveBillToFirebase(generatedBill);
                                        bills.add(generatedBill);

                                        new AlertDialog.Builder(Donhang.this)
                                                .setTitle("Thành công")
                                                .setMessage("Thanh toán thành công!\nSố dư mới: " + newBalance + " VNĐ")
                                                .setPositiveButton("OK", (dialog, which) -> {
                                                    Intent intent = new Intent(Donhang.this, Bill.class);
                                                    startActivity(intent);
                                                    finish();
                                                })
                                                .show();
                                    } else {
                                        Toast.makeText(Donhang.this, "Lỗi khi cập nhật số dư", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(Donhang.this, "Số dư không đủ để thanh toán", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Donhang.this, "Không tìm thấy thông tin tài khoản", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Donhang.this, "Lỗi khi kiểm tra số dư", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateBill() {
        if (cartItems != null && !cartItems.isEmpty()) {
            StringBuilder bill = new StringBuilder();
            totalAmount = 0;

            for (Foods food : cartItems) {
                int quantity = food.getNumberInCart();
                double itemTotalPrice = food.getPrice() * quantity;

                bill.append(food.getTitle())
                        .append(" x").append(quantity)
                        .append(" - ").append(String.format("%.0f", itemTotalPrice))
                        .append(" VNĐ\n");

                totalAmount += itemTotalPrice;
            }

            double percentTax = 0.02;
            double deliveryFee = 20000;
            double tax = totalAmount * percentTax;
            totalAmount = totalAmount + tax + deliveryFee;

            bill.append("\nTổng cộng: ").append(String.format("%.0f", totalAmount - tax - deliveryFee)).append(" VNĐ");
            bill.append("\nPhí giao hàng: ").append(String.format("%.0f", deliveryFee)).append(" VNĐ");
            bill.append("\nThuế (2%): ").append(String.format("%.0f", tax)).append(" VNĐ");
            bill.append("\nTổng tiền: ").append(String.format("%.0f", totalAmount)).append(" VNĐ");

            generatedBill = bill.toString();
            billDetails.setText(generatedBill);
        } else {
            billDetails.setText("Giỏ hàng của bạn đang trống. ");
        }
    }

    private void saveBillToFirebase(String bill) {
        String billId = databaseReference.push().getKey();
        if (billId != null) {
            HashMap<String, Object> billData = new HashMap<>();
            billData.put("bill", bill);
            billData.put("status", "Đang chờ xác nhận");

            databaseReference.child(billId).setValue(billData).addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Toast.makeText(Donhang.this, "Lỗi khi lưu hóa đơn. ", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
