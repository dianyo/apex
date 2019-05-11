package dianyo.apex;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.v4.app.DialogFragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.honorato.multistatetogglebutton.MultiStateToggleButton;
import org.honorato.multistatetogglebutton.ToggleButton;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class Setting extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private ImageButton backButton;
    private ImageButton setNoticeButton;
    private ImageButton settingPowerButton;
    private TextView settingPowerText;
    private MultiStateToggleButton negPressureMultiButton, pumpingFreqMultiButton;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private ManageConnectThread manageConnectThread;
    private Boolean blueTooth;
    private static String address ="98:D3:32:30:86:78";
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                get device's name for debugging
                Log.d("device", device.getName());
                if (device.getName().equals(getString(R.string.deviceBluetoothName))) {
                    mBluetoothAdapter.cancelDiscovery();
                    Log.d("device", "find the device, Cancel Discovery");
                    startBluetooth();
                }
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sharedPreferences = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE);
        processView();
        settingBluetooth();
        pressButton();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
            startBluetooth();
        }
    }

    private void settingBluetooth(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d("device", "This device does not support Bluetooth");
            blueTooth = false;
            return ;
        }
        if (!mBluetoothAdapter.isEnabled()){
            blueTooth = true;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            blueTooth = true;
            startBluetooth();
        }
    }
    private void startBluetooth(){
        //see if the desired device is already known
        boolean paired = false;
        Set<BluetoothDevice> pairedDevice = mBluetoothAdapter.getBondedDevices();
        if (pairedDevice.size() > 0) {
            Log.d("device", "there are " + pairedDevice.size() + " devices paired");
            for (BluetoothDevice device : pairedDevice) {
                if (device.getName().equals(getString(R.string.deviceBluetoothName))) {
                    Log.d("device", "address = " + device.getAddress());
                    paired = true;
                }
            }
        }


        // not paired -> find new device and connect it
        if (!paired) {
            Log.d("device", "no paired device,  Start Discovery");
            mBluetoothAdapter.startDiscovery();
        } else {
            Log.d("device", "Find paired bluetooth device");

            BluetoothDevice mDevice = mBluetoothAdapter.getRemoteDevice(address);
            ConnectThread mConnectThread = new ConnectThread(mDevice);
            mConnectThread.start();
//            AcceptThread mAcceptThread = new AcceptThread();
//            mAcceptThread.start();
        }

    }
    private void processView() {
        backButton = (ImageButton) findViewById(R.id.backButton);
        setNoticeButton = (ImageButton) findViewById(R.id.settingNoticeButton);
        settingPowerButton = (ImageButton) findViewById(R.id.settingPowerButton);
        settingPowerText = (TextView) findViewById(R.id.settingPowerText);
        negPressureMultiButton = (MultiStateToggleButton) findViewById(R.id.settingNegPressureMultiButton);
        pumpingFreqMultiButton = (MultiStateToggleButton) findViewById(R.id.settingPumpingFreqMultiButton);
        settingPowerText.setText(sharedPreferences.getString("Power", "Off"));
        setButtonBySharedPreference();
    }
    private void pressButton(){
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setNoticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment setNoticeDialog = CustomDialogFragment.newInstance("noticeSetting");
                setNoticeDialog.show(getSupportFragmentManager(), "dialog");
            }
        });

        settingPowerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPowerText = sharedPreferences.getString("Power", "Off");
                newPowerText = (newPowerText.equals("Off")) ? "On" : "Off";
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Power", newPowerText);
                editor.apply();
            }
        });

        negPressureMultiButton.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("Negative Pressure", value);
                editor.apply();
                if (blueTooth) {
                    if(manageConnectThread == null)
                        Log.d("device", "null thread");
                    manageConnectThread.sendData(getBluetoothDataFromSharedPreference());
                }
            }
        });

        pumpingFreqMultiButton.setOnValueChangedListener(new ToggleButton.OnValueChangedListener() {
            @Override
            public void onValueChanged(int value) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("Pumping Frequency", value);
                editor.apply();
                if (blueTooth)
                    manageConnectThread.sendData(getBluetoothDataFromSharedPreference());
            }
        });

    }
    private String getBluetoothDataFromSharedPreference(){
        String negPressureValue = String.valueOf(sharedPreferences.getInt("Negative Pressure", 0));
        String pumpingFreqValue = String.valueOf(sharedPreferences.getInt("Pumping Frequency", 0));
        Log.d("data to send", negPressureValue + pumpingFreqValue);
        return  negPressureValue + pumpingFreqValue;
    }
    private void setButtonBySharedPreference() {
        negPressureMultiButton.setValue(sharedPreferences.getInt("Negative Pressure", 0));
        pumpingFreqMultiButton.setValue(sharedPreferences.getInt("Pumping Frequency", 0));
    }
    private void setSharedPreferenceFromData(String data){
        int negPressureValue = data.charAt(0);
        int pumpingFreqValue = data.charAt(1);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("Negative Pressure", negPressureValue);
        editor.putInt("Pumping Frequency", pumpingFreqValue);
        editor.apply();
        setButtonBySharedPreference();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mReceiver);
    }
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(getString(R.string.deviceUUID)));
            } catch (IOException e) {
                Log.e("device", "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            Log.d("deivce", "start run the connect thread");
            // Cancel discovery because it otherwise slows down the connection.
            mBluetoothAdapter.cancelDiscovery();
            Log.d("device", "Connecting...");
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.d("device", "socket connected!");

            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                Log.e("device", "Unable to connect!");
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e("device", "Could not close the client socket", closeException);
                }
                return;
            }
            Log.d("device", "Socket..." + mmSocket.toString());

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageConnectThread = new ManageConnectThread(mmSocket);
            Log.d("device", "manage connect thread successfully open");
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e("device", "Could not close the client socket", e);
            }
        }
    }
    private class AcceptThread extends  Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
                        getString(R.string.deviceBluetoothName), UUID.fromString(getString(R.string.deviceUUID)));
            } catch (IOException e){
                Log.e("device", "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run(){
            BluetoothSocket socket = null;
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e("device", "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    manageConnectThread = new ManageConnectThread(socket);
                    setSharedPreferenceFromData(manageConnectThread.receiveData());
                    try { mmServerSocket.close(); } catch (IOException e) {
                        Log.e("device", "Could not close the connect socket", e); }
                    break;
                }
            }
        }
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e("device", "Could not close the connect socket", e);
            }
        }

    }
}
