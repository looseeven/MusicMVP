package com.tw.music.contarct;

import com.tw.music.presenter.BasePresenter;

import android.net.Uri;

public interface Contarct {
    interface View extends BaseView<prePresenter> {
    	/**
         * 错误提示
         */
        void showError();

        /**
         * 准备提示
         */
        void onPrepared();
    }
    interface prePresenter extends BasePresenter {
        void setUri(Uri mUri); //设置启动URI
    }
    
    interface mainView extends BaseView<mainPresenter> {
    	/**
    	 * 设置壁纸
    	 */
    	void setWallPaper(int position);
    	/**
    	 * 显示歌词/音谱
    	 * true 歌词
    	 * false 音谱
    	 */
    	void showLrcorVis(Boolean b);
    	/**
    	 * 设置壁纸
    	 * mShuffle  1重复 
    	 * mRepeat 1顺序 2 单曲 3随机
    	 */
    	void showRepeat(int Repeat,int mShuffle);
    	/**
    	 * 显示是否收藏
    	 */
    	void showCollect(Boolean b);
    	/**
    	 * 音乐列表显示抽屉
    	 * 0播放列表 1SD 2USB 3iNand 4收藏
    	 */
    	void showListDrawer(int i);
    }
    interface mainPresenter extends BasePresenter {
    	/**
    	 * 切换壁纸
    	 */
    	void setChangeWall();
    	/**
    	 * 打开EQ
    	 */
    	void openEQ();
    	/**
    	 * 打开Home
    	 */
    	void openHome();
    	/**
    	 * 切换音谱/歌词
    	 */
    	void setChangeLrcorVis();
    	/**
    	 * 上一曲
    	 */
		void setPrev();
		/**
    	 * 下一曲
    	 */
		void setNext();
		/**
		 * 切循环模式
		 */
		void setRepeat();
		/**
		 * 收藏
		 */
		void setCollect();
		/**
		 * 当前播放列表
		 */
		void openPlayList();
		/**
		 * SD列表
		 */
		void openSDList();
		/**
		 * 本地iNand列表
		 */
		void openiNandList();
		/**
		 * 收藏列表界面
		 */
		void openCollectList();
		/**
		 * USB列表界面
		 */
		void openUSBList();
    }
}
