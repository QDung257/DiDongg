package com.example.project183.Profile;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project183.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class activity_deposit extends AppCompatActivity {

    private TextView tvCurrentBalance;
    private EditText etDepositAmount;
    private Button btnConfirmDeposit;
    private ImageView backprf;

    private DatabaseReference userRef;
    private FirebaseAuth mAuth;
    private long currentBalance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        // Khởi tạo các view
        tvCurrentBalance = findViewById(R.id.tvCurrentBalance);
        etDepositAmount = findViewById(R.id.etDepositAmount);
        btnConfirmDeposit = findViewById(R.id.btnConfirmDeposit);
        backprf = findViewById(R.id.backprf1);

        // Thiết lập Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Lấy số dư hiện tại từ Firebase và hiển thị
            userRef.child("balance").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        currentBalance = snapshot.getValue(Long.class); // Lấy số dư từ Firebase
                        tvCurrentBalance.setText("Số dư hiện tại: " + currentBalance + " VND");
                    } else {
                        tvCurrentBalance.setText("Số dư hiện tại: 0 VND");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(activity_deposit.this, "Lỗi khi lấy số dư", Toast.LENGTH_SHORT).show();
                }
            });
        }
        backprf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Xử lý sự kiện khi người dùng nhấn nút Xác nhận
        btnConfirmDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String amountStr = etDepositAmount.getText().toString().trim();

                if (!amountStr.isEmpty()) {
                    long depositAmount = Long.parseLong(amountStr);

                    if (depositAmount > 0) {
                        // Thêm số tiền vào số dư hiện tại
                        updateBalance(depositAmount);
                    } else {
                        Toast.makeText(activity_deposit.this, "Số tiền phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(activity_deposit.this, "Vui lòng nhập số tiền", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateBalance(long depositAmount) {
        long newBalance = currentBalance + depositAmount;

        // Cập nhật số dư mới trong Firebase
        userRef.child("balance").setValue(newBalance).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(activity_deposit.this, "Nạp tiền thành công", Toast.LENGTH_SHORT).show();
                tvCurrentBalance.setText("Số dư hiện tại: " + newBalance + " VND");
                finish(); // Quay lại màn hình trước sau khi nạp tiền thành công
            } else {
                Toast.makeText(activity_deposit.this, "Lỗi khi nạp tiền", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


