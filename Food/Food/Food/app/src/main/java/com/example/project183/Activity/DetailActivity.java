package com.example.project183.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.project183.Domain.Foods;
import com.example.project183.Helper.ManagmentCart;
import com.example.project183.R;
import com.example.project183.databinding.ActivityDetailBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class DetailActivity extends BaseActivity {
    ActivityDetailBinding binding;
    private Foods object;
    private int num = 1;
    private ManagmentCart managmentCart;
    private ImageView favoriteButton;
    private Foods currentFood; // Current food item

    private static ArrayList<Foods> favoriteFoods = new ArrayList<>(); // Favorite foods list
    private DatabaseReference databaseReference; // Firebase database reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        databaseReference = FirebaseDatabase.getInstance().getReference("favoriteFoods");

        getIntentExtra();
        setVariable();
        loadFavoritesFromFirebase(); // Load favorites on activity start
    }

    private void setVariable() {
        managmentCart = new ManagmentCart(this);
        binding.backBtn.setOnClickListener(v -> finish());

        binding.imgcart.setOnClickListener(view -> {
            if (!managmentCart.getListCart().isEmpty()) {
                startActivity(new Intent(DetailActivity.this, CartActivity.class));
            } else {
                Toast.makeText(DetailActivity.this, "Giỏ hàng đang trống!", Toast.LENGTH_SHORT).show();
            }
        });

        Glide.with(this)
                .load(object.getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(60))
                .into(binding.pic);

        binding.priceTxt.setText(String.format("%.0f VNĐ", object.getPrice()));
        binding.titleTxt.setText(object.getTitle());
        binding.descriptionTxt.setText(object.getDescription());
        binding.ratingTxt.setText(object.getStar() + " Xếp Hạng");
        binding.ratingBar.setRating((float) object.getStar());
        binding.totalTxt.setText(String.format("%.0f VNĐ", object.getPrice()));
        binding.timeTxt.setText(object.getTimeValue() + " Phút");

        binding.plusBtn.setOnClickListener(v -> {
            num++;
            binding.numTxt.setText(num + " ");
            binding.totalTxt.setText(String.format("%.0f VNĐ", num * object.getPrice()));
        });

        binding.minusBtn.setOnClickListener(v -> {
            if (num > 1) {
                num--;
                binding.numTxt.setText(num + "");
                binding.totalTxt.setText(String.format("%.0f VNĐ", num * object.getPrice()));
            }
        });

        binding.addBtn.setOnClickListener(v -> {
            object.setNumberInCart(num);
            managmentCart.insertFood(object);
        });

        currentFood = (Foods) getIntent().getSerializableExtra("object");

        favoriteButton = findViewById(R.id.imgyeuthich);
        favoriteButton.setOnClickListener(v -> {
            if (isFoodInFavorites(currentFood)) {
                Toast.makeText(DetailActivity.this, "Món ăn đã có trong danh sách yêu thích!", Toast.LENGTH_SHORT).show();
            } else {
                favoriteFoods.add(currentFood);
                saveFavoriteToFirebase(currentFood);
                Toast.makeText(DetailActivity.this, "Đã thêm vào danh sách yêu thích!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isFoodInFavorites(Foods food) {
        for (Foods f : favoriteFoods) {
            if (f.getId() == food.getId()) {
                return true;
            }
        }
        return false;
    }

    private void saveFavoriteToFirebase(Foods food) {
        // Lưu món ăn yêu thích vào Firebase
        databaseReference.child(String.valueOf(food.getId())).setValue(food).addOnCompleteListener(task -> {
        });
    }


    private void loadFavoritesFromFirebase() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                favoriteFoods.clear(); // Xóa dữ liệu cũ trước khi cập nhật
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Foods food = dataSnapshot.getValue(Foods.class);
                    if (food != null) {
                        favoriteFoods.add(food);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(DetailActivity.this, "Không thể truy xuất danh sách yêu thích", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static ArrayList<Foods> getFavoriteFoods() {
        return favoriteFoods;
    }

    public static void clearFavoriteFoods() {
        favoriteFoods.clear();
    }

    private void getIntentExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
    }
}
