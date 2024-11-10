package com.example.project183.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project183.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class quenmk extends AppCompatActivity {
ImageView imgthoat;
EditText editemail;
Button btnmk;
FirebaseAuth auth;
ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quenmk);


        imgthoat = findViewById(R.id.imgback2);
        editemail = findViewById(R.id.emailquenmk);
        btnmk = findViewById(R.id.btn_quenmk);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();


        imgthoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(quenmk.this,dangnhap.class);
                startActivity(i);
                finish();
            }
        });

        btnmk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editemail.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplication(), "Vui lòng điền tài khoản Email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(quenmk.this, "Chúng tôi đã gửi hướng dẫn đổi mật khẩu!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(quenmk.this, "Lỗi khi gửi tin đến Email!", Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });

    }
}