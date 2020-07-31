package com.example.livefilter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.livefilter.models.FilterPost;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.parceler.Parcels;

import java.io.File;

public class PostActivity extends AppCompatActivity {

    public static final String TAG = "PostActivity";

    EditText etFilterName;
    EditText etDescription;
    ImageView ivFilteredImage;
    Button btPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // unwrap information passed into intent
        final String photoFile = getIntent().getExtras().getString("photoFilePath");
        String[] effectNames = getIntent().getExtras().getStringArray("effectNames");
        int[] effectIntensities = getIntent().getExtras().getIntArray("effectIntensities");

        final JSONArray effectNamesArray = convertStringArrayToJSON(effectNames);
        final JSONArray effectIntensitiesArray = convertIntArrayToJSON(effectIntensities);

        // set up image views
        etFilterName = findViewById(R.id.etFilterName);
        etDescription = findViewById(R.id.etFilterDescription);
        ivFilteredImage = findViewById(R.id.ivFilteredImage);
        btPost = findViewById(R.id.btPost);

        // load image from filepath into imageView
        ivFilteredImage.setImageBitmap(BitmapFactory.decodeFile(photoFile));

        // when post button is clicked, save post to parse database
        btPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get values from edit text fields
                // make sure values passed in are not empty
                String filterName = etFilterName.getText().toString();
                if(filterName.isEmpty()) {
                    Toast.makeText(PostActivity.this, "filter name cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                String description = etDescription.getText().toString();
                if(description.isEmpty()) {
                    Toast.makeText(PostActivity.this, "description cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                ParseUser currentUser = ParseUser.getCurrentUser();

                savePost(filterName, description, photoFile, effectNamesArray, effectIntensitiesArray, currentUser);
            }
        });


    }

    private void savePost(String filterName, String description, String photoFile, JSONArray effectNames, JSONArray effectIntensities, ParseUser currentUser) {

        // create new object to store filterPost info
        FilterPost filterPost = new FilterPost();

        // set given parameters
        filterPost.setName(filterName);
        filterPost.setDescription(description);
        filterPost.setEffectNames(effectNames);
        filterPost.setEffectIntensities(effectIntensities);
        filterPost.setUser(currentUser);

        // set up new photoFile
        File filteredPhoto = new File(photoFile);
        filterPost.setAfter(new ParseFile(filteredPhoto));

        // save callback to save post to parse in background
        filterPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    // if an exception was thrown, there was an error
                    Log.e(TAG, "Error while saving post", e);
                    Toast.makeText(PostActivity.this, "Error saving post", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i(TAG, "Posting successful!");

                // launch main activity once post succeeded
                goMainActivity();
            }
        });


    }

    // convert string array to JSONArray to put in Parse
    private JSONArray convertStringArrayToJSON(String[] stringArray) {
        JSONArray output = new JSONArray();
        for(int i = 0; i < stringArray.length; i++){
            output.put(stringArray[i]);
        }
        return output;
    }

    // convert int array to JSONArray to put in Parse
    private JSONArray convertIntArrayToJSON(int[] intArray) {
        JSONArray output = new JSONArray();
        for(int i = 0; i < intArray.length; i++){
            output.put(intArray[i]);
        }
        return output;
    }

    // launches main activity
    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}