package com.tqc.gdd01;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class Begin extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.begin);

        ImageView imageView =findViewById(R.id.imageView2);
        imageView.setImageResource(R.drawable.runner);

        Button b1 = (Button)findViewById(R.id.button1);
        b1.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Begin.this, choose.class);
                startActivity(intent);
            }
        });

    }
}
