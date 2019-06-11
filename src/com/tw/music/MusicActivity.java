package com.tw.music;

import java.util.Locale;

import android.graphics.PixelFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tw.music.activity.BaseActivity;
import com.tw.music.contarct.Contarct;
import com.tw.music.contarct.Contarct.mainPresenter;
import com.tw.music.presenter.MusicPresenter;
import com.tw.music.utils.lrc.LrcView;

public class MusicActivity extends BaseActivity implements Contarct.mainView{
	private static final String TAG = "MusicActivity";
	private Contarct.mainPresenter mPresenter;
	private boolean isPlayPause = false;

	@Override
	public void initView() {
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.music);
		new MusicPresenter(this);
		mPresenter.onstart(MusicActivity.this);
	}

	@Override
	public void initData() {

	}

	public void onClick(View v){
		switch (v.getId()) {
		case R.id.btn_bg:
			mPresenter.setChangeWall();
			break;
		case R.id.eq:
			mPresenter.openEQ();
			break;
		case R.id.home:
			mPresenter.openHome();
			break;
		case R.id.back:
			finish();
			break;
		case R.id.pp2:
		case R.id.pp:
			mPresenter.setPlayPlause();
			break;
		case R.id.iv_lrc:
		case R.id.iv_fx:
			mPresenter.setChangeLrcorVis();
			break;
		case R.id.prev2:
		case R.id.prev:
			mPresenter.setPrev();
			break;
		case R.id.next2:
		case R.id.next:
			mPresenter.setNext();
			break;
		case R.id.repeat:
			mPresenter.setRepeat();
			break;
		case R.id.play_list:
			findViewById(R.id.ll_music_list).setVisibility(View.VISIBLE);
			findViewById(R.id.ll_music_play).setVisibility(View.GONE);
			break;
		case R.id.ll_back_play:
			findViewById(R.id.ll_music_list).setVisibility(View.GONE);
			findViewById(R.id.ll_music_play).setVisibility(View.VISIBLE);
			break;
		case R.id.iv_collect:
			mPresenter.setCollect();
			break;
		case R.id.playlist:
			mPresenter.openPlayList();
			break;
		case R.id.sd:
			mPresenter.openSDList();
			break;
		case R.id.usb:
			mPresenter.openUSBList();
			break;
		case R.id.inand:
			mPresenter.openiNandList();
			break;
		case R.id.collect:
			mPresenter.openCollectList();
			break;
		}
	}

	@Override
	public void setID3(String title, String artist, String album) {
		((TextView)findViewById(R.id.song)).setText(title);
		((TextView)findViewById(R.id.artist)).setText(artist);
		((TextView)findViewById(R.id.album)).setText(album);
		((TextView) findViewById(R.id.tv_music_artis)).setText(artist);
		((TextView) findViewById(R.id.tv_music_title)).setText(title);
	}

	@Override
	public void setSeekBar(int totaltime, int currenttime) {
		((ProgressBar)findViewById(R.id.progress)).setMax(totaltime/1000);
		((ProgressBar)findViewById(R.id.progress)).setProgress(currenttime/1000);
		totaltime = totaltime / 1000;
		int stotaltime = totaltime;
		int mtotaltime = stotaltime / 60;
		int htotaltime = mtotaltime / 60;
		stotaltime %= 60;
		mtotaltime %= 60;
		htotaltime %= 24;
		if(htotaltime == 0) {
			((TextView)findViewById(R.id.totaltime)).setText(String.format(Locale.US, "%d:%02d", mtotaltime, stotaltime));
		} else {
			((TextView)findViewById(R.id.totaltime)).setText(String.format(Locale.US, "%d:%02d:%02d", htotaltime, mtotaltime, stotaltime));
		}
		currenttime = currenttime / 1000;
		int scurrenttime = currenttime;
		int mcurrenttime = scurrenttime / 60;
		int hcurrenttime = mcurrenttime / 60;
		scurrenttime %= 60;
		mcurrenttime %= 60;
		hcurrenttime %= 24;
		if(hcurrenttime == 0) {
			((TextView)findViewById(R.id.currenttime)).setText(String.format(Locale.US, "%d:%02d", mcurrenttime, scurrenttime));
		} else {
			((TextView)findViewById(R.id.currenttime)).setText(String.format(Locale.US, "%d:%02d:%02d", hcurrenttime, mcurrenttime, scurrenttime));
		}
	}

	@Override
	public void showPlaypause(Boolean playpause) {
		isPlayPause = playpause;
		if(playpause) {
			((ImageView)findViewById(R.id.pp)).getDrawable().setLevel(1);
			((ImageView)findViewById(R.id.pp2)).getDrawable().setLevel(1);
		} else {
			((ImageView)findViewById(R.id.pp)).getDrawable().setLevel(0);
			((ImageView)findViewById(R.id.pp2)).getDrawable().setLevel(0);
		}
	}

	@Override
	public void onPointerCaptureChanged(boolean hasCapture) {

	}

	@Override
	public void setPresenter(mainPresenter presenter) {
		this.mPresenter = presenter;
	}

	int[] wallps = new int[]{R.drawable.bg,R.drawable.bg0,R.drawable.bg1,R.drawable.bg2,R.drawable.bg3,R.drawable.bg5,R.drawable.bg6}; 
	@Override
	public void setWallPaper(int position) {
		switch (position) {
		case 0:
			findViewById(R.id.drag_layer).setBackgroundResource(R.drawable.bg);
			break;
		case 1:
			findViewById(R.id.drag_layer).setBackgroundResource(R.drawable.bg0);
			break;
		case 2:
			findViewById(R.id.drag_layer).setBackgroundResource(R.drawable.bg1);
			break;
		case 3:
			findViewById(R.id.drag_layer).setBackgroundResource(R.drawable.bg2);
			break;
		case 4:
			findViewById(R.id.drag_layer).setBackgroundResource(R.drawable.bg3);
			break;
		case 5:
			findViewById(R.id.drag_layer).setBackgroundResource(R.drawable.bg4);
			break;
		case 6:
			findViewById(R.id.drag_layer).setBackgroundResource(R.drawable.bg5);
			break;
		case 7:
			findViewById(R.id.drag_layer).setBackgroundResource(R.drawable.bg6);
			break;
		}
	}
	@Override
	public void ondestroy() {
		mPresenter.setDestroy();
	}

	@Override
	public void onresume() {
		mPresenter.setResume();
	}

	@Override
	public void onpause() {
		mPresenter.setPause();
	}

	@Override
	public void showLrcorVis(Boolean b) {
		((ImageView) findViewById(R.id.iv_fx)).getDrawable().setLevel(b?0:1);
		((ImageView) findViewById(R.id.iv_lrc)).getDrawable().setLevel(b?1:0);
		if (b) {
			((LinearLayout) findViewById(R.id.ll_fx)).setVisibility(View.INVISIBLE);
			((LrcView) findViewById(R.id.lrc_view)).setVisibility(View.VISIBLE);
		}else{
			((LrcView) findViewById(R.id.lrc_view)).setVisibility(View.INVISIBLE);
			((LinearLayout) findViewById(R.id.ll_fx)).setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void showRepeat(int Repeat, int mShuffle) {
		((ImageView)findViewById(R.id.repeat)).getDrawable().setLevel(Repeat);
	}

	@Override
	public void showCollect(Boolean b) {
		((ImageView) findViewById(R.id.iv_collect)).getDrawable().setLevel(b?1:0);
	}

	@Override
	public void onBackPressed() {
		if(findViewById(R.id.ll_music_play).getVisibility() == View.VISIBLE){
			finish();
		}else{
			findViewById(R.id.ll_music_list).setVisibility(View.GONE);
			findViewById(R.id.ll_music_play).setVisibility(View.VISIBLE);
		}
	}

	private static final int[] TR_ID = new int[] {R.id.playlist, R.id.sd, R.id.usb, R.id.inand,R.id.collect};
	@Override
	public void showListDrawer(int i) {
		for (int j = 0; j <= 4; j++) {
			if (i == j) {
				((TextView)findViewById(TR_ID[j])).getBackground().setLevel(1);
				((TextView)findViewById(TR_ID[j])).getCompoundDrawables()[1].setLevel(1);
			}else{
				((TextView)findViewById(TR_ID[j])).getBackground().setLevel(0);
				((TextView)findViewById(TR_ID[j])).getCompoundDrawables()[1].setLevel(0);
			}
		}
	}
}
