package com.eleven.lib.ecsegmentedcontrol;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.eleven.lib.library.ECSegmentedControl;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    private ECSegmentedControl mSegmentedControl;
    private ECSegmentedControl mSegmentedControl2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSegmentedControl = (ECSegmentedControl) findViewById(R.id.segmentedControl);
        mSegmentedControl2 = (ECSegmentedControl) findViewById(R.id.segmentedControl2);
        mSegmentedControl2.setSelectedIndex(2);
        mSegmentedControl.setECSegmentedControlListener(new ECSegmentedControl.ECSegmentedControlListener() {
            @Override
            public void onSelectIndex(int index) {
                Log.d(TAG, "select index " + index);
            }
        });

    }
}
