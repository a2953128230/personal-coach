package com.example.sport_test.posedetector.camerax;

import androidx.annotation.OptIn;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.core.content.ContextCompat;

import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sport_test.databinding.FragmentCameraBinding;
import com.example.sport_test.posedetector.GraphicOverlay;
import com.example.sport_test.posedetector.PoseGraphic;
import com.example.sport_test.posedetector.ScopedExecutor;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.pose.Pose;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class CameraFragment extends Fragment {


    public static CameraFragment newInstance() {
        return new CameraFragment();
    }

    private static final String PUSHUPS = "pushups";
    private static final String SQUATS = "squats";

    private Boolean pushups = null;
    private Boolean squats = null;
    String chooseExerciseName;
    private FragmentCameraBinding binding;

    private GraphicOverlay graphicOverlay = null;

    private ScopedExecutor executor = new ScopedExecutor(TaskExecutors.MAIN_THREAD);

    private Executor classificationExecutor = Executors.newSingleThreadExecutor();

    public class PoseWithClassification {
        public Pose pose;
        public List<String> classificationResult;

        public PoseWithClassification(Pose pose, List<String> classificationResult) {
            this.pose = pose;
            this.classificationResult = classificationResult;
        }
    }

    private Preview preview = null;
    private ImageAnalysis imageAnalyzer = null;

    private TextToSpeech text_to_speech;
    private Context safeContext;
    private Application application;
    private CameraViewModel viewModel = null;
    String toSpeak;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        viewModel = new CameraViewModel(requireActivity().getApplication());
        safeContext = context;
        application = requireActivity().getApplication();

        OrientationEventListener mOrientationListener = new OrientationEventListener(application, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (getActivity() != null) {
                    if (orientation == 0) {
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else if (orientation == 180) {
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    } else if (orientation == 90) {
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    } else if (orientation == 270) {
                        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    }
                }
            }
        };

        if (mOrientationListener.canDetectOrientation()) {
            mOrientationListener.enable();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        // 獲取傳遞的參數
        chooseExerciseName = getArguments().getString("chooseExerciseName");
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentCameraBinding.inflate(inflater, container, false);

        text_to_speech = new TextToSpeech(requireActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Log.d( "TextToSpeech", "TextToSpeech initialized successfully.");
                    text_to_speech.setLanguage(Locale.CHINESE);
                    Log.d( "TextToSpeech", "TextToSpeech setLanguage ok!");
                } else {
                    Log.d("TextToSpeech", "TextToSpeech initialization failed.");
                }
            }
        });

        graphicOverlay = binding.graphicOverlay;

        binding.facingSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.which_camera = 1 - viewModel.which_camera;
                bindUseCases(viewModel.which_camera, viewModel.isStart);
            }
        });

        binding.startStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewModel.isStart) {
                    long temp = (System.currentTimeMillis() / 1000) - viewModel.start_time;
                } else {
                    viewModel.start_time = System.currentTimeMillis() / 1000;
                    viewModel.isStart = true;
                    binding.startStop.setBackgroundColor(Color.parseColor("#b71c1c"));
                    //binding.startStop.setText("Stop");
                    //binding.cardButton.setTextColor(Color.parseColor("#ffffff"));
                    Toast.makeText(requireContext(), "請開始運動" , Toast.LENGTH_SHORT).show();

                    bindUseCases(viewModel.which_camera, viewModel.isStart);
                }
            }
        });

        binding.speaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.speaker = !viewModel.speaker;
                if (viewModel.speaker) {
                    Toast.makeText(requireContext(), "語音模式已停用", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(requireContext(), "語音模式已啟動", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (viewModel.prothom) {
            if (chooseExerciseName != null) {
                toSpeak = "將相機放置在一個可以清晰看到你做" + chooseExerciseName + " 的位置";
                Toast.makeText(requireContext(), "將相機放置在一個可以清晰看到你做\n" + chooseExerciseName + " 的位置" , Toast.LENGTH_SHORT).show();
                text_to_speech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH,null,null);
            }
            Toast.makeText(requireContext(), "語音模式已啟動", Toast.LENGTH_SHORT).show();
            viewModel.prothom = false;
        }
        bindUseCases(viewModel.which_camera, viewModel.isStart);
    }

    private void bindUseCases(int which_camera, boolean isStart) {
        final boolean[] needUpdateGraphicOverlayImageSourceInfo = {true};

        Log.d("FaceDetection", "bindUseCases() called");

        viewModel.cameraProviderFuture.addListener(() -> {
            Log.d("FaceDetection", "cameraProviderFuture listener called");
            // Preview
            preview = new Preview.Builder()
                    .build();
            preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

            Log.d("FaceDetection", "preview.setSurfaceProvider() ok!");

            imageAnalyzer = new ImageAnalysis.Builder().build();
            imageAnalyzer.setAnalyzer(
                    // imageProcessor.processImageProxy will use another thread to run the detection underneath,
                    // thus we can just runs the analyzer itself on main thread.
                    ContextCompat.getMainExecutor(safeContext),
                    new ImageAnalysis.Analyzer() {
                        @Override
                        public void analyze(@NonNull ImageProxy imageProxy) {
                            Log.d("FaceDetection", "ImageAnalysis.Analyzer() called");
                            if (needUpdateGraphicOverlayImageSourceInfo[0]) {
                                boolean isImageFlipped = which_camera == CameraSelector.LENS_FACING_FRONT;
                                int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                                if (rotationDegrees == 0 || rotationDegrees == 180) {
                                    Log.d("FaceDetection", "ImageAnalysis.Analyzer():if");
                                    graphicOverlay.setImageSourceInfo(imageProxy.getWidth(), imageProxy.getHeight(), isImageFlipped);
                                } else {
                                    Log.d("FaceDetection", "ImageAnalysis.Analyzer():else1");
                                    graphicOverlay.setImageSourceInfo(imageProxy.getHeight(), imageProxy.getWidth(), isImageFlipped);
                                    Log.d("FaceDetection", "ImageAnalysis.Analyzer():else2");
                                }
                                needUpdateGraphicOverlayImageSourceInfo[0] = false;
                            }
                            try {
                                Log.d("FaceDetection", "process image");
                                processImageProxy(imageProxy, graphicOverlay);
                            } catch (Exception e) {
                                if (e instanceof MlKitException) {
                                    throw e;
                                } else {
                                    Log.e("FaceDetection", "Failed to process image. Error: " + e.getLocalizedMessage());
                                    Toast.makeText(safeContext, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

            // Select back camera as a default
            CameraSelector cameraSelector;
            if (which_camera == CameraSelector.LENS_FACING_FRONT) {
                cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build();
            } else {
                cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
            }

            try {
                // Unbind use cases before rebinding
                viewModel.cameraProvider.unbindAll();

                // Bind use cases to camera
                if (isStart) {
                    viewModel.cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalyzer).getCameraControl();
                } else {
                    viewModel.cameraProvider.bindToLifecycle(this, cameraSelector, preview).getCameraControl();
                }
            } catch (Exception exc) {
                Log.e("FaceDetection", "Use case binding failed", exc);
            }
        }, ContextCompat.getMainExecutor(safeContext));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)   // 在程式碼中使用了實驗性API，，這個 API 可能在未來的版本中會發生變化
    private void processImageProxy(ImageProxy imageProxy, GraphicOverlay graphicOverlay) {
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            Log.d("Camera", "dis3");
            Task<PoseWithClassification> result = viewModel.poseDetector.process(image)
                    .continueWith(classificationExecutor, task -> {
                        Log.d("Camera", "dis1");
                        Pose pose = task.getResult();
                        List<String> classificationResult = viewModel.poseClassifierProcessor.getPoseResult(pose);
                        return new PoseWithClassification(pose, classificationResult);
                    })
                    .addOnSuccessListener(executor, results -> {
                        boolean ff = false;
                        // poseCounter( results , ff );
                        graphicOverlay.clear();
                        graphicOverlay.add(
                                new PoseGraphic(
                                        graphicOverlay,
                                        results.pose,
                                        true,
                                        true,
                                        true,
                                        results.classificationResult,
                                        true));
                        graphicOverlay.postInvalidate();
                    })
                    .addOnFailureListener(executor, e -> {
                        Log.e("PoseEstimation", "姿勢偵測失敗");
                    })
                    .addOnCompleteListener(task -> {
                        imageProxy.close();
                    });
        }
    }

//    private void poseCounter(PoseWithClassification results, boolean ff) {
//        Log.d("Camera", "dis2");
//        List<String> temp = results.classificationResult;
//        String hu;
//        List<String> hu_list;
//        assert (temp.size() <= 2);
//        if (temp.size() == 2) {
//            hu = temp.get(0);   // %s : %d reps
//            hu_list = stringToWords(hu);    // stringToWords( "pushups_down : 13 reps" ), hu_list = [ "pushups_down" , ":" , "13" , "reps" ]
//            assert (hu_list.size() == 4 || hu_list.size() == 0);
//            if (hu_list.size() == 4) {
//                if ((hu_list.get(0).equals("pushups_down")) && (pushups == true)) {
//                    ff = true;
//                    boolean haha = false;
//                    if ((viewModel.now != "pushups")) {
//                        viewModel.now = "pushups";
//                        ++viewModel.pushups;
//                        viewModel.squats = 0;
//                        haha = true;
//                    } else if (viewModel.pushups_cnt < Integer.parseInt(hu_list.get(2))) {
//                        ++viewModel.pushups;
//                        haha = true;
//                    }
//                    viewModel.pushups_cnt = Integer.parseInt(hu_list.get(2));
//                    String toSpeak = "Pushup number " + Integer.toString(viewModel.pushups_cnt);
//                    if (viewModel.pushups > 3) toSpeak = Integer.toString(viewModel.pushups_cnt);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        if (!viewModel.speaker && haha)
//                            text_to_speech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
//                    } else {
//                        if (!viewModel.speaker && haha)
//                            text_to_speech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
//                    }
//                } else if ((hu_list.get(0).equals("squats_down")) && (squats == true)) {
//                    ff = true;
//                    boolean haha = false;
//                    if ((viewModel.now != "squats")) {
//                        viewModel.now = "squats";
//                        ++viewModel.squats;
//                        viewModel.pushups = 0;
//                        haha = true;
//                    } else if (viewModel.squats_cnt < Integer.parseInt(hu_list.get(2))) {
//                        ++viewModel.squats;
//                        haha = true;
//                    }
//                    viewModel.squats_cnt = Integer.parseInt(hu_list.get(2));
//                    String toSpeak = "Squat number " + Integer.toString(viewModel.squats_cnt);
//                    if (viewModel.squats > 3) toSpeak = Integer.toString(viewModel.squats_cnt);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        if (!viewModel.speaker && haha)
//                            text_to_speech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
//                    } else {
//                        if (!viewModel.speaker && haha)
//                            text_to_speech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
//                    }
//                }
//                if (temp.size() == 2) {
//                    hu = temp.get(0);
//                    hu_list = stringToWords(hu);
//                    assert (hu_list.size() == 4 || hu_list.size() == 0);
//                    if (hu_list.size() == 4) {
//                        if ((hu_list.get(0).equals("pushups_down")) && (pushups == true)) {
//                            ff = true;
//                            boolean haha = false;
//                            if ((viewModel.now != "pushups")) {
//                                viewModel.now = "pushups";
//                                ++viewModel.pushups;
//                                viewModel.squats = 0;
//                                haha = true;
//                            } else if (viewModel.pushups_cnt < Integer.parseInt(hu_list.get(2))) {
//                                ++viewModel.pushups;
//                                haha = true;
//                            }
//                            viewModel.pushups_cnt = Integer.parseInt(hu_list.get(2));
//                            String toSpeak = "Pushup number " + Integer.toString(viewModel.pushups_cnt);
//                            if (viewModel.pushups > 3) toSpeak = Integer.toString(viewModel.pushups_cnt);
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                if (!viewModel.speaker && haha)
//                                    text_to_speech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
//                            } else {
//                                if (!viewModel.speaker && haha)
//                                    text_to_speech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
//                            }
//                        } else if ((hu_list.get(0).equals("squats_down")) && (squats == true)) {
//                            ff = true;
//                            boolean haha = false;
//                            if ((viewModel.now != "squats")) {
//                                viewModel.now = "squats";
//                                ++viewModel.squats;
//                                viewModel.pushups = 0;
//                                haha = true;
//                            } else if (viewModel.squats_cnt < Integer.parseInt(hu_list.get(2))) {
//                                ++viewModel.squats;
//                                haha = true;
//                            }
//                            viewModel.squats_cnt = Integer.parseInt(hu_list.get(2));
//                            String toSpeak = "Squat number " + Integer.toString(viewModel.squats_cnt);
//                            if (viewModel.squats > 3) toSpeak = Integer.toString(viewModel.squats_cnt);
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                                if (!viewModel.speaker && haha)
//                                    text_to_speech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null, null);
//                            } else {
//                                if (!viewModel.speaker && haha)
//                                    text_to_speech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
//                            }
//                        }
//                        //binding.textViewReps.setText("SQUATS: " + Integer.toString(viewModel.squats_cnt));
//                    }
//                }
//            }
//        }
//    }


    //    pushups_down : 13 reps
    //    pushups_up : 0.6. confidence
    //    List<String> hu_list = stringToWords( "pushups_down : 13 reps" );
    //    hu_list = [ "pushups_down" , ":" , "13" , "reps" ]
    public List<String> stringToWords(String inputString) {
        return Arrays.stream(inputString.trim().split("\\s+"))
                .filter(word -> !word.trim().isEmpty())
                .collect(Collectors.toList());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(getActivity() != null) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
        if (text_to_speech != null) {
            text_to_speech.stop();
            text_to_speech.shutdown();
        }
    }
}