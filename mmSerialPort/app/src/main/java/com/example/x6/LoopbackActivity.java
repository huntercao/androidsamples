package com.example.x6;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.TextView;
import com.example.x6.serial.SerialPort;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

public class LoopbackActivity extends AppCompatActivity {

    byte mValueToSend;
    boolean mByteReceivedBack;
    Object mByteReceivedBackSemaphore = new Object();
    Integer mIncoming = new Integer(0);
    Integer mOutgoing = new Integer(0);
    Integer mLost = new Integer(0);
    Integer mCorrupted = new Integer(0);

    SerialPort mSerialttyS1;
    OutputStream mOutputStream;
    InputStream mInputStream;
    ReadThread mReadThread;
    SendingThread mSendingThread;


    TextView mTextViewOutgoing;
    TextView mTextViewIncoming;
    TextView mTextViewLost;
    TextView mTextViewCorrupted;

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                int size;
                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        onDataReceived(buffer, size);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private class SendingThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                synchronized (mByteReceivedBackSemaphore) {
                    mByteReceivedBack = false;
                    try {
                        if (mOutputStream != null) {
                            mOutputStream.write(mValueToSend);
                        } else {
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    mOutgoing++;
                    try {
                        mByteReceivedBackSemaphore.wait(100);
                        if (mByteReceivedBack == true) {
                            mIncoming++;
                        } else {
                            mLost++;
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                mTextViewOutgoing.setText(mOutgoing.toString());
                                mTextViewLost.setText(mLost.toString());
                                mTextViewIncoming.setText(mIncoming.toString());
                                mTextViewCorrupted.setText(mCorrupted.toString());
                            }
                        });
                    } catch (InterruptedException e) {

                    }
                }
            }
        }

    }

    private void onDataReceived(byte[] buffer, int size) {
        synchronized (mByteReceivedBackSemaphore) {
            int i;
            for (i = 0; i < size; i++) {
                if ((buffer[i] == mValueToSend) && (mByteReceivedBack == false)) {
                    mValueToSend++;
                    mByteReceivedBack = true;
                    mByteReceivedBackSemaphore.notify();
                } else {
                    mCorrupted++;
                }
            }
        }
    }

    private void DisplayError(int resourceId) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Error");
        b.setMessage(resourceId);
        b.setPositiveButton("OK", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                LoopbackActivity.this.finish();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loopback);

        mTextViewOutgoing = (TextView) findViewById(R.id.TextViewOutgoingValue);
        mTextViewIncoming = (TextView) findViewById(R.id.TextViewIncomingValue);
        mTextViewLost = (TextView) findViewById(R.id.textViewLostValue);
        mTextViewCorrupted = (TextView) findViewById(R.id.textViewCorruptedValue);

        try {
            mSerialttyS1 = new SerialPort(new File("/dev/ttyS1"), 115200, 0);
            mOutputStream = mSerialttyS1.getOutputStream();
            mInputStream = mSerialttyS1.getInputStream();

            mReadThread = new ReadThread();
            mReadThread.start();

            mSendingThread = new SendingThread();
            mSendingThread.start();
        } catch (SecurityException e) {
            DisplayError(R.string.error_security);
        } catch (IOException e) {
            DisplayError(R.string.error_unknown);
            //e.printStackTrace();
        } catch (InvalidParameterException e) {
            DisplayError(R.string.error_configuration);
        }
    }
}
