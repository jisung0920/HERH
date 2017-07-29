package com.example.jisung.herh;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class User_ResActivity extends AppCompatActivity {
    ArrayList<reserver> list;
    ListView listView;
    reserverAdapter adapter;
    String store,user_id;
    String dateText;
    String nowDate;

    @Override
      protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_res);
        list = new ArrayList<reserver>();  //Menu형태의 리스트를 생성한다.
        Intent getintent = getIntent();
        store = getintent.getStringExtra("store");
        user_id = getintent.getStringExtra("id");
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy-MM-dd");
        nowDate = sdfNow.format(date);

        CalendarView calendar = (CalendarView) findViewById(R.id.calendarView3);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                View r_view = View.inflate(view.getContext(), R.layout.r_list, null);   //뷰 가져오기
                dateText = dateCovert(year + "-" + (month + 1) + "-" + dayOfMonth);
                getDbData("http://jisung0920.cafe24.com/hers.php"); //서버에서 데이터 가져오는 함수 호출
                final AlertDialog.Builder dialog = new AlertDialog.Builder(view.getContext()); //대화상자 생성
                dialog.setView(r_view); //대화상자 뷰 설정
                listView = (ListView) r_view.findViewById(R.id.list_item); //리스트 뷰 가져오기
                TextView s_date = (TextView) r_view.findViewById(R.id.textView2); //택스트 뷰 가져오기
                s_date.setText(dateText); // 텍스트 뷰 설정
                Button button = (Button) r_view.findViewById(R.id.button); // 버튼 뷰 가져오기
                if(nowDate.compareTo(dateText)>0)
                    button.setVisibility(View.INVISIBLE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { //버튼 클릭 이번트 설정
                        Intent intent = new Intent(User_ResActivity.this, ReserveActivity.class); //인텐드 생성 및 설정
                        intent.putExtra("date",dateText);
                        intent.putExtra("store",store);
                        intent.putExtra("id",user_id);
                        Log.d("test11","idchecl"+user_id);
                        startActivity(intent); // 인텐트 실행
                    }
                });
                adapter = new reserverAdapter(r_view.getContext(), list); //어뎁터 생성 및 설정
                listView.setAdapter(adapter); //리스트뷰와 어뎁터 연결
                dialog.show(); // 대화상자 보여주기

            }

        });
        TextView store_name = (TextView) findViewById(R.id.textView);
        store_name.setText(store);
    }
    private  String dateCovert(String str){
        SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d("test11D",dateFormat
                    .format(date));
        return dateFormat.format(date);
    }

    private void getDbData(String string) { // 서버의 DB에서 data 가져오는 메소드
        class GetDataJSON extends AsyncTask<String, Void, String> { // 서버 관련이라 멀티 thread를 사용하기 위해
            // AsyncTask를 사용한다.
            @Override
            protected String doInBackground(String... params) {// AsyncTask의 overide 메소드

                String uri = params[0]; //params에 php 주소가 있으므로 uri에 주소가 들어간다.
                try {
                    URL url = new URL(uri);//url 객체가 생성된다.
                    HttpURLConnection con = (HttpURLConnection) url.openConnection(); //url을 연결하기 위한 객체 con을 생성
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    String postData = "store=" + URLEncoder.encode(store) +"&date=" + URLEncoder.encode(dateText);
                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(postData.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();
                    InputStream inputStream = con.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String tmp;
                    StringBuilder stringBuilder = new StringBuilder(); //받아올 데이터들을 저장하기 위한 객체 생성

                    while ((tmp = bufferedReader.readLine()) != null) {//한줄씩 읽어온다.
                        stringBuilder.append(tmp + "\n");
                    }

                    bufferedReader.close();
                    inputStream.close();
                    con.disconnect();
                    Log.d("check11",stringBuilder.toString().trim());
                    return stringBuilder.toString().trim();//onPostExecute실행

                    //받아온 데이터를 StringBuilder 의 형태로 만든다.

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String result) {// AsyncTask의 overide 메소드
                // 위의 작업이 끝날때 실행된다
                //doinbackgroud 의 return인 sb.toString().trim()이 result로 온다.
                try {
                    JSONObject jsonObject = new JSONObject(result);//jsonObject 형태로 위에서 데이터들을 포멧한다.
                    JSONArray reserveData = jsonObject.getJSONArray("response"); //json형태에서 response라는 키를 갖는 배열을 가져온다.
                    list.clear();
                    for (int i = 0; i < reserveData.length(); i++) { //배열의 길이만큼 반복해서 더한다.
                        JSONObject object = reserveData.getJSONObject(i);
                        String name = object.getString("user_name");
                        Log.d("check11","array error1");
                        int num = object.getInt("number");
                        int error = object.getInt("error_number");
                        int allow = object.getInt("allow");
                        String tel = object.getString("user_tel");
                        String time = object.getString("time");
                        Log.d("check11","array error");
                        reserver res = new reserver(time,name,tel,num,error,allow);
                        list.add(res);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(string); //doinBackground 메소드를 실행시킨다.
    }
}
