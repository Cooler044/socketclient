package example.socketclient;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity {

    private EditText ip;
    private EditText port;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ip = (EditText) findViewById(R.id.IPfield);
        port = (EditText) findViewById(R.id.portField);

        final Button button = (Button) findViewById(R.id.clearButton);
        button.setOnClickListener(clearButtonOnClickListener);
    }

    View.OnClickListener clearButtonOnClickListener =
            new View.OnClickListener() {
                public void onClick(View v) {

                ip.setText("");
                port.setText("");
                }
            };

    //https://blog.nikitaog.me/2014/10/11/android-looper-handler-handlerthread-i/
    //https://codereview.stackexchange.com/questions/22778/socket-handling-in-a-thread
}