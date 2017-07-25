package com.example.jisung.herh;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.net.MalformedURLException;
import java.sql.Time;

public class ReserveActivity extends AppCompatActivity {
    private TimePicker time;
    private EditText peopleNum, phoneNum, name;
    private NumberPicker error;
    // 유저 정보


    private TextView date,store;
    private String store_name, day_infor;

    protected void init(){  // 정보 저장

        Intent intent = getIntent();
        store_name = intent.getStringExtra("store");
        day_infor = intent.getStringExtra("date");

        date = (TextView) findViewById(R.id.date);
        store = (TextView) findViewById(R.id.store);


        date.setText(day_infor);        // 날짜 변경
        store.setText(store_name);      // 가게명 변경

        time = (TimePicker) findViewById(R.id.time);

        peopleNum = (EditText) findViewById(R.id.people_Num);


        error = (NumberPicker) findViewById(R.id.error); // 오차 인원
        error.setMinValue(0);
        error.setMaxValue(10);

        name = (EditText) findViewById(R.id.name);  // 팀 명

        phoneNum = (EditText) findViewById(R.id.phoneNum);  //전화번호

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve);
        init();
    }

    public void onClick(View v) {

        final String user = name.getText().toString();
        final String phone_Num = phoneNum.getText().toString();
        final String people_Num = peopleNum.getText().toString();
        final int error_Num = error.getValue();
        final int hour = time.getHour();
        final int min = time.getMinute();
        final String time = hour+":"+min+":00";

        // 입력이 다 이뤄지지 않을 경우
        if (people_Num.equals("") || user.equals("") || phone_Num.equals("")){
            Toast.makeText(this, "모든 정보를 입력하세요.", Toast.LENGTH_SHORT).show();
        }
        // 입력이 다 이뤄진 경우
        else {
            View dlgview = View.inflate(this, R.layout.pop_up, null);
            final AlertDialog.Builder dlg = new AlertDialog.Builder(this);

            final TextView user_infor = (TextView) dlgview.findViewById(R.id.team_Infor);
            final TextView peo_infor = (TextView) dlgview.findViewById(R.id.peo_Infor);
            final TextView phone_infor = (TextView) dlgview.findViewById(R.id.phone_Infor);
            final TextView time_infor = (TextView) dlgview.findViewById(R.id.time_Infor);

            user_infor.setText(user);
            time_infor.setText(hour + "시 " + min + "분");
            phone_infor.setText(phone_Num);
            peo_infor.setText(people_Num + "(" + error_Num + ")" + "명");


            final DialogInterface exit = dlg.setView(dlgview).show();

            Button yes = (Button) dlgview.findViewById((R.id.yes));
            Button no = (Button) dlgview.findViewById((R.id.no));
            final String userID ="test@test.com";

            //예약 확인시
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        PHPRequest request = new PHPRequest("http://127.0.0.1/test/Data_insert.php");//밑에꺼 다시
                        String result = request.PhPtest(userID,user,phone_Num,date.toString(),time,people_Num,error.toString(),store.toString());
                        if(result.equals("1")){
                            Toast.makeText(getApplication(),"저장되었습니다.",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(getApplication(),"?",Toast.LENGTH_SHORT).show();
                        }
                    }catch (MalformedURLException e){
                        e.printStackTrace();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent save = new Intent(ReserveActivity.this, MainActivity.class);
                    startActivity(save);
                    finish();

                }
            });
            // 예약 취소시
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exit.dismiss();
                }
            });
        }
    }
}