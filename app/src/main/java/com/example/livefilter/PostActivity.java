package com.example.livefilter;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONArray;
import org.parceler.Parcels;

public class PostActivity extends AppCompatActivity {

    EditText etFilterName;
    EditText etDescription;
    ImageView ivFilteredImage;
    Button btPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // unwrap information passed into intent
        String photoFile = getIntent().getExtras().getString("photoFilePath");
        String[] effectNames = getIntent().getExtras().getStringArray("effectNames");
        int[] effectIntensities = getIntent().getExtras().getIntArray("effectIntensities");

        JSONArray effectNamesArray = convertStringArrayToJSON(effectNames);
        JSONArray effectIntensitiesArray = convertIntArrayToJSON(effectIntensities);

        // set up image views
        etFilterName = findViewById(R.id.etFilterName);
        etDescription = findViewById(R.id.etFilterDescription);
        ivFilteredImage = findViewById(R.id.ivFilteredImage);
        btPost = findViewById(R.id.btPost);

        // load image from filepath into imageView
        ivFilteredImage.setImageBitmap(BitmapFactory.decodeFile(photoFile));


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
}