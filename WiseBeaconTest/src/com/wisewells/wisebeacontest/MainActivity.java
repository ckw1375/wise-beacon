package com.wisewells.wisebeacontest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Point;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.wisewells.wisebeacontest.data.BeaconGroup;
import com.wisewells.wisebeacontest.data.Content;
import com.wisewells.wisebeacontest.data.Topology;

public class MainActivity extends Activity {
	
    public static final String INTENT_PARCEL = "parcel";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
		((Button) findViewById(R.id.main_parcel_button))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						onParcelButtonClick();
					}
				});
		
		((Button) findViewById(R.id.main_service_button))
		.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onServiceButtonClick();
			}
		});
    }


    private void onServiceButtonClick() {
    	ServiceConnection conn = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.d("Service", "onServiceDisconnected");
			}
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.d("Service", "onServiceConnected");	
				
			}
		};
    	
    	bindService(new Intent(this, BeaconService.class), conn, 0);
	}


	private void onParcelButtonClick() {
    	Content content = new Content();
    	BeaconGroup group = new BeaconGroup();
    	Topology topology = new Topology();
    	
    	topology.setCode("t-000");
    	topology.setName("topology A");
    	topology.setBeaconGroup(group);
//    	topology.setContent(content);
    	
    	content.setChildren(null);
    	content.setCode("c-000");
    	content.setName("Content A");
    	content.setParent(null);
    	content.setTopology(topology);
    	
    	Intent intent = new Intent(this, ParcelActivity.class);
    	intent.putExtra(INTENT_PARCEL, content);
    	startActivity(intent);
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
