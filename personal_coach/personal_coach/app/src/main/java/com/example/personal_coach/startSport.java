package com.example.personal_coach;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.mediapipe.components.CameraHelper;
import com.google.mediapipe.formats.proto.LandmarkProto;
import com.google.mediapipe.glutil.EglManager;
import com.google.mediapipe.components.FrameProcessor;
import com.google.mediapipe.components.ExternalTextureConverter;
import com.google.mediapipe.components.CameraXPreviewHelper;
import com.google.mediapipe.framework.AndroidAssetUtil;
import com.google.mediapipe.framework.AndroidPacketGetter;
import com.google.mediapipe.framework.Packet;
import com.google.mediapipe.framework.PacketGetter;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmarkList;
import com.google.mediapipe.formats.proto.LandmarkProto.NormalizedLandmark;
import com.google.mediapipe.components.PermissionHelper;

import com.google.protobuf.InvalidProtocolBufferException;

import java.util.HashSet;
import java.util.Set;

/* 使用 MediaPipe 框架對攝影機預覽畫面進行姿態追蹤。 */
public class startSport extends AppCompatActivity {
    private static final String TAG = "startSport";

    // 指定 MediaPipe 圖形的二進制文件名稱
    private static final String BINARY_GRAPH_NAME = "pose_tracking_gpu.binarypb";

    /* 輸入、輸出即時影像 */
    private static final String INPUT_VIDEO_STREAM_NAME = "input_video";
    private static final String OUTPUT_VIDEO_STREAM_NAME = "output_video";

    private static final String OUTPUT_LANDMARKS_STREAM_NAME = "pose_landmarks";
    private static final int NUM_HANDS = 2;

    /* 姿勢計數器 */
    private float poseCount = 0;
    private int direction = 0;
    TextView poseCountTextView;

    /* 語音功能 */
    private TextToSpeech tts;
    private Handler handler = new Handler();
    private Set<String> spokenNumbers = new HashSet<>();
    String chooseExercise;

    // 指定使用後置鏡頭
    private static final CameraHelper.CameraFacing CAMERA_FACING = CameraHelper.CameraFacing.BACK;
    // 預處理：將相機預覽畫面傳送到mediapipe框架處理器中
    private static final boolean FLIP_FRAMES_VERTICALLY = true;

    /* 載入動態連結資料庫 */
    static {
        System.loadLibrary("mediapipe_jni");    // mediapipe資料庫
        System.loadLibrary("opencv_java3"); // opencv資料庫
    }

    /* 顯示相機預覽幀 */
    private SurfaceTexture previewFrameTexture;
    private SurfaceView previewDisplayView;
    private EglManager eglManager;

    // 處理幀
    private FrameProcessor processor;

    // 將幀轉換為MediaPipe圖形可以使用的格式
    private ExternalTextureConverter converter;

