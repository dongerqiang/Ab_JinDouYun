package com.qdigo.jindouyun.wheel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.qdigo.jindouyun.R;

/**
 * Adapter for countries
 */
 public class StringAdapter extends AbstractWheelTextAdapter {
    String[] items;


    public StringAdapter(Context context, String[] items) {
        super(context,R.layout.modify_item, NO_RESOURCE);
        this.items = items;

        setItemTextResource(R.id.item);
    }


    @Override
    public View getItem(int index, View cachedView, ViewGroup parent) {
        View view = super.getItem(index, cachedView, parent);
        return view;
    }

    public int getItemsCount() {
        return items.length;
    }

    @Override
    public CharSequence getItemText(int index) {
        return items[index];
    }
}
