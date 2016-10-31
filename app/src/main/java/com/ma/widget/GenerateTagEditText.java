package com.ma.widget;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * 生成EditText的组件
 * Created by mapengtang on 2016/10/28.
 */

public class GenerateTagEditText {

    private List<EditText> mEditTextList;
    private OnFlowViewChangeListener mListener;
    private Context context;
    private int textSize;
    private int height;
    private int containerWidth;
    private int marginLeft;
    private int marginRight;
    private boolean notHandle = false;
    private int initCount;
    private boolean notHandleFocusChanged = false;

    public GenerateTagEditText(Context context) {
        this.context = context;
    }

    public void generate(int width1, int width2, int height, int textSize) {
        this.textSize = textSize;
        this.height = height;
        getViews(context, width1, width2);
        registerListener();
    }

    public void setMargin(int marginLeft, int marginRight) {
        this.marginLeft = marginLeft;
        this.marginRight = marginRight;
    }

    public void setListener(OnFlowViewChangeListener mListener) {
        this.mListener = mListener;
    }

    public void setContainerWidth(int containerWidth) {
        this.containerWidth = containerWidth;
    }

    private void getViews(Context context, int width1, int width2) {
        mEditTextList = new ArrayList<>();
        if (width1 > 0 && width2 > 0 && width1 > width2) {
            int temp = width1;
            width1 = width2;
            width2 = temp;
        }

        if (width1 > 0) {
            mEditTextList.add(new TagEditTextBuilder()
                    .setContext(context)
                    .setWidth(width1)
                    .setTextSize(textSize)
                    .setHeight(height)
                    .setMarginLeft(marginLeft)
                    .setMarginRight(marginRight)
                    .create());

        }

        if (width2 > 0) {
            mEditTextList.add(new TagEditTextBuilder()
                    .setContext(context)
                    .setWidth(width2)
                    .setTextSize(textSize)
                    .setHeight(height)
                    .setMarginLeft(marginLeft)
                    .setMarginRight(marginRight)
                    .create());
        }
        initCount = mEditTextList.size();
    }

    private void registerListener() {
        if (mEditTextList == null || mEditTextList.size() == 0) {
            return;
        }
        //注册监听事件
        for (int i = 0; i < mEditTextList.size(); ++i) {
            final EditText editText = mEditTextList.get(i);
            final int index = i;
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    textChangeJudge(editText, s.toString(), index);
                }
            });

            editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (index == 0 || hasFocus == false || notHandleFocusChanged) {
                        notHandleFocusChanged = false;
                        return;
                    }
                    for (int j = index; j >= 0; --j) {
                        EditText selectEditext = mEditTextList.get(j);
                        String selectEditextText = selectEditext.getText().toString();
                        if (!selectEditextText.isEmpty()) {
                            selectEditext.setSelection(selectEditextText.length());
                            notHandleFocusChanged = true;
                            selectEditext.requestFocus();
                            return;
                        } else {
                            selectEditext.clearFocus();
                        }
                    }
                    mEditTextList.get(0).setSelection(0);
                    notHandleFocusChanged = true;
                    mEditTextList.get(0).requestFocus();
                }
            });
            if (i != 0) {
                //删除键的监听
                editText.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_DEL && mEditTextList.get(index).getSelectionEnd() == 0) {
                            EditText lastEditText = mEditTextList.get(index - 1);
                            lastEditText.setSelection(lastEditText.getText().length());
                            notHandleFocusChanged = true;
                            lastEditText.requestFocus();
                            EditText currentEditText = mEditTextList.get(index);
                            String currentStr = currentEditText.getText().toString();
                            if (currentStr.isEmpty()) {
                                if (mEditTextList.size() > initCount) {
                                    notHandleFocusChanged = true;
                                    EditText delLastEditText = mEditTextList.get(mEditTextList.size() - 1);
                                    delLastEditText.setSelection(delLastEditText.getText().toString().length());
                                    delLastEditText.requestFocus();
                                    mEditTextList.remove(currentEditText);
                                    mListener.onRemoveView(currentEditText);
                                }
                            }
                        }
                        return false;
                    }
                });
            }
        }
    }

    private void textChangeJudge(EditText editText, String text, int index) {
        if (notHandle) {
            notHandle = false;
            return;
        }
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(editText.getTextSize());
        Rect rect = new Rect();
        paint.getTextBounds(text, 0, text.length(), rect);
        if (rect.width() > editText.getMeasuredWidth()) {
            editText.setText(text.substring(0, text.length() - 1));
            notHandle = true;
            String subStr = text.substring(text.length() - 1, text.length());
            if (index + 1 < mEditTextList.size()) {
                EditText lastEditText = mEditTextList.get(index + 1);
                lastEditText.setText(subStr);
                notHandle = true;
                lastEditText.setSelection(lastEditText.getText().length());
                notHandleFocusChanged = true;
                lastEditText.requestFocus();
            } else {
                //添加组件
                mEditTextList.add(new TagEditTextBuilder()
                        .setContext(context)
                        .setWidth(containerWidth)
                        .setTextSize(textSize)
                        .setHeight(height)
                        .setMarginLeft(marginLeft)
                        .setMarginRight(marginRight)
                        .create());
                final EditText lastEditText = mEditTextList.get(mEditTextList.size() - 1);
                lastEditText.setText(subStr);
                notHandle = true;
                mListener.onAddView(lastEditText);
                lastEditText.setSelection(lastEditText.getText().length());
                notHandleFocusChanged = true;
                lastEditText.requestFocus();
                registerListener();
            }
        } else if (rect.width() < editText.getMeasuredWidth()) {
            String currentStr = editText.getText().toString();
            if (mEditTextList.size() > index + 1) {
                EditText nextEditText = mEditTextList.get(index + 1);
                String nextStr = nextEditText.getText().toString();
                if (nextStr.isEmpty()) {
                    return;
                }
                currentStr = currentStr + nextStr.substring(0, 1);
                nextStr = nextStr.substring(1);
                notHandle = true;
                editText.setText(currentStr);
                nextEditText.setText(nextStr);
            }
        }
    }

    public List<EditText> getEditTextList() {
        return mEditTextList;
    }

}
