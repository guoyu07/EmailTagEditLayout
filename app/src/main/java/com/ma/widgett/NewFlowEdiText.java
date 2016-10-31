package com.ma.widgett;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

/**
 * 重写布局的EditText事件
 * Created by mapengtang on 2016/10/28.
 */

public class NewFlowEdiText extends EditText {

    public NewFlowEdiText(Context context) {
        super(context);
        registerListener();
    }

    public NewFlowEdiText(Context context, AttributeSet attrs) {
        super(context, attrs);
        registerListener();
    }

    public NewFlowEdiText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        registerListener();
    }

    private void registerListener() {
        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_NAVIGATE_NEXT || keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_SPACE) {
                    //添加字符串
                }
                return false;
            }
        });
    }
}
