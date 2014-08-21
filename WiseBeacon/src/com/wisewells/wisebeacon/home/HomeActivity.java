package com.wisewells.wisebeacon.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.beacongroup.BeaconGroupActivity;
import com.wisewells.wisebeacon.common.BaseActivity;
import com.wisewells.wisebeacon.service.ServiceActivity;

public class HomeActivity extends BaseActivity {

	private View mBeaconGroupButton;
	private View mServiceButton;
	private View mManualButton;
		
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_home);
        L.enableDebugLogging(true);
        
        setTitle("WISEBEACON MANAGER");
        setDescription("*** 님 안녕하세요(시스템관리자)");
        
        mBeaconGroupButton = findViewById(R.id.layout_beacon_group_button);
        mBeaconGroupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBeaconButtonClicked();
			}
		});
        
        mServiceButton = findViewById(R.id.layout_service_button);
        mServiceButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onServiceButtonClicked();
			}
		});
        
        mManualButton = findViewById(R.id.btn_manual);
        mManualButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onSettingButtonClicked();
			}
		});
        
        findViewById(R.id.txt_header_title);
    }

	private void onSettingButtonClicked() {
		L.e(getExternalFilesDir(null).getAbsolutePath());
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
