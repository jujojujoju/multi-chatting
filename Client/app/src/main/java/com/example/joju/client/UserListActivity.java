package com.example.joju.client;


import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserListActivity extends AppCompatActivity {
    private TCPThread tcpThread = null;
    private UserListAdapter userListAdapter;
    private ListView listView;
    private EditText chatText;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        tcpThread = new TCPThread(this);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String pwd = intent.getStringExtra("pwd");

        tcpThread.setID(id);
        tcpThread.setPassword(pwd);
        tcpThread.start();

//        try {
//            tcpThread.join();
//        } catch (Exception e) {
//        }
//        tcpThread.Destroy();

        listView = (ListView) findViewById(R.id.userlist);

        userListAdapter = new UserListAdapter(getApplicationContext(), R.layout.userprofile);
        listView.setAdapter(userListAdapter);

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        listView.setAdapter(userListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(UserListActivity.this, userListAdapter.getItem(position).getId(), Toast.LENGTH_SHORT).show();
                enterRoom(userListAdapter.getItem(position).getId());

            }
        });

        //to scroll the list view to bottom on data change
        userListAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(userListAdapter.getCount() - 1);
            }
        });

    }

    private boolean enterRoom(final String id) {
        Intent intent2Chatting = new Intent(this, ChattingActivity.class);
        intent2Chatting.putExtra("id", tcpThread.getUserId());
        intent2Chatting.putExtra("targetID", id);
        intent2Chatting.putExtra("pwd", tcpThread.getPassword());
        intent2Chatting.putExtra("name", tcpThread.getUserName());
        startActivity(intent2Chatting);
        System.out.println("start Chatting Activity");
        return true;
    }

    public void printUserList(JSONArray userlist) {
        userListAdapter.add(new UserProfile("All Users"));
        try {
            JSONObject obj;
            for (int i = 0; i < userlist.length(); i++) {
                obj = userlist.getJSONObject(i);
                userListAdapter.add(new UserProfile(obj.getString("id"), obj.getString("name"), obj.getString("status")));
            }
        } catch (Exception e) {

        }
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
