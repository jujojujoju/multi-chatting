package com.example.joju.client;

import android.app.Activity;
import android.content.Intent;
import android.os.*;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Objects;

public class TCPThread extends Thread implements Parcelable {

    private static final String serverIP = "172.20.10.2";
    private static final int serverPort = 5556;
    private String ID = "";
    private String password = "";
    private String response = "";
    private String userName = "";
    private transient Activity activity;
    private Socket socket_init = null;

    public TCPThread(Activity act) {
        setActivity(act);
    }

    protected TCPThread(Parcel in) {
        ID = in.readString();
        password = in.readString();
        response = in.readString();
        userName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ID);
        dest.writeString(password);
        dest.writeString(response);
        dest.writeString(userName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TCPThread> CREATOR = new Creator<TCPThread>() {
        @Override
        public TCPThread createFromParcel(Parcel in) {
            return new TCPThread(in);
        }

        @Override
        public TCPThread[] newArray(int size) {
            return new TCPThread[size];
        }
    };

    public void setActivity(Activity act) {
        this.activity = act;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean responseOK() {
        return response.equals("ok");
    }

    public void setResponse(String res) {
        response = res;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    private JSONObject getUserJson() {
        JSONObject user = new JSONObject();
        try {
            user.put("id", ID);
            user.put("pwd", password);
//            user.put("name", userName);
            return user;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void run() {
//        if thread is not interrupted connect to server
        while (!Thread.currentThread().isInterrupted()) {
            try {
                connect();
                System.out.println("connection success");
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            if (this.activity.getClass().getName().equals(MainActivity.class.getName())) {
                if (!login()) continue;

                goChatActivity();

                runChat();
            } else if (this.activity.getClass().getName().equals(SigninActivity.class.getName())) {
                signIn();
            }

        }

    }

    private void signIn() {

    }

    private void connect() throws Exception {
        InetAddress serverAddr = InetAddress.getByName(serverIP);
        System.out.println("C: Connecting...");
        System.out.println("IP : " + serverIP);
        System.out.println("Port : " + serverPort);
        socket_init = new Socket(serverAddr, serverPort);
    }

    private void loginFail() {
        (this.activity).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(TCPThread.this.activity, "login fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goChatActivity() {
        this.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Intent intent2Chatting = new Intent(TCPThread.this.activity, ChattingActivity.class);      ////인텐트 객체 하나 생성 후 메모리 할당 후 보내는액티비티와 받는 액티비티 입력
                intent2Chatting.putExtra("tcpThread", (Parcelable) TCPThread.this);
                TCPThread.this.activity.startActivity(intent2Chatting);
                System.out.println("start Catting Activity");

                Toast.makeText(TCPThread.this.activity, "Hello " + getUserName(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean login() {
        JSONObject json = new JSONObject();
        try {
            json.put("type", 300);
            json.put("user", getUserJson());
        } catch (Exception e) {
            return false;
        }
        String data = json.toString();
        System.out.println(data);

        try {
            BufferedOutputStream bos = new BufferedOutputStream(socket_init.getOutputStream());
            bos.write(longToBytes(data.length()));
            bos.write(data.getBytes());
            bos.flush();

            JSONObject responseObj = readMessage();
            setResponse(responseObj.getString("data"));
            setUserName(responseOK() ? responseObj.getJSONObject("user").getString("name") : "");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (!responseOK()) {
                Destroy();
                loginFail();
                return false;
            } else {
                return true;
            }
        }
    }

    private void runChat() {
        while (!Thread.currentThread().isInterrupted()) {
            JSONObject msg = readMessage();

        }
    }

    public JSONObject readMessage() {
        try {
            DataInputStream dis = new DataInputStream(socket_init.getInputStream());
            long size = dis.readLong();

            byte[] buf = new byte[(int) size];
            int total_size = 0;
            int read_size = 0;

            while (total_size < size) {
                read_size = dis.read(buf, read_size, (int) size - read_size);
                System.out.println("read size : " + read_size);
                total_size += read_size;
            }
            System.out.println("total size : " + total_size);
            System.out.println(new String(buf));

            return new JSONObject(new String(buf));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendMessage(String msg) {
        if (socket_init == null) return;

        JSONObject json = new JSONObject();
        try {
            json.put("type", 200);
            json.put("user", getUserJson());
            json.put("msg", msg);
        } catch (Exception e) {
            return;
        }
        String data = json.toString();
        System.out.println(data);

        try {
            BufferedOutputStream bos = new BufferedOutputStream(socket_init.getOutputStream());
            bos.write(longToBytes(data.length()));
            bos.write(data.getBytes());
            bos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
    }

    private byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Byte.SIZE);
        buffer.putLong(x);
        return buffer.array();
    }

    public long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Byte.SIZE);
        buffer.put(bytes);
        buffer.flip();
        return buffer.getLong();
    }

    public void Destroy() {
        try {
            if (socket_init != null) {
                socket_init.close();
                socket_init = null;
            }
        } catch (IOException e) {
        }
    }

}