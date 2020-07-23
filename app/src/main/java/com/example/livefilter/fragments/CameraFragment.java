package com.example.livefilter.fragments;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.livefilter.MainActivity;
import com.example.livefilter.R;
import com.example.livefilter.models.CameraLoader;

import java.io.File;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSepiaToneFilter;


public class CameraFragment extends Fragment {
    public static final String TAG = "CameraFragment";
    public static final int CAPTURE_IMAGE_REQUEST_CODE = 24;

    private File photoFile;
    private GPUImageView gpuImageView;
    public String photoFileName = "photo.jpg";
    private CameraLoader cameraLoader;

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

        // create implicit  intent to take a picture and return control to the calling application
        // Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // requestPermissions(new String[]{Manifest.permission.CAMERA}, CAPTURE_IMAGE_REQUEST_CODE);
        Log.i(TAG, "camera started");

        photoFile = getPhotoFileUri(photoFileName);

        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider.livefilter", photoFile);

        // tell application where you want output
        // intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);


        gpuImageView = view.findViewById(R.id.gpuimageview);

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

    // helper to get photo file URI (string representing file)
    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        Log.i(TAG, "creating new file");
        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);

    }
}