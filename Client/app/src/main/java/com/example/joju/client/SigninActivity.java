package com.example.joju.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SigninActivity extends AppCompatActivity {
    TCPThread tcpThread = new TCPThread(this);
    View.OnClickListener listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            EditText idText = (EditText) findViewById(R.id.IDInput);  //매칭된 레이아웃에서 해당아이디를 찾아 글자박스의 객체 생성
            String userid = idText.getText().toString();                ///스트링 객체 선언 후 글자박스의 객체의 스트링타입을 넣어줌

            EditText nameText = (EditText) findViewById(R.id.NameInput);
            String username = nameText.getText().toString();

            EditText passwordText = (EditText) findViewById(R.id.PasswordInput);
            String userpassword = passwordText.getText().toString();

            EditText passwordCheckText = (EditText) findViewById(R.id.PasswordInput2);
            String passwordcheck = passwordCheckText.getText().toString();

            if (userpassword.equals(passwordcheck))
            {
                tcpThread.setPassword(userpassword);
                tcpThread.setID(userid);
                tcpThread.setUserName(username);

                Intent intent2Chatting = new Intent(SigninActivity.this, ChattingActivity.class);      ////인텐트 객체 하나 생성 후 메모리 할당 후 보내는액티비티와 받는 액티비티 입력

                System.out.println("signed id" + userid);
                System.out.println(userpassword);

                tcpThread.start();
                try {
                    tcpThread.join();
                } catch (Exception ignored) {
                }

                if (tcpThread.responseOK()) {
                    intent2Chatting.putExtra("id", userid);
                    intent2Chatting.putExtra("password", userpassword);
                    intent2Chatting.putExtra("name", username);
                    startActivity(intent2Chatting);
                    Toast.makeText(SigninActivity.this, "signup and login success", Toast.LENGTH_SHORT).show();
                }else
                {
                    Toast.makeText(SigninActivity.this, "id already exist", Toast.LENGTH_SHORT).show();
                }

            }
            else
                Toast.makeText(SigninActivity.this, "password not same", Toast.LENGTH_SHORT).show();

        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        Button signupButton = (Button) findViewById(R.id.enter);
        signupButton.setOnClickListener(listener);
    }

}
