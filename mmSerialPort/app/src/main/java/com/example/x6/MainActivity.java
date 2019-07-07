package com.example.x6;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.File;

import com.example.x6.serial.SerialPort;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private byte[] mBytes = new byte[]{0x00,0x01,0x02,0x03,0x04,0x05};
    private SerialPort serialttyS1;
    private InputStream ttyS1InputStream;
    private OutputStream ttyS1OutputStream;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    private void init_serial() {
        try {
            serialttyS1 = new SerialPort(new File("/dev/ttyS1"),115200,0);
            ttyS1InputStream = serialttyS1.getInputStream();
            ttyS1OutputStream = serialttyS1.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void read_serial() {
        try {
            ttyS1InputStream.read(mBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write_serial() {
        try {
            ttyS1OutputStream.write(mBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        final Button buttonSerial = findViewById(R.id.set_button);
        buttonSerial.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                init_serial();
                read_serial();
                write_serial();
            }
        });
    }

}
