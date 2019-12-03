package com.epuxun.mvvm.base.recycler.multi;

import androidx.annotation.NonNull;

import com.epuxun.mvvm.base.recycler.BaseRecyclerViewAdapter;
import com.epuxun.mvvm.base.recycler.BaseViewHolder;

import java.util.List;

public abstract class BaseMultiItemAdapter <T extends BaseMultiItemEntity, K extends BaseViewHolder> extends BaseRecyclerViewAdapter<T,K> {

    public BaseMultiItemAdapter(@NonNull List<BaseMultiItemEntity> data) {
        super(data);
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).getItemType();
    }
}
