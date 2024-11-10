package com.example.project183.Activity;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.project183.Adapter.CartAdapter;
import com.example.project183.Domain.Foods;
import com.example.project183.Helper.ManagmentCart;
import com.example.project183.R;
import com.example.project183.databinding.ActivityCartBinding;

import java.util.ArrayList;

public class CartActivity extends BaseActivity {
    ActivityCartBinding binding;
    private ManagmentCart managmentCart;
    private Button checkoutbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);

        setVariable();
        calculateCart();
        initCartList();

        checkoutbtn = findViewById(R.id.checkOutBtn);
        checkoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proceedToCheckout(); // Call the method to handle checkout
            }
        });



    }

    private void proceedToCheckout() {
        ArrayList<Foods> cartItems = managmentCart.getListCart();
        Intent intent = new Intent(CartActivity.this, Donhang.class);

        // Pass the list of food items to DonHangActivity
        intent.putExtra("cartItems", cartItems);
        startActivity(intent);
    }

    private void initCartList() {
        if(managmentCart.getListCart().isEmpty()){
            binding.Emptytxt.setVisibility(View.VISIBLE);
            binding.scrollViewCart.setVisibility(View.GONE);
        }else {
            binding.Emptytxt.setVisibility(View.GONE);
            binding.scrollViewCart.setVisibility(View.VISIBLE);
        }
        binding.cartView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        binding.cartView.setAdapter(new CartAdapter(managmentCart.getListCart(), managmentCart, () -> calculateCart()));
    }

    private void calculateCart() {

        double percentTax = 0.02; //2% thuế
        double delivery = 20000; //20000
        double tax = Math.round(managmentCart.getTotalFee() * percentTax );
        double itemTotal = Math.round(managmentCart.getTotalFee());
        double total = Math.round((managmentCart.getTotalFee() + tax + delivery));



        binding.totalfeetxt.setText(String.format("%.0f VNĐ",itemTotal));
        binding.taxtxt.setText(String.format("%.0f VNĐ", tax));
        binding.deliverytxt.setText(String.format("%.0f VNĐ",delivery));
        binding.totaltxt.setText(String.format("%.0f VNĐ",total));
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(view -> startActivity(new Intent(CartActivity.this,MainActivity.class)));

    }
}