package com.example.sport_test.posedetector.camerax;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.sport_test.R;
import com.example.sport_test.posedetector.GraphicOverlay;
import com.example.sport_test.posedetector.PoseGraphic;
import com.example.sport_test.posedetector.ScopedExecutor;
import com.example.sport_test.posedetector.classification.PoseClassifierProcessor;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.gms.tasks.Tasks;
import com.google.common.util.concurrent.ListenableFuture;

import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseDetection;
import com.google.mlkit.vision.pose.PoseDetector;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class Activity_camerax extends AppCompatActivity {
    private static Activity_camerax instance;
    public static Activity_camerax getInstance() {
        return instance;
    }

    private final ScopedExecutor executor = new ScopedExecutor(TaskExecutors.MAIN_THREAD);
    // 指定姿勢偵測工具
    private final PoseDetectorOptions options = new PoseDetectorOptions.Builder()
            .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
            .build();
    private final PoseDetector poseDetector = PoseDetection.getClient(options);
    private final ExecutorService classificationExecutor = Executors.newSingleThreadExecutor();
    private final HandlerThread handlerThread = new HandlerThread("PoseClassifierThread");
    private PoseClassifierProcessor poseClassifierProcessor;
    private ImageCapture imageCapture;
    public class PoseWithClassification {
        public Pose pose;
        public List<String> classificationResult;

        public PoseWithClassification(Pose pose, List<String> classificationResult) {
            this.pose = pose;
            this.classificationResult = classificationResult;
        }

        public List<String> getClassificationResult() {
            return classificationResult;
        }

        public void setClassificationResult(List<String> classificationResult) {
            this.classificationResult = classificationResult;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax);

        instance = this;

        // 接收傳遞文字(運動項目)
        Intent intent = getIntent();
        String chooseSport = intent.getStringExtra("sport");

        bindUseCases(1 , chooseSport , true);
        PreviewView previewView = findViewById(R.id.previewView);


    }


    private void bindUseCases(int which_camera , String chooseSport , boolean isStart ) {
        GraphicOverlay graphicOverlay = findViewById(R.id.graphic_overlay);
        handlerThread.start();
        Handler handler = new Handler(handlerThread.getLooper());
        handler.post(() -> {
            poseClassifierProcessor = new PoseClassifierProcessor(Activity_camerax.getInstance(), true);
        });

        PreviewView previewView = findViewById(R.id.previewView);

        Log.d("Camera", "hohoho");

        ListenableFuture<ProcessCameraProvider> cameraProviderFuture =
                ProcessCameraProvider.getInstance(Activity_camerax.getInstance());

        cameraProviderFuture.addListener(() -> {
            try {
                // Camera provider is now guaranteed to be available
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // 設置相機預覽畫面
                Preview preview = new Preview.Builder().build();

                // 拍照
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
                                // enable the following line if RGBA output is needed.
                                .setTargetResolution(new Size(1280, 720))
                                .build();

                imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
                    boolean needUpdateGraphicOverlayImageSourceInfo = true;
                    @Override
                    public void analyze(@NonNull ImageProxy imageProxy) {
                        ContextCompat.getMainExecutor(Activity_camerax.getInstance());
                        Log.d("FaceDetection", "huhuhuh2");
                        if (needUpdateGraphicOverlayImageSourceInfo) {
                            boolean isImageFlipped = which_camera == CameraSelector.LENS_FACING_FRONT;
                            int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                            if (rotationDegrees == 0 || rotationDegrees == 180) {
                                Log.d("FaceDetection", "huhuhuh4");
                                graphicOverlay.setImageSourceInfo(imageProxy.getWidth(), imageProxy.getHeight(), isImageFlipped);
                            } else {
                                Log.d("FaceDetection", "huhuhuh5");
                                graphicOverlay.setImageSourceInfo(imageProxy.getWidth(), imageProxy.getHeight(), isImageFlipped);
                            }
                            needUpdateGraphicOverlayImageSourceInfo = false;
                        }
                        try {
                            Log.d("FaceDetection", "huhuhuh3");
                            processImageProxy(imageProxy, graphicOverlay, chooseSport);
                        } catch (Exception e) {
                            if (e instanceof MlKitException) {
                                throw e;
                            } else {
                                Log.e("FaceDetection", "Failed to process image. Error: " + e.getLocalizedMessage());
                                Toast.makeText(Activity_camerax.getInstance(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                // 前後置鏡頭
                CameraSelector cameraSelector = null;
                if (which_camera == CameraSelector.LENS_FACING_FRONT) {
                    cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
                } else {
                    cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
                }


                try {
                    if (isStart) {
                        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis).getCameraControl();
                    } else {
                        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis).getCameraControl();
                    }

                } catch (Exception exc) {
                    Log.e("FaceDetection", "Use case binding failed", exc);
                }

                // 顯示相機預覽畫面
                preview.setSurfaceProvider(
                        previewView.getSurfaceProvider());
            } catch (InterruptedException | ExecutionException e) {
                Log.e("Activity_camerax", "Error: 相機初始化失敗", e);
            }
        }, ContextCompat.getMainExecutor(Activity_camerax.getInstance()));

    }
    // 準備輸入影像
    @OptIn(markerClass = ExperimentalGetImage.class)   // 在程式碼中使用了實驗性API，，這個 API 可能在未來的版本中會發生變化
    public void processImageProxy(ImageProxy imageProxy , GraphicOverlay graphicOverlay , String chooseSport) {
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            Task<PoseWithClassification> result = poseDetector.process(image)
                    .continueWith(classificationExecutor, new Continuation<Pose, PoseWithClassification>() {
                        @Override
                        public PoseWithClassification then(@NonNull Task<Pose> task) throws Exception {
                            Pose pose = task.getResult();
                            List<String> classificationResult = poseClassifierProcessor.getPoseResult(pose);
                            return new PoseWithClassification(pose, classificationResult);
                        }
                    })
                    .addOnSuccessListener( executor , results -> {
                        boolean ff = false;
                        List<String> temp = results.getClassificationResult();
                        onSuccess( ff , temp , results , chooseSport );
                        graphicOverlay.clear();
                        graphicOverlay.add(
                                new PoseGraphic(
                                        graphicOverlay,
                                        results.pose,
                                        true,
                                        true,
                                        true,
                                        results.classificationResult,
                                        ff));
                        graphicOverlay.postInvalidate();
                    })
                    .addOnFailureListener( executor , e -> {
                        Log.e("PoseEstimation", "姿勢偵測失敗");
                    })
                    .addOnCompleteListener(task -> {
                        // 關閉 ImageProxy，釋放資源
                        imageProxy.close();
                    });
        }
    }


    private void onSuccess( boolean ff , List<String> temp , PoseWithClassification results, String chooseSport) {
        assert(temp.size() <= 2);
        if( temp.size() == 2 ){
            String hu = temp.get(0);    // %s : %d reps
            List<String> hu_list = stringToWords(hu);
            assert (hu_list.size() == 4 || hu_list.size() == 0);
//            if( hu_list.size() == 4 ){
//                switch(chooseSport){
//                    case "situps":
//                        if( hu_list.get(0) == "situps_down"  ){
//                            ff = true;
//                            boolean haha = false;
//                        }
//                        break;
//                    case "pushups":
//                        break;
//                    case "gluteBridges":
//                        break;
//                    case "squats":
//                        break;
//                    case "button5":
//                        break;
//                }
//            }

        }
    }


//    pushups_down : 13 reps
//    pushups_up : 0.6. confidence
//    List<String> hu_list = stringToWords( "pushups_down : 13 reps" );
//    hu_list = [ "pushups_down" , ":" , "13" , "reps" ]
    public List<String> stringToWords(String inputString) {
        return Arrays.stream(inputString.trim().split("\\s+"))
                .filter(word -> !word.trim().isEmpty())
                .collect(Collectors.toList());
    }

    public void click_which_camera( View view ){

    }

    public void click_start_stop( View view ){

    }

    public void click_take_photo( View view ){
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerThread.quitSafely();
    }
}