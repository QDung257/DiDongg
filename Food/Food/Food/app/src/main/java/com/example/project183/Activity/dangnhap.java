package com.example.project183.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project183.Admin.AdminImageActivity;
import com.example.project183.Admin.adminn;
import com.example.project183.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class dangnhap extends AppCompatActivity {

    EditText emailedit, passedit;
    ImageView dangnhap;
    TextView textdangky, quenmkhau;
    ImageView huongdan;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dangnhap);
        auth = FirebaseAuth.getInstance();

        huongdan = findViewById(R.id.huongdan);
        dangnhap = findViewById(R.id.nutdangnhap);
        textdangky = findViewById(R.id.chuyendangky);
        emailedit = findViewById(R.id.email);
        passedit = findViewById(R.id.password);
        quenmkhau = findViewById(R.id.quenmatkhau);

        huongdan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserTypeDialog();
            }
        });

        quenmkhau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(dangnhap.this, quenmk.class);
                startActivity(i);
                finish();
            }
        });

        dangnhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        textdangky.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(dangnhap.this, Dangky.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void showUserTypeDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_user_type);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ImageView adminImageView = dialog.findViewById(R.id.adminImageView);
        ImageView userImageView = dialog.findViewById(R.id.userImageView);

        adminImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình Admin
                Intent intent = new Intent(dangnhap.this, AdminImageActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        userImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển đến màn hình Người dùng
                Intent intent = new Intent(dangnhap.this, UserImageActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void login() {
        String email = emailedit.getText().toString();
        String pass = passedit.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (pass.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập Password!", Toast.LENGTH_SHORT).show();
            return;
        }

        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String adminEmail = "admin@gmail.com";

                    if (email.equals(adminEmail)) {
                        Toast.makeText(getApplicationContext(), "Đăng nhập thành công với tư cách admin!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(dangnhap.this, adminn.class);
                        startActivity(i);
                    } else {
                        Toast.makeText(getApplicationContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(dangnhap.this, MainActivity.class);
                        startActivity(i);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Tài khoản hoặc mật khẩu sai!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
