package example.socketclient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginActivity extends Activity {

    public final static String EXTRA_ADDRESS = "example.socketclient.ADDRESS";
    public final static String EXTRA_PORT = "example.socketclient.PORT";
    public static final String PREFS_NAME = "dataStorage";


    private EditText ipEditText;
    private EditText portEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ipEditText = (EditText) findViewById(R.id.IPfield);
        portEditText = (EditText) findViewById(R.id.portField);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedAddress = settings.getString("address", "");
        String savedPort = settings.getString("port", "");

        ipEditText.setText(savedAddress);
        portEditText.setText(savedPort);
    }

    public void ClearFields(View v) {
        ipEditText.setText("");
        portEditText.setText("");
    }

    public void Connect(View v) {
        String address = ipEditText.getText().toString();
        String port = portEditText.getText().toString();

        //save addr and port to a sharedprefs object.
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("address", address);
        editor.putString("port", port);
        editor.commit();

        //launch connectionactivity
        Intent intent = new Intent(this, ConnectionActivity.class);
        intent.putExtra(EXTRA_ADDRESS, address);
        intent.putExtra(EXTRA_PORT, port);
        startActivity(intent);
    }

    //https://blog.nikitaog.me/2014/10/11/android-looper-handler-handlerthread-i/
    //https://codereview.stackexchange.com/questions/22778/socket-handling-in-a-thread
}