package com.epuxun.mvvm.base.recycler.multi;

import androidx.annotation.NonNull;

import com.epuxun.mvvm.base.recycler.BaseViewHolder;
import com.epuxun.mvvm.base.recycler.touch.BaseItemRecyclerViewAdapter;

import java.util.List;

/**
 * 可拖走滑动的多布局适配器
 */
public abstract class BaseMultiItemDragAdapter<T extends BaseMultiItemEntity, K extends BaseViewHolder>
        extends BaseItemRecyclerViewAdapter<T,K> {

    public BaseMultiItemDragAdapter(@NonNull List<BaseMultiItemEntity> data) {
        super(data);
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getItemType();
    }
}