    /* 獲取應用程序訊息、幫助設置相機預覽 */
    private ApplicationInfo applicationInfo;
    private CameraXPreviewHelper cameraHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutResId());

        /* 讀取頁面傳遞的資訊 */
        Bundle chooseExerciseName = getIntent().getExtras();
        if (chooseExerciseName != null) {
            chooseExercise = chooseExerciseName.getString("chooseExerciseName");
        }

        poseCountTextView = findViewById(R.id.poseCount);
        previewDisplayView = new SurfaceView(this);
        setupPreviewDisplayView();

        /* 文字轉語音 */
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    // TextToSpeech 初始化成功
                    Log.v("TTS", "初始化成功");
                    String text = "請將手機放置在一個可以清晰看見你運動的位置，並確保您全身都在畫面中。運動項目" + chooseExercise;
                    tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                } else {
                    // TextToSpeech 初始化失敗
                    Log.e("TTS", "初始化失敗");
                }
            }
        });

        /* 嘗試獲取應用程序信息 */
        try {
            applicationInfo =
                    getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Cannot find application info: " + e);
        }

        /* 處理即時影像 */
        AndroidAssetUtil.initializeNativeAssetManager(this);
        eglManager = new EglManager(null);
        processor =
                new FrameProcessor(
                        this,
                        eglManager.getNativeContext(),
                        BINARY_GRAPH_NAME,
                        INPUT_VIDEO_STREAM_NAME,
                        OUTPUT_VIDEO_STREAM_NAME);
        processor
                .getVideoSurfaceOutput()
                .setFlipY(FLIP_FRAMES_VERTICALLY);


        /* 從處理後的影像中擷取出人體關鍵點的資訊，並將其顯示在畫面上 */
        processor.addPacketCallback(
                OUTPUT_LANDMARKS_STREAM_NAME,   // 繪畫出關鍵點
                (packet) -> {
                    Log.v(TAG, "Received Pose landmarks packet.");
                    try {
                        byte[] landmarksRaw = PacketGetter.getProtoBytes(packet);
                        NormalizedLandmarkList poseLandmarks = NormalizedLandmarkList.parseFrom(landmarksRaw);
                        Log.v(TAG, "[TS:" + packet.getTimestamp() + "] " + PoseLandMark.getkey(poseLandmarks));
                        SurfaceHolder surfaceHolder = previewDisplayView.getHolder();

                        if ("伏地挺身".equals(chooseExercise)) {
                            start_Sport(PoseLandMark.pushup(poseLandmarks) , "Push-Up：");
                        } else if ("臀橋".equals(chooseExercise)) {
                            start_Sport(PoseLandMark.GluteBridge(poseLandmarks), "Glute-Bridge：");
                        } else if ("仰臥起坐".equals(chooseExercise)) {
                            start_Sport(PoseLandMark.situp(poseLandmarks) , "Sit-Up：");
                        } else if ("深蹲".equals(chooseExercise)) {
                            start_Sport(PoseLandMark.squat(poseLandmarks) , "Squat：");
                        } else if ("俯臥背伸".equals(chooseExercise)) {
                            start_Sport(PoseLandMark.prone_back_extension((poseLandmarks)), "Prone-Extension：");
                        }

                    } catch (InvalidProtocolBufferException exception) {
                        Log.e(TAG, "failed to get proto.", exception);
                    }
                }
        );
        PermissionHelper.checkAndRequestCameraPermissions(this);
    }

    protected int getContentViewLayoutResId() {
        return R.layout.camera_startsport;
    }

    @Override
    protected void onResume() {
        super.onResume();
        converter = new ExternalTextureConverter(eglManager.getContext(), 2);
        converter.setFlipY(FLIP_FRAMES_VERTICALLY);
        converter.setConsumer(processor);

        /* 如果已獲取相機權限，啟動相機預覽 */
        if (PermissionHelper.cameraPermissionsGranted(this)) {
            startCamera();
        }
    }

    /* 跳轉到其他畫面時：關閉資源，隱藏預覽視圖 */
    @Override
    protected void onPause() {
        super.onPause();
        converter.close();
        previewDisplayView.setVisibility(View.GONE);
    }

    /* 系統向使用者發出提示，詢問是否允許使用權限。當使用者回應後，系統會呼叫此方法，應用程式可以獲取權限狀態，並執行相應的操作 */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /* 相機啟動時，繪製預覽畫面 */
    protected void onCameraStarted(SurfaceTexture surfaceTexture) {
        previewFrameTexture = surfaceTexture;
        previewDisplayView.setVisibility(View.VISIBLE);
    }

    /* 回傳相機預覽畫面的解析度 */
    protected Size cameraTargetResolution() {
        return null;
    }

    /* 啟動相機，顯示即時影像 */
    public void startCamera() {
        cameraHelper = new CameraXPreviewHelper();
        cameraHelper.setOnCameraStartedListener(
                surfaceTexture -> {
                    onCameraStarted(surfaceTexture);
                });
        CameraHelper.CameraFacing cameraFacing = CameraHelper.CameraFacing.BACK;    // 指定相機的方向為後置鏡頭
        cameraHelper.startCamera( this, cameraFacing, /*unusedSurfaceTexture=*/ null, cameraTargetResolution());    // 畫面顯示
    }

    /* 畫面的寬度和高度 */
    protected Size computeViewSize(int width, int height) {
        return new Size(width, height);
    }

    /* 當設備的螢幕方向改變調用此函式 */
    protected void onPreviewDisplaySurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Size viewSize = computeViewSize(width, height);
        Size displaySize = cameraHelper.computeDisplaySizeFromViewSize(viewSize);
        boolean isCameraRotated = cameraHelper.isCameraRotated();   // 檢查手機是否旋轉

        converter.setSurfaceTextureAndAttachToGLContext(
                previewFrameTexture,
                isCameraRotated ? displaySize.getHeight() : displaySize.getWidth(),
                isCameraRotated ? displaySize.getWidth() : displaySize.getHeight());
    }

    /* 設置預覽視圖 */
    private void setupPreviewDisplayView() {
        previewDisplayView.setVisibility(View.GONE);    // 將預覽視圖設為不可見，在設置預覽視圖之前將其隱藏，確保在設置之前不會顯示空白視圖
        ViewGroup viewGroup = findViewById(R.id.preview_display_layout);    // 找到佈局中的預覽視圖佈局
        viewGroup.addView(previewDisplayView);  // 將預覽視圖添加到佈局中
        previewDisplayView
                .getHolder()
                .addCallback(
                        new SurfaceHolder.Callback() {
                            /* 創建預覽視圖時調用 */
                            @Override
                            public void surfaceCreated(SurfaceHolder holder) {
                                processor.getVideoSurfaceOutput().setSurface(holder.getSurface());
                                Log.d("Surface","Surface Created");
                            }

                            /* 變更預覽視圖時調用 */
                            @Override
                            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                                onPreviewDisplaySurfaceChanged(holder, format, width, height);
                                Log.d("Surface","Surface Changed");
                            }

                            /* 銷毀預覽視圖時調用 */
                            @Override
                            public void surfaceDestroyed(SurfaceHolder holder) {
                                processor.getVideoSurfaceOutput().setSurface(null);
                                Log.d("Surface","Surface destroy");
                            }
                        });
    }

    private void start_Sport(String sport, String sportTAG) {
        if (sport.equals("UP")) {
            if (direction == 0) {
                poseCount += 0.5;
                direction = 1;
            }
        }
        if (sport.equals("DOWN")) {
            if (direction == 1) {
                poseCount += 0.5;
                direction = 0;
            }
        }
        if (poseCount % 1 == 0) {
            final String poseCountStr = String.format("%.0f", poseCount);

            SharedPreferences sharedPref = getSharedPreferences("counter", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            SharedPreferences.Editor editor2 = sharedPref.edit();

            if ("伏地挺身".equals(chooseExercise)) {
                editor.putFloat("Push-Up", poseCount);
            } else if ("臀橋".equals(chooseExercise)) {
                editor.putFloat("Glute-Bridge", poseCount);
            } else if ("仰臥起坐".equals(chooseExercise)) {
                editor.putFloat("Sit-Up", poseCount);
            } else if ("深蹲".equals(chooseExercise)) {
                editor.putFloat("Squat", poseCount);
            } else if ("俯臥背伸".equals(chooseExercise)) {
                editor.putFloat("Prone-Extension", poseCount);
            }
            editor.commit();

            editor.putFloat("Push-Up", 0.0f);
            editor.putFloat("Glute-Bridge", 0.0f);
            editor.putFloat("Sit-Up", 0.0f);
            editor.putFloat("Sqaut", 0.0f);
            editor.putFloat("Prone-Extension", 0.0f);

            editor2.commit();


            Log.v(TAG, sportTAG + poseCountStr);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    poseCountTextView.setText(sportTAG + poseCountStr);

                    if (!spokenNumbers.contains(poseCountStr)) {
                        spokenNumbers.add(poseCountStr);
                        tts.speak(poseCountStr, TextToSpeech.QUEUE_FLUSH, null, null);
                        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                            @Override
                            public void onStart(String utteranceId) {}

                            @Override
                            public void onDone(String utteranceId) {
                                startSport.this.finish();
                            }

                            @Override
                            public void onError(String utteranceId) {}
                        });
                    }
                }
            });
        }
    }
}