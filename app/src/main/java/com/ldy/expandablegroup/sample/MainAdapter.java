package com.ldy.expandablegroup.sample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ldy.expandablegroup.BaseExpandableGroupAdapter;
import com.ldy.expandablegroup.ExpandableGroupItem;

import java.util.ArrayList;

/**
 * Created by ldy on 2017/5/18.
 */

public class MainAdapter extends BaseExpandableGroupAdapter {

    private final ArrayList<ExpandableGroupItem> itemList = new ArrayList<>();
    private Context context;

    public MainAdapter(Context context) {
        this.context = context;

        for (int i = 0; i < 4; i++) {
            View title = LayoutInflater.from(context).inflate(R.layout.item_title, null);
            View content = LayoutInflater.from(context).inflate(R.layout.item_content, null);
            TextView tvTitle = (TextView) title.findViewById(R.id.tv_title);
            ImageView ivContent = (ImageView) content.findViewById(R.id.iv_content);
            switch (i) {
                case 0:
                    tvTitle.setText("飞鸟");
                    ivContent.setImageResource(R.mipmap.ic_bird);
                    break;
                case 1:
                    tvTitle.setText("灯塔");
                    ivContent.setImageResource(R.mipmap.ic_light);
                    break;
                case 2:
                    tvTitle.setText("远山");
                    ivContent.setImageResource(R.mipmap.ic_mount);
                    break;
                case 3:
                    tvTitle.setText("日出");
                    ivContent.setImageResource(R.mipmap.ic_sunrise);
                    break;
            }
            ExpandableGroupItem item = new ExpandableGroupItem(context);
            item.setView(title, DensityUtil.dip2px(context,48), DensityUtil.dip2px(context,31), content);
            itemList.add(item);
        }

    }

    @Override
    public ExpandableGroupItem getView(int position) {
        return itemList.get(position);
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public void itemExpandStart(int position) {
        super.itemExpandStart(position);
        for (int i = 0, size = itemList.size(); i < size; i++) {
            View title = itemList.get(i).getTitle();
            ImageView arrow = (ImageView) title.findViewById(R.id.iv_title_arrow);
            View divider = title.findViewById(R.id.divider);
            if (i == position) {
                if (i == 0) {
                    arrow.setImageDrawable(null);
                } else {
                    arrow.setImageResource(R.mipmap.ic_arrow_down);
                }
            } else if (i == position + 1) {
                arrow.setImageResource(R.mipmap.ic_arrow_up);
            } else {
                arrow.setImageResource(R.mipmap.ic_arrow_up);
            }

            //shadow
            if (i > position + 1) {
                title.setBackgroundResource(R.mipmap.bg_shadow);
            } else {
                title.setBackgroundColor(0xffffffff);
            }

            //divider
            divider.setVisibility(View.GONE);

            if (i == size - 1 && position != size - 1) {
                divider.setVisibility(View.GONE);
            } else {
                divider.setVisibility(View.VISIBLE);
            }
        }
    }
}
