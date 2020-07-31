package com.example.livefilter.fragments;

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

        // requestPermissions(new String[]{Manifest.permission.CAMERA}, CAPTURE_IMAGE_REQUEST_CODE);
        // requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_REQUEST_CODE);
        Log.i(TAG, "camera started");

//        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.livefilter", photoFile);

        // tell application where you want output
        // intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

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
        gpuImageView.setRenderMode(GPUImageView.RENDERMODE_CONTINUOUSLY);

        // gpuImageView.setImage(fileProvider); // this loads image on the current thread, should be run in a thread
        Log.i(TAG, "image was set");

        // set up spinner for selecting filters
        appliedFilters = new AppliedFilter();
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
                saveImage();
            }
        });

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
            }
        } catch (IOException e) {
            Log.e(TAG, "problem finding or saving file", e);
        }

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