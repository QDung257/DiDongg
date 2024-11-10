package com.example.project183.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project183.Activity.dangnhap;
import com.example.project183.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

public class profile extends AppCompatActivity {

    private TextView tvGreeting;
    private TextView tvBalance;
    private Button btnDeposit;
    private Button btnEditProfile;
    private Button logoutBtn;
    private ImageView backprf;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        tvGreeting = findViewById(R.id.tvGreeting);
        tvBalance = findViewById(R.id.tvBalance);
        btnDeposit = findViewById(R.id.btnDeposit);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        logoutBtn = findViewById(R.id.logoutBtn);
        backprf = findViewById(R.id.backprf);

        // Khởi tạo Firebase Auth và Database Reference
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Lấy tên người dùng và số dư từ Firebase
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String username = snapshot.child("username").getValue(String.class); // Đổi tên từ name thành username
                        Long balance = snapshot.child("balance").getValue(Long.class);

                        // Cập nhật TextView với dữ liệu từ Firebase
                        tvGreeting.setText("Xin chào, " + username);
                        tvBalance.setText("Số dư tài khoản: " + balance + " VND");
                    } else {
                        Toast.makeText(profile.this, "Không tìm thấy dữ liệu người dùng", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(profile.this, "Lỗi khi lấy dữ liệu từ Firebase", Toast.LENGTH_SHORT).show();
                }
            });
        }

        backprf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Thiết lập sự kiện click cho các nút
        btnDeposit.setOnClickListener(v -> {
            Intent intent = new Intent(profile.this, activity_deposit.class);
            startActivity(intent);
        });

        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(profile.this, activity_edit_profile.class);
            startActivity(intent);
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo AlertDialog để xác nhận đăng xuất
                new AlertDialog.Builder(profile.this)
                        .setTitle("Xác nhận đăng xuất")
                        .setMessage("Bạn có chắc muốn đăng xuất không?")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Thực hiện đăng xuất khỏi Firebase
                                FirebaseAuth.getInstance().signOut();

                                // Hiển thị thông báo đăng xuất thành công
                                Toast.makeText(profile.this, "Bạn đã đăng xuất", Toast.LENGTH_SHORT).show();

                                // Chuyển về màn hình đăng nhập
                                Intent loginIntent = new Intent(profile.this, dangnhap.class);
                                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(loginIntent);

                                // Kết thúc activity hiện tại để không quay lại khi nhấn nút back
                                finish();
                            }
                        })
                        .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // Đóng hộp thoại
                            }
                        })
                        .show();
            }
        });
    }
}
