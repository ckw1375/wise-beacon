package com.wisewells.wisebeacon.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.wisewells.wisebeacon.R;

public class HeaderPopupMenu extends PopupWindow {
	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	
	public interface OnListPopupItemClickListener {
		public void onItemClick(int position);
	}
	
	private OnListPopupItemClickListener mListener;
	
	public void setOnListPopupItemClickListener(OnListPopupItemClickListener listener) {
		mListener = listener;
	}
	
	private void onListPopupItemClick(int position){
		if(null != mListener){
			mListener.onItemClick(position);
		}
		
	}
	
	public HeaderPopupMenu(Context context) {
		super(context);
		View contentView = LayoutInflater.from(context).inflate(R.layout.view_popup_menu, null, false);
		setContentView(contentView);
		setWindowLayoutMode(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		
		mAdapter = new ArrayAdapter<String>(context, R.layout.list_row_popup_menu, 
				context.getResources().getStringArray(R.array.popup_menu));
		
		mListView = (ListView) contentView.findViewById(R.id.list_popup_menu);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				onListPopupItemClick(position);
				dismiss();
			}
		});
		
		setOutsideTouchable(true);
		setFocusable(true);
		setTouchInterceptor(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
					dismiss();
					return true;
				}
				return false;
			}
		});
	}
}
