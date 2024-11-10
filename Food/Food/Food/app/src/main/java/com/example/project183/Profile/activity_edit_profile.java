package com.example.project183.Profile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project183.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class activity_edit_profile extends AppCompatActivity {

    private EditText etNewName;
    private EditText etNewPassword;
    private Button btnSaveChanges;
    private ImageView backprf;

    private DatabaseReference userRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Khởi tạo các view
        etNewName = findViewById(R.id.etNewName);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        backprf = findViewById(R.id.backprf2);

        // Thiết lập Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        }

        backprf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Xử lý sự kiện khi người dùng nhấn nút Lưu thay đổi
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = etNewName.getText().toString().trim();
                String newPassword = etNewPassword.getText().toString().trim();

                if (!newName.isEmpty()) {
                    updateName(newName);
                }

                if (!newPassword.isEmpty()) {
                    updatePassword(newPassword);
                }
            }
        });
    }

    private void updateName(String newName) {
        // Cập nhật tên mới vào Firebase Database
        userRef.child("username").setValue(newName).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(activity_edit_profile.this, "Cập nhật tên thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(activity_edit_profile.this, "Lỗi khi cập nhật tên", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePassword(String newPassword) {
        // Cập nhật mật khẩu mới vào Firebase Authentication
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(activity_edit_profile.this, "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity_edit_profile.this, "Lỗi khi cập nhật mật khẩu", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
