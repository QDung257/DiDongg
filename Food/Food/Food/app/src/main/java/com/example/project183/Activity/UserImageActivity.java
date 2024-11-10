package com.example.project183.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project183.Adapter.ImageAdapter;
import com.example.project183.R;

import java.io.IOException;
import java.util.ArrayList;

public class UserImageActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private ArrayList<String> imageList;
    private ImageView back ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_huongdanuser);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        imageList = new ArrayList<>();
        loadUserImages(); // Load ảnh từ thư mục User

        imageAdapter = new ImageAdapter(this, imageList);
        recyclerView.setAdapter(imageAdapter);

        back = findViewById(R.id.backhdus);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(UserImageActivity.this, dangnhap.class);
                startActivity(i);
            }
        });

    }

    private void loadUserImages() {
        try {
            String[] images = getAssets().list("KH"); // Kiểm tra thư mục "User" trong assets
            if (images != null) {
                for (String image : images) {
                    imageList.add("KH/" + image); // Thêm đường dẫn ảnh vào danh sách
                }
            } else {
                System.out.println("Thư mục 'KH' không tồn tại hoặc không chứa ảnh nào.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Lỗi khi tải ảnh từ thư mục 'KH': " + e.getMessage());
        }
    }
}