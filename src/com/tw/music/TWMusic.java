package com.tw.music;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.tw.john.TWUtil;
import android.util.Log;

import com.tw.music.bean.MusicName;
import com.tw.music.bean.Record;
import com.tw.music.utils.SharedPreferencesUtils;

public class TWMusic extends TWUtil {
	private static TWMusic mTW = new TWMusic();
	private static int mCount = 0;
	public static TWMusic open() {
		if(mCount++ == 0) {
	        if(mTW.open(new short[] {(short)0x0202,(short)0x0304,(short)0x9e03, (short)0x9e1f}) != 0) {
	        	mCount--;
	        	return null;
	        }
	        mTW.start();
	        mTW.resume();
	        mTW.setIndex(mTW.mCurrentPath);
	        mTW.mPlaylistRecord = new Record("Playlist", 0, 0);
	        mTW.loadFile(mTW.mPlaylistRecord, mTW.mCurrentPath);
	        mTW.toRPlaylist(mTW.mCurrentIndex);
	        mTW.initRecord();
		}
		return mTW;
	}

	public void close() {
		if(mCount > 0) {
			if(--mCount == 0) {
				stop();
				super.close();
			}
		}
	}

	public static final int REQUEST_SOURCE = 0x9e11;
	public static final int REQUEST_MEDIA = 0x0502;
	public static final int REQUEST_SERVICE = 0x9e00;
	public static final int RETURN_MUSIC = 0x9e03;
	public static final int RETURN_MOUNT = 0x9e1f;

	public void requestSource(int source) {
		write(REQUEST_SOURCE, (1<<7) | (1<<6), source);
	}

	public void requestSource(boolean is) {
		requestSource(is ? 0x03 : 0x83);
	}

	public static final int ACTIVITY_RUSEME = 0x03;
	public static final int ACTIVITY_PAUSE = 0x83;
    public int mSDRecordLevel = 0; //为SD的序列号1 2 3
    public int mUSBRecordLevel = 0; //为USB的序列号1 2 3

	private int mService = 0;
	public void requestService(int activity) {
		mService = activity;
		write(REQUEST_SERVICE, activity);
	}

	public int getService() {
		return mService;
	}

	public void media(int type, int cindex, int tindex, int ctime, int percent) {
		write(REQUEST_MEDIA, (tindex<<16) | (cindex & 0xffff), (type<<31) | ((percent & 0x7f)<<24) | (ctime & 0xffffff));
	}

	public Record mPlaylistRecord;
    public int[] mRPlaylist;
	public int mCurrentRIndex;

    public String mCurrentAPath;
	public String mCurrentPath;
	public String mCurrentLrcViewPath;
	public int mCurrentIndex;
	public int mCurrentPos;
	public int mShuffle;
	public int mRepeat = 1;

	public String mCurrentArtist;
	public String mCurrentAlbum;
	public String mCurrentSong;
	public Bitmap mAlbumArt;

	public Record mSDRecord;
	public Record mUSBRecord;
	public Record mMediaRecord;
	public Record mCList;
	public Record mLikeRecord = new Record("LIKE", 4, 0);//用于存放收藏列表的目录
	public ArrayList<MusicName> likeMusic = new ArrayList<MusicName>(); //收藏歌曲的列表 存有名字+路径
	public ArrayList<Record> mSDRecordArrayList = new ArrayList<Record>(); //用于存放所有SD有关音乐的目录 SD1 2 3
	public ArrayList<Record> mUSBRecordArrayList = new ArrayList<Record>();//用于存放所有USB有关音乐的目录 USB1 2 3
	private void resume() {
        try {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader("/data/tw/music"));
				mCurrentAPath = br.readLine();
				mCurrentIndex = Integer.valueOf(br.readLine());
				mCurrentPos = Integer.valueOf(br.readLine());
				mShuffle = Integer.valueOf(br.readLine());
				mRepeat = Integer.valueOf(br.readLine());
			} catch (Exception e) {
			} finally {
				if(br != null) {
					br.close();
					br = null;
				}
			}
	        if(mRepeat < 1) {
	            mRepeat = 1;
	        }
	        if(mCurrentAPath != null) {
	            mCurrentPath = mCurrentAPath.substring(0, mCurrentAPath.lastIndexOf("/"));
	        }
		} catch (Exception e) {
		}
    }

    public void loadFile(Record record, String path) {
    	if ((record != null) && (path != null)) {
    		File[] file = new File(path).listFiles(new FileFilter() {
				@Override
				public boolean accept(File f) {
					String n = f.getName().toUpperCase(Locale.ENGLISH);
					if (f.isFile() && !n.startsWith(".") &&
							(n.endsWith(".MP3") ||
//									n.endsWith(".WMA") ||
									n.endsWith(".AAC") ||
									n.endsWith(".OGG") ||
									n.endsWith(".PCM") ||
									n.endsWith(".M4A") ||
//									n.endsWith(".AC3") ||
									n.endsWith(".EC3") ||
									n.endsWith(".DTSHD") ||
									n.endsWith(".MKA") ||
//									n.endsWith(".RA") ||
									n.endsWith(".WAV") ||
									n.endsWith(".CD") ||
									n.endsWith(".AMR") ||
									n.endsWith(".MP2") ||
									n.endsWith(".APE") ||
									n.endsWith(".DTS") ||
									n.endsWith(".FLAC") ||
									n.endsWith(".MIDI") ||
									n.endsWith(".MID") ||
									n.endsWith(".MPC") ||
									n.endsWith(".TTA") ||
									n.endsWith(".ASX") ||
									n.endsWith(".AIFF") ||
									n.endsWith(".AU"))) {
						return true;
					}
					return false;
				}
			});
    		record.mCLength = 0;
    		if(file != null) {
        		record.setLength(file.length);
        		for(File f : file) {
        			String n = f.getName();
        			record.add(n.substring(0, n.lastIndexOf(".")), f.getAbsolutePath());
        		}
    		}
    	}
    }
	public Record loadCollectFile(Context c,Record record) {
		record = mPlaylistRecord;
		record.mLName = null;
		record.mCLength = 0;
		int i = 0;
		for (MusicName f : mPlaylistRecord.mLName) {
			if(SharedPreferencesUtils.getBooleanPref(c, "music", f.mName)){
				i++;
			}
		}
		record.setLength(i);
		for (MusicName f : mPlaylistRecord.mLName) {
			if(SharedPreferencesUtils.getBooleanPref(c, "music", f.mName)){
				record.add(f);
			}
		}
		return record;
	}

    private int getPRandom(int index, int length) {
    	int p;
		int j;
    	while(((p = (int)(Math.random() * length)) == 0) && (index == 0));
		for(j = p; j < length; j++) {
			if(mRPlaylist[j] == 0) {
				break;
			}
		}
		if(j == length) {
			for(j = 1; j < p; j++) {
				if(mRPlaylist[j] == 0) {
					break;
				}
			}
		}
		return j;
    }

    public void toRPlaylist(int index) {
    	mRPlaylist = null;
    	mCurrentRIndex = 0;
    	if(mPlaylistRecord != null) {
    		int length = mPlaylistRecord.mCLength;
    		if(length > 0) {
        		mRPlaylist = new int [length];
        		if(index >= length) {
        			index = 0;
        		}
        		mRPlaylist[0] = index;
        		if(length > 1) {
	    			for(int i = index + 1; i < length; i++) {
	    				int p = i - index;
	    				if(mShuffle != 0) {
	    					p = getPRandom(index, length);
	    				}
	    				mRPlaylist[p] = i;
	    			}
	    			for(int i = 0; i < index; i++) {
	    				int p = i + length - index;
	    				if(mShuffle != 0) {
	    					p = getPRandom(index, length);
	    				}
	    				mRPlaylist[p] = i;
	    			}
        		}
    		}
    	}
    }

    public boolean loadFileIsHas(String path) {
        if ((path != null)) {
            File[] file = new File(path).listFiles(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    String n = f.getName().toUpperCase(Locale.ENGLISH);
                    if (f.isFile()
                            && !n.startsWith(".")
                            && (n.endsWith(".MP3") || n.endsWith(".AAC")
                                    || n.endsWith(".OGG")
                                    || n.endsWith(".PCM")
                                    || n.endsWith(".M4A")
                                    || n.endsWith(".EC3")
                                    || n.endsWith(".DTSHD")
                                    || n.endsWith(".MKA")
                                    || n.endsWith(".WAV")
                                    || n.endsWith(".CD")
                                    || n.endsWith(".AMR")
                                    || n.endsWith(".MP2")
                                    || n.endsWith(".APE")
                                    || n.endsWith(".DTS")
                                    || n.endsWith(".FLAC")
                                    || n.endsWith(".MIDI")
                                    || n.endsWith(".MID")
                                    || n.endsWith(".MPC")
                                    || n.endsWith(".TTA")
                                    || n.endsWith(".ASX")
                                    || n.endsWith(".AIFF")
                                    || n.endsWith(".AU"))) {
                        return true;
                    }
                    return false;
                }
            });
            if (file != null && file.length > 0) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
    
    private void initRecord() {
		try {
			mTW.mSDRecord = new Record("SD", 1, 0);
			File[] fileSD = new File("/storage").listFiles(new FileFilter() {
				@Override
				public boolean accept(File f) {
					String n = f.getName();
					if(f.canRead() && f.isDirectory() && n.startsWith("extsd")) {
						return true;
					}
					return false;
				}
			});
			if(fileSD != null) {
				for(File f : fileSD) {
					addRecordSD(f.getAbsolutePath());
				}
			}
			mTW.mUSBRecord = new Record("USB", 2, 0);
			File[] fileUSB = new File("/storage").listFiles(new FileFilter() {
				@Override
				public boolean accept(File f) {
					String n = f.getName();
					if(f.canRead() && f.isDirectory() && n.startsWith("usb")) {
						return true;
					}
					return false;
				}
			});
			if(fileUSB != null) {
				for(File f : fileUSB) {
					addRecordUSB(f.getAbsolutePath());
				}
			}
			mTW.mMediaRecord = new Record("iNand", 3, 0);
			loadVolume(mTW.mMediaRecord, "/mnt/sdcard/iNand");
			mTW.mCList = mTW.mPlaylistRecord;
			if (mTW.mCList.mCLength == 0) {
				if(mTW.mSDRecordArrayList.size() > 0) {
					mTW.mCList = mTW.mSDRecordArrayList.get(0);
				} else {
					mTW.mCList = mTW.mSDRecord;
				}
				if (mTW.mCList.mCLength == 0) {
					if(mTW.mUSBRecordArrayList.size() > 0) {
						mTW.mCList = mTW.mUSBRecordArrayList.get(0);
					} else {
						mTW.mCList = mTW.mUSBRecord;
					}
					if (mTW.mCList.mCLength == 0) {
						mTW.mCList = mTW.mMediaRecord;
						if (mTW.mCList.mCLength == 0) {
							mTW.mCList = mTW.mPlaylistRecord;
						}
					}
				}
			}
		}catch (Exception e){
			Log.i("md","initRecord "+e.toString());
		}
	}
	
    public void loadVolume(Record record, String volume) {
		if ((record != null) && (volume != null)) {
			try {
				BufferedReader br = null;
				try {
					String xpath = null;
					if(volume.startsWith("/storage/usb") || volume.startsWith("/storage/extsd")) {
						xpath = "/data/tw/" + volume.substring(9);
					} else {
						xpath = volume + "/DCIM";
					}
					br = new BufferedReader(new FileReader(xpath + "/.music"));
					String path = null;
					ArrayList<MusicName> l = new ArrayList<MusicName>();
					while((path = br.readLine()) != null) {
						File f = new File(volume + "/" + path);
						if (f.canRead() && f.isDirectory()) {
							String n = f.getName();
							String p = f.getAbsolutePath();
							if(n.equals(".")) {
								String p2 = p.substring(0, p.lastIndexOf("/"));
								String p3 = p2.substring(p2.lastIndexOf("/") + 1);
								if(mTW.loadFileIsHas(p)){
									l.add(new MusicName(p3, p));
								}
							} else {
								if(mTW.loadFileIsHas(p)){
									l.add(new MusicName(n, p));
								}
							}
						}
					}
					record.setLength(l.size());
					for(MusicName n : l) {
						record.add(n);
					}
					l.clear();
				} catch (Exception e) {
				} finally {
					if(br != null) {
						br.close();
						br = null;
					}
				}
			} catch (Exception e) {
			}
		}
	}

	public void addRecordSD(String path) {
		for(Record r : mTW.mSDRecordArrayList) {
			if(path.equals(r.mName)) {
				return;
			}
		}
		Record r = new Record(path, 1, 0);
		loadVolume(r, path);
		mTW.mSDRecordArrayList.add(r);
		if((mTW.mCList != null) && mTW.mCList.mName.equals("SD")) {
			mTW.mCList = mTW.mSDRecordArrayList.get(0);
		}
	}

	public void addRecordUSB(String path) {
		for(Record r : mTW.mUSBRecordArrayList) {
			if(path.equals(r.mName)) {
				return;
			}
		}
		Record r = new Record(path, 2, 0);
		loadVolume(r, path);
		mTW.mUSBRecordArrayList.add(r);
		if((mTW.mCList != null) && mTW.mCList.mName.equals("USB")) {
			mTW.mCList = mTW.mUSBRecordArrayList.get(0);
		}
	}

	public void removeRecordSD(String path) {
		for(Record r : mTW.mSDRecordArrayList) {
			if(path.equals(r.mName)) {
				Record t = mTW.mCList;
				if(mTW.mCList.mLevel == 1) {
					t = mTW.mCList.mPrev;
				}
				String s = t.mName;
				r.clearRecord();
				mTW.mSDRecordArrayList.remove(r);
				if(mTW.mSDRecordLevel >= mTW.mSDRecordArrayList.size()){
					mTW.mSDRecordLevel = mTW.mSDRecordArrayList.size() - 1;
					if(mTW.mSDRecordLevel < 0) {
						mTW.mSDRecordLevel = 0;
					}
				}
				if(path.equals(s)) {
					if(mTW.mSDRecordArrayList.size() > 0) {
						mTW.mCList = mTW.mSDRecordArrayList.get(mTW.mSDRecordLevel);
					} else {
						mTW.mCList = mTW.mSDRecord;
					}
				}
				return;
			}
		}
	}

	public void removeRecordUSB(String path) {
		for(Record r : mTW.mUSBRecordArrayList) {
			if(path.equals(r.mName)) {
				Record t = mTW.mCList;
				if(mTW.mCList.mLevel == 1) {
					t = mTW.mCList.mPrev;
				}
				String s = t.mName;
				r.clearRecord();
				mTW.mUSBRecordArrayList.remove(r);
				if(mTW.mUSBRecordLevel >= mTW.mUSBRecordArrayList.size()){
					mTW.mUSBRecordLevel = mTW.mUSBRecordArrayList.size() - 1;
					if(mTW.mUSBRecordLevel < 0) {
						mTW.mUSBRecordLevel = 0;
					}
				}
				if(path.equals(s)) {
					if(mTW.mUSBRecordArrayList.size() > 0) {
						mTW.mCList = mTW.mUSBRecordArrayList.get(mTW.mUSBRecordLevel);
					} else {
						mTW.mCList = mTW.mUSBRecord;
					}
				}
				return;
			}
		}
	}
	
	/**
	 * 根据播放路径拿到临时播放目录
	 * @param args
	 */
	private void setIndex(String args) {
		if (args == null) {
			return;
		}
		if(args.contains("/mnt/sdcard/iNand")){
			mTW.mCList = mTW.mMediaRecord;
			mTW.mCList.mIndex = 3;
		}else if(args.contains("/storage/usb")){
			if(mTW.mUSBRecordArrayList.size() > 0) {
				if(mTW.mUSBRecordLevel >= mTW.mUSBRecordArrayList.size()) {
					mTW.mUSBRecordLevel = 0;
				}
				mTW.mCList = mTW.mUSBRecordArrayList.get(mTW.mUSBRecordLevel);
			} else {
				mTW.mCList = mTW.mUSBRecord;
			}    			
		}else if(args.contains("/storage/extsd")){
			if(mTW.mSDRecordArrayList.size() > 0) {
				if(mTW.mSDRecordLevel >= mTW.mSDRecordArrayList.size()) {
					mTW.mSDRecordLevel = 0;
				}
				mTW.mCList = mTW.mSDRecordArrayList.get(mTW.mSDRecordLevel);
			} else {
				mTW.mCList = mTW.mSDRecord;
			}
		}else{
			mTW.mCList = mTW.mPlaylistRecord;
			mTW.mCList.mIndex = 0;
		}
	}
}
