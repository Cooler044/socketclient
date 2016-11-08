package example.socketclient;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ConnectionService extends Service {

    String acc_email;
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        ConnectionService getService() {
            return ConnectionService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        startService();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() { }

    // Init variables and open socket
    private void startService() {

        acc_email = "test@test.com";

        try {
            openConnection();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // this method opens connection
    public void openConnection() throws InterruptedException
    {
        try {
            WatchData data = new WatchData();
            data.email = acc_email;
            data.ctx = this;

            //new thread for socket connection
            new WatchSocket().execute(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //our data class
    class WatchData
    {
        String email;
        Context ctx;
    }

    class SocketData
    {
        Socket sock;
        Context ctx;
    }

    class WatchSocket extends AsyncTask<WatchData , Integer, Integer>
    {
        Context mCtx;
        Socket mySock;

        protected void onProgressUpdate(Integer... progress)
        { }

        protected void onPostExecute(Integer result)
        {

        }
        protected Integer doInBackground(WatchData... param)
        {
            InetAddress serverAddr;

            mCtx = param[0].ctx;
            String email = param[0].email;

            try {
                while(true)
                {
                    serverAddr = InetAddress.getByName("192.168.0.10");
                    mySock = new Socket(serverAddr, 4505);

                    SocketData data = new SocketData();
                    data.ctx = mCtx;
                    data.sock = mySock;

                    GetPacket pack = new GetPacket();
                    AsyncTask<SocketData, Integer, Integer> running = pack.execute(data);

                    String message = email;

                    try {
                        PrintWriter out = new PrintWriter( new BufferedWriter( new OutputStreamWriter(mySock.getOutputStream())),true);

                        out.println(message);

                    } catch(Exception e){

                    }

                    while(running.getStatus().equals(AsyncTask.Status.RUNNING))
                    {

                    }

                    //if asynctasc finished, connection is broken. Close the socket and restart it in while(true)
                    try
                    {
                        mySock.close();
                    }
                    catch(Exception e)
                    {}
                }
            } catch (Exception e) {
                return -1;
            }
        }
    }

    class GetPacket extends AsyncTask<SocketData, Integer, Integer>
    {
        Context mCtx;
        char[] mData;
        Socket mySock;

        protected void onProgressUpdate(Integer... progress)
        {
            try
            {
                String prop = String.valueOf(mData);
                //process message as you like
            }
            catch(Exception e)
            {
                Log.e("Error", "Socket error: " + e.getMessage());
            }
        }

        protected void onPostExecute(Integer result)
        {
// Это выполнится после завершения работы потока
        }

        protected Integer doInBackground(SocketData... param)
        {
            mySock = param[0].sock;
            mCtx = param[0].ctx;
            mData = new char[4096];

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(mySock.getInputStream()));
                int read = 0;

                while ((read = reader.read(mData)) >= 0 && !isCancelled())
                {
                    //call onprogressupdate
                    if(read > 0) publishProgress(read);
                }
                reader.close();
            } catch (IOException e) {
                return -1;
            }
            catch (Exception e) {
                return -1;
            }
            return 0;
        }
    }
}