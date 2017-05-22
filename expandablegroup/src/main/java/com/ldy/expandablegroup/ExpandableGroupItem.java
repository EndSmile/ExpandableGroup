package com.ldy.expandablegroup;

import android.content.Context;
import android.support.annotation.Px;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by ldy on 2017/4/19.
 */

public class ExpandableGroupItem extends LinearLayout {
    private View title;
    private int maxTitleHeight;
    private int minTitleHeight;
    private View content;

    public ExpandableGroupItem(Context context) {
        super(context);
        setOrientation(VERTICAL);
    }

    /**
     * 设置item的内容及对应高度
     * @param title title
     * @param maxTitleHeight title在当前展开项及下一项高度
     * @param minTitleHeight title在position>expandPosition+1时的高度
     * @param content   content
     */
    public void setView(View title,
                        @Px int maxTitleHeight, @Px int minTitleHeight,
                        View content) {
        this.title = title;
        this.maxTitleHeight = maxTitleHeight;
        this.minTitleHeight = minTitleHeight;
        this.content = content;
    }

    void addView(@Px int titleHeight, @Px int contentHeight) {
        removeAllViews();
        addView(title, FrameLayout.LayoutParams.MATCH_PARENT, titleHeight);
        addView(content, FrameLayout.LayoutParams.MATCH_PARENT, contentHeight);
    }

    void changeContentHeight(@Px int contentHeight){
        ViewGroup.LayoutParams layoutParams = content.getLayoutParams();
        layoutParams.height = contentHeight;
        content.setLayoutParams(layoutParams);
    }

    public void setTitleClickListener(OnClickListener onClickListener){
        title.setOnClickListener(onClickListener);
    }

    public View getTitle() {
        return title;
    }

    public int getMaxTitleHeight() {
        return maxTitleHeight;
    }

    public int getMinTitleHeight() {
        return minTitleHeight;
    }

    public View getContent() {
        return content;
    }
}
