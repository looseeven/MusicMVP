package com.tw.music.presenter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.tw.music.MusicService;
import com.tw.music.R;
import com.tw.music.TWMusic;
import com.tw.music.bean.MusicName;
import com.tw.music.bean.Record;
import com.tw.music.contarct.Contarct;
import com.tw.music.utils.CollectionUtils;
import com.tw.music.utils.SharedPreferencesUtils;

public class MusicPresenter implements Contarct.mainPresenter{
	private static final String TAG = "MusicPresenter";
	private Contarct.mainView mainView;
	private TWMusic mTW = null;
	Context mContext; 
	int wallpoition =0;//壁纸
	private MusicService mService = null;
	
	
	public MusicPresenter(Contarct.mainView view) {
		mainView = view;
		mainView.setPresenter(this);
	}

	@Override
	public void onstart(Context mContext) {
		mTW = TWMusic.open();
		this.mContext = mContext;
		fullScreen((Activity) mContext);
		showFreqView=(SharedPreferencesUtils.getBooleanPref(mContext, "music","showFreqView"));
		wallpoition = SharedPreferencesUtils.getIntPref(mContext, "id", "id");
		mainView.setWallPaper(wallpoition);
	}

	@Override
	public void setPause() {

	}

	@Override
	public void setResume() {
	}

	@Override
	public void setSeekBar(int progress) {

	}

	@Override
	public void setPlayPlause() {
		if(mService != null) {
			if(mService.isPlaying()) {
				mService.pause();
			} else {
				mService.start();
			}
		}
	}

