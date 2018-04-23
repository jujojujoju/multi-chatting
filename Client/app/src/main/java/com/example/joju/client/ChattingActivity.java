package com.example.joju.client;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Objects;

public class ChattingActivity extends AppCompatActivity {
    private TCPThread tcpThread = null;
    private ChatArrayAdapter chatArrayAdapter;
    private ListView listView;
    private EditText chatText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        tcpThread = new TCPThread(this);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String pwd = intent.getStringExtra("pwd");
        String target = intent.getStringExtra("targetID");

        tcpThread.setID(id);
        tcpThread.setTarget(target);
        tcpThread.setPassword(pwd);
        tcpThread.start();

        Toast.makeText(ChattingActivity.this, "enjoy chatting !!", Toast.LENGTH_SHORT).show();

        Button buttonSend = (Button) findViewById(R.id.send);

        listView = (ListView) findViewById(R.id.msgview);

        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.right);
        listView.setAdapter(chatArrayAdapter);

        chatText = (EditText) findViewById(R.id.msg);
        chatText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return (event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER)
                        && sendChatMessage(chatText.getText().toString());
            }
        });
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!chatText.getText().toString().equals(""))
                    sendChatMessage(chatText.getText().toString());
            }
        });

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(chatArrayAdapter);

        //to scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(chatArrayAdapter.getCount() - 1);
            }
        });
    }

    public void printChat(JSONObject value) {
        try {
            System.out.println("chat message");
            if (value.getJSONObject("user").getString("id").equals(tcpThread.getUserId())) {
                chatArrayAdapter.add(new ChatMessage(true, value.getString("data"), value.getString("time")));
                chatText.setText("");
            } else {
                chatArrayAdapter.add(new ChatMessage(false, value.getString("data"), value.getString("time"), value.getJSONObject("user").getString("name")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printChatArray(JSONArray msgs) {
        try {
            JSONObject obj;
            for (int i = 0; i < msgs.length(); i++) {
                obj = msgs.getJSONObject(i);
                if (!obj.getString("senderID").equals(tcpThread.getUserId()))
                    chatArrayAdapter.add(new ChatMessage(false, obj.getString("contents"), obj.getString("sended"), obj.getString("sender")));
                else
                    chatArrayAdapter.add(new ChatMessage(true, obj.getString("contents"), obj.getString("sended")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean sendChatMessage(final String msg) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (!tcpThread.getTarget().equals("AllUser")) {
                    tcpThread.sendMessage(msg, tcpThread.getTarget());
                } else {
                    tcpThread.sendMessage(msg);
                }
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
        }
        return true;

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
