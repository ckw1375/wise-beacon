package com.wisewells.wisebeacon.common;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.view.HeaderPopupMenu;

public class BaseActivity extends Activity {
	
	private TextView mHeaderTitle;
	private TextView mDescription;
	private ImageView mMenuButton;
	private ImageView mHomeButton;
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		mHeaderTitle = (TextView) findViewById(R.id.txt_header_title);
		mDescription = (TextView) findViewById(R.id.txt_description);
		
		mMenuButton = (ImageView) findViewById(R.id.img_popup_menu);
		mMenuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				HeaderPopupMenu menu = new HeaderPopupMenu(BaseActivity.this);
				menu.showAsDropDown(v);
			}
		});
		
		mHomeButton = (ImageView) findViewById(R.id.img_home);
		mHomeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
	}
	
	public void setTitle(String title) {
		mHeaderTitle.setText(title);
	}
	
	public void setDescription(String desc) {
		mDescription.setText(desc);
	}
	
	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(new CalligraphyContextWrapper(newBase));
	}
}
