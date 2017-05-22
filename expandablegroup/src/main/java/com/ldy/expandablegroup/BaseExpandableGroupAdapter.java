package com.ldy.expandablegroup;

/**
 * Created by ldy on 2017/4/19.
 *
 */
public abstract class BaseExpandableGroupAdapter {

    public abstract ExpandableGroupItem getView(int position);
    public abstract int getCount();
    public void itemExpandStart(int position){}
    public void itemExpandEnd(int position){}
}
