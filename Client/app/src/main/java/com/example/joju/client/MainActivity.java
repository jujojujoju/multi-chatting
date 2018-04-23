package com.example.joju.client;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TCPThread tcpThread = null;

    View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.button_login) {

                EditText idText = (EditText) findViewById(R.id.IDInput);  //매칭된 레이아웃에서 해당아이디를 찾아 글자박스의 객체 생성
                String userid = idText.getText().toString();                ///스트링 객체 선언 후 글자박스의 객체의 스트링타입을 넣어줌

                EditText passwordText = (EditText) findViewById(R.id.PasswordInput);  //매칭된 레이아웃에서 해당아이디를 찾아 글자박스의 객체 생성
                String userpassword = passwordText.getText().toString();                ///스트링 객체 선언 후 글자박스의 객체의 스트링타입을 넣어줌
                tcpThread = new TCPThread(MainActivity.this);
                tcpThread.setID(userid);
                tcpThread.setPassword(userpassword);

                tcpThread.start();
                try {
                    tcpThread.join();
                } catch (Exception e) {
                }
                tcpThread.Destroy();

                if (tcpThread.isLogin()) {
//                    goChatActivity();
                    goUserListActivity();
                } else {
                    Toast.makeText(MainActivity.this, "login fail!!", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(MainActivity.this, "please signUp", Toast.LENGTH_SHORT).show();
                Intent intent1 = new Intent(MainActivity.this, SigninActivity.class);      ////인텐트 객체 하나 생성 후 메모리 할당 후 보내는액티비티와 받는 액티비티 입력
                startActivity(intent1);
            }
        }

    };

    private void goUserListActivity() {
        Intent intent2Chatting = new Intent(this, UserListActivity.class);      ////인텐트 객체 하나 생성 후 메모리 할당 후 보내는액티비티와 받는 액티비티 입력
        intent2Chatting.putExtra("id", tcpThread.getUserId());
        intent2Chatting.putExtra("pwd", tcpThread.getPassword());
        intent2Chatting.putExtra("name", tcpThread.getUserName());
        startActivity(intent2Chatting);
        System.out.println("start UserList Activity");

        Toast.makeText(this, "Hello " + tcpThread.getUserName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = (Button) findViewById(R.id.button_login);
        loginButton.setOnClickListener(listener);

        Button signupButton = (Button) findViewById(R.id.button_siginup);
        signupButton.setOnClickListener(listener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (tcpThread != null) {
            tcpThread.interrupt();
            tcpThread.Destroy();
        }
    }
}
