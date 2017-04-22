package com.gizwits.opensource.appkit.ControlModule;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.gizwits.opensource.appkit.R;

import java.util.Calendar;

public class TimerSet extends Activity implements View.OnClickListener {

//    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private EditText mEditText;

    // 定义5个记录当前时间的变量
    byte[] setRepeatDay = new byte[7] ;
//    private int year;
//    private int month;
//    private int day;
    private int hour;
    private int minute;

    //日期设置
    private Button sun_btn;
    private Button mon_btn;
    private Button tue_btn;
    private Button wed_btn;
    private Button thu_btn;
    private Button fri_btn;
    private Button sat_btn;

    //日期显示
    private TextView mon_text;
    private TextView tue_text;
    private TextView wed_text;
    private TextView thu_text;
    private TextView fri_text;
    private TextView sat_text;
    private TextView sun_text;

    private EditText reaptead;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_timer_set);

        initView();

    }

    private void initView() {
//        mDatePicker = (DatePicker) findViewById(R.id.datePicker);
        mTimePicker = (TimePicker) findViewById(R.id.timePicker);
//        mEditText = (EditText) findViewById(R.id.show);
        sun_btn = (Button)findViewById(R.id.sun_btn);
        mon_btn = (Button)findViewById(R.id.mon_btn);
        tue_btn = (Button)findViewById(R.id.tue_btn);
        wed_btn = (Button)findViewById(R.id.wed_btn);
        thu_btn = (Button)findViewById(R.id.thu_btn);
        fri_btn = (Button)findViewById(R.id.fri_btn);
        sat_btn = (Button)findViewById(R.id.sat_btn);
        mon_text = (TextView)findViewById(R.id.mon_text);
        tue_text = (TextView)findViewById(R.id.tue_text);
        wed_text = (TextView)findViewById(R.id.wed_text);
        thu_text = (TextView)findViewById(R.id.thu_text);
        fri_text = (TextView)findViewById(R.id.fri_text);
        sat_text = (TextView)findViewById(R.id.sat_text);
        sun_text = (TextView)findViewById(R.id.sun_text);


       // reaptead = (EditText) findViewById(R.id.repeat);
        sun_btn.setOnClickListener(this);
        mon_btn.setOnClickListener(this);
        tue_btn.setOnClickListener(this);
        wed_btn.setOnClickListener(this);
        thu_btn.setOnClickListener(this);
        fri_btn.setOnClickListener(this);
        sat_btn.setOnClickListener(this);
        Button cancel = (Button) findViewById(R.id.cancel);
        Button complete = (Button)findViewById(R.id.complete);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                byte[] data = {(byte)hour,(byte)minute};
                byte[] both = new byte[data.length+setRepeatDay.length];
                System.arraycopy(data,0,both,0,data.length);
                System.arraycopy(setRepeatDay,0,both,data.length,setRepeatDay.length);
                intent.putExtra("extra_data",both);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        // 获取当前的年、月、日、小时、分钟
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);


        // 为TimePicker指定监听器
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker arg0, int hour, int minute) {
                TimerSet.this.hour = hour;
                TimerSet.this.minute = minute;
                // 显示当前日期、时间
//                showDate(year, month, day, hour, minute);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sun_btn:
                if(sun_btn.isSelected()){
                    sun_btn.setSelected(false);
                    setRepeatDay[6] = 0x00;
                    sun_text.setText("");
                }
                else {
                    sun_btn.setSelected(true);
                    setRepeatDay[6] = 0x01;
                    sun_text.setText(R.string.Sunday);
                }
                break;
            case R.id.mon_btn:
                if(mon_btn.isSelected()){
                    mon_btn.setSelected(false);
                    setRepeatDay[0] = 0x00;
                    mon_text.setText("");
                }
                else {
                    mon_btn.setSelected(true);
                    setRepeatDay[0] = 0x01;
                    mon_text.setText(R.string.Monday);
                }
                break;
            case R.id.tue_btn:
                if(tue_btn.isSelected()){
                    tue_btn.setSelected(false);
                    setRepeatDay[1] = 0x00;
                    tue_text.setText("");
                }
                else {
                    tue_btn.setSelected(true);
                    setRepeatDay[1] = 0x01;
                    tue_text.setText(R.string.Tuesday);
                }
                break;
            case R.id.wed_btn:
                if(wed_btn.isSelected()){
                    wed_btn.setSelected(false);
                    setRepeatDay[2] = 0x00;
                    wed_text.setText("");
                }
                else {
                    wed_btn.setSelected(true);
                    setRepeatDay[2] = 0x01;
                    wed_text.setText(R.string.Wednesday);
                }
                break;
            case R.id.thu_btn:
                if(thu_btn.isSelected()){
                    thu_btn.setSelected(false);
                    setRepeatDay[3] = 0x00;
                    thu_text.setText("");
                }
                else {
                    thu_btn.setSelected(true);
                    setRepeatDay[3] = 0x01;
                    thu_text.setText(R.string.Thursday);
                }
                break;
            case R.id.fri_btn:
                if(fri_btn.isSelected()){
                    fri_btn.setSelected(false);
                    setRepeatDay[4] = 0x00;
                    fri_text.setText("");
                }
                else {
                    fri_btn.setSelected(true);
                    setRepeatDay[4] = 0x01;
                    fri_text.setText(R.string.Friday);
                }
                break;
            case R.id.sat_btn:
                if(sat_btn.isSelected()){
                    sat_btn.setSelected(false);
                    setRepeatDay[5] = 0x00;
                    sat_text.setText("");
                }
                else {
                    sat_btn.setSelected(true);
                    setRepeatDay[5] = 0x01;
                    sat_text.setText(R.string.Saturday);
                }
                break;
            default:
                break;
        }
    }
}