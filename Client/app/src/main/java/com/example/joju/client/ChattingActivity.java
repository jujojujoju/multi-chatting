package com.example.joju.client;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;


public class ChattingActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        System.out.println("chatting activity");
        Toast.makeText(ChattingActivity.this, "enjoy chatting !!", Toast.LENGTH_SHORT).show();

        Intent intent = getIntent();
        TCPThread tcpThread = intent.getParcelableExtra("tcpThread");
        System.out.println(tcpThread.getUserName());

    }
}