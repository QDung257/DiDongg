package com.example.project183.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.project183.Activity.BaseActivity;
import com.example.project183.Activity.CartActivity;
import com.example.project183.Domain.Foods;
import com.example.project183.Helper.ManagmentCart;
import com.example.project183.R;
import com.example.project183.databinding.ActivityAdminDetailBinding;
import com.example.project183.databinding.ActivityDetailBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;

public class AdminDetailActivity extends BaseActivity {
    ActivityAdminDetailBinding binding;
    private Foods object;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        setVariable();
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());



        Glide.with(this)
                .load(object.getImagePath())
                .transform(new CenterCrop(), new RoundedCorners(60))
                .into(binding.pic);

        binding.priceTxt.setText(String.format("%.0f VNĐ", object.getPrice()));
        binding.titleTxt.setText(object.getTitle());
        binding.descriptionTxt.setText(object.getDescription());
        binding.ratingTxt.setText(object.getStar() + " Xếp Hạng");
        binding.ratingBar.setRating((float) object.getStar());
        binding.timeTxt.setText(object.getTimeValue() + " Phút");


    }


    private void getIntentExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
    }
}
