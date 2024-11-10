package com.example.project183.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.project183.Activity.BaseActivity;
import com.example.project183.Adapter.CategoryAdapter;
import com.example.project183.Adapter.FoodListAdapter;
import com.example.project183.Adapter.SliderAdapter;
import com.example.project183.Domain.Category;
import com.example.project183.Domain.Foods;
import com.example.project183.Domain.SliderItems;
import com.example.project183.Profile.profile;
import com.example.project183.R;
import com.example.project183.databinding.ActivityAdminnBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class adminn extends BaseActivity {
    ActivityAdminnBinding binding;

    private EditText timKiemTxt;
    private RecyclerView searchResultsView;
    private ArrayList<Foods> foodlists;
    private AdminFoodListAdapter adminfoodListAdapter;
    private ImageView imgprf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initCategory();
        initBanner();
        bottomNavigation();

        timKiemTxt = findViewById(R.id.timkiemtxtadm);
        searchResultsView = findViewById(R.id.searchResultsViewadm);
        imgprf = findViewById(R.id.imgprfadm);

        foodlists = new ArrayList<>();
        adminfoodListAdapter = new AdminFoodListAdapter(foodlists);

        searchResultsView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsView.setAdapter(adminfoodListAdapter);

        timKiemTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) {
                    searchResultsView.setVisibility(View.VISIBLE);
                    searchProducts(s.toString());
                } else {
                    searchResultsView.setVisibility(View.GONE);
                    foodlists.clear();
                    adminfoodListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        imgprf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(adminn.this, profile.class);
                startActivity(i);
            }
        });

    }

    private void searchProducts(String searchText) {
        DatabaseReference productsRef = database.getReference("Foods");

        if (searchText.isEmpty()) {
            foodlists.clear();
            adminfoodListAdapter.notifyDataSetChanged();
            return;
        }

        Query query = productsRef.orderByChild("Title")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodlists.clear();
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Foods foods = productSnapshot.getValue(Foods.class);
                    if (foods != null && foods.getTitle().contains(searchText)) {
                        foodlists.add(foods);
                    }
                }
                adminfoodListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(adminn.this, "Lỗi khi tìm kiếm sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void bottomNavigation() {
        binding.btn1adm.setOnClickListener(view -> {
            Intent intent = new Intent(adminn.this, NhanTinAdmin.class);
            startActivity(intent);
        });
        binding.btn2adm.setOnClickListener(view -> {
            Intent intent = new Intent(adminn.this, Admin_donhang.class);
            startActivity(intent);
        });

        binding.btn3adm.setOnClickListener(view -> {
            Intent intent = new Intent(adminn.this, profile.class);
            startActivity(intent);
        });



    }

    private void initBanner() {
        DatabaseReference myRef = database.getReference("Banners");
        binding.progressBarBanneradm.setVisibility(View.VISIBLE);
        ArrayList<SliderItems> items = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(SliderItems.class));
                    }
                    banner(items);
                }
                binding.progressBarBanneradm.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(adminn.this, "Lỗi khi tải banner", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void banner(ArrayList<SliderItems> items) {
        binding.viewpager2adm.setAdapter(new AdminSliderAdapter(items, binding.viewpager2adm));
        binding.viewpager2adm.setClipChildren(false);
        binding.viewpager2adm.setClipToPadding(false);
        binding.viewpager2adm.setOffscreenPageLimit(3);
        binding.viewpager2adm.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));

        binding.viewpager2adm.setPageTransformer(compositePageTransformer);
    }

    private void initCategory() {
        DatabaseReference myRef = database.getReference("Category");
        binding.progressBarCategoryadm.setVisibility(View.VISIBLE);
        ArrayList<Category> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Category.class));
                    }
                    if (list.size() > 0) {
                        binding.categoryViewadm.setLayoutManager(new GridLayoutManager(adminn.this, 3));
                        binding.categoryViewadm.setAdapter(new AdminCategoryAdapter(list));
                    }
                }
                binding.progressBarCategoryadm.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(adminn.this, "Lỗi khi tải danh mục", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
