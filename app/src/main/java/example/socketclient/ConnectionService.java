package example.socketclient;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionService extends Service {

    //Strings to register to create intent filter for registering the recivers
    //private static final String ACTION_STRING_SEND_MESSAGE = "ToService";
    private static final String ACTION_STRING_ACTIVITY = "ToActivity";

    private Handler connectionHandler;
    private Socket mSock;
    private String mAddress;
    private String mPort;

    //STEP1: Create a broadcast receiver
    private BroadcastReceiver sendMessageReceiver = new BroadcastReceiver() {

        //Send message to sendMessage thread queue
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");
            Log.e("Service", "Receive broadcast from activity, message is: " + message);

            Message msg = new Message();
            Bundle b = new Bundle();
            b.putString("message", message); // for example
            msg.setData(b);

            connectionHandler.sendMessage(msg);
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAddress = intent.getStringExtra(ConnectionActivity.EXTRA_ADDRESS);
        mPort = intent.getStringExtra(ConnectionActivity.EXTRA_PORT);

        Log.d("Service", mAddress + ":" + mPort);

        openConnection();

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //STEP2: register the receiver
        if (sendMessageReceiver != null) {
            //Create an intent filter to listen to the broadcast sent with the action "ACTION_STRING_SERVICE"
            IntentFilter intentFilter = new IntentFilter(ConnectionActivity.ACTION_STRING_SEND_MESSAGE);
            //Map the intent filter to the receiver
            registerReceiver(sendMessageReceiver, intentFilter);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service", "onDestroy");
        //STEP3: Unregister the receiver
        unregisterReceiver(sendMessageReceiver);
    }

    //send broadcast from activity to all receivers listening to the action "ACTION_STRING_ACTIVITY"
    private void sendBroadcast() {
        Intent new_intent = new Intent();
        new_intent.setAction(ACTION_STRING_ACTIVITY);
        sendBroadcast(new_intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class sendMessageThread extends HandlerThread {

        sendMessageThread() {
            super("sendMessageThread");
        }

        public void prepareHandler(){
            connectionHandler = new Handler(getLooper()) {
                public void handleMessage(Message msg) {
                    Log.e("sendMessageThread", "handleMessage");

                    Bundle bundle = msg.getData();
                    String message = bundle.getString("message");
                    Log.e("sendMessageThread", "message received:" + message);

                    try {
                        PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(mSock.getOutputStream())),true);
                        out.println(message);

                    } catch(Exception e){
                        Log.e("sendMessageThread", "Exception occured during sending message");
                    }

                }
            };
        }
    }

    class connectionListener extends Thread {

        public void run() {
            try {
                while (true) {
                    InetAddress serverAddr = InetAddress.getByName(mAddress);
                    mSock = new Socket(serverAddr, Integer.parseInt(mPort));
                    char[] data = new char[4096];

                    BufferedReader reader = new BufferedReader(new InputStreamReader(mSock.getInputStream()));
                    int read = 0;
                    while ((read = reader.read(data)) >= 0){
                        //process message here
                        if(read > 0) Log.e("SocketClient", "Received packet: " + data);
                    }

                    reader.close();

                }
            }
            catch (Exception ex) {
                Log.e("SocketClient", "Exception Occured during in ConnectionListenerThread");
            }
        }

    }

    public void openConnection() {
        sendMessageThread sendMsg = new sendMessageThread();
        sendMsg.start();
        sendMsg.prepareHandler();

        connectionListener listener = new connectionListener();
        listener.start();
    }
}