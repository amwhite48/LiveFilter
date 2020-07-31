package com.example.livefilter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class PostActivity extends AppCompatActivity {

    EditText etFilterName;
    EditText etDescription;
    ImageView ivFilteredImage;
    Button btPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        etFilterName = findViewById(R.id.etFilterName);
        etDescription = findViewById(R.id.etFilterDescription);
        ivFilteredImage = findViewById(R.id.ivFilteredImage);
        btPost = findViewById(R.id.btPost);
    }
}