package com.example.livefilter.fragments;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.livefilter.PostActivity;
import com.example.livefilter.R;
import com.example.livefilter.models.AppliedFilter;
import com.example.livefilter.models.CameraLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter;
import jp.co.cyberagent.android.gpuimage.util.Rotation;


public class CameraFragment extends Fragment {
    public static final String TAG = "CameraFragment";
    public static final int CAPTURE_IMAGE_REQUEST_CODE = 24;
    public static final int WRITE_REQUEST_CODE = 35;

    public String folderName = "LiveFilter";
    private CameraLoader cameraLoader;

    private ImageButton ibSave;
    private GPUImageView gpuImageView;
    private SeekBar sbEffect;
    private Spinner spFilter;
    private AppliedFilter appliedFilters;
    private String currentFilter;
    private String currentFilePath;

    public CameraFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAPTURE_IMAGE_REQUEST_CODE);
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
        Log.i(TAG, "camera started");


        // set up image view
        ibSave = view.findViewById(R.id.ibSaveButton);
        gpuImageView = view.findViewById(R.id.gpuimageview);
        sbEffect = view.findViewById(R.id.sbEffect);
        spFilter = view.findViewById(R.id.spFilters);

        // start camera
        cameraLoader = new CameraLoader(getActivity());
        cameraLoader.setOnPreviewFrameListener(new CameraLoader.OnPreviewFrameListener() {
            @Override
            public void onPreviewFrame(byte[] data, int width, int height) {
                gpuImageView.updatePreviewFrame(data, width, height);
            }
        });
        gpuImageView.setRatio(0.75f); // set aspect ratio
        // set up rotation
        updateGPUImageViewRotation();
        gpuImageView.setRenderMode(GPUImageView.RENDERMODE_CONTINUOUSLY);

        // gpuImageView.setImage(fileProvider); // this loads image on the current thread, should be run in a thread
        Log.i(TAG, "image was set");

        // if camera has filter details given to it, apply those to camera
        setUpAppliedFilter();

        // set up spinner for selecting filters
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, appliedFilters.getEffectsList());
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // set adapter on Spinner
        spFilter.setAdapter(arrayAdapter);

        spFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i(TAG, i + "th item selected");
                Log.i(TAG, appliedFilters.getEffectsList()[i]);
                currentFilter = appliedFilters.getEffectsList()[i];
                // if selected filter isn't yet applied, add it
                if(appliedFilters.filterApplied(currentFilter)) {
                    // set seekbar to current value of given filter and refresh it
                    sbEffect.setProgress(appliedFilters.getFilterValue(currentFilter));
                    sbEffect.refreshDrawableState();
                } else {
                    // if filter not applied, add it
                    appliedFilters.addFilter(currentFilter);
                    gpuImageView.setFilter(appliedFilters.getFiltersApplied());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // set up adjustable filters with seekbar
        sbEffect.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                // if value of seekbar has changed, update value of current filter
                appliedFilters.adjustFilter(currentFilter, i);
                // update image view with progress
                gpuImageView.setFilter(appliedFilters.getFiltersApplied());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        // set up image save button
        ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ibSave.setBackground(getResources().getDrawable(R.drawable.rounded_corner_darkened));
                saveImage();
                ibSave.setBackground(getResources().getDrawable(R.drawable.rounded_corners_button));
                // when image is saved, launch post activity
                launchPostActivity();
            }
        });

    }

    // update rotation of imageview
    private void updateGPUImageViewRotation() {
        Rotation rotation = getRotation(cameraLoader.getCameraOrientation());
        // not flipped either direction by default
        boolean flipHoriz = false;
        boolean flipVert = false;
        // if camera is front facing, it needs to be flipped horizontally
        if (cameraLoader.isFrontFacing()) { // Front-facing cameras need mirroring
            if (rotation == Rotation.NORMAL || rotation == Rotation.ROTATION_180) {
                flipHoriz = true;
            } else {
                flipVert = true;
            }
        }
        gpuImageView.getGPUImage().setRotation(rotation, flipHoriz, flipVert);
    }

    private Rotation getRotation(int orientation) {
        //  return the current orientation of the camera as a Rotation object
        switch (orientation) {
            case 90:
                return Rotation.ROTATION_90;
            case 180:
                return Rotation.ROTATION_180;
            case 270:
                return Rotation.ROTATION_270;
            default:
                return Rotation.NORMAL;
        }
    }

    private void setUpAppliedFilter() {

        Bundle bundle = this.getArguments();
        // if fragment was given filter info, add effects to appliedFilter
        if(bundle != null) {
            String[] appliedEffects = bundle.getStringArray("effectNames");
            int[] appliedIntensities = bundle.getIntArray("effectIntensities");
            appliedFilters = new AppliedFilter(appliedEffects, appliedIntensities);
        } else {
            // if not given filter data, make a new applied filter group
            appliedFilters = new AppliedFilter();
        }

    }


    private void saveImage() {
        
        //Bitmap takenImage = gpuImageView.getGPUImage().getBitmapWithFilterApplied();
        Bitmap takenImage = null;
        try {
            takenImage = gpuImageView.capture();
        } catch (InterruptedException e) {
            Log.e(TAG, "camera interrupted", e);
        }

        // get root directory
        Log.i(TAG, "attempting to access root directory");
        String root = Environment.getExternalStorageDirectory().toString();
        Log.i(TAG, "root directory " + root);
        File savedDir = new File(root + "/" + folderName);
        if(!savedDir.exists()) {
            savedDir.mkdirs();
            Log.i(TAG, "directory created");
        }

        // put time taken into file name
        String timeTaken = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        // make a new file
        File file = new File(savedDir, "livefilter_" + timeTaken + ".jpg");
        Log.i(TAG, "saving file " + file.getAbsolutePath());

        // write bitmap to file
        try {
            Log.i(TAG, "saving image to file");
            FileOutputStream out = new FileOutputStream(file);
            takenImage.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            if(file.exists()){
                Log.i(TAG, "file exists");
                Toast.makeText(getContext(), "saved file " + file.getAbsolutePath(), Toast.LENGTH_SHORT);
                currentFilePath = file.getAbsolutePath();
            }
        } catch (IOException e) {
            Log.e(TAG, "problem finding or saving file", e);
        }

    }

    // launch detail to post and share filter
    private void launchPostActivity() {
        // get filter information to pass to post activity
        String[] effectNames = appliedFilters.getAppliedNames();
        int[] effectIntensities = appliedFilters.getAppliedIntensities(effectNames);

        Intent intent = new Intent(getContext(), PostActivity.class);

        // pass in filter and photo info into new intent
        intent.putExtra("effectNames", effectNames);
        intent.putExtra("effectIntensities", effectIntensities);
        intent.putExtra("photoFilePath", currentFilePath);

        getContext().startActivity(intent);

    }

    @Override
    public void onResume() {
        super.onResume();
        // if view is GPU image view and the layout is not requested
        if(ViewCompat.isLaidOut(gpuImageView) && !gpuImageView.isLayoutRequested()) {
            cameraLoader.onResume(gpuImageView.getWidth(), gpuImageView.getHeight());
        } else {
            gpuImageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    gpuImageView.removeOnLayoutChangeListener(this);
                    cameraLoader.onResume(gpuImageView.getWidth(), gpuImageView.getHeight());
                }
            });
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraLoader.onPause();
    }

}