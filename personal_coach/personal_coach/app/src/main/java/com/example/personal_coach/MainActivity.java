package com.example.personal_coach;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
    }

    private void jumpToCamera(String exerciseName) {
        Intent intent = new Intent(MainActivity.this, startSport.class);
        intent.putExtra("chooseExerciseName", exerciseName);
        startActivity(intent);
    }

}