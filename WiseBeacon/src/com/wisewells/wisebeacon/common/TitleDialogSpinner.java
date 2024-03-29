package com.wisewells.wisebeacon.common;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.wisewells.sdk.utils.L;
import com.wisewells.wisebeacon.R;
import com.wisewells.wisebeacon.common.TitleDialogSpinnerAdapter.AddItemListener;

public class TitleDialogSpinner extends TextView implements View.OnClickListener{
	
	private TitleDialogSpinnerListDialog mDialog;
	private FragmentManager mManager;
	private OnSpinnerItemSelectedListener mListener;
	private TitleDialogSpinnerAdapter<?> mAdapter;
	private String mPrompt;
	
	public TitleDialogSpinner(Context context) {
		this(context, null, 0);
	}
	
	public TitleDialogSpinner(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}
	
	public TitleDialogSpinner(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOnClickListener(this);
		mDialog = new TitleDialogSpinnerListDialog();
	}

	@Override
	public void onClick(View v) {
		if(mManager == null)
			throw new RuntimeException("Must set FragmentManager used by setFragmentManager.");
		mDialog.show(mManager, "Dialog");
	}
	
	public void setPrompt(String prompt) {
		mPrompt = prompt;
	}
	
	public void setFragmentManager(FragmentManager manager) {
		mManager = manager;
	}
	
	public void setAdapter(TitleDialogSpinnerAdapter<?> adapter) {
		mAdapter = adapter;
		mAdapter.setAddItemListener(new AddItemListener() {
			@Override
			public void onItemAdded(int position) {
				mDialog.onItemClicked(null, null, position, position);
			}
		});
	}
	
	public void setOnItemSelectedListener(OnSpinnerItemSelectedListener listener) {
		mListener = listener;
	}
	
	public interface OnSpinnerItemSelectedListener {
		void onItemSelected(AdapterView<?> parent, View view, int position, long id);
	}
	
	private class TitleDialogSpinnerListDialog extends DialogFragment {
		private TextView mTitleView;
		private ListView mListView;
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setStyle(STYLE_NO_TITLE, 0);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.dialog_with_listview_used_by_spinner, container, false);
		
			mTitleView = (TextView) v.findViewById(R.id.txt_dialog_title);
			mTitleView.setText(mPrompt);
			
			mListView = (ListView) v.findViewById(R.id.list_dialog);
			mListView.setAdapter(mAdapter);
			mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					onItemClicked(parent, view, position, id);
				}
			});
			
			return v;
		}
		
		public void onItemClicked(AdapterView<?> parent, View view, int position, long id) {
			Object item = mAdapter.getItem(position);			
			if(item instanceof CharSequence) setText((CharSequence) item);
			else setText(item.toString());
			
			if(mListener != null) {
				mListener.onItemSelected(parent, view, position, id);
			}
			if(mDialog.isVisible())
				dismiss();
		}
	}
}
