package com.wisewells.wisebeacon.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.wisewells.sdk.services.WiseService;
import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;

public class MainActivity extends Activity {

	private Button mBeaconButton;
	private Button mServiceButton;
	private Button mTopologyButton;
	private Button mHistoryButton;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        L.enableDebugLogging(true);
        
        mBeaconButton = (Button) findViewById(R.id.main_beacon_button);
        mBeaconButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBeaconButtonClick();
			}
		});
        
        mServiceButton = (Button) findViewById(R.id.main_service_button);
        mServiceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onServiceButtonClick();
			}
		});
        
        mTopologyButton = (Button) findViewById(R.id.main_topology_button);
        mTopologyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onTopologyButtonClick();
			}
		});
    }

    private void onTopologyButtonClick() {
    	
	}

	private void onServiceButtonClick() {
    	ServiceConnection conn = new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {
				L.i(name.toString());
			}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				L.i(name.toString());
			}
		};
		
		startService(new Intent(this, WiseService.class));
    	boolean result = bindService(new Intent(this, WiseService.class), conn, 0);
	}

	private void onBeaconButtonClick() {
    	Intent intent = new Intent(this, BeaconActivity.class);
    	startActivity(intent);
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
