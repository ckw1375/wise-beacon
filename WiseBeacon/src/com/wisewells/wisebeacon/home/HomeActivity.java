package com.wisewells.wisebeacon.home;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wisewells.agent.WiseAgent;
import com.wisewells.agent.WiseServer;
import com.wisewells.sdk.WiseObjects;
import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.Dummy;
import com.wisewells.wisebeacon.R;

public class HomeActivity extends Activity {

	private Button mBeaconButton;
	private Button mServiceButton;
	private Button mTopologyButton;
	private Button mHistoryButton;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
        
        WiseObjects.getInstance().putBeaconGroup(Dummy.getUUidGroup());
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	
	private void onHistoryButtonClick() {
		Toast.makeText(this, "minor : " + WiseServer.requestMinor(), Toast.LENGTH_SHORT).show();
	}
	
    private void onTopologyButtonClick() {
    	Toast.makeText(this, "code : " + WiseServer.requestCode(HomeActivity.class), Toast.LENGTH_SHORT).show();
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
		
		startService(new Intent(this, WiseAgent.class));
    	boolean result = bindService(new Intent(this, WiseAgent.class), conn, 0);
	}

	private void onBeaconButtonClick() {
    	Intent intent = new Intent(this, GroupActivity.class);
    	startActivity(intent);
	}
}
