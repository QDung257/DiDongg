package com.example.project183.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project183.Adapter.FoodListAdapter;
import com.example.project183.Domain.Foods;
import com.example.project183.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Favorite extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FoodListAdapter adapter;
    private TextView noFavoriteText;
    private ImageView backBtn;
    private Button clearFavoritesBtn;

    private DatabaseReference databaseReference;
    private ArrayList<Foods> favoriteFoods; // Danh sách món ăn yêu thích

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        recyclerView = findViewById(R.id.foodListView);
        noFavoriteText = findViewById(R.id.noFavoriteText);
        backBtn = findViewById(R.id.backBtn);
        clearFavoritesBtn = findViewById(R.id.clearFavoritesBtn);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        databaseReference = FirebaseDatabase.getInstance().getReference("favoriteFoods"); // Khởi tạo Firebase reference
        favoriteFoods = new ArrayList<>(); // Khởi tạo danh sách món ăn yêu thích

        adapter = new FoodListAdapter(favoriteFoods); // Khởi tạo adapter
        recyclerView.setAdapter(adapter); // Đặt adapter cho RecyclerView

        // Lấy dữ liệu từ Firebase
        getFavoriteFoodsFromFirebase();

        backBtn.setOnClickListener(v -> finish());

        clearFavoritesBtn.setOnClickListener(view -> {
            DetailActivity.clearFavoriteFoods(); // Xóa danh sách món yêu thích
            databaseReference.removeValue(); // Xóa dữ liệu trong Firebase
            adapter.notifyDataSetChanged(); // Thông báo adapter đã thay đổi
            updateClearButtonVisibility(); // Cập nhật trạng thái hiển thị của nút xóa
            noFavoriteText.setVisibility(View.VISIBLE); // Hiển thị thông báo không có món yêu thích nào
        });
    }

    // Phương thức lấy dữ liệu món ăn yêu thích từ Firebase
    private void getFavoriteFoodsFromFirebase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                favoriteFoods.clear(); // Xóa danh sách cũ
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Foods food = snapshot.getValue(Foods.class); // Lấy dữ liệu món ăn
                    if (food != null) {
                        favoriteFoods.add(food); // Thêm món ăn vào danh sách
                    }
                }
                adapter.notifyDataSetChanged(); // Cập nhật adapter
                updateClearButtonVisibility(); // Cập nhật trạng thái hiển thị của nút xóa
                // Kiểm tra và cập nhật hiển thị của RecyclerView và thông báo
                if (favoriteFoods.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    noFavoriteText.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    noFavoriteText.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu cần
            }
        });
    }

    // Phương thức cập nhật trạng thái hiển thị của nút xóa
    private void updateClearButtonVisibility() {
        if (favoriteFoods.isEmpty()) {
            clearFavoritesBtn.setVisibility(View.GONE);
        } else {
            clearFavoritesBtn.setVisibility(View.VISIBLE);
        }
    }
}
