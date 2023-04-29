package com.example.personal_coach;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class GDD01_child extends Activity
 {
 Bundle bundle;
 Intent intent;
 /** Called when the activity is first created. */
@Override
public void onCreate(Bundle savedInstanceState)
        {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylayout);
        intent=this.getIntent();
        bundle = intent.getExtras();

        final String Sex = bundle.getString("Sex");
        final String date = bundle.getString("date");
        double height = bundle.getDouble("height");
        double weight = bundle.getDouble("weight");

        final String BMI_result = this.getBMI(height,weight);
        String BMI_advice = this.getAdvice(Sex,height,weight);
        ImageView imageView =findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.strong);

        TextView tvBMI=(TextView) findViewById(R.id.tvBMI);
        tvBMI.setText(BMI_result);
        //TextView tvAdvice=(TextView) findViewById(R.id.tvAdvice);
        //tvAdvice.setText(BMI_advice);
        TextView tvDate =(TextView) findViewById(R.id.tvDate);
        tvDate.setText(date);
        Toast.makeText(this,BMI_advice,Toast.LENGTH_LONG).show();


        Button b1 = (Button) findViewById(R.id.button1);
        b1.setOnClickListener(new Button.OnClickListener()
        {
        public void onClick(View v)
        {
        GDD01_child.this.finish();
        }
        });
        Button b2 = (Button) findViewById(R.id.button);
        b2.setOnClickListener(new Button.OnClickListener() {
                @Override
                public void onClick(View view) {
                        showAlertDialog(date,Sex,height,weight);
                }
        });

        }


//BMI值格式化
private String format(double num) {
        String msg = String.format("%.2f", num);
        return msg;
}
//取得BMI值
private String getBMI (double height, double weight)
        {
                        double bmi = weight/(height*height);
                        String msg = getString(R.string.report_result)+format(bmi);
        return msg;
        }
//依BMI值給出建議
private String getAdvice (String Sex, double height, double weight)
        {
                double bmi = weight/(height*height);
                String msg="";
                if(Sex.equals("M")){
                        if(bmi>25){
                                msg = getString(R.string.advice_heavy);

                        }else if(bmi<20){
                                msg = getString(R.string.advice_light);

                        }else{
                                msg = getString(R.string.advice_average);

                        }
                }
                else {
                        if (bmi > 22) {
                                msg = getString(R.string.advice_heavy);

                        } else if (bmi < 18) {
                                msg = getString(R.string.advice_light);

                        } else {
                                msg = getString(R.string.advice_average);
                        }
                }
        return msg;
        }
        private void showAlertDialog(final String date, final String sex, double height, double weight)
         {
                 double bmi = weight/(height*height);
                 String bmiString = String.format("%.2f", bmi);

                 AlertDialog.Builder builder = new AlertDialog.Builder(this);
                 builder.setTitle("BMI資訊");
                 String msg = "日期 : "+date+"\n"+"性別 : "+sex+"\n"+ "BMI結果 : " + bmiString;
                 builder.setMessage(msg);
                 builder.setPositiveButton("確認", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                                 SharedPreferences preferences = getSharedPreferences("myPrefs", MODE_PRIVATE);
                                 SharedPreferences.Editor editor = preferences.edit();
                                 int value = preferences.getInt("value", 0);
                                 if(true) {
                                         editor.putString("pDate" + value, date);
                                         editor.putString("pSex" + value, sex);
                                         editor.putString("pBmi" + value, bmiString);
                                         editor.putInt("value", (value+1));
                                         editor.apply();
                                 }
                                 Intent intent = new Intent(GDD01_child.this, record.class);
                                 startActivity(intent);
                         }
                 });
                 builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                                 dialogInterface.dismiss();
                                 finish();
                         }
                 });
                 AlertDialog dialog = builder.create();
                 dialog.show();
         }
 }
