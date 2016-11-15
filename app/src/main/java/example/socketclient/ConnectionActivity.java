package example.socketclient;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class ConnectionActivity extends Activity {

    //Strings to register to create intent filter for registering the recivers
    static final String ACTION_STRING_SEND_MESSAGE = "SendMessageToService";
    static final String ACTION_STRING_ACTIVITY = "ToActivity";
    public final static String EXTRA_ADDRESS = "example.socketclient.ADDRESS";
    public final static String EXTRA_PORT = "example.socketclient.PORT";

    private BroadcastReceiver activityReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Activity", "received message in activity!");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        Intent intent = getIntent();
        String address = intent.getStringExtra(LoginActivity.EXTRA_ADDRESS);
        String port = intent.getStringExtra(LoginActivity.EXTRA_PORT);

        Log.d("ConnectionActivity", address + ":" + port);

        //STEP2: register the receiver
        if (activityReceiver != null) {
            //Create an intent filter to listen to the broadcast sent with the action "ACTION_STRING_ACTIVITY"
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);
            //Map the intent filter to the receiver
            registerReceiver(activityReceiver, intentFilter);
        }

        //Start the service on launching the application
        Intent anotherIntent = new Intent(this, ConnectionService.class);
        anotherIntent.putExtra(EXTRA_ADDRESS, address);
        anotherIntent.putExtra(EXTRA_PORT, port);
        startService(anotherIntent);

    }

    //send broadcast from activity to all receivers listening to the action "ACTION_STRING_SERVICE"
    public void sendSendMessageBroadcast() {
        EditText messageEditText = (EditText) findViewById(R.id.messageEditText);
        String messageString =  messageEditText.getText().toString();
        Intent new_intent = new Intent();
        new_intent.putExtra("message", messageString);
        new_intent.setAction(ACTION_STRING_SEND_MESSAGE);
        sendBroadcast(new_intent);
    }

    public void sendMessageClicked(View view) {
        EditText messageEditText = (EditText) findViewById(R.id.messageEditText);
        messageEditText.setText("");
        sendSendMessageBroadcast();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service", "onDestroy");
        //STEP3: Unregister the receiver
        unregisterReceiver(activityReceiver);
    }
}
