package com.example.project183.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project183.Activity.dangnhap;
import com.example.project183.Adapter.ImageAdapter;
import com.example.project183.R;

import java.io.IOException;
import java.util.ArrayList;

public class AdminImageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private ArrayList<String> imageList;
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huongdan);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        imageList = new ArrayList<>();
        loadAdminImages(); // Load ảnh từ thư mục Admin

        imageAdapter = new ImageAdapter(this, imageList);
        recyclerView.setAdapter(imageAdapter);

        back = findViewById(R.id.backhdad);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AdminImageActivity.this, dangnhap.class);
                startActivity(i);
            }
        });

    }

    private void loadAdminImages() {
        try {
            String[] images = getAssets().list("Admin"); // Kiểm tra thư mục "Admin" trong assets
            if (images != null) {
                for (String image : images) {
                    imageList.add("Admin/" + image); // Thêm đường dẫn ảnh vào danh sách
                }
            } else {
                System.out.println("Thư mục 'Admin' không tồn tại hoặc không chứa ảnh nào.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi tải ảnh từ thư mục 'Admin': " + e.getMessage());
        }
    }
}