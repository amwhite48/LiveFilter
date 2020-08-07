package com.example.livefilter;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
    private ImageView ivAfter;
    private Button btTryFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        // unwrap filterPost from intent
        filterPost = Parcels.unwrap(getIntent().getParcelableExtra(FilterPost.class.getSimpleName()));

        // set up custom actionbar
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        // find views for view elements of filterPost
        tvName = findViewById(R.id.tvNameD);
        tvDescription = findViewById(R.id.tvDescriptionD);
        tvUsername = findViewById(R.id.tvUsernameD);
        ivAfter = findViewById(R.id.ivAfterD);

        btTryFilter = findViewById(R.id.btTryFilter);

        tvName.setText(filterPost.getName());
        tvUsername.setText(filterPost.getUser().getUsername());
        tvDescription.setText(filterPost.getDescription());
        // load images using Glide
        ParseFile afterImage = filterPost.getAfter();
        if(afterImage != null) {
            Glide.with(this).load(afterImage.getUrl()).into(ivAfter);
        }

        // when button is clicked, launch camera fragment with filter applied
        btTryFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCameraWithFilter();

            }
        });


    }

    private void launchCameraWithFilter() {
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("cameraLaunch", 1);
        i.putExtra(FilterPost.class.getSimpleName(), Parcels.wrap(filterPost));
        startActivity(i);
    }
}