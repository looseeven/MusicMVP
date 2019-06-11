package com.tw.mvp;

import android.net.Uri;

public interface Contarct {
    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends BasePresenter {
        void setUri(Uri mUri); //设置启动URI
    }
}
