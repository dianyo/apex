package dianyo.apex;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Created by dianyo on 2017/5/19.
 */

public class ManageConnectThread {
    private BluetoothSocket mSocket;
//    private PrintStream printStream;
//    private InputStreamReader inputStreamReader;

    public ManageConnectThread(BluetoothSocket socket) {
        mSocket = socket;
    }

    public void sendData(String data)  {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream(4);
            output.write(data.getBytes());
            OutputStream outputStream = mSocket.getOutputStream();
            outputStream.write(output.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("device", "send the data");
    }

    public String receiveData() {
        byte[] buffer = new byte[4];
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(buffer);
            InputStream inputStream = mSocket.getInputStream();
            inputStream.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer.toString();
    }

}
