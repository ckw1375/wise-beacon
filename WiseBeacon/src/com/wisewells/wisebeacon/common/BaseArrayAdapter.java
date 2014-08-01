package com.wisewells.wisebeacon.common;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class BaseArrayAdapter<T> extends BaseAdapter {

	protected Context mContext;
	protected ArrayList<T> mItems;

	public BaseArrayAdapter(Context context) {
		mContext = context;
		mItems = new ArrayList<T>();
	}
	
	public BaseArrayAdapter(Context context, Collection<T> items) {
		mContext = context;
		mItems = (ArrayList<T>) items;	
	}

	public void add(T item) {
		mItems.add(item);
		notifyDataSetChanged();
	}
	
	public void addAll(Collection<T> collection) {
		mItems.addAll(collection);
	}

	public void remove(int index) {
		mItems.remove(index);
		notifyDataSetChanged();
	}

	public void clear() {
		mItems.clear();
		notifyDataSetChanged();
	}
	
	public void replaceWith(Collection<T> items) {
		mItems.clear();
		mItems.addAll(items);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	@Override
	public T getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public Context getContext() {
		return mContext;
	}

	public ArrayList<T> getItems() {
		return mItems;
	}

	public void setItems(Collection<T> items) {		
		mItems = (ArrayList<T>) items;
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent);	
}