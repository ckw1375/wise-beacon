package com.wisewells.wisebeacon.common;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TitleDialogSpinnerAdapter<T> extends ArrayAdapter<T> {

	private AddItemListener mListener;
	
	public TitleDialogSpinnerAdapter(Context context) {
		super(context, android.R.layout.simple_spinner_dropdown_item);
	}
	
	public TitleDialogSpinnerAdapter(Context context, T[] items) {
		super(context, android.R.layout.simple_spinner_dropdown_item, items);
	}
	
	@Override
	public void add(T object) {
		super.add(object);
		mListener.onItemAdded(getPosition(object));
	}
	
	public void setAddItemListener(AddItemListener listener) {
		mListener = listener;
	}
	
	public interface AddItemListener {
		void onItemAdded(int position);
	}
}
