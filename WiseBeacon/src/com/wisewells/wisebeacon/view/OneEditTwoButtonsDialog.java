package com.wisewells.wisebeacon.view;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wisewells.wisebeacon.R;

public class OneEditTwoButtonsDialog extends DialogFragment {

	private TextView mTitleView;
	private TextView mEditTitleView;
	private EditText mEditText;
	private ImageButton mOkButton;;
	private ImageButton mCancelButton;
	
	private DialogListener mConfirmListener;
	
	private String mPrompt;
	private String mEditTitle;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, 0);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.dialog_with_1_edittext_2_buttons, container, false);
		
		mTitleView = (TextView) v.findViewById(R.id.txt_dialog_title);
		mTitleView.setText(mPrompt);
		
		mEditTitleView = (TextView) v.findViewById(R.id.txt_edit_1_title);
		mEditTitleView.setText(mEditTitle);
		
		mEditText = (EditText) v.findViewById(R.id.edit_1);
		
		mOkButton = (ImageButton) v.findViewById(R.id.img_ok);		
		mOkButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mConfirmListener.onOkButtonClicked(mEditText.getText().toString());
				dismiss();
			}
		});
		
		mCancelButton = (ImageButton) v.findViewById(R.id.img_cancel);
		mCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});		
		
		return v;
	}
	
	public void setPrompt(String prompt) {
		mPrompt = prompt;
	}
	
	public void setEditTitle(String title) {
		mEditTitle = title;
	}
	
	public void setDialogListener(DialogListener listener) {
		mConfirmListener = listener;
	}
	
	public interface DialogListener {
		void onOkButtonClicked(String str);
	}
	
	/*
	 * Builder 패턴 공부해보자.. 
	 * 나중에 시간나면 생각해 보쟈 : )
	 */
	static public class Builder {
		public OneEditTwoButtonsDialog create(String prompt, String editTitle, DialogListener listener) {
			OneEditTwoButtonsDialog dialog = new OneEditTwoButtonsDialog();
			dialog.setPrompt(prompt);
			dialog.setEditTitle(editTitle);
			dialog.setDialogListener(listener);
			return dialog;
		}
	}
}
