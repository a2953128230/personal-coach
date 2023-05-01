package com.example.personal_coach;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    private TextView pushup_textView;
    private TextView glutebridge_textView;
    private TextView situp_textView;
    private TextView squat_textView;
    private TextView proneextension_textView;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences("counter", MODE_PRIVATE);

        pushup_textView = findViewById(R.id.pushup_textView);
        glutebridge_textView = findViewById(R.id.glutebridge_textView);
        situp_textView = findViewById(R.id.situp_textView);
        squat_textView = findViewById(R.id.squat_textView);
        proneextension_textView = findViewById(R.id.proneextension_textView);

        Button btn_pushup = findViewById(R.id.btn_pushup); // "伏地挺身"
        btn_pushup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "btn_pushups clicked");
                jumpToCamera("伏地挺身");
            }
        });

        Button btn_GluteBridge = findViewById(R.id.btn_GluteBridge);    // "臀橋"
        btn_GluteBridge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "btn_gluteBridges clicked");
                jumpToCamera("臀橋");
            }
        });

        Button btn_situp = findViewById(R.id.btn_situp);  // "仰臥起坐"
        btn_situp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "btn_situps clicked");
                jumpToCamera("仰臥起坐");
            }
        });

        Button btn_squat = findViewById(R.id.btn_squat);  // "深蹲"
        btn_squat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "btn_squats clicked");
                jumpToCamera("深蹲");
            }
        });

        Button btn_ProneExtension = findViewById(R.id.btn_ProneExtension); // "俯臥背伸"
        btn_ProneExtension.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MainActivity", "btn_button5 clicked");
                jumpToCamera("俯臥背伸");
            }
        });

//        文字按鈕
        Button b1t = findViewById(R.id.btn_situpst);
        Button b2t = findViewById(R.id.btn_squatst);
        Button b3t = findViewById(R.id.btn_pushupst);
        Button b4t = findViewById(R.id.btn_gluteBridgest);
        Button b5t = findViewById(R.id.btn_proextension);

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

        //    menu button
        Button bmiButton = findViewById(R.id.BMIbutton);
        Button recordButton = findViewById(R.id.RecordButton);
        Button mainMenu = findViewById(R.id.mainmenuButton);


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

        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
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
    }

    protected void onResume() {
        super.onResume();

        // 讀poseCount值
        float pushUpCount = sharedPref.getFloat("Push-Up", 0);
        float gluteBridgeCount = sharedPref.getFloat("Glute-Bridge", 0);
        float sitUpCount = sharedPref.getFloat("Sit-Up", 0);
        float squatCount = sharedPref.getFloat("Squat", 0);
        float proneExtensionCount = sharedPref.getFloat("Prone-Extension", 0);

        Log.d("MainActivity", "squatCount = " + squatCount);

        // 將poseCount的值設置到TextView上
        final String PushUpCountStr = String.format("%.0f", pushUpCount);
        final String gluteBrudgeCountStr = String.format("%.0f", gluteBridgeCount);
        final String sitUpCountStr = String.format("%.0f", sitUpCount);
        final String squatCountStr = String.format("%.0f", squatCount);
        final String proneExtensionCountStr = String.format("%.0f", proneExtensionCount);

        Log.d("Squat Count", "Squat Count: " + squatCountStr);

        pushup_textView.setText(PushUpCountStr);
        glutebridge_textView.setText(gluteBrudgeCountStr);
        situp_textView.setText(sitUpCountStr);
        squat_textView.setText(squatCountStr);
        proneextension_textView.setText(proneExtensionCountStr);
    }

    private void jumpToCamera(String exerciseName) {
        Intent intent = new Intent(MainActivity.this, startSport.class);
        intent.putExtra("chooseExerciseName", exerciseName);
        startActivity(intent);
    }
}