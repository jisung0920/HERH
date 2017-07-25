package com.example.jisung.herh;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import java.util.Scanner;

public class regitActivity extends AppCompatActivity {

    EditText email,pw,pwC;
    String id,pw1,pw2;
    Boolean comp=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regit);
        init();


    }
    void init(){
        email =(EditText)findViewById(R.id.email);
        pw=(EditText)findViewById(R.id.password);
        pwC = (EditText)findViewById(R.id.pwcheck);
    }

    void onClick(View v){
        id = email.getText().toString();
        pw1 = pw.getText().toString();
        pw2 = pwC.getText().toString();
        if(id.equals("")|| pw1.equals("")||pw2.equals(""))
            Toast.makeText(this, "양식을 입력해 주세요", Toast.LENGTH_SHORT).show();
        else if(!pw1.equals(pw2))
            Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
        else{
            RegitDB("http://jisung0920.cafe24.com/hers_regist.php");


        }
    }
    private void RegitDB(String string) { // 서버의 DB에서 data 가져오는 메소드
        class GetDataJSON extends AsyncTask<String, Void, String> { // 서버 관련이라 멀티 thread를 사용하기 위해
            // AsyncTask를 사용한다.
            @Override
            protected String doInBackground(String... params) {// AsyncTask의 overide 메소드
                String uri = params[0]; //params에 php 주소가 있으므로 uri에 주소가 들어간다.
                try {
                    Log.d("check11","1");
                    URL url = new URL(uri);//url 객체가 생성된다.
                    HttpURLConnection con = (HttpURLConnection) url.openConnection(); //url을 연결하기 위한 객체 con을 생성
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    String postData = "id=" + URLEncoder.encode(id) +"&pw=" + URLEncoder.encode(pw1);
                    Log.d("check11","1-1"+postData);
                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(postData.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();
                    Log.d("check11","2");
                    InputStream inputStream;
                    if (con.getResponseCode() == HttpURLConnection.HTTP_OK)
                        inputStream =con.getInputStream();
                    else inputStream = con.getErrorStream();
                    String result = loginResult(inputStream);
                    inputStream.close();
                    con.disconnect();
                    Log.d("check11","3"+result);
                    return result;
                    //받아온 데이터를 StringBuilder 의 형태로 만든다.

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            protected void onPostExecute(String result) {// AsyncTask의 overide 메소드
                // 위의 작업이 끝날때 실행된다
                //doinbackgroud 의 return인 sb.toString().trim()이 result로 온다.
                Log.d("check11","4"+result+"여기까지");
                try {
                    if(result.equals("SUC")) {
                        comp = true;
                        Log.d("check11","t"+result);
                    }
                    else
                        comp = false;
                    Log.d("check11","5"+comp);

                } catch (Exception e) {
                    e.printStackTrace();

                }

                if(comp){
                    Toast.makeText(regitActivity.this, "회원가입되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(regitActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                    Toast.makeText(regitActivity.this, "이미 가입된 이메일주소 입니다.", Toast.LENGTH_SHORT).show();
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
        Log.d("check11","1-3"+data);
        return data;

    }

}
