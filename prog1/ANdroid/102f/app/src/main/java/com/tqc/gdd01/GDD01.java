package com.tqc.gdd01;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class GDD01 extends Activity
{
  private EditText etheight;
  private EditText etweight;
  private EditText edate;
  private RadioButton rb1;
  private RadioButton rb2;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    etheight = findViewById(R.id.editTextNumberDecimal1);
    etweight = findViewById(R.id.editTextNumberDecimal2);
    edate    = findViewById(R.id.editTextDate);
    rb1 = findViewById(R.id.radioButton);
    rb2 = findViewById(R.id.radioButton2);
    Button bmiButton = findViewById(R.id.BMIbutton);
    Button recordButton = findViewById(R.id.RecordButton);
    Button wkb = findViewById(R.id.wkb);
    Button mainmenu = findViewById(R.id.mainmenuButton);

    Button b1 = (Button) findViewById(R.id.button1);
    b1.setOnClickListener(new Button.OnClickListener()
    {
      public void onClick(View v)
      {
        //從輸入介面中取出了的身高、體重值，要將身高、體重值傳送給 child_Activity 後作計算
        double height = Double.parseDouble(etheight.getText().toString());
        double weight = Double.parseDouble(etweight.getText().toString());
        String sex = rb1.isChecked() ? "M" : "F";
        String date = edate.getText().toString();

        //這些附加在 Intent 上的訊息都儲存在 Bundle 物件中
        Bundle bundle = new Bundle();
        bundle.putDouble("height",height);
        bundle.putDouble("weight",weight);
        bundle.putString("Sex",sex);
        bundle.putString("date",date);
        //透過「intent.putExtras(bundle)」敘述，將「bundle」 物件附加在 Intent 上，隨著 Intent 送出而送出
        Intent intent = new Intent(GDD01.this,GDD01_child.class);
        intent.putExtras(bundle);
        startActivity(intent);
      }
    });

    bmiButton.setOnClickListener(new Button.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(GDD01.this, GDD01.class);
        startActivity(intent);
      }
    });
    recordButton.setOnClickListener(new Button.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(GDD01.this, record.class);
        startActivity(intent);
      }
    });
    wkb.setOnClickListener(new Button.OnClickListener() {
      @Override
      public void onClick(View view) {

        Intent intent = new Intent(GDD01.this, wkl.class);
        startActivity(intent);
      }
    });

    mainmenu.setOnClickListener(new Button.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(GDD01.this, choose.class);
        startActivity(intent);
      }
    });

  }

}
