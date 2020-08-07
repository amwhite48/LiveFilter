package com.example.livefilter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.livefilter.fragments.CameraFragment;
import com.example.livefilter.fragments.HomeFragment;
import com.example.livefilter.fragments.ProfileFragment;
import com.example.livefilter.models.FilterPost;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";
    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;boolean launchCameraWithFilter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        launchCameraWithFilter = getIntent().getIntExtra("cameraLaunch", 0) == 1;

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                Fragment fragment = new HomeFragment();
                switch (menuItem.getItemId()) {
                    case R.id.action_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.action_camera:
                        fragment = new CameraFragment();
                        if(launchCameraWithFilter) {
                            Bundle bundle = getFilterBundle();
                            fragment.setArguments(bundle);
                        }
                        break;
                    case R.id.action_profile:
                        fragment = new ProfileFragment();
                        break;
                }
                // switch to selected fragment after completing switch statement
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        // if intent launched from details activity, launch camera
        Log.i(TAG, "cameraLaunch value: " + getIntent().getIntExtra("cameraLaunch", 0));
        if(launchCameraWithFilter) {
            bottomNavigationView.setSelectedItemId(R.id.action_camera);

        } else {
            // set default selection on navigation bar
            bottomNavigationView.setSelectedItemId(R.id.action_home );
        }


    }

    // bundle filter attributes to send to camera fragment
    private Bundle getFilterBundle() {
        FilterPost filterPost = Parcels.unwrap(getIntent().getParcelableExtra(FilterPost.class.getSimpleName()));
        Bundle bundle = new Bundle();
        bundle.putStringArray("effectNames", getStringArrayFromJsonArray(filterPost.getEffectNames()));
        try {
            bundle.putIntArray("effectIntensities", getIntArrayFromJsonArray(filterPost.getEffectIntensities()));
        } catch (JSONException e) {
            Log.e(TAG, "error getting effect intensities", e);
        }
        return bundle;
    }

    // generate String array from jsonArray
    private String[] getStringArrayFromJsonArray(JSONArray jsonArray) {

        String[] stringArray = new String[jsonArray.length()];
        // iterate through each item in jsonArray and add it to stringArray
        for(int i = 0; i < jsonArray.length(); i++) {
            stringArray[i] = jsonArray.optString(i);
            Log.i(TAG, stringArray[i] + " added to array");
        }
        return stringArray;
    }

    // generate int array from jsonArray
    private int[] getIntArrayFromJsonArray(JSONArray jsonArray) throws JSONException {
        int[] intArray = new int[jsonArray.length()];
        // iterate through each item in jsonArray and add it to stringArray
        for(int i = 0; i < jsonArray.length(); i++) {
            intArray[i] = jsonArray.getInt(i);
            Log.i(TAG, intArray[i] + " added to array");
        }
        return intArray;
    }
}