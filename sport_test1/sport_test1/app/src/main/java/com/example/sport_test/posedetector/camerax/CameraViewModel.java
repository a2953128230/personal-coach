package com.example.sport_test.posedetector.camerax;

import android.app.Application;

import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.lifecycle.ViewModel;

import com.example.sport_test.posedetector.classification.PoseClassifierProcessor;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;

import java.io.Closeable;
import java.util.concurrent.ExecutionException;

public class CameraViewModel extends ViewModel {
    public boolean prothom = true;
    public boolean isFlash = false;
    public int which_camera = 1;
    public int pushups = 0;
    public int squats = 0;
    public boolean isStart = false;
    public long start_time = 0;
    public int pushups_cnt = 0;
    public int squats_cnt = 0;
    public String now = "nothing";
    public boolean speaker = false;
    public ProcessCameraProvider cameraProvider;
    public ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    public PoseClassifierProcessor poseClassifierProcessor;
    public PoseDetector poseDetector;
    public PoseDetectorOptions options;

    public CameraViewModel(Application application) {
        super();
        cameraProviderFuture = ProcessCameraProvider.getInstance(application);
        try {
            cameraProvider = cameraProviderFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        poseClassifierProcessor = new PoseClassifierProcessor(application, true);
        options = new PoseDetectorOptions.Builder().setDetectorMode(PoseDetectorOptions.STREAM_MODE).build();
        poseDetector = PoseDetection.getClient(options);
    }
}