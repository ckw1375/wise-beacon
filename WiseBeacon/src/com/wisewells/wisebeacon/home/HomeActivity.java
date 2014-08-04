package com.wisewells.wisebeacon.home;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.wisewells.sdk.WiseManager;
import com.wisewells.sdk.datas.BeaconGroup;
import com.wisewells.sdk.datas.Service;
import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.beacongroup.BeaconGroupActivity;
import com.wisewells.wisebeacon.service.ServiceActivity;

public class HomeActivity extends Activity {

	private Button mBeaconGroupButton;
	private Button mServiceButton;
	private Button mSettingButton;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        L.enableDebugLogging(true);
        mBeaconGroupButton = (Button) findViewById(R.id.btn_beacon_group);
        mBeaconGroupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBeaconButtonClicked();
			}
		});
        
        mServiceButton = (Button) findViewById(R.id.btn_service_button);
        mServiceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onServiceButtonClicked();
			}
		});
        
        mSettingButton = (Button) findViewById(R.id.btn_setting);
        mSettingButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onSettingButtonClicked();
			}
		});
    }

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
	
	private void onSettingButtonClicked() {
		try {
			List<BeaconGroup> groups = WiseManager.getInstance(this).getBeaconGroupsInAuthority();
			for(BeaconGroup group : groups) {
				L.w(group.toString() + " children : " + group.getChildCodes().size());
			}
			
			List<Service> services = WiseManager.getInstance(this).getServices(null);
			for(Service service : services) {
				L.w(service.toString());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	

	private void onServiceButtonClicked() {
		Intent intent = new Intent(this, ServiceActivity.class);
    	startActivity(intent);
	}

	private void onBeaconButtonClicked() {
    	Intent intent = new Intent(this, BeaconGroupActivity.class);
    	startActivity(intent);
	}
}
