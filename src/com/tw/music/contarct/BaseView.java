/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.tw.music.contarct;

public interface BaseView<T> {
    void setPresenter(T presenter);
    /**
	 * @param title 歌曲
	 * @param artist 专辑
	 * @param album 歌手
	 */
	void setID3(String title,String artist,String album);
	/**
	 * @param totaltime 总长度
	 * @param currenttime 实时进度
	 */
	void setSeekBar(int totaltime,int currenttime);
	/**
	 * @param totaltime 总长度
	 * @param currenttime 实时进度
	 */
	void showPlaypause(Boolean playpause);
}