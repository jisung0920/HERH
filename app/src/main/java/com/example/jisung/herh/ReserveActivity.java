package com.example.jisung.herh;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLEncoder;

import java.util.Scanner;


public class ReserveActivity extends AppCompatActivity {
    private TimePicker timer;
    private EditText peopleNum, phoneNum, name;
    private NumberPicker error;
    private Display display;
    private String user, phone_Num, people_Num, time;
    private int error_Num, hour, min;
    // 유저 정보
    private TextView date, store;
    private String store_name, day_infor, user_id;



    protected void init() {  // 정보 저장
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        Intent intent = getIntent();
        store_name = intent.getStringExtra("store");
        day_infor = intent.getStringExtra("date");
        user_id = intent.getStringExtra("id");

        date = (TextView) findViewById(R.id.date);
        store = (TextView) findViewById(R.id.store);


        date.setText(day_infor);        // 날짜 변경
        store.setText(store_name);      // 가게명 변경

        timer = (TimePicker) findViewById(R.id.time);

        peopleNum = (EditText) findViewById(R.id.people_Num);


        error = (NumberPicker) findViewById(R.id.error); // 오차 인원
        error.setMinValue(0);
        error.setMaxValue(10);
        name = (EditText) findViewById(R.id.name);  // 팀 명
        phoneNum = (EditText) findViewById(R.id.phoneNum);  //전화번호

    }

    protected void send_Data(String string) {
        class Send_Data extends AsyncTask<String, Void, String> {
            String loginResult(InputStream in) {
                String data = "";
                Scanner s = new Scanner(in);
                data += s.nextLine();
                s.close();
                return data;
            }
            @Override
            protected String doInBackground(String... params) {
                String uri = params[0]; //params에 php 주소가 있으므로 uri에 주소가 들어간다.
                try {
                    URL url = new URL(uri);//url 객체가 생성된다.
                    HttpURLConnection con = (HttpURLConnection) url.openConnection(); //url을 연결하기 위한 객체 con을 생성
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    String postData = "userID=" + URLEncoder.encode(user_id) + "&userName=" + URLEncoder.encode(user) + "&Tel=" + URLEncoder.encode(phone_Num) +
                            "&date=" + URLEncoder.encode(day_infor) + "&time=" + URLEncoder.encode(time) + "&number=" + URLEncoder.encode(people_Num) + "&error=" +
                            URLEncoder.encode(error_Num + "") + "&store=" + URLEncoder.encode(store_name);
                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(postData.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    InputStream inputStream = con.getInputStream();
                    String result = loginResult(inputStream);
                    inputStream.close();
                    con.disconnect();
                    return result;//onPostExecute실행


                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String result) {

                Log.d("teset", result);
                if (result.equals("SUC")) {
                    Toast.makeText(ReserveActivity.this, "예약 되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent save = new Intent(ReserveActivity.this, MainActivity.class);
                    save.putExtra("id", user_id);
                    startActivity(save);
                    finish();
                } else {
                    Toast.makeText(ReserveActivity.this, "예약에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        Send_Data g = new Send_Data();
        g.execute(string);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);
        if(!NetworkCheck.connect(this)) {
            Toast.makeText(this, "인터넷 연결 상태를 확인해 주세요", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
//        numberPickerTextColor((NumberPicker) findViewById(R.id.error), Color.BLACK);
//        dateTimePickerTextColour((TimePicker) findViewById(R.id.time), Color.BLACK);
        init();
    }

    public void onClick(View v) {
        if(!NetworkCheck.connect(this)) {
            Toast.makeText(this, "인터넷 연결 상태를 확인해 주세요", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        user = name.getText().toString();
        phone_Num = phoneNum.getText().toString();
        people_Num = peopleNum.getText().toString();
        error_Num = error.getValue();
        hour = timer.getHour();
        min = timer.getMinute();
        time = hour + ":" + min + ":00";

        // 입력이 다 이뤄지지 않을 경우
        if (people_Num.equals("") || user.equals("") || phone_Num.equals("")) {
            Toast.makeText(this, "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
        else if(Integer.parseInt(people_Num)<5){
            Toast.makeText(this, "예약인원을 확인해 주세요.(5명 이상)", Toast.LENGTH_SHORT).show();
        }
        else if(phone_Num.length()<10)
            Toast.makeText(this, "연락처를 확인해 주세요.", Toast.LENGTH_SHORT).show();
        // 입력이 다 이뤄진 경우
        else {
            View dlgview = View.inflate(this, R.layout.pop_up, null);
//            final AlertDialog.Builder dlg = new AlertDialog.Builder(this);

            final Dialog dialog = new Dialog(this); //대화상자 생성
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(dlgview); //대화상자 뷰 설정
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();

            params.width = (int) (display.getWidth() * 0.9);
            params.height = (int) (display.getHeight() * 0.5);

            final TextView store_title = (TextView) dlgview.findViewById(R.id.store_name);
            final TextView user_infor = (TextView) dlgview.findViewById(R.id.team_Infor);
            final TextView peo_infor = (TextView) dlgview.findViewById(R.id.peo_Infor);
            final TextView phone_infor = (TextView) dlgview.findViewById(R.id.phone_Infor);
            final TextView time_infor = (TextView) dlgview.findViewById(R.id.time_Infor);
            final TextView date_infor = (TextView) dlgview.findViewById(R.id.date_Infor);
            store_title.setText(store_name);
            user_infor.setText(user);
            time_infor.setText(hour + "시 " + min + "분");
            date_infor.setText(day_infor);
            phone_infor.setText(phone_Num);
            peo_infor.setText(people_Num + "(" + error_Num + ")" + "명");
            dialog.getWindow().setAttributes(params);
            dialog.show();


            Button yes = (Button) dlgview.findViewById((R.id.yes));
            Button no = (Button) dlgview.findViewById((R.id.no));


            //예약 확인시
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    send_Data("http://jisung0920.cafe24.com/hers_data_send.php");
                    dialog.dismiss();

                }
            });
            // 예약 취소시
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }



//    void numberPickerTextColor(NumberPicker $v, int $c) {
//        for (int i = 0, j = $v.getChildCount(); i < j; i++) {
//            View t0 = $v.getChildAt(i);
//            if (t0 instanceof EditText) {
//                try {
//                    Field t1 = $v.getClass().getDeclaredField("mSelectorWheelPaint");
//                    t1.setAccessible(true);
//                    ((Paint) t1.get($v)).setColor($c);
//                    ((EditText) t0).setTextColor($c);
//                    $v.invalidate();
//                } catch (Exception e) {
//                }
//            }
//        }
//    }
//
//    void dateTimePickerTextColour(ViewGroup $picker, int $c) {
//
//        for (int i = 0, j = $picker.getChildCount(); i < j; i++) {
//            View t0 = (View) $picker.getChildAt(i);
//
//            //NumberPicker는 아까만든 함수로 발라내고
//            if (t0 instanceof NumberPicker) numberPickerTextColor((NumberPicker) t0, $c);
//
//                //아니면 계속 돌아봐
//            else if (t0 instanceof ViewGroup) dateTimePickerTextColour((ViewGroup) t0, $c);
//        }
//    }
}


