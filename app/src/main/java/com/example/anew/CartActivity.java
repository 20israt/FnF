package com.example.anew;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CartActivity extends AppCompatActivity {
    public static final int PICK_IMAGE_REQUEST = 1;
    private EditText editTextName;
    private TextView textViewItemPrice;
    private ImageView imageViewItem;
    private Button buttonOrder;
    private Button buttonSelectImage;
    private Button buttonSearch;
    private TextView textViewItemId;

    private DatabaseHelper databaseHelper;
    private byte[] productImageByteArray;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        editTextName = findViewById(R.id.edit_text_item_name);
        textViewItemPrice = findViewById(R.id.text_view_item_price);
        textViewItemId = findViewById(R.id.text_view_item_id);
        imageViewItem = findViewById(R.id.image_view_item);
        buttonOrder = findViewById(R.id.button_order);
        buttonSearch = findViewById(R.id.button_search);
        databaseHelper = new DatabaseHelper(this);

        buttonSearch.setOnClickListener(view -> searchItem());
        buttonOrder.setOnClickListener(view -> orderItem());
    }



    private void searchItem() {
        String itemName = editTextName.getText().toString();
        if(itemName.isEmpty()){
            Toast.makeText(this, "Please enter an item name to search", Toast.LENGTH_SHORT).show();
            return;
        }
        Cursor cursor = databaseHelper.getProductByName(itemName);
        if(cursor != null && cursor.moveToFirst()){
            int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_ID));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_PRICE));
            byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_PRODUCT_IMAGE_URI));
            textViewItemPrice.setText(String.valueOf(price));
            textViewItemId.setText("Item ID: " +itemId);

            if(image != null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
                imageViewItem.setImageBitmap(bitmap);
                productImageByteArray = image;
            }
            cursor.close();;
        }else{
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
        }
    }

    private void orderItem() {
        String itemName = editTextName.getText().toString();
        databaseHelper.orderItem(itemName);
        buttonOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });
    }
}

