package com.example.jisung.herh;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;
    int RC_SIGN_IN = 1000;
    String TAG = "TAG";

    Button userBtn; //사용자 선택 버튼
    Button hostBtn; //사업자 선택 버튼
    Button loginBtn; // 로그인 버튼
    String codeS;
    EditText code; // 사업자 코드번호
    LinearLayout userScreen; // 사용자 화면
    LinearLayout hostScreen; // 사업자 화면
    Intent intent;
    String id, token;
    SharedPreferences tmp;
    SharedPreferences.Editor editor;
    SignInButton signInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());


        findViewById(R.id.sign_in_button).setOnClickListener(this);


    }

    public void onClick(View v) {
        if (!NetworkCheck.connect(this)) {
            Toast.makeText(this, "인터넷 연결 상태를 확인해 주세요", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return ;
        }
        if (v.getId() == R.id.sign_in_button)
            signIn();
        else if (v.getId() == R.id.chose_user) {
            userBtn.setBackgroundResource(R.color.loginBtnOn);
            hostBtn.setBackgroundResource(R.color.loginButton);
            userScreen.setVisibility(View.VISIBLE);
            hostScreen.setVisibility(View.INVISIBLE);
        }//화면 변경
        else if (v.getId() == R.id.chose_host) {
            hostBtn.setBackgroundResource(R.color.loginBtnOn);
            userBtn.setBackgroundResource(R.color.loginButton);
            userScreen.setVisibility(View.INVISIBLE);
            hostScreen.setVisibility(View.VISIBLE);
            if (!codeS.equals("")) {
//                gethostData("http://jisung0920.cafe24.com/hers_host_info.php");
            }

        }//화면 변경
        else if (v.getId() == R.id.login) {
            codeS = code.getText().toString();
            id = codeS;
            gethostData("http://jisung0920.cafe24.com/hers_host_info.php");
            tokenRegit("http://jisung0920.cafe24.com/hers_push_resigster.php");

        }


    }


    void init() {//초기화
        tmp = getSharedPreferences("autoLogin", MODE_PRIVATE);
        editor = tmp.edit();
        userBtn = (Button) findViewById(R.id.chose_user);
        hostBtn = (Button) findViewById(R.id.chose_host);
        loginBtn = (Button) findViewById(R.id.login);
        code = (EditText) findViewById(R.id.code);
        userScreen = (LinearLayout) findViewById(R.id.screen_user);
        hostScreen = (LinearLayout) findViewById(R.id.screen_host);
        code.setText(tmp.getString("code", ""));
        codeS = code.getText().toString();
        token = FirebaseInstanceId.getInstance().getToken();

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {

        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d("test11", acct.getEmail());
            Toast.makeText(this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();

            id = acct.getEmail();
            tokenRegit("http://jisung0920.cafe24.com/hers_push_resigster.php");
            Log.d("test11", id + "/" + token);
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("id", id);

            startActivity(intent);
            finish();

        } else {
            Toast.makeText(this, "인터넷 연결상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getApplicationContext(), "" + connectionResult, Toast.LENGTH_SHORT).show();
    }

    private void tokenRegit(String string) { // 서버의 DB에서 data 가져오는 메소드
        class pushData extends AsyncTask<String, Void, String> { // 서버 관련이라 멀티 thread를 사용하기 위해
            // AsyncTask를 사용한다.
            @Override
            protected String doInBackground(String... params) {// AsyncTask의 overide 메소드

                String uri = params[0]; //params에 php 주소가 있으므로 uri에 주소가 들어간다.
                try {
                    URL url = new URL(uri);//url 객체가 생성된다.
                    HttpURLConnection con = (HttpURLConnection) url.openConnection(); //url을 연결하기 위한 객체 con을 생성
                    con.setRequestMethod("POST");
                    con.setDoOutput(true);
                    Log.d("test11", "2" + id + "/" + token);
                    String postData = "user_id=" + URLEncoder.encode(id) + "&Token=" + URLEncoder.encode(token);
                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(postData.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();

                    InputStream inputStream = con.getInputStream();
                    String result = loginResult(inputStream);
                    Log.d("test11", result);

                    inputStream.close();
                    con.disconnect();
                    return result;//onPostExecute실행
                    //받아온 데이터를 StringBuilder 의 형태로 만든다.

                } catch (Exception e) {
                    e.printStackTrace();

                }
                return null;
            }

            protected void onPostExecute(String result) {// AsyncTask의 overide 메소드

                Log.d("test11+", "." + result + ".");

            }
        }
        pushData g = new pushData();
        g.execute(string); //doinBackground 메소드를 실행시킨다.
    }

    private void gethostData(String string) { // 서버의 DB에서 data 가져오는 메소드
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
                    String postData = "code=" + URLEncoder.encode(codeS);
                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(postData.getBytes("UTF-8"));
                    outputStream.flush();
                    outputStream.close();
                    String result = "";
                    InputStream inputStream;
                    if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        inputStream = con.getInputStream();
                        result = loginResult(inputStream);
                    } else {
                        inputStream = con.getErrorStream();
                        cancel(true);
                        Toast.makeText(LoginActivity.this, "코드를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    }

                    Log.d("test11", result);

                    inputStream.close();
                    con.disconnect();
                    return result;//onPostExecute실행

                    //받아온 데이터를 StringBuilder 의 형태로 만든다.

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "코드를 확인해주세요.", Toast.LENGTH_SHORT).show();

                }
                return null;
            }

            protected void onPostExecute(String result) {// AsyncTask의 overide 메소드
                // 위의 작업이 끝날때 실행된다
                //doinbackgroud 의 return인 sb.toString().trim()이 result로 온다.

                try {
                    if(result.equals("FAL"))
                        Toast.makeText(LoginActivity.this, "코드를 확인해주세요.", Toast.LENGTH_SHORT).show();
                    else {
                        editor.putString("code", code.getText().toString());
                        editor.commit();
                        intent = new Intent(LoginActivity.this, ResCheckActivity.class);
                        intent.putExtra("store", result);//tmp
                        startActivity(intent);
//                    finish();
                    }
                } catch (Exception e) {
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
        return data;
    }
}