	@Override
	public void setDestroy() {

	}
	@SuppressLint("NewApi")
	private void fullScreen(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				//5.x开始需要把颜色设置透明，否则导航栏会呈现系统默认的浅灰色
				Window window = activity.getWindow();
				View decorView = window.getDecorView();
				//两个 flag 要结合使用，表示让应用的主体内容占用系统状态栏的空间
				int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
				decorView.setSystemUiVisibility(option);
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.setStatusBarColor(Color.TRANSPARENT);
				//导航栏颜色也可以正常设置
				//	                window.setNavigationBarColor(Color.TRANSPARENT);
			} else {
				Window window = activity.getWindow();
				WindowManager.LayoutParams attributes = window.getAttributes();
				int flagTranslucentStatus = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
				int flagTranslucentNavigation = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
				attributes.flags |= flagTranslucentStatus;
				attributes.flags |= flagTranslucentNavigation;
				window.setAttributes(attributes);
			}
		}
	}

	@Override
	public void setChangeWall() {
		if(wallpoition==6){
			wallpoition=0;
		}else{
			wallpoition+=1;
		}
		SharedPreferencesUtils.setIntPref(mContext, "id", "id", wallpoition);
		mainView.setWallPaper(wallpoition);
	}

	@Override
	public void openEQ() {
		try {
			Intent it = new Intent();
			it.setClassName("com.tw.eq", "com.tw.eq.EQActivity");
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(it);
		} catch (Exception e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

	@Override
	public void openHome() {
		try {
			Intent it = new Intent(Intent.ACTION_MAIN);
			it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			it.addCategory(Intent.CATEGORY_HOME);
			mContext.startActivity(it);
		} catch (Exception e) {
			Log.e(TAG, Log.getStackTraceString(e));
		}
	}

    private boolean showFreqView=true;
	@Override
	public void setChangeLrcorVis() {
		showFreqView=!showFreqView;
		mainView.showLrcorVis(showFreqView);
		SharedPreferencesUtils.setBooleanPref(mContext, "music","showFreqView", showFreqView);
	}

	@Override
	public void setPrev() {
		if(mService != null) {
        	mService.prev();
    	}
	}

	@Override
	public void setNext() {
		if(mService != null) {
        	mService.next();
    	}
	}

	@Override
	public void setRepeat() {
		int repeatImage = mTW.mRepeat;
		repeatImage++;
        if (repeatImage>3) {
        	repeatImage=0;
            mTW.mRepeat=1;
            mTW.mShuffle = 0;
        }else {
            if (repeatImage==1) {
                mTW.mRepeat = 1;
                mTW.mShuffle = 0;
            }else if (repeatImage==2){
                mTW.mShuffle = 0;
                mTW.mRepeat=2;
            }else {
                mTW.mShuffle = 1;
                mTW.mRepeat=3;
            }
        }
        mainView.showRepeat(mTW.mRepeat, mTW.mShuffle);
        mTW.toRPlaylist(mTW.mCurrentIndex);
	}

    private ArrayList<MusicName> likeMusic = new ArrayList<MusicName>();
	private boolean isCollectMusic = false;
	private ListView mList;
	
	@Override
	public void setCollect() {
		if(!TextUtils.isEmpty(mTW.mCurrentAPath)){ //判断路径是否为空
			if (CollectionUtils.itBeenCollected(mContext, mTW.mCurrentAPath, likeMusic)) { 
				if(((ImageView) mAdapter.ivLove) != null){
					((ImageView) mAdapter.ivLove).getDrawable().setLevel(0);
				}
				CollectionUtils.removeMusicFromCollectionList(mTW.mCurrentAPath,likeMusic);
			} else {
				if(((ImageView) mAdapter.ivLove) != null){
					((ImageView) mAdapter.ivLove).getDrawable().setLevel(1);
				}
				if(!CollectionUtils.itBeenCollected(mContext,  mTW.mCurrentAPath, likeMusic)){
					CollectionUtils.addMusicToCollectionList(new MusicName(mService.getFileName(), mTW.mCurrentAPath), likeMusic);
				}
			}
			if(isCollectMusic){
				mList.requestLayout();
			}
			mAdapter.notifyDataSetChanged();
			CollectionUtils.saveCollectionMusicList(mContext,likeMusic);
		}
	}
	private MyListAdapter mAdapter;
    private Record mSDRecord;
    private Record mUSBRecord;
    private Record mMediaRecord;
    private Record mLikeRecord = new Record("LIKE", 4, 0);
    private Record mCList;
    private class MyListAdapter extends BaseAdapter {
		public MyListAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {

			if(isCollectMusic){
				return likeMusic.size();
			}

			if(mCList == null) {
				return 0;
			} else if(mCList.mLevel == 0) {
				return mCList.mCLength;
			} else {
				return mCList.mCLength + 1;
			}
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v;
			if(convertView == null) {
				v = newView(parent);
			} else {
				v = convertView;
			}
			bindView(v, position, parent);
			return v;
		}

		@Override
		public int getViewTypeCount() {
		    return 2;
		}
	    private class ViewHolder {
	    	ImageView play_indicator;
	        ImageView icon;
	        ImageView music_icon;
	        ImageView ivLove;
	        ImageView ivItemDel;
	        ImageView ivIsPlaying;
	        TextView line;
	        TextView tvArtist;
	        TextView tvIndex;
	        ImageView file_icon;
	    }

	    public ViewHolder vh;
	    public View newView(ViewGroup parent) {
			View v = LayoutInflater.from(mContext).inflate(R.layout.music_list_item, parent, false);
             vh = new ViewHolder();
            vh.music_icon = (ImageView) v.findViewById(R.id.music_icon);
            vh.line = (TextView) v.findViewById(R.id.line);
            vh.play_indicator = (ImageView) v.findViewById(R.id.play_indicator);
            vh.tvArtist=(TextView) v.findViewById(R.id.tv_artist);
            vh.tvIndex=(TextView) v.findViewById(R.id.tv_index);
 		    vh.file_icon=(ImageView) v.findViewById(R.id.file_item);
 		    vh.ivIsPlaying=(ImageView) v.findViewById(R.id.iv_is_playing);
 		    vh.ivLove=(ImageView) v.findViewById(R.id.btn_item_love);
 		    vh.ivItemDel=(ImageView) v.findViewById(R.id.btn_item_delete);
            v.setTag(vh);
            return v;
		}

	    private void bindView(View v, final int position, ViewGroup parent) {
			final ViewHolder vh = (ViewHolder) v.getTag();
			String name, path;

			vh.music_icon.setVisibility(View.GONE);
			vh.tvArtist.setVisibility(View.GONE);
			vh.tvIndex.setVisibility(View.GONE);
			vh.ivIsPlaying.setVisibility(View.GONE);
			vh.ivLove.setVisibility(View.VISIBLE);
			vh.ivItemDel.setVisibility(View.GONE);
			if (isCollectMusic) {
				vh.line.setText(likeMusic.get(position).mName);
				vh.tvIndex.setText(String.valueOf(position+1));
				vh.tvIndex.setVisibility(View.VISIBLE);
				vh.file_icon.getDrawable().setLevel(0);
				vh.play_indicator.setVisibility(View.GONE);
				vh.music_icon.setVisibility(View.VISIBLE);

				vh.ivLove.setVisibility(View.GONE);
				vh.ivItemDel.setVisibility(View.VISIBLE);
//	            ivCollect.getDrawable().setLevel(1);
				
				if(mTW.mCurrentAPath.equals(likeMusic.get(position).mPath)){
					changeTextColor(vh,R.color.text_green);
					vh.music_icon.getDrawable().setLevel(1);
//					vh.ivItemDel.setVisibility(View.VISIBLE);
					vh.ivIsPlaying.setVisibility(View.VISIBLE);
//					((RelativeLayout)v).getBackground().setLevel(2);
				}else{
					changeTextColor(vh,R.color.text_white);
					vh.music_icon.getDrawable().setLevel(0);
					vh.ivIsPlaying.setVisibility(View.GONE);
					vh.play_indicator.setVisibility(View.GONE);
				}
				final String nameString;
				final String pathString ;
				if(mLikeRecord.mLevel == 0) {
				    nameString=mLikeRecord.mLName[position].mName;
	                pathString=mLikeRecord.mLName[position].mPath;
                } else if(position == 0) {
                    nameString = mLikeRecord.mName;
                    pathString = null;
                } else {
                    nameString = mLikeRecord.mLName[position - 1].mName;
                    pathString = mLikeRecord.mLName[position - 1].mPath;
                }
                   final boolean isEmpty=TextUtils.isEmpty(nameString);

                   vh.ivItemDel.setOnClickListener(new OnClickListener() {
                       @Override
                       public void onClick(View arg0) {
                           	 if(!isEmpty){
                                        vh.ivLove.getDrawable().setLevel(0);
                                        if (mTW.mCurrentAPath.equals(pathString)) {
                                        	mainView.showCollect(false);
										}
							CollectionUtils.removeMusicFromCollectionList(pathString,likeMusic);
							CollectionUtils.saveCollectionMusicList(mContext,likeMusic);
                                    collectList();
            						mTW.mCurrentPath = "/mnt/sdcard/iNand/.";
            						mTW.mPlaylistRecord.copyLName(mLikeRecord);
                                }

                       }
                   });
			} else {
				if(mCList.mLevel == 0) {
					name = mCList.mLName[position].mName;
					path = mCList.mLName[position].mPath;
				} else if(position == 0) {
					name = mCList.mName;
					path = null;
				} else {
					name = mCList.mLName[position - 1].mName;
					path = mCList.mLName[position - 1].mPath;
				}
				vh.line.setText(name);
				Log.i("md", "name: "+name);
				if((mCList.mLevel != 0) && (position == 0)) {
//					((RelativeLayout)v).getBackground().setLevel(1);
					vh.play_indicator.getDrawable().setLevel(2);
					vh.file_icon.getDrawable().setLevel(1);
					vh.ivIsPlaying.setVisibility(View.GONE);
					vh.ivLove.setVisibility(View.GONE);
//	                vh.ivItemDel.setVisibility(View.GONE);
				} else {
					vh.play_indicator.setVisibility(View.VISIBLE);
//					((RelativeLayout)v).getBackground().setLevel(0);
					changeTextColor(vh,R.color.text_white);
					vh.music_icon.setVisibility(View.VISIBLE);
					vh.ivLove.setVisibility(View.VISIBLE);
					vh.music_icon.getDrawable().setLevel(0);
					vh.ivIsPlaying.setVisibility(View.GONE);
					if((mCList.mLevel == 1) || (mCList.mIndex == 0)) {
						vh.play_indicator.getDrawable().setLevel(0);
						vh.file_icon.getDrawable().setLevel(0);
						vh.tvIndex.setVisibility(View.VISIBLE);
						if((path != null) && path.equals(mTW.mCurrentAPath)) {
//							((RelativeLayout)v).getBackground().setLevel(2);
							changeTextColor(vh,R.color.text_green);
							vh.ivIsPlaying.setVisibility(View.VISIBLE);
//							vh.ivLove.setVisibility(View.VISIBLE);
							vh.music_icon.getDrawable().setLevel(1);
						}
					} else {
						vh.play_indicator.getDrawable().setLevel(1);
						vh.file_icon.getDrawable().setLevel(1);
	                    vh.tvIndex.setVisibility(View.GONE);
	                    vh.music_icon.setVisibility(View.GONE);
	                    vh.ivLove.setVisibility(View.GONE);
						if((path != null) && path.equals(mTW.mCurrentPath)) {
//							((RelativeLayout)v).getBackground().setLevel(1);
							vh.ivIsPlaying.setVisibility(View.GONE);
						}

					}

					if ((mCList.mLevel !=0 && position==0)||(mCList.mIndex!=0 && position==0)) {
						vh.tvIndex.setVisibility(View.GONE);
						vh.music_icon.setVisibility(View.GONE);
						vh.ivLove.setVisibility(View.GONE);
					}

					if (mCList.mLevel!=0 && position!=0) {
						vh.tvIndex.setText(String.valueOf(position));
					}else {
						vh.tvIndex.setText(String.valueOf(position+1));
					}

					vh.ivIsPlaying.setBackgroundResource(R.anim.lev_play_now);
                    AnimationDrawable anim = (AnimationDrawable) vh.ivIsPlaying.getBackground();
                    anim.start();


					final String nameString=name;
					final String pathString=path;
					final boolean isEmpty=TextUtils.isEmpty(name);
					//                    Log.i("gss", "pathString:"+pathString);

					if (CollectionUtils.itBeenCollected(mContext,pathString,likeMusic)){
						vh.ivLove.getDrawable().setLevel(1);
					}else {
						vh.ivLove.getDrawable().setLevel(0);
					}

					vh.ivLove.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {
							try {
								if(!isEmpty){
									if (CollectionUtils.itBeenCollected(mContext,pathString,likeMusic)) {
										if (mTW.mCurrentAPath.equals(pathString)) {
                                        	mainView.showCollect(false);
										}
										CollectionUtils.removeMusicFromCollectionList(pathString,likeMusic);
									} else {
										if (mTW.mCurrentAPath.equals(pathString)) {
                                        	mainView.showCollect(true);
										}
										CollectionUtils.addMusicToCollectionList(new MusicName(nameString, pathString), likeMusic);
									}
									if(isCollectMusic){
										mList.requestLayout();
									}
									mAdapter.notifyDataSetChanged();
									CollectionUtils.saveCollectionMusicList(mContext,likeMusic);

								}
							} catch (Exception e) {
							}
                        }
                    });
				}
			}
		}

	   private void changeTextColor(ViewHolder vh,int color) {
		   vh.line.setTextColor(mContext.getResources().getColor(color));
		   vh.tvIndex.setTextColor(mContext.getResources().getColor(color));
		   vh.tvArtist.setTextColor(mContext.getResources().getColor(color));

	   }

        private Context mContext;
        public Object ivLove;
    }
    
    private void collectList() {
		// TODO Auto-generated method stub
		MusicName[] mLName = new MusicName[likeMusic.size()];
		for(int i = 0; i < likeMusic.size();i++){
			mLName[i] = new MusicName(likeMusic.get(i).mName,likeMusic.get(i).mPath);
		}
		mLikeRecord.mLName = mLName;
		mLikeRecord.mLength = likeMusic.size();
		mCList = mLikeRecord;
		mCList.mIndex = 4;
		mAdapter.notifyDataSetChanged();
    }

	@Override
	public void openPlayList() {
		mainView.showListDrawer(0);
	}

	@Override
	public void openSDList() {
		mainView.showListDrawer(1);
		
	}
	@Override
	public void openUSBList() {
		mainView.showListDrawer(2);
		
	}
	@Override
	public void openiNandList() {
		mainView.showListDrawer(3);
		
	}

	@Override
	public void openCollectList() {
		mainView.showListDrawer(4);
		
	}

	
}
