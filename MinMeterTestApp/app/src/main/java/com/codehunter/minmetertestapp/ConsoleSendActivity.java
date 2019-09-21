package com.codehunter.minmetertestapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidParameterException;

import android.view.KeyEvent;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ConsoleSendActivity extends AppCompatActivity {
    EditText mSend;

    SerialPort mSerialttyS1;
    OutputStream mOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console_send);

        try {
            mSerialttyS1 = new SerialPort(new File("/dev/ttyS1"), 115200, 0);
            mOutputStream = mSerialttyS1.getOutputStream();
			
			mSend = findViewById(R.id.EditTextSend);
			
			mSend.setOnEditorActionListener(new OnEditorActionListener() {
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					int i;
					CharSequence t = v.getText();
					char[] text = new char[t.length()];
					for (i=0; i<t.length(); i++) {
						text[i] = t.charAt(i);
					}
					try{
					    mOutputStream.write(new String(text).getBytes());
                        mOutputStream.write('\n');
                    } catch ( IOException e) {
					    e.printStackTrace();
                    }
                    return false;
				}				
			});

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
                ConsoleSendActivity.this.finish();
            }
        });
    }
}
