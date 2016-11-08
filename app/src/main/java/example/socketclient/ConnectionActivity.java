package example.socketclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ConnectionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        Intent intent = getIntent();
        String address = intent.getStringExtra(LoginActivity.EXTRA_ADDRESS);
        String port = intent.getStringExtra(LoginActivity.EXTRA_PORT);

    }
}
