package com.example.joju.client;


import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class UserListActivity extends AppCompatActivity {
    private TCPThread tcpThread = null;
    private UserListAdapter userListAdapter;
    private ListView listView;
    private String id;
    private String pwd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);



        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        pwd = intent.getStringExtra("pwd");


        listView = (ListView) findViewById(R.id.userlist);
//        listView = (ListView) findViewById(R.id.ListView);

        userListAdapter = new UserListAdapter(getApplicationContext(), R.layout.userprofile);
        listView.setAdapter(userListAdapter);

        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
//        listView.setAdapter(userListAdapter);
//        setListViewHeightBasedOnChildren(listView);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(UserListActivity.this, userListAdapter.getItem(position).getId(), Toast.LENGTH_SHORT).show();
//                enterRoom(userListAdapter.getItem(position).getId());
                getUserList();
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

        getUserList();
    }

    private void getUserList() {
        // list clear
        userListAdapter.clear();
        userListAdapter = new UserListAdapter(getApplicationContext(), R.layout.userprofile);
        listView.setAdapter(userListAdapter);

        TCPThread tcpThread = new TCPThread(this);

        tcpThread.setID(id);
        tcpThread.setPassword(pwd);
        tcpThread.start();
        try {
            tcpThread.join();
        } catch (Exception e) {}
        tcpThread.Destroy();
    }

    private boolean updateStatus() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                tcpThread.sendStatus("online");
            }
        });
        t.start();
        try {
            t.join();
        } catch (Exception e) {
        }
        return true;

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
//            updateStatus();
//            tcpThread.sendStatus("offline");
            tcpThread.interrupt();
            tcpThread.Destroy();
        }
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.AT_MOST);

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


}
