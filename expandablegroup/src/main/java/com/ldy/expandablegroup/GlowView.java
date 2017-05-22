package com.ldy.expandablegroup;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by ldy on 2017/5/16.
 */

public class GlowView extends View {
    private EdgeEffectCompat mTopGlow;
    private EdgeEffectCompat mBottomGlow;

    public GlowView(Context context) {
        this(context, null);
    }

    public GlowView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTopGlow != null) {
            if (!mTopGlow.isFinished()) {
                int restoreCount = canvas.save();
                if (mTopGlow.draw(canvas)) {//绘制边缘效果图，如果绘制需要进行动画效果返回true
                    ViewCompat.postInvalidateOnAnimation(this);//进行动画
                }
                canvas.restoreToCount(restoreCount);
            }
        }
        if (mBottomGlow != null) {
            if (!mBottomGlow.isFinished()) {
                int restoreCount = canvas.save();
                //下面两行代码的作用就是把画布平移旋转到底部展示，并让效果向上显示
                canvas.translate(0, getHeight());
                canvas.rotate(180, getWidth() / 2, 0);
                if (mBottomGlow.draw(canvas)) {//绘制边缘效果图，如果绘制需要进行动画效果返回true
                    ViewCompat.postInvalidateOnAnimation(this);//进行动画
                }
                canvas.restoreToCount(restoreCount);
            }
        }
    }

    public void topOnPull(float deltaDistance, float displacement) {
        ensureTopGlow();
        mTopGlow.onPull(deltaDistance, displacement);
        if (mTopGlow != null && (!mTopGlow.isFinished())) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void bottomOnPull(float deltaDistance, float displacement) {
        ensureBottomGlow();
        //由于翻转180度显示，所以X轴坐标需要以中心翻转
        mBottomGlow.onPull(deltaDistance, 1 - displacement);
        if (mBottomGlow != null && (!mBottomGlow.isFinished())) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void releaseAll() {
        if (mTopGlow != null) {
            mTopGlow.onRelease();
        }
        if (mBottomGlow != null) {
            mBottomGlow.onRelease();
        }
    }

    void ensureTopGlow() {
        if (mTopGlow != null) {
            return;
        }
        mTopGlow = new EdgeEffectCompat(getContext());
        mTopGlow.setSize(getWidth(), getHeight());
    }

    void ensureBottomGlow() {
        if (mBottomGlow != null) {
            return;
        }
        mBottomGlow = new EdgeEffectCompat(getContext());
        mBottomGlow.setSize(getWidth(), getHeight());
    }

}
