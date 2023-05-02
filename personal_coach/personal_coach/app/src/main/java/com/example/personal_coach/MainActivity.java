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
    private SharedPreferences sharedPref1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences("counter", MODE_PRIVATE);
        sharedPref1 = getSharedPreferences("myPrefs1", MODE_PRIVATE);

        SharedPreferences.Editor editor3 = sharedPref1.edit();

        pushup_textView = findViewById(R.id.pushup_textView);
        glutebridge_textView = findViewById(R.id.glutebridge_textView);
        situp_textView = findViewById(R.id.situp_textView);
        squat_textView = findViewById(R.id.squat_textView);
        proneextension_textView = findViewById(R.id.proneextension_textView);

        float pushupCountTotal = sharedPref1.getFloat("PushUpTotal", 0.0f);
        float glutebridgeCountTotal = sharedPref1.getFloat("GluteBridgeTotal", 0.0f);
        float situpCountTotal = sharedPref1.getFloat("SitUpTotal", 0.0f);
        float squatCountTotal = sharedPref1.getFloat("SquatTotal", 0.0f);
        float proneextensionCountTotal = sharedPref1.getFloat("ProneExtensionTotal", 0.0f);

        Button refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor3.putFloat("PushUpTotal", 0.0f);
                editor3.putFloat("GluteBridgeTotal", 0.0f);
                editor3.putFloat("SitUpTotal", 0.0f);
                editor3.putFloat("SquatTotal", 0.0f);
                editor3.putFloat("ProneExtensionTotal", 0.0f);
                editor3.commit();

                final String PushUpCountStr = String.format("%.0f", pushupCountTotal);
                final String gluteBridgeCountStr = String.format("%.0f", glutebridgeCountTotal);
                final String sitUpCountStr = String.format("%.0f", situpCountTotal);
                final String squatCountStr = String.format("%.0f", squatCountTotal);
                final String proneExtensionCountStr = String.format("%.0f", proneextensionCountTotal);

                pushup_textView.setText(PushUpCountStr);
                glutebridge_textView.setText(gluteBridgeCountStr);
                situp_textView.setText(sitUpCountStr);
                squat_textView.setText(squatCountStr);
                proneextension_textView.setText(proneExtensionCountStr);
            }
        });

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

        SharedPreferences.Editor editor1 = sharedPref1.edit();
        SharedPreferences.Editor editor2 = sharedPref.edit();

        float pushupCountTotal = sharedPref1.getFloat("PushUpTotal", 0.0f);
        float glutebridgeCountTotal = sharedPref1.getFloat("GluteBridgeTotal", 0.0f);
        float situpCountTotal = sharedPref1.getFloat("SitUpTotal", 0.0f);
        float squatCountTotal = sharedPref1.getFloat("SquatTotal", 0.0f);
        float proneextensionCountTotal = sharedPref1.getFloat("ProneExtensionTotal", 0.0f);

        float pushUpCount = 0.0f;
        float gluteBridgeCount = 0.0f;
        float sitUpCount = 0.0f;
        float squatCount = 0.0f;
        float proneExtensionCount = 0.0f;

        // 讀poseCount值
        pushUpCount = sharedPref.getFloat("Push-Up", 0);
        gluteBridgeCount = sharedPref.getFloat("Glute-Bridge", 0);
        sitUpCount = sharedPref.getFloat("Sit-Up", 0);
        squatCount = sharedPref.getFloat("Squat", 0);
        proneExtensionCount = sharedPref.getFloat("Prone-Extension", 0);

        pushupCountTotal += pushUpCount;
        glutebridgeCountTotal += gluteBridgeCount;
        situpCountTotal += sitUpCount;
        squatCountTotal += squatCount;
        proneextensionCountTotal += proneExtensionCount;

        editor2.putFloat("Push-Up", 0.0f);
        editor2.putFloat("Glute-Bridge", 0.0f);
        editor2.putFloat("Sit-Up", 0.0f);
        editor2.putFloat("Squat", 0.0f);
        editor2.putFloat("Prone-Extension", 0.0f);

        editor2.commit();

        Log.d("MainActivity", "squatCount = " + squatCount);

        // 將poseCount的值設置到TextView上
        final String PushUpCountStr = String.format("%.0f", pushupCountTotal);
        final String gluteBridgeCountStr = String.format("%.0f", glutebridgeCountTotal);
        final String sitUpCountStr = String.format("%.0f", situpCountTotal);
        final String squatCountStr = String.format("%.0f", squatCountTotal);
        final String proneExtensionCountStr = String.format("%.0f", proneextensionCountTotal);

        Log.d("Squat Count", "Squat Count: " + squatCountStr);

        pushup_textView.setText(PushUpCountStr);
        glutebridge_textView.setText(gluteBridgeCountStr);
        situp_textView.setText(sitUpCountStr);
        squat_textView.setText(squatCountStr);
        proneextension_textView.setText(proneExtensionCountStr);

        editor1.putFloat("PushUpTotal", pushupCountTotal);
        editor1.putFloat("GluteBridgeTotal", glutebridgeCountTotal);
        editor1.putFloat("SitUpTotal", situpCountTotal);
        editor1.putFloat("SquatTotal", squatCountTotal);
        editor1.putFloat("ProneExtensionTotal", proneextensionCountTotal);

        editor1.commit();
    }

    private void jumpToCamera(String exerciseName) {
        Intent intent = new Intent(MainActivity.this, startSport.class);
        intent.putExtra("chooseExerciseName", exerciseName);
        startActivity(intent);
    }
}