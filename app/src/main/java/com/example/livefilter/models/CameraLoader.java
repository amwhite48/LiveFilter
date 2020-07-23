package com.example.livefilter.models;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.SessionConfiguration;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Build;
import android.util.Log;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.Arrays;

import static com.example.livefilter.ImageUtils.generateNV21Data;
import static java.util.Arrays.asList;

public class CameraLoader {

    public static final String TAG = "CameraLoader";
    private OnPreviewFrameListener onPreviewFrameListener;

    private Activity activity;

    private CameraManager cameraManager;
    private CameraCharacteristics cameraCharacteristics;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private ImageReader imageReader;

    private String cameraID;
    private int cameraFacing;
    private int viewWidth;
    private int viewHeight;
    private double aspectRatio;

    // constructor
    public CameraLoader(Activity currentActivity) {
        this.activity = currentActivity;
        // camera faces back by default
        cameraFacing = CameraCharacteristics.LENS_FACING_BACK;
        aspectRatio = 0.75; // aspect ratio is 4:3 by default
        this.cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);

    }

    // when camera is started / image preview resumed
    public void onResume(int frameWidth, int frameHeight) {
        this.viewWidth = frameWidth;
        this.viewHeight = frameHeight;
        // call helper method to set up camera features
        setUpCamera();
    }

    public void onPause() {
        // call helper to end camera session
        releaseCamera();
    }

    // flips camera direction
    public void switchCamera() {
        // negate direction camera is facing
        cameraFacing ^= 1;
        Log.i(TAG, "flipping camera to " + cameraFacing);
        releaseCamera();
        // set up camera facing new direction
        setUpCamera();
    }


    // use annotation rather than manual check for camera permission, which we enabled
    @SuppressLint("MissingPermission")
    private void setUpCamera() {
        try {
            // get ID of camera for direction it is facing
            cameraID = getCameraID(cameraFacing);
            cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraID);
            // launch the camera with the given id
            cameraManager.openCamera(cameraID, cameraDeviceCallback, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "error accessing camera", e);
        }
    }

    // get ID of current camera given direction camera is facing (for phones with multiple cameras)
    // throws exception if camera can't be accessed
    private String getCameraID(int facing) throws CameraAccessException {
        // iterate through all cameras
        for(String cameraID: cameraManager.getCameraIdList()) {
            if(cameraManager.getCameraCharacteristics(cameraID).get(CameraCharacteristics.LENS_FACING) == facing) {
                // if current cameraID is the same as the value passed in, return the cameraID
                return cameraID;
            }
        }
        // by default, return the direction passed in
        return Integer.toString(facing);
    }

    private void releaseCamera() {
        // set all camera objects to null
        if(cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
        if(cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if(imageReader != null) {
            imageReader.close();
            imageReader = null;
        }
    }

    // get direction camera is facing for when phone camera is rotated
    public int getCameraOrientation() {
        int degrees = activity.getWindowManager().getDefaultDisplay().getRotation();
        // get degress of rotation from window manager info
        switch (degrees) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
            default:
                degrees = 0;
                break;
        }

        int orientation = 0;

        // get screen orientation from camera
        try {
            String cameraId = getCameraID(cameraFacing);
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
            orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch (CameraAccessException e) {
            Log.e(TAG, "Error accessing camera (for orientation purposes)", e);
        }

        Log.i(TAG, "degrees: " + degrees + ", orientation: " + orientation + ", mCameraFacing: " + cameraFacing);
        if (cameraFacing == CameraCharacteristics.LENS_FACING_FRONT) {
            return (orientation + degrees) % 360;
        } else {
            return (orientation - degrees) % 360;
        }
    }

    // check if device has multiple cameras
    public boolean hasMultipleCameras() {
        try {
            return cameraManager.getCameraIdList().length > 1;
        } catch (CameraAccessException e) {
            Log.e(TAG, "error accessing camera (has multiple cameras method)", e);
        }
        // return false by default
        return false;
    }

    // for checking if current camera is front facing
    public boolean isFrontFacing() {
        return cameraFacing == CameraCharacteristics.LENS_FACING_FRONT;
    }

    // set onPreviewFrameListener to parameter passed in
    public void setOnPreviewFrameListener(OnPreviewFrameListener mOnPreviewFrameListener) {
        this.onPreviewFrameListener = mOnPreviewFrameListener;
    }

    // most of the heavy lifting in this method - starts the capture session
    @SuppressWarnings("deprecation")
    @RequiresApi(api = Build.VERSION_CODES.P)
    private void startCaptureSession() {
        // choose size for camera
        Size size = getOptimalSize();
        Log.i(TAG, "Selected capture size: " + size.toString());
        // make instance of image reader
        imageReader = ImageReader.newInstance(size.getWidth(), size.getHeight(), ImageFormat.YUV_420_888, 2);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader imageReader) {
                if(imageReader != null) {
                    Image image = imageReader.acquireNextImage();
                    if (image != null) {
                        if(onPreviewFrameListener != null) {
                            Log.i(TAG, "image non null");
                            byte[] data = generateNV21Data(image);
                            onPreviewFrameListener.onPreviewFrame(data, image.getWidth(), image.getHeight());
                        }
                        image.close();
                    }

                }
            }
        }, null);

        // make a capture session
        try {
            cameraDevice.createCaptureSession(asList(imageReader.getSurface()), stateCallback, null);
        } catch (CameraAccessException e) {
            Log.e(TAG, "camera access error in startCaptureSession", e);
        }
    }

    private Size getOptimalSize() {
        Log.i(TAG, "Width: " + this.viewWidth + ", Height: " + this.viewHeight);
        // size is 0 until initialized
        if(viewWidth == 0 || viewHeight == 0) {
            return new Size(0, 0);
        }
        // set up surfaces for making a capture session
        StreamConfigurationMap map = cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        Size[] sizes = map.getOutputSizes(ImageFormat.YUV_420_888);
        int orientation = this.getCameraOrientation();
        int width;
        int height;
        // swap dimensions of camera if in a landscape orientation
        if(orientation == 90 | orientation == 270) {
            width = this.viewHeight;
            height = this.viewWidth;
        } else {
            width = this.viewWidth;
            height = this.viewHeight;
        }
        return getSuitableSizes(sizes, width, height, aspectRatio);
    }

    // gets optimal size based on given possible sizes
    private Size getSuitableSizes(Size[] sizes, int width, int height, double aspectRatio) {
        int minDifference = Integer.MAX_VALUE;
        // index of given size array with suitable size
        int index = 0;
        Log.i(TAG, "Finding suitable size for aspect ratio " + aspectRatio);
        for(int i = 0; i < sizes.length; i++) {
            Size size = sizes[i];
            // if proportions are suitable for aspect ratio
            if(size.getWidth() * aspectRatio == size.getWidth()) {
                int difference = Math.abs(width - size.getWidth());
                // if width of size matches the desired size, return that size
                if(difference == 0) {
                    return size;
                }
                // if we can't get the desired width, get the size with the width closest to what we want
                if(difference < minDifference) {
                    minDifference = difference;
                    index = i;
                }
            }
        }
        // if no matching size was found, return the closest size
        return sizes[index];

    }


    private CameraDevice.StateCallback cameraDeviceCallback = new CameraDevice.StateCallback() {
        @RequiresApi(api = Build.VERSION_CODES.P)
        @Override
        public void onOpened(@NonNull CameraDevice camera) {
            cameraDevice = camera;
            // calls helper method to set up image capture
            startCaptureSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice camera) {
            // close camera when callback is disconnected
            cameraDevice.close();
            cameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice camera, int i) {
            // close camera when error happens
            cameraDevice.close();
            cameraDevice = null;
        }
    };

    private CameraCaptureSession.StateCallback stateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(@NonNull CameraCaptureSession captureSession) {
            // cannot configure if camera device is null
            if(cameraDevice == null) {
                return;
            }

            cameraCaptureSession = captureSession;

            try {
                // create a preview
                CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                builder.addTarget(imageReader.getSurface());
                // continuously update preview image
                builder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                // turn on auto flash
                builder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                cameraCaptureSession.setRepeatingRequest(builder.build(), null, null);
            } catch (CameraAccessException e) {
                Log.e(TAG, "configuration of camera capture session failed due to camera access", e);
            }

        }

        @Override
        public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {
            Log.e(TAG, "configuration of camera capture session failed");
        }
    };



    // interface representing the data in the camera preview
   public interface OnPreviewFrameListener {
       void onPreviewFrame(byte[] data, int width, int height);
    }


}
