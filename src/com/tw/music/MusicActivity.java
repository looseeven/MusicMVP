package com.tw.music;

import java.util.Locale;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.tw.music.activity.BaseActivity;
import com.tw.music.contarct.Contarct;
import com.tw.music.contarct.Contarct.mainPresenter;
import com.tw.music.lrc.LrcView;
import com.tw.music.presenter.MusicPresenter;
import com.tw.music.utils.CircleImageView;
import com.tw.music.visualizer.BaseVisualizerView;

public class MusicActivity extends BaseActivity implements Contarct.mainView{
	private mainPresenter mPresenter;
	private boolean isPlayPause = false;
	private ListView mList;
	private CircleImageView mAlbumArt;
	private ImageView mAlbumArt2;
	private SeekBar mProgress;
	private LinearLayout ll_fx; //频谱
	public static LrcView lrc_view; //歌词
	public static int fx_height; //获取频谱界面高度

	@Override
	public void initView() {
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		setContentView(R.layout.music_act);
		lrc_view = (LrcView) findViewById(R.id.lrc_view);
		ll_fx = (LinearLayout) findViewById(R.id.ll_fx);
		mList = (ListView)findViewById(R.id.list);
		mList.setOnItemClickListener(itemClickListener);
		ll_fx.setOrientation(LinearLayout.VERTICAL);
		mAlbumArt2=(ImageView) findViewById(R.id.iv_album);
		mAlbumArt = (CircleImageView)findViewById(R.id.albumart);
		mProgress = ((SeekBar)findViewById(R.id.progress));
		mProgress.setOnSeekBarChangeListener(seekbarlistener);
		ll_fx.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				ll_fx.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				fx_height = ll_fx.getHeight();
			}
		});
		new MusicPresenter(MusicActivity.this);
		mPresenter.onstart(MusicActivity.this);
	}

	@Override
	public void initData() {
	}
	OnSeekBarChangeListener seekbarlistener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			if (fromUser) {
				mPresenter.setSeekBar(progress);
			}
		}
	};

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			mPresenter.setListitemlistener(position);
		}
	};

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
			if (isPlayPause) {
				mAlbumArt.pauseMusic();
				((ImageView)findViewById(R.id.pp)).getDrawable().setLevel(0);
				((ImageView)findViewById(R.id.pp2)).getDrawable().setLevel(0);
			} else {
				((ImageView)findViewById(R.id.pp)).getDrawable().setLevel(1);
				((ImageView)findViewById(R.id.pp2)).getDrawable().setLevel(1);
			}
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
	public void showID3(String title, String artist, String album) {
		((TextView)findViewById(R.id.song)).setText(title);
		((TextView)findViewById(R.id.artist)).setText(artist);
		((TextView)findViewById(R.id.album)).setText(album);
		((TextView) findViewById(R.id.tv_music_artis)).setText(artist);
		((TextView) findViewById(R.id.tv_music_title)).setText(title);
	}

	@Override
	public void showSeekBar(int totaltime, int currenttime) {
		mProgress.setMax(totaltime);
		mProgress.setProgress(currenttime);
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
	public int STATE_PLAYING =1;//正在播放
	public int STATE_PAUSE =2;//暂停
	public int STATE_STOP =3;//停止

	@Override
	public void setPresenter(mainPresenter presenter) {
		this.mPresenter = presenter;
	}

	int[] wallps = new int[]{R.drawable.bg,R.drawable.bg0,R.drawable.bg1,R.drawable.bg2,R.drawable.bg3,R.drawable.bg4,R.drawable.bg5,R.drawable.bg6};

	@Override
	public void showWallPaper(int position) {
		findViewById(R.id.drag_layer).setBackgroundResource(wallps[position]);
	}

	@Override
	public void showLrcorVis(Boolean b) {
		((ImageView) findViewById(R.id.iv_fx)).getDrawable().setLevel(b?0:1);
		//		((ImageView) findViewById(R.id.iv_lrc)).getDrawable().setLevel(b?1:0);
		if (b) {
			ll_fx.setVisibility(View.INVISIBLE);
			lrc_view.setVisibility(View.VISIBLE);
		}else{
			lrc_view.setVisibility(View.INVISIBLE);
			ll_fx.setVisibility(View.VISIBLE);
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

	@Override
	public void showAlbumArt(Bitmap bm) {
		if(bm == null) {
			mAlbumArt.setImageResource(R.drawable.album_l);
			mAlbumArt2.setImageResource(R.drawable.album123);
		} else {
			mAlbumArt.setImageBitmap(bm);
			mAlbumArt2.setImageBitmap(bm);
		}
	}

	@Override
	public void updateAdapterData(BaseAdapter adapter) {
		mList.setAdapter(adapter);
	}

	@Override
	public void showSmoothScrollToPosition(int position) {
		mList.smoothScrollToPosition(position);
	}

	@Override
	public void showVisualizerView(BaseVisualizerView mBaseVisualizerView) {
		ll_fx.addView(mBaseVisualizerView);
	}


	@Override
	public void ondestroy() {
		mPresenter.ondestroy();
	}

	@Override
	public void onresume() {
		mPresenter.onresume();
	}

	@Override
	public void onpause() {
		mPresenter.onpause();
	}

	@Override
	public void showPlaypause(Boolean playpause, Boolean ispause) {
		isPlayPause = playpause;
		if(playpause) {
			mAlbumArt.playMusic();
			((ImageView)findViewById(R.id.pp)).getDrawable().setLevel(1);
			((ImageView)findViewById(R.id.pp2)).getDrawable().setLevel(1);
		} else {
			if (ispause) {
				mAlbumArt.pauseMusic();
			}else{
				mAlbumArt.stopMusic();
			}
			((ImageView)findViewById(R.id.pp)).getDrawable().setLevel(0);
			((ImageView)findViewById(R.id.pp2)).getDrawable().setLevel(0);
		}

	}
}
