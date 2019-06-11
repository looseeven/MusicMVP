package com.tw.mvp.v;

import com.tw.mvp.Contarct;
import com.tw.mvp.Contarct.Presenter;
import com.tw.mvp.R;
import com.tw.mvp.activity.BaseActivity;
import com.tw.mvp.p.PreviewPresenter;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class PreviewActivity extends BaseActivity implements Contarct.View{
	private static final String TAG = "PreviewActivity";

	public static Uri mUri;
	private Contarct.Presenter mPresenter;
	private TextView mTitle; //歌曲
	private TextView mArtist; //专辑
	private SeekBar mSeekBar; //播放进度条
	private ProgressBar pb; //加载进度条
	private TextView mLoadingText; //提示信息
	private boolean isPlayPause = false;
	
	@Override
	public void initView() {
		Intent intent = getIntent();
		if (intent == null) {
			finish();
			return;
		}
		mUri = intent.getData();
		if (mUri == null) {
			finish();
			return;
		}
		new PreviewPresenter(this);
    	setVolumeControlStream(AudioManager.STREAM_MUSIC);
		mPresenter.onstart(PreviewActivity.this);
		setContentView(R.layout.music_preview_act);
		mTitle = (TextView) findViewById(R.id.line1);
		mArtist = (TextView) findViewById(R.id.line2);
		mSeekBar = (SeekBar) findViewById(R.id.progress);
		mSeekBar.setOnSeekBarChangeListener(mSeekListener);
		pb = (ProgressBar) findViewById(R.id.spinner);
		mLoadingText = (TextView) findViewById(R.id.loading);
	}

	@Override
	public void initData() {
		if (mUri.getScheme().equals("http")) {
			String msg = getString(R.string.streamloadingtext, mUri.getHost());
			mLoadingText.setText(msg);
		} else {
			mLoadingText.setVisibility(View.GONE);
		}
		mPresenter.setUri(mUri);
	}

	public void onClick(View v){
		mPresenter.setPlayPlause();
		if (isPlayPause) {
			((ImageButton)findViewById(R.id.playpause)).setImageResource(R.drawable.btn_playback_ic_play_small);
		} else {
			((ImageButton)findViewById(R.id.playpause)).setImageResource(R.drawable.btn_playback_ic_pause_small);
		}
	}

	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
			if (!fromuser) {
				return;
			}
			mPresenter.setSeekBar(progress);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	};
	@Override
	public void setPresenter(Presenter presenter) {
		this.mPresenter = presenter;
	}


	@Override
	public void showError() {
		Toast.makeText(this, R.string.playback_failed, Toast.LENGTH_SHORT).show();
		finish();
	}

	@Override
	public void onPrepared() {
		pb.setVisibility(View.GONE);
		View v = findViewById(R.id.titleandbuttons);
		v.setVisibility(View.VISIBLE);
		mSeekBar.setVisibility(View.VISIBLE);
	}
	@Override
	public void setID3(String title, String artist, String album) {
		mTitle.setText(title);
		mArtist.setText(artist);
	}

	@Override
	public void setSeekBar(int totaltime, int currenttime) {
		mSeekBar.setMax(totaltime);
		mSeekBar.setProgress(currenttime);
	}

	@Override
	public void showPlaypause(Boolean playpause) {
		isPlayPause = playpause;
		if (playpause) {
			((ImageButton)findViewById(R.id.playpause)).setImageResource(R.drawable.btn_playback_ic_pause_small);
		} else {
			((ImageButton)findViewById(R.id.playpause)).setImageResource(R.drawable.btn_playback_ic_play_small);
		}
	}

	@Override
	public void ondestroy() {
		mPresenter.setDestroy();
	}
}
