package com.ma.widget;

import android.view.View;

/**
 * 容器组件的监听事件
 * Created by mapengtang on 2016/10/28.
 */

public interface OnFlowViewChangeListener {
    void onAddView(View childView);

    void onRemoveView(View childView);
}
