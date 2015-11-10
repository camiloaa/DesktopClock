package com.better.deskclock;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by caar on 2015-11-09.
 */
public class LightControlActivity extends Activity {
    private static final String TAG = "LightFragment";
    /**
     * Local Bluetooth adapter
     */
    private BluetoothAdapter mBluetoothAdapter = null;

    /**
     * Member object for the chat services
     */
    private BluetoothSerialPortService mSPPService = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            //FragmentActivity activity = Activity.this.getActivity
            //Toast.makeText(activity, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            //activity.finish();
        }

        updateLayout();
    }

    private void updateLayout() {
        setContentView(R.layout.light_control);

        final Button alarmButton = (Button) findViewById(R.id.alarm_button);
        alarmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(LightControlActivity.this, AlarmClock.class));
            }
        });


    }
}
