package com.wisewells.sdk.aidl;

interface EditObjectListener {
	oneway void onEditSuccess(String result, inout Bundle data);
	oneway void onEditFail(String result);
}
