package com.example.project183.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project183.Profile.User;
import com.example.project183.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class Dangky extends AppCompatActivity {
    ImageView imgback, imgdangki;
    TextView textdangnhapp, quenmkhau1;
    EditText editmail, editpass, editName;
    FirebaseAuth auth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangky);

        auth = FirebaseAuth.getInstance();

        quenmkhau1 = findViewById(R.id.quenmatkhau1);
        imgback = findViewById(R.id.back);
        imgdangki = findViewById((R.id.nutdangnhap1));
        textdangnhapp = findViewById(R.id.chuyendangnhap);
        editmail = findViewById(R.id.email1);
        editpass = findViewById(R.id.password1);
        editName = findViewById(R.id.nameuser);
        progressBar = findViewById(R.id.progressBar);

        quenmkhau1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Dangky.this, quenmk.class);
                startActivity(i);
                finish();
            }
        });

        imgdangki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editmail.getText().toString().trim();
                String password = editpass.getText().toString().trim();
                String name = editName.getText().toString().trim(); // Lấy tên người dùng

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập Email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập Password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(name)) { // Kiểm tra tên không rỗng
                    Toast.makeText(getApplicationContext(), "Vui lòng nhập tên!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Mật khẩu quá ngắn! Hãy nhập 6 kí tự trở lên!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Hiển thị progressBar khi đang đăng ký
                progressBar.setVisibility(View.VISIBLE);

                // Tạo tài khoản
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Dangky.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);  // Ẩn progressBar khi hoàn thành

                        if (task.isSuccessful()) {
                            // Lưu tên người dùng vào Firebase Database
                            saveUserData(name, email);

                            Toast.makeText(Dangky.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Dangky.this, dangnhap.class);
                            startActivity(intent);
                            finish();
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(Dangky.this, "Đăng ký không thành công: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        imgback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        textdangnhapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dangky.this, dangnhap.class));
                finish();
            }
        });
    }

    private void saveUserData(String name, String email) {
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Tạo đối tượng User và lưu vào Firebase
        User user = new User(name, email, 0); // Giả sử số dư khởi tạo là 0
        userRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(Dangky.this, "Thông tin người dùng đã được lưu!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Dangky.this, "Lỗi khi lưu thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
