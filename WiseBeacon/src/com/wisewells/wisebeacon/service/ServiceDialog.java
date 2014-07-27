package com.wisewells.wisebeacon.service;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.wisewells.wisebeacon.R;

public class ServiceDialog extends DialogFragment {

	private EditText mEditText;
	private Button mConfirmButton;
	private Button mCancelButton;
	
	private ConfirmListener mConfirmListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_with_1_edittext_2_buttons, container, false);
		mEditText = (EditText) v.findViewById(R.id.edit_1);
		
		mConfirmButton = (Button) v.findViewById(R.id.btn_confirm);		
		mConfirmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mConfirmListener.onConfirmButtonClicked(mEditText.getText().toString());
				dismiss();
			}
		});
		
		mCancelButton = (Button) v.findViewById(R.id.btn_cancel);
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});		
		return v;
	}
	
	public void setConfirmListener(ConfirmListener listener) {
		mConfirmListener = listener;
	}
	
	public interface ConfirmListener {
		void onConfirmButtonClicked(String str);
	}
}
