package com.ldy.expandablegroup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by ldy on 2017/4/19.
 */
public class ExpandableGroup extends FrameLayout {
    private GlowView glowView;
    public int minTriggerScroll;
    private List<Integer> titleMinHeightList = new ArrayList<>();
    private List<Integer> titleMaxHeightList = new ArrayList<>();
    private List<ExpandableGroupItem> itemList = new ArrayList<>();
    private List<Integer> itemYList = new ArrayList<>();
    private List<Integer> itemTitleHeightList = new ArrayList<>();

    private int expandPosition = 0;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private BaseExpandableGroupAdapter adapter;
    private int criticalHeight;

    public ExpandableGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        float scale = context.getResources().getDisplayMetrics().density;
        minTriggerScroll = (int) (scale * 16);
    }

    public void setAdapter(final BaseExpandableGroupAdapter adapter) {
        if (adapter == null) {
            return;
        }
        this.adapter = adapter;
        if (getHeight() != 0) {
            initView(adapter);
        } else {
            requestLayout();
        }
    }

    private void initView(final BaseExpandableGroupAdapter adapter) {
        int count = adapter.getCount();

        //initData
        for (int i = 0; i < count; i++) {
            ExpandableGroupItem item = adapter.getView(i);
            itemList.add(item);
            titleMinHeightList.add(item.getMinTitleHeight());
            titleMaxHeightList.add(item.getMaxTitleHeight());
            itemYList.add(0);
            itemTitleHeightList.add(item.getMaxTitleHeight());
        }

        //initView
        for (int i = 0; i < count; i++) {
            ExpandableGroupItem item = itemList.get(i);
            int titleHeight = i <= expandPosition ? titleMaxHeightList.get(i) : titleMaxHeightList.get(i);
            item.addView(titleHeight, getItemContentHeight(i));
            addView(item, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

            final int finalI = i;
            item.setTitleClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(finalI);
                }
            });
        }
        glowView = new GlowView(getContext());
        addView(glowView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        expand(expandPosition);
    }

    public void select(int position) {
        if (position <= 0) {
            return;
        }
        if (position == expandPosition) {
            position -= 1;
        }
        expand(position);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        criticalHeight = (int) (1F / 7F * h);
    }

    int height;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (height != getHeight()) {
            height = getHeight();
            if (itemList.isEmpty()) {
                if (adapter != null)
                    initView(adapter);
            } else {
                int count = adapter.getCount();
                for (int i = 0; i < count; i++) {
                    ExpandableGroupItem item = itemList.get(i);
                    item.changeContentHeight(getItemContentHeight(i));
                }
                expand(expandPosition);
            }
        }
    }

    public void expand(final int position) {
        if (position < 0 || position >= itemList.size()) {
            return;
        }
        expandPosition = position;

        final List<Integer> targetItemTitleHeightList = new ArrayList<>(itemTitleHeightList);
        final List<Integer> targetItemYList = new ArrayList<>(itemYList);

        //设置每项title的高度
        for (int i = 0; i <= position + 1 && i < titleMaxHeightList.size(); i++) {
            targetItemTitleHeightList.set(i, titleMaxHeightList.get(i));
        }
        for (int i = position + 2; i < itemTitleHeightList.size() && i < titleMaxHeightList.size(); i++) {
            targetItemTitleHeightList.set(i, titleMinHeightList.get(i));
        }

        //position之前的y,只露出title
        for (int i = position - 1, y = 0; i >= 0; i--) {
            y -= targetItemTitleHeightList.get(i);
            targetItemYList.set(i, y);
        }

        //position的y
        targetItemYList.set(position, 0);

        //position之后的y
        for (int i = position + 1, y = targetItemTitleHeightList.get(position) + getItemContentHeight(position);
             i < itemYList.size(); i++) {
            targetItemYList.set(i, y);
            y += targetItemTitleHeightList.get(i);
        }

        //动画部分
        List<Animator> animatorList = new ArrayList<>();
        for (int i = 0, length = itemYList.size(); i < length; i++) {
            final ExpandableGroupItem expandableGroupItem = itemList.get(i);

            //y位置变化动画
            int itemY = itemYList.get(i);
            int targetItemY = targetItemYList.get(i);
            final IntegerHolder itemYHolder = new IntegerHolder();
            ObjectAnimator itemYAnimator = buildAnimator(itemY, targetItemY, itemYHolder,
                    new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            setItemY(expandableGroupItem, itemYHolder.getValue());
                        }
                    });
            if (itemYAnimator != null) {
                animatorList.add(itemYAnimator);
            }

            //高度变化动画
            final int itemTitleHeight = itemTitleHeightList.get(i);
            int targetItemTitleHeight = targetItemTitleHeightList.get(i);
            final IntegerHolder itemTitleHeightHolder = new IntegerHolder();
            ObjectAnimator itemTitleHeightAnimator = buildAnimator(itemTitleHeight, targetItemTitleHeight, itemTitleHeightHolder,
                    new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            setItemTitleHeight(expandableGroupItem, itemTitleHeightHolder.getValue());
                        }
                    });
            if (itemTitleHeightAnimator != null) {
                animatorList.add(itemTitleHeightAnimator);
            }
        }
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorList);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                adapter.itemExpandEnd(position);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                adapter.itemExpandStart(position);
            }
        });
        animatorSet.start();

        itemYList = targetItemYList;
        itemTitleHeightList = targetItemTitleHeightList;
    }

    private void setItemTitleHeight(ExpandableGroupItem expandableGroupItem, int value) {
        expandableGroupItem.getTitle().getLayoutParams().height = value;
        expandableGroupItem.requestLayout();
    }

    private void setItemY(ExpandableGroupItem expandableGroupItem, int value) {
        expandableGroupItem.setTranslationY(value);
    }

    private ObjectAnimator buildAnimator(int start, final int end, final IntegerHolder holder,
                                         final ValueAnimator.AnimatorUpdateListener listener) {
        if (start != end) {
            ObjectAnimator animator = ObjectAnimator.ofInt(holder, "value", start, end);
            animator.setDuration(200);
            animator.setInterpolator(interpolator);
            animator.addUpdateListener(listener);
            //防止动画的最后位置不准
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    holder.setValue(end);
                    listener.onAnimationUpdate(null);
                }
            });
            return animator;
        } else {
            return null;
        }
    }


    @Px
    private int getItemContentHeight(int position) {
        int height = getHeight();
        int size = titleMinHeightList.size();
        for (int i = position; i < size; i++) {
            if (i <= position + 1) {
                height -= titleMaxHeightList.get(i);
            } else {
                height -= titleMinHeightList.get(i);
            }
        }
        return height;
    }


    private int lastPointY;
    private int movePosition;
    private boolean isNewEvent;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastPointY = (int) ev.getY();
                movePosition = -1;
                return false;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getY() - lastPointY) > minTriggerScroll) {
                    return true;
                } else {
                    return false;
                }

        }
        return super.onInterceptTouchEvent(ev);
    }

    private float lastMoveY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        getParent().requestDisallowInterceptTouchEvent(true);
        float moveY = event.getY() - lastPointY;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (isNewEvent) {
                    if (Math.abs(moveY) > minTriggerScroll) {
                        //达到滑动的临界条件
                        isNewEvent = false;
                    } else {
                        return false;
                    }
                }
                if (moveY >= 0) {
                    if (expandPosition == 0) {
                        glowView.topOnPull(Math.abs(moveY) / getHeight(), event.getX() / getWidth());
                        return true;
                    } else {
                        movePosition = expandPosition;
                    }
                } else {
                    if (expandPosition == itemList.size() - 1) {
                        glowView.bottomOnPull(Math.abs(moveY) / getHeight(), event.getX() / getWidth());
                        return true;
                    }
                    movePosition = expandPosition + 1;
                }
                move(movePosition, (int) moveY);
                lastMoveY = moveY;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (movePosition == -1) {
                    glowView.releaseAll();
                    return true;
                }
                itemYList.set(movePosition, (int) (itemYList.get(movePosition) + lastMoveY));

                if (lastPointY - event.getY() >= criticalHeight) {
                    int resultPosition;
                    if (expandPosition + 1 >= itemList.size()) {
                        resultPosition = expandPosition;
                    } else {
                        resultPosition = expandPosition + 1;
                    }
                    expand(resultPosition);
                } else {
                    if (lastPointY - event.getY() <= -criticalHeight) {
                        int resultPosition;
                        if (expandPosition - 1 < 0) {
                            resultPosition = expandPosition;
                        } else {
                            resultPosition = expandPosition - 1;
                        }
                        expand(resultPosition);
                    } else {
                        expand(expandPosition);
                    }
                }

        }
        return true;
    }

    private void move(int position, int moveY) {
        int y = itemYList.get(position) + moveY;
        ExpandableGroupItem item = itemList.get(position);
        item.setY(y);
    }


    private class IntegerHolder {
        private int value;

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }
}
