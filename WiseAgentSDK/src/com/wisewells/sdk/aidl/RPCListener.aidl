package com.wisewells.sdk.aidl;

interface RPCListener {
	oneway void onSuccess(inout Bundle data);
	oneway void onFail(String err);
}
