package com.codehunter.minmetertestapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

public class ConsoleReceivedActivity extends AppCompatActivity {
    EditText mReception;

    SerialPort mSerialttyS1;
    InputStream mInputStream;
    ReadThread mReadThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console_received);

        mReception = findViewById(R.id.EditTextReception);

        try {
            mSerialttyS1 = new SerialPort(new File("/dev/ttyS1"), 115200, 0);
            mInputStream = mSerialttyS1.getInputStream();

            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (SecurityException e){
            DisplayError(R.string.error_security);
        } catch (IOException e){
            DisplayError(R.string.error_unknown);
        } catch (InvalidParameterException e) {
            DisplayError(R.string.error_configuration);
        }

    }

    private void DisplayError(int resourceId) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Error");
        b.setMessage(resourceId);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ConsoleReceivedActivity.this.finish();
            }
        });
    }

    private void onDataReceived(final byte[] buffer, final int size){
        runOnUiThread(new Runnable(){
            public void run(){
                if(mReception != null){
                    mReception.append(new String(buffer, 0, size));
                }
            }

        });
    }
    private class ReadThread extends Thread {
        @Override
        public void run(){
            super.run();
            while (!isInterrupted()){
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if(size > 0){
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }

    }

}
