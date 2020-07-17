package com.example.livefilter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.livefilter.models.FilterPost;
import com.parse.ParseFile;

import org.parceler.Parcels;

public class Details extends AppCompatActivity {

    FilterPost filterPost;

    private TextView tvName;
    private TextView tvDescription;
    private TextView tvUsername;
    private ImageView ivBefore;
    private ImageView ivAfter;

    private Button btTryFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        // unwrap filterPost from intent
        filterPost = Parcels.unwrap(getIntent().getParcelableExtra(FilterPost.class.getSimpleName()));

        // find views for view elements of filterPost
        tvName = findViewById(R.id.tvNameD);
        tvDescription = findViewById(R.id.tvDescriptionD);
        tvUsername = findViewById(R.id.tvUsernameD);
        ivBefore = findViewById(R.id.ivBeforeD);
        ivAfter = findViewById(R.id.ivAfterD);

        btTryFilter = findViewById(R.id.btTryFilter);

        tvName.setText(filterPost.getName());
        tvUsername.setText(filterPost.getUser().getUsername());
        tvDescription.setText(filterPost.getDescription());
        // load images using Glide
        ParseFile beforeImage = filterPost.getBefore();
        ParseFile afterImage = filterPost.getAfter();
        if(beforeImage != null) {
            Glide.with(this).load(beforeImage.getUrl()).into(ivBefore);
        }
        if(afterImage != null) {
            Glide.with(this).load(afterImage.getUrl()).into(ivAfter);
        }


    }
}