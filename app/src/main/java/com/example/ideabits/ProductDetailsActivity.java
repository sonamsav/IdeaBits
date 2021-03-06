package com.example.ideabits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ideabits.Model.Products;
import com.example.ideabits.Prevalent.Prevalent;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class ProductDetailsActivity extends AppCompatActivity {

    private ImageView productImage;
    private Button saveImage;
    private TextView productDescription, productName;
    private String productID = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        saveImage = findViewById(R.id.save_image_btn);
        productID = getIntent().getStringExtra("pid");
        productImage = findViewById(R.id.product_image_details);
        productDescription = findViewById(R.id.product_name_details);
        productName = findViewById(R.id.product_description_details);

        getProductDetails(productID);
        saveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedImageList();
            }
        });

    }
    private void savedImageList()
    {

        String saveCurrentTime, saveCurrentDate;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss");
        saveCurrentTime = currentDate.format(calForDate.getTime());

        final DatabaseReference saveListRef = FirebaseDatabase.getInstance().getReference().child("Save List");

        final HashMap<String, Object> saveMap = new HashMap<>();
        saveMap.put("pid", productID);
        saveMap.put("description",productDescription.getText().toString());

        saveMap.put("pname", productName.getText().toString());
        saveMap.put("date", saveCurrentDate);
        saveMap.put("time", saveCurrentTime);




        saveListRef.child("User View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID).updateChildren(saveMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    saveListRef.child("Admin View").child(Prevalent.currentOnlineUser.getPhone()).child("Products").child(productID).updateChildren(saveMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ProductDetailsActivity.this, "Image Saved", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(ProductDetailsActivity.this, HomePageActivity.class);
                                startActivity(intent);
                            }

                        }

                    });
                }
            }
        });
    }

    private void getProductDetails(String productID)
    {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Products");
        productRef.child(productID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                if (dataSnapshot.exists())
                {
                    Products products = dataSnapshot.getValue(Products.class);

                    productName.setText(products.getPname());
                    productDescription.setText(products.getDescription());
                    Picasso.get().load(products.getImage()).into(productImage);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
