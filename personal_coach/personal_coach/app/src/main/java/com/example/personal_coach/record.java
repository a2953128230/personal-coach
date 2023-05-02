package com.example.personal_coach;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class record extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);

        Button bmiButton = findViewById(R.id.BMIbutton);
        Button recordButton = findViewById(R.id.RecordButton);
        Button mainMenu = findViewById(R.id.mainmenuButton);
        Button menuButton = findViewById(R.id.label);

        SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
        int value = preferences.getInt("value", 0);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<String> data1 = new ArrayList<>();

        ArrayList<String> data2 = new ArrayList<>();
        ArrayList<String> data3 = new ArrayList<>();
        for(int i=0;i<=value;i++)
        {
            String date = preferences.getString("pDate"+i, null);
            data1.add(date);
            String sex = preferences.getString("pSex"+i, null);
            data2.add(sex);
            String Bmi = preferences.getString("pBmi"+i, null);
            data3.add(Bmi);
        }


        MyAdapter adapter = new MyAdapter(data1,data2,data3);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        Button clearButton = findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data1.clear();
                data2.clear();
                data3.clear();

                SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                // Clear the data in your ArrayLists as well, if necessary
                // ...
                // Notify the adapter that the data has changed
                adapter.notifyDataSetChanged();

                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.closeDrawers();
            }
        });


        bmiButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(record.this, GDD01.class);
                startActivity(intent);
            }
        });
        recordButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(record.this, record.class);
                startActivity(intent);
            }
        });

        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(record.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });


    }
}

