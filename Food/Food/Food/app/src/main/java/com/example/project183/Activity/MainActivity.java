package com.example.project183.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.project183.Adapter.CategoryAdapter;
import com.example.project183.Adapter.FoodListAdapter;
import com.example.project183.Adapter.SliderAdapter;
import com.example.project183.Admin.adminn;
import com.example.project183.Domain.Category;
import com.example.project183.Domain.Foods;
import com.example.project183.Domain.SliderItems;
import com.example.project183.Helper.ManagmentCart;
import com.example.project183.Profile.profile;
import com.example.project183.R;
import com.example.project183.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;

    private ImageView imgtuvan,imggopy,imgprf;
    private EditText timKiemTxt;
    private RecyclerView searchResultsView;
    private ArrayList<Foods> foodlists;
    private FoodListAdapter foodListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        initCategory();
        initBanner();

        bottomNavigation();

        imgprf =  findViewById(R.id.imgprf);
        imgtuvan = findViewById(R.id.imgtuvan);
        imggopy = findViewById(R.id.imggopy);
        imgprf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, profile.class);
                startActivity(i);
            }
        });

        imgtuvan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Liên hệ")
                        .setMessage("Bạn có chắc muốn liên hệ không?")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User clicked "Có", proceed with dialing
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                intent.setData(Uri.parse("tel:0343547805")); // Thay đổi số điện thoại tại đây
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton("Không", null) // Dismiss the dialog if "Không" is clicked
                        .show();
            }
        });

        imggopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:")); // Chỉ định rõ là gửi email
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"nqdung2507@gmail.com"}); // Thay đổi email tại đây
                intent.putExtra(Intent.EXTRA_SUBJECT, "Yêu cầu hỗ trợ"); // Chủ đề email
                intent.putExtra(Intent.EXTRA_TEXT, "Xin chào,\n\nTôi đang gặp một số vấn đề và cần sự trợ giúp của bạn.\n\nXin cảm ơn!"); // Nội dung email
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(Intent.createChooser(intent, "Send Email"));
                }

            }
        });

        timKiemTxt = findViewById(R.id.timkiemtxt);
        searchResultsView = findViewById(R.id.searchResultsView);

        foodlists = new ArrayList<>();
        foodListAdapter = new FoodListAdapter(foodlists);

        searchResultsView.setLayoutManager(new LinearLayoutManager(this));
        searchResultsView.setAdapter(foodListAdapter);

        timKiemTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Kiểm tra xem có kết quả tìm kiếm hay không
                if (s.length() > 0) {
                    searchResultsView.setVisibility(View.VISIBLE); // Hiển thị RecyclerView
                    searchProducts(s.toString()); // Gọi hàm tìm kiếm
                } else {
                    searchResultsView.setVisibility(View.GONE); // Ẩn RecyclerView nếu ô tìm kiếm trống
                    foodlists.clear(); // Xóa dữ liệu cũ
                    foodListAdapter.notifyDataSetChanged(); // Cập nhật adapter
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


    }


    private void searchProducts(String searchText) {
        DatabaseReference productsRef = FirebaseDatabase.getInstance().getReference("Foods");

        if (searchText.isEmpty()) {
            // Nếu ô tìm kiếm trống, làm rõ danh sách sản phẩm và cập nhật RecyclerView
            foodlists.clear(); // Xóa dữ liệu cũ trước khi cập nhật
            foodListAdapter.notifyDataSetChanged();
            return; // Kết thúc hàm nếu ô tìm kiếm trống
        }

        // Truy vấn sản phẩm theo tên
        Query query = productsRef.orderByChild("Title")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodlists.clear(); // Xóa dữ liệu cũ trước khi cập nhật

                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    Foods foods = productSnapshot.getValue(Foods.class);
                    if (foods != null) {
                        // Kiểm tra nếu tên sản phẩm chứa chuỗi tìm kiếm
                        if (foods.getTitle().contains(searchText)) {
                            foodlists.add(foods);
                        }
                    }
                }

                // Cập nhật RecyclerView
                foodListAdapter.notifyDataSetChanged();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Lỗi khi tìm kiếm sản phẩm", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void bottomNavigation() {
        binding.btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NhanTin.class);
                startActivity(intent);
            }
        });
        binding.btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ManagmentCart managmentCart = new ManagmentCart(MainActivity.this);
                if (!managmentCart.getListCart().isEmpty()){
                    Intent intent = new Intent(MainActivity.this, CartActivity.class);
                    startActivity(intent);
                } else{
                    Toast.makeText(MainActivity.this,"Giỏ hàng đang trống!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Favorite.class);
                startActivity(intent);
            }
        });

        binding.btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Bill.class);
                startActivity(intent);
            }
        });
        binding.btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, profile.class);
                startActivity(intent);
            }
        });


    }



    private void initBanner() {
        DatabaseReference myRef=database.getReference("Banners");
        binding.progressBarBanner.setVisibility(View.VISIBLE);
        ArrayList<SliderItems> items= new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot issue:snapshot.getChildren()){
                        items.add(issue.getValue(SliderItems.class));
                    }
                    banner(items);
                    binding.progressBarBanner.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void banner(ArrayList<SliderItems> items){
        binding.viewpager2.setAdapter(new SliderAdapter(items,binding.viewpager2));
        binding.viewpager2.setClipChildren(false);
        binding.viewpager2.setClipToPadding(false);
        binding.viewpager2.setOffscreenPageLimit(3);
        binding.viewpager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));

        binding.viewpager2.setPageTransformer(compositePageTransformer);
    }


    private void initCategory() {
        DatabaseReference myRef=database.getReference("Category");
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        ArrayList<Category> list=new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot issue:snapshot.getChildren()) {
                        list.add(issue.getValue(Category.class));


                    }
                    if (list.size()>0){
                        binding.categoryView.setLayoutManager(new GridLayoutManager(MainActivity.this,3));
                        binding.categoryView.setAdapter(new CategoryAdapter(list));
                    }
                    binding.progressBarCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}