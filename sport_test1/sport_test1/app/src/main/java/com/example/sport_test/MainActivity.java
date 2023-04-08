package com.example.sport_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.sport_test.databinding.ActivityMainBinding;
import com.example.sport_test.posedetector.camerax.CameraFragment;

public class MainActivity extends AppCompatActivity{

    // 表示當向用戶請求權限時，需要傳遞的請求碼
    private static final int REQUEST_CODE_PERMISSIONS = 10;

    // 確定是否已經授予了所需的所有權限
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    // FragmentManger
    private FragmentManager manager;
    private FragmentTransaction transaction;

    private ActivityMainBinding activityMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 請求相機權限
        if (allPermissionsGranted()) {
            startLogic();
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        manager = getSupportFragmentManager();

        Button bmiButton = findViewById(R.id.BMIbutton);
        Button recordButton = findViewById(R.id.RecordButton);
        Button mainMenu = findViewById(R.id.mainmenuButton);

        Button b1t = findViewById(R.id.btn_situpst);
        Button b2t = findViewById(R.id.btn_squatst);
        Button b3t = findViewById(R.id.btn_pushupst);
        Button b4t = findViewById(R.id.btn_gluteBridgest);
        Button b5t = findViewById(R.id.btn_button5t);

        bmiButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GDD01.class);
                startActivity(intent);
            }
        });
        recordButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, record.class);
                startActivity(intent);
            }
        });

        mainMenu.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button menuButton = findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        Button btn_situps = findViewById(R.id.btn_situps);
        btn_situps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "btn_situps clicked");
                jumpToCamera("仰臥起坐");
            }
        });

        Button btn_pushups = findViewById(R.id.btn_pushups);
        btn_pushups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "btn_pushups clicked");
                jumpToCamera("伏地挺身");
            }
        });

        Button btn_gluteBridges = findViewById(R.id.btn_gluteBridges);
        btn_gluteBridges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "btn_gluteBridges clicked");
                jumpToCamera("臀橋");
            }
        });

        Button btn_squats = findViewById(R.id.btn_squats);
        btn_squats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "btn_squats clicked");
                jumpToCamera("深蹲");
            }
        });

        Button btn_button5 = findViewById(R.id.btn_button5);
        btn_button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "btn_button5 clicked");
                jumpToCamera("俯臥背伸");
            }
        });


        b1t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "btn_situps clicked");
                jumpToCamera("仰臥起坐");
            }
        });

        b3t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "btn_pushups clicked");
                jumpToCamera("伏地挺身");
            }
        });

        b4t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "btn_gluteBridges clicked");
                jumpToCamera("臀橋");
            }
        });

        b2t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "btn_squats clicked");
                jumpToCamera("深蹲");
            }
        });

        b5t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "btn_button5 clicked");
                jumpToCamera("俯臥背伸");
            }
        });

    }

    // 檢查所有必須權限是否已經被授權
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // 啟動邏輯
    public void startLogic() {
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());
        manager = getSupportFragmentManager();
    }

    // 當用戶授權或拒絕權限時調用
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startLogic();
            } else {
                Toast.makeText(this, "用戶未授權權限", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void jumpToCamera(String exerciseName) {
        // 創建一個新的 CameraFragment 實例，並將運動名稱作為參數傳遞
        CameraFragment fragment = new CameraFragment();
        Bundle args = new Bundle();
        args.putString("chooseExerciseName", exerciseName);
        fragment.setArguments(args);

        // 使用 FragmentManager 創建一個新的 FragmentTransaction
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // 添加 CameraFragment 到當前的活動中
        fragmentTransaction.add(R.id.drawer_layout, fragment);

        // 將當前的 Fragment 加入到返回堆疊，使返回鍵按下時可以返回到前一個 Fragment
        fragmentTransaction.addToBackStack(null);

        // 進行 FragmentTransaction，顯示 CameraFragment
        fragmentTransaction.commit();
    }

}