package com.codehunter.mmbottomnavigation;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;

import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            String strPage = " Page";
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(getString(R.string.title_home) + strPage);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(getString(R.string.title_dashboard) + strPage);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(getString(R.string.title_notifications) + strPage);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
