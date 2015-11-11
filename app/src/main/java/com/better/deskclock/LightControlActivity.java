/**
 * Created by caar on 2015-11-09.
 */

package com.better.deskclock;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LightControlActivity extends Activity {
    private class LightRegisters {
        public int stepDirection = 0;
        public int stepCounter = -1;
        public int output = -1;
        public int waitTime = -1;
    }
    // Register related constants
    final int MAX_REGISTER_ADDRESS = 32;
    final int STEP_REGISTER = 0;
    final int LIGHT_REGISTER = 1;
    final int STEP_COUNTER_REGISTER = 2;
    final int WAIT_COUNTER_REGISTER = 4;
    final int WARM_LIGHT_ADDRESS = 0;
    final int COLD_LIGHT_ADDRESS = 16;

    private boolean lightOn = false;

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
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            this.finish();
        }

        updateLayout();
    }

    public void cycleLight(View v) {
        if (!lightOn) {
            LightRegisters onRegisters = new LightRegisters();
            onRegisters.stepDirection = 1;
            onRegisters.output = 0;
            onRegisters.stepCounter = 127;
            onRegisters.waitTime = 1;
            updateLight(WARM_LIGHT_ADDRESS, onRegisters);
            updateLight(COLD_LIGHT_ADDRESS, onRegisters);
            Toast.makeText(this, "Light is on", Toast.LENGTH_SHORT).show();
            lightOn = true;
        } else {
            LightRegisters offRegisters = new LightRegisters();
            offRegisters.stepDirection = 255;
            offRegisters.output = 127;
            offRegisters.stepCounter = 127;
            offRegisters.waitTime = 1;
            updateLight(WARM_LIGHT_ADDRESS, offRegisters);
            updateLight(COLD_LIGHT_ADDRESS, offRegisters);
            Toast.makeText(this, "Light is off", Toast.LENGTH_SHORT).show();
            lightOn = false;
        }
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

    /* Light registers must be updated in the given order.
    * */
    private void updateLight(int lightNumber, LightRegisters registers) {
        if (registers.waitTime >= 0) {
            int w_low = registers.waitTime % 256;
            int w_hi = registers.waitTime / 256;
            writeRegister(lightNumber + WAIT_COUNTER_REGISTER, w_low);
            writeRegister(lightNumber + WAIT_COUNTER_REGISTER + 1, w_hi);
        }
        writeRegister(lightNumber + STEP_COUNTER_REGISTER, registers.stepCounter);
        writeRegister(lightNumber + LIGHT_REGISTER, registers.output);
        writeRegister(lightNumber + STEP_REGISTER, registers.stepDirection);
    }

    private int writeRegister(int regNumber, int regValue) {
        String btMessage;
        if (regValue == -1)
            return -1;
        if (regNumber > MAX_REGISTER_ADDRESS || regNumber < 0)
            return -1;
        byte val = (byte) regValue;
        byte reg = (byte) regNumber;
        btMessage = String.format("W%02x%02x",reg,val);
        return 0;
    }

}
