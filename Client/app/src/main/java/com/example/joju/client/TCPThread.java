package com.example.joju.client;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class TCPThread extends Thread {
    private static final String serverIP = "172.20.10.2";
    private static final int serverPort = 5555;
    private String ID = "";
    private String password = "";
    private String response = "";
    private String userName = "";
    private Boolean is_login = false;
    private Boolean is_signup = false;
    private Activity activity;
    private Socket socket_init = null;
    private String target;

    public TCPThread(Activity act) {
        setActivity(act);
    }

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

    public boolean isLogin() {
        return is_login;
    }

    public boolean isSignup() {
        return is_signup;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getTarget() {
        return target;
    }

    public void setResponse(String res) {
        response = res;
    }

    public String getUserId() {
        return this.ID;
    }

    public String getPassword() {
        return password;
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
                continue;
            }
            try {
                if (this.activity.getClass().getName().equals(MainActivity.class.getName())) {
                    login_signup(true);
                    break;
                } else if (this.activity.getClass().getName().equals(ChattingActivity.class.getName())) {
                    if (!login_signup(true)) {
                        break;
                    }
                    if (getTarget().equals("AllUser")) {
                        getStackedMessages();
                    } else {
                        getStackedPersonalMessages(getTarget());
                    }
                    runChat();
                } else if (this.activity.getClass().getName().equals(SigninActivity.class.getName())) {
                    login_signup(false);
                    break;
                } else if (this.activity.getClass().getName().equals(UserListActivity.class.getName())) {
                    getUserList();
                    break;
                }
            } catch (Exception e) {
                goMainActivity();
            }
        }
    }

    private void userUpdate() {
        while (!Thread.currentThread().isInterrupted()) {
            final JSONObject responseObj = readMessage();

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (responseObj.isNull("userlist")) {
                        System.out.println("userlist : nothing");
                    } else {
                        try {
                            printUserList(responseObj.getJSONArray("userlist"));
                        } catch (Exception e) {
                        }
                    }
                }
            });
        }
    }


    private boolean getUserList() {
        JSONObject json = new JSONObject();
        try {
            json.put("user", getUserJson());
            json.put("type", 700);
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
            System.out.println("response :  \n" + responseObj);

            if (responseObj.isNull("userlist")) {
                System.out.println("userlist : nothing");
            } else {
                printUserList(responseObj.getJSONArray("userlist"));
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    private boolean getStackedPersonalMessages(String target) {
        JSONObject json = new JSONObject();
        try {
            json.put("user", getUserJson());
            json.put("targetID", target);
            json.put("type", 600);
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
            System.out.println("response :  \n" + responseObj);

            if (responseObj.isNull("messages")) {
                System.out.println("messages : nothing");
                return false;
            } else {
                printChatArray(responseObj.getJSONArray("messages"));
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return true;

    }

    private boolean getStackedMessages() {
        JSONObject json = new JSONObject();
        try {
            json.put("user", getUserJson());
            json.put("type", 500);
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
            System.out.println("response :  \n" + responseObj);

            if (responseObj.isNull("messages")) {
                System.out.println("messages : nothing");
                return false;
            } else {
                printChatArray(responseObj.getJSONArray("messages"));
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return true;
    }

    private void connect() throws Exception {
        InetAddress serverAddr = InetAddress.getByName(serverIP);
        System.out.println("C: Connecting...");
        System.out.println("IP : " + serverIP);
        System.out.println("Port : " + serverPort);
        socket_init = new Socket(serverAddr, serverPort);
    }

    private boolean login_signup(boolean loginFlag) {

        JSONObject json = new JSONObject();
        try {
            json.put("user", getUserJson());
            if (loginFlag) {
                json.put("type", 300);
            } else {
                json.put("type", 400);
                json.getJSONObject("user").put("name", getUserName());
            }
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
            System.out.println("response :  \n" + responseObj);

            setResponse(responseObj.getString("data"));
            setUserName(responseOK() ? responseObj.getJSONObject("user").getString("name") : "");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (!responseOK()) {
                Destroy();
                return false;
            } else {
                if (loginFlag) {
                    is_login = true;
                } else {
                    is_signup = true;
                }
                return true;
            }
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

            return new JSONObject(new String(buf));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void sendMessage(String msg, String target) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", 800);
            json.put("user", getUserJson());
            json.put("targetID", target);
            json.put("msg", msg);
        } catch (Exception e) {
            return;
        }

        String data = json.toString();
        try {
            BufferedOutputStream bos = new BufferedOutputStream(socket_init.getOutputStream());
            bos.write(longToBytes(data.length()));
            bos.write(data.getBytes());
            bos.flush();
        } catch (Exception e) {
        } finally {
        }
    }

    public void sendStatus(String online) {
        JSONObject json = new JSONObject();
        try {
            json.put("user", getUserJson());
            json.put("type", 700);
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
        } catch (Exception e) {
        } finally {
        }
    }

    public void sendMessage(String msg) {
        JSONObject json = new JSONObject();
        try {
            json.put("type", 200);
            json.put("user", getUserJson());
            json.put("msg", msg);
        } catch (Exception e) {
            return;
        }

        String data = json.toString();
        try {
            BufferedOutputStream bos = new BufferedOutputStream(socket_init.getOutputStream());
            bos.write(longToBytes(data.length()));
            bos.write(data.getBytes());
            bos.flush();
        } catch (Exception e) {
        } finally {
        }
    }

    private void printUserList(final JSONArray userlist) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UserListActivity userListActivity = (UserListActivity) TCPThread.this.activity;
                userListActivity.printUserList(userlist);
            }
        });
    }

    private void runChat() {
        while (!Thread.currentThread().isInterrupted()) {
            final JSONObject msg = TCPThread.this.readMessage();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ChattingActivity chattingActivity = (ChattingActivity) TCPThread.this.activity;
                    System.out.println(msg);
                    chattingActivity.printChat(msg);
                }
            });
        }
    }

    private void printChatArray(final JSONArray msgs) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ChattingActivity chattingActivity = (ChattingActivity) TCPThread.this.activity;
                chattingActivity.printChatArray(msgs);
            }
        });
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

    private void goMainActivity() {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent2Chatting = new Intent(TCPThread.this.activity, MainActivity.class);      ////인텐트 객체 하나 생성 후 메모리 할당 후 보내는액티비티와 받는 액티비티 입력
                TCPThread.this.activity.startActivity(intent2Chatting);
                System.out.println("start Catting Activity");
                Toast.makeText(TCPThread.this.activity, "connection close...", Toast.LENGTH_SHORT).show();
            }
        });

    }

}