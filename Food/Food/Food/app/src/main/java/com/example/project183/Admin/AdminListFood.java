package com.example.project183.Admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project183.Activity.BaseActivity;
import com.example.project183.Adapter.FoodListAdapter;
import com.example.project183.Domain.Foods;
import com.example.project183.R;
import com.example.project183.databinding.ActivityAdminListFoodBinding;
import com.example.project183.databinding.ActivityListFoodBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class AdminListFood extends BaseActivity {
    ActivityAdminListFoodBinding binding;
    private int categoryId;
    private String categoryName;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private ImageView imgFood;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityAdminListFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://dacn-9074a.appspot.com");

        getIntentExtra();
        initList();
        setupAddFoodButton();

        imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri selectedImageUri = result.getData().getData();
                imgFood.setImageURI(selectedImageUri);  // Display the selected image in ImageView
            }
        });
    }
    private void setupAddFoodButton() {
        binding.btnAddadm.setOnClickListener(v -> {
            addFoodDialog();
        });
    }

    private void addFoodDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_food, null);  // Use your existing add_food.xml
        dialogBuilder.setView(dialogView);

        EditText foodDescriptionEditText = dialogView.findViewById(R.id.edit_description);
        EditText foodPriceEditText = dialogView.findViewById(R.id.edit_price);
        EditText foodTimeValueEditText = dialogView.findViewById(R.id.edit_time);
        EditText starEdittext = dialogView.findViewById(R.id.edit_star);
        EditText foodTitleEditText = dialogView.findViewById(R.id.edit_title);
        imgFood = dialogView.findViewById(R.id.imgHinh);
        Button saveButton = dialogView.findViewById(R.id.btn_save);

        imgFood.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);  // Launch the image picker
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.setCancelable(true);

        // Firebase reference for food ID
        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Foods");
        Query query = foodRef.orderByKey().limitToLast(1); // Query to get the last node based on key (ID)
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Long currentId = 0L;  // Default ID if no data exists

                for (DataSnapshot child : snapshot.getChildren()) {
                    // Parse the last ID as Long, assuming the key is the ID
                    currentId = Long.parseLong(child.getKey());
                }

                Long newId = currentId + 1;  // Increment ID by 1 for the new item
                // Proceed to save the new food item with this newId

                saveButton.setOnClickListener(v -> {
                    // Get data from EditText fields
                    String foodDescription = foodDescriptionEditText.getText().toString().trim();
                    String foodPrice = foodPriceEditText.getText().toString().trim();
                    String foodTimeValue = foodTimeValueEditText.getText().toString().trim();
                    String starText = starEdittext.getText().toString().trim();
                    String foodTitle = foodTitleEditText.getText().toString().trim();

                    // Check if any required field is empty
                    if (foodDescription.isEmpty() || foodPrice.isEmpty() || foodTimeValue.isEmpty() || starText.isEmpty() || foodTitle.isEmpty()) {
                        Toast.makeText(AdminListFood.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                        return;  // Stop further execution if any field is empty
                    }

                    // Continue with the rest of the code if all fields are filled
                    Double star = Double.valueOf(starText);

                    // Upload the image to Firebase Storage
                    Calendar calendar = Calendar.getInstance();
                    StorageReference imageRef = storageRef.child("food_image_" + calendar.getTimeInMillis() + ".png");
                    imgFood.setDrawingCacheEnabled(true);
                    imgFood.buildDrawingCache();
                    Bitmap bitmap = imgFood.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = imageRef.putBytes(data);
                    uploadTask.addOnFailureListener(e -> {
                        Toast.makeText(AdminListFood.this, "Lỗi khi tải ảnh!", Toast.LENGTH_SHORT).show();
                    }).addOnSuccessListener(taskSnapshot -> {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            Toast.makeText(AdminListFood.this, "Lưu ảnh: " + imageUrl, Toast.LENGTH_SHORT).show();

                            // Create a new Foods object with all the fields
                            Foods newFood = new Foods(
                                    newId,
                                    foodDescription,
                                    false,
                                    categoryId,
                                    Double.parseDouble(foodPrice),
                                    imageUrl,
                                    1,
                                    1,
                                    1,
                                    foodTimeValue,
                                    star,
                                    foodTitle
                            );

                            // Save the new Foods object to Firebase Database
                            //DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Foods");
                            foodRef.child(String.valueOf(newId)).setValue(newFood, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                                    if (error == null) {
                                        Toast.makeText(AdminListFood.this, "Lưu dữ liệu thành công", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(AdminListFood.this, "Lỗi lưu dữ liệu", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            dialog.dismiss();
                        }).addOnFailureListener(e -> {
                            Toast.makeText(AdminListFood.this, "Không lấy được hình ảnh!", Toast.LENGTH_SHORT).show();
                        });
                    });
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AdminListFood.this, "Lỗi!", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }

    private void initList() {
        DatabaseReference myRef=database.getReference("Foods");
        binding.progressBaradm.setVisibility(View.VISIBLE);
        ArrayList<Foods> list =new ArrayList<>();
        Query query=myRef.orderByChild("CategoryId").equalTo(categoryId);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot issue:
                            snapshot.getChildren()){
                        list.add(issue.getValue(Foods.class));
                    }
                    if(list.size()>0){
                        binding.foodListViewadm.setLayoutManager((new LinearLayoutManager(AdminListFood.this,LinearLayoutManager.VERTICAL,false)));
                        binding.foodListViewadm.setAdapter(new AdminFoodListAdapter(list));
                    }
                    binding.progressBaradm.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void getIntentExtra() {
        categoryId=getIntent().getIntExtra("CategoryId",0);
        categoryName=getIntent().getStringExtra("CategoryName");

        binding.titleTxt.setText(categoryName);
        binding.backBtnadm.setOnClickListener(v->finish());

    }
}