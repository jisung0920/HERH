package com.example.jisung.herh;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.Scanner;

public class MymenuActivity extends AppCompatActivity {
    TextView userInfo;
    ListView listView, r_listView;
    String userId;
    ArrayList<reserver_al> r_list;
    reserver_user_Adapter r_adapter;
    String store,name,time;
    int allow;
    int flag=0;
    String dateText;
    int posit;
    Display display;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mymenu);
        if(!NetworkCheck.connect(this)) {
            Toast.makeText(this, "인터넷 연결 상태를 확인해 주세요", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        userInfo = (TextView)findViewById(R.id.userInfo);
        r_list = new ArrayList<reserver_al>();
        r_adapter = new reserver_user_Adapter(this,r_list);
        r_listView = (ListView) findViewById(R.id.recordList);
        r_listView.setAdapter(r_adapter);
        Intent getIntent = getIntent();
        userId = getIntent.getStringExtra("id");
        getDbResData("http://jisung0920.cafe24.com/hers_user_res.php");

        userInfo.setText(userId);



        r_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                posit = position;
                final View re_view = View.inflate(view.getContext(), R.layout.r_list_item_3,null);
                final Dialog dialog = new Dialog(view.getContext()); //대화상자 생성
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(re_view);


                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();

                params.width = (int) (display.getWidth() * 0.8);
                params.height = (int) (display.getHeight() * 0.6);
                dialog.getWindow().setAttributes(params);


                TextView resName = (TextView) re_view.findViewById(R.id.res_name);
                TextView resTel = (TextView) re_view.findViewById(R.id.res_tel);
                TextView resTime = (TextView) re_view.findViewById(R.id.res_time);
                TextView resNum = (TextView) re_view.findViewById(R.id.res_num);
                resName.setText("예약명 : " + r_list.get(position).getName());
                resTel.setText("전화번호 : " + r_list.get(position).getTel());
                resTime.setText("시간 : " + r_list.get(position).getTime());
                resNum.setText("인원 : " + r_list.get(position).getNum());

                Button refuse = (Button) re_view.findViewById(R.id.res_ref);
                refuse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        time = r_list.get(position).getTime();
                        name = r_list.get(position).getName();
                        allow = r_list.get(position).getAllow();
                        if(flag == 1){
                            r_list.get(position).setAllow(2);
                        }
                        r_adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    private void getDbResData(String string) { // 서버의 DB에서 data 가져오는 메소드
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
                    String postData = "id=" + URLEncoder.encode(userId);
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

                    return stringBuilder.toString().trim();//onPostExecute실행

                    //받아온 데이터를 StringBuilder 의 형태로 만든다.

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String result) {
                try {
                    Log.d("test11",result);
                    JSONObject jsonObject = new JSONObject(result);//jsonObject 형태로 위에서 데이터들을 포멧한다.
                    JSONArray reserveData = jsonObject.getJSONArray("response"); //json형태에서 response라는 키를 갖는 배열을 가져온다.
                    for (int i = 0; i < reserveData.length(); i++) { //배열의 길이만큼 반복해서 더한다.
                        JSONObject object = reserveData.getJSONObject(i);
                        String name = object.getString("store_name");
                        Log.d("check11","array error1");
                        int num = object.getInt("number");
                        int error = object.getInt("error_number");
                        int allow = object.getInt("allow");
                        String tel = object.getString("user_tel");
                        String time = object.getString("time");
                        String date = object.getString("date");
                        Log.d("check11","array error");
                        reserver_al res = new reserver_al(time,name,tel,num,error,allow,date);
                        r_list.add(res);
                    }
                    r_adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(string); //doinBackground 메소드를 실행시킨다.
    }

    String loginResult(InputStream in) {
        String data = "";
        Scanner s = new Scanner(in);
        data += s.nextLine();
        s.close();
        return data;}

}