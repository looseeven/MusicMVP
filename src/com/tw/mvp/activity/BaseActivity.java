package com.tw.mvp.activity;

import android.app.Activity;
import android.os.Bundle;

public abstract class BaseActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
	}

	public abstract void initView() ;
	public abstract void initData() ;
	public abstract void ondestroy() ;
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ondestroy();
	}
}
