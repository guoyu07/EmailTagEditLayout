package com.ma.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.EditText;

import com.ma.emailtageditlayout.R;

/**
 * 输入的文本框EditText
 * Created by mapengtang on 2016/10/28.
 */

public class TagEditTextBuilder {

    private Context context;
    private int height;
    private int width;
    private int textSize;
    private int marginLeft;
    private int marginRight;

    public TagEditTextBuilder() {
    }

    public EditText create() {
        //背景颜色不要
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        EditText editText = (EditText) layoutInflater.inflate(R.layout.flow_tag_edittex_view, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(layoutParams);
        marginLayoutParams.leftMargin = marginLeft;
        marginLayoutParams.rightMargin = marginRight;
        editText.setLayoutParams(marginLayoutParams);
        editText.setTextSize(textSize);
        editText.setSingleLine(true);
        return editText;
    }

    public TagEditTextBuilder setContext(Context context) {
        this.context = context;
        return this;
    }

    public TagEditTextBuilder setWidth(int width) {
        this.width = width;
        return this;
    }

    public TagEditTextBuilder setHeight(int height) {
        this.height = height;
        return this;
    }

    public TagEditTextBuilder setTextSize(int textSize) {
        this.textSize = textSize;
        return this;
    }

    public TagEditTextBuilder setMarginLeft(int marginLeft){
        this.marginLeft = marginLeft;
        return this;
    }

    public TagEditTextBuilder setMarginRight(int marginRight){
        this.marginRight = marginRight;
        return this;
    }
}
