package com.wisewells.wisebeacon.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.beacongroup.GroupActivity;
import com.wisewells.wisebeacon.service.ServiceActivity;

public class HomeActivity extends Activity {

	private Button mBeaconButton;
	private Button mServiceButton;
	private Button mTopologyButton;
	private Button mHistoryButton;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
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
        
        mHistoryButton = (Button) findViewById(R.id.main_history_button);
        mHistoryButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onHistoryButtonClick();
			}
		});
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	
	private void onHistoryButtonClick() {
	}
	
    private void onTopologyButtonClick() {
	}

	private void onServiceButtonClick() {
		Intent intent = new Intent(this, ServiceActivity.class);
    	startActivity(intent);
	}

	private void onBeaconButtonClick() {
    	Intent intent = new Intent(this, GroupActivity.class);
    	startActivity(intent);
	}
}
