package com.epuxun.mvvm.base.recycler;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private final SparseArray<View> views;

    private BaseRecyclerViewAdapter adapter;
    private Application application;

    BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        this.views = new SparseArray<>();
    }

    void setAdapter(BaseRecyclerViewAdapter adapter, Application application) {
        this.adapter = adapter;
        this.application = application;
    }

    /**
     * 获取item的view
     *
     * @param viewId view的id
     */
    public <T extends View> T getView(@IdRes int viewId) {
        View view = views.get(viewId);
        if (view == null) {
            view = itemView.findViewById(viewId);
            views.put(viewId, view);
        }
        return (T) view;
    }

    /**
     * 设置view的标签
     *
     * @param viewId view的id
     * @param tag    标签
     */
    public BaseViewHolder setTag(@IdRes int viewId, Object tag) {
        View view = getView(viewId);
        view.setTag(tag);
        return this;
    }

    /**
     * 设置view的标签
     *
     * @param viewId view的id
     * @param key    key
     * @param tag    标签
     */
    public BaseViewHolder setTag(@IdRes int viewId, int key, Object tag) {
        View view = getView(viewId);
        view.setTag(key, tag);
        return this;
    }

    /**
     * 设置view的背景颜色
     *
     * @param viewId view的id
     * @param color  颜色
     */
    public BaseViewHolder setBackgroundColor(@IdRes int viewId, @ColorInt int color) {
        View view = getView(viewId);
        view.setBackgroundColor(color);
        return this;
    }

    /**
     * 设置view的背景
     *
     * @param viewId        view的id
     * @param backgroundRes 资源文件id
     */
    public BaseViewHolder setBackgroundResource(@IdRes int viewId, @DrawableRes int backgroundRes) {
        View view = getView(viewId);
        view.setBackgroundResource(backgroundRes);
        return this;
    }

    /**
     * 设置view的背景
     *
     * @param viewId view的id
     * @param bitmap bitmap
     */
    public BaseViewHolder setBackground(@IdRes int viewId, Bitmap bitmap) {
        View view = getView(viewId);
        BitmapDrawable bd = new BitmapDrawable(application.getResources(), bitmap);
        view.setBackground(bd);
        return this;
    }

    /**
     * 将视图可见性设置为GONE
     */
    public BaseViewHolder setGone(@IdRes int viewId) {
        View view = getView(viewId);
        view.setVisibility(View.GONE);
        return this;
    }

    /**
     * 将视图可见性设置为INVISIBLE。
     */
    public BaseViewHolder setInvisible(@IdRes int viewId) {
        View view = getView(viewId);
        view.setVisibility(View.INVISIBLE);
        return this;
    }

    /**
     * 将视图可见性设置为VISIBLE
     */
    public BaseViewHolder setVisible(@IdRes int viewId) {
        View view = getView(viewId);
        view.setVisibility(View.VISIBLE);
        return this;
    }

    /**
     * 设置view的点击状态
     *
     * @param viewId view的id
     * @param enable 为true时view可以点击，为false时点击失效
     */
    public BaseViewHolder setEnabled(@IdRes int viewId, boolean enable) {
        View view = getView(viewId);
        view.setEnabled(enable);
        return this;
    }

    /**
     * view的点击事件，在Adapter中调用
     *
     * @param viewId   view的id
     * @param listener 长按事件
     */
    @Deprecated
    public BaseViewHolder setOnClickListener(@IdRes int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        view.setOnClickListener(listener);
        return this;
    }

    /**
     * 绑定item中子view的点击事件后，可以在Activity中通过mAdapter.setOnItemChildClickListener调用
     *
     * @param viewIds item的view的id
     */
    public BaseViewHolder addOnClickListener(@IdRes final int... viewIds) {
        for (int viewId : viewIds) {
            View view = getView(viewId);
            if (view != null) {
                if (!view.isClickable()) {
                    view.setClickable(true);
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (adapter.getOnItemChildClickListener() != null) {
                            adapter.getOnItemChildClickListener().onItemChildClick(adapter, v, getLayoutPosition());
                        }
                    }
                });
            }
        }
        return this;
    }

    /**
     * view的长按事件，在Adapter中调用
     *
     * @param viewId   view的id
     * @param listener 长按事件
     */
    @Deprecated
    public BaseViewHolder setOnLongClickListener(@IdRes int viewId, View.OnLongClickListener listener) {
        View view = getView(viewId);
        view.setOnLongClickListener(listener);
        return this;
    }

    /**
     * 绑定item中子view的长按事件后，可以在Activity中通过mAdapter.setOnItemChildLongClickListener调用
     *
     * @param viewIds item的view的id
     */
    public BaseViewHolder addOnLongClickListener(@IdRes final int... viewIds) {
        for (int viewId : viewIds) {
            View view = getView(viewId);
            if (view != null) {
                if (!view.isLongClickable()) {
                    view.setLongClickable(true);
                }
                view.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (adapter.getOnItemChildLongClickListener() != null) {
                            return adapter.getOnItemChildLongClickListener().onItemChildLongClick(adapter, v, getLayoutPosition());
                        }
                        return false;
                    }
                });
            }
        }
        return this;
    }

    /**
     * view的触摸事件
     *
     * @param viewId   View的id
     * @param listener 触摸事件
     */
    @Deprecated
    public BaseViewHolder setOnTouchListener(@IdRes int viewId, View.OnTouchListener listener) {
        View view = getView(viewId);
        view.setOnTouchListener(listener);
        return this;
    }

    /**
     * 设置TextView的文字
     *
     * @param viewId TextView的id
     * @param value  文本内容
     */
    public BaseViewHolder setText(@IdRes int viewId, CharSequence value) {
        TextView view = getView(viewId);
        view.setText(value);
        return this;
    }

    /**
     * 设置TextView的文字颜色
     *
     * @param viewId    TextView的id
     * @param textColor 文字颜色
     */
    public BaseViewHolder setTextColor(@IdRes int viewId, @ColorInt int textColor) {
        TextView view = getView(viewId);
        view.setTextColor(textColor);
        return this;
    }

    /**
     * 为TextView创建超链接
     *
     * @param viewId TextView的id
     */
    public BaseViewHolder addLinks(@IdRes int viewId) {
        TextView view = getView(viewId);
        Linkify.addLinks(view, Linkify.ALL);
        return this;
    }

    /**
     * 设置TextView的字体，并启用子像素渲染。
     *
     * @param viewId   TextView的id
     * @param typeface 字体
     */
    public BaseViewHolder setTypeface(@IdRes int viewId, Typeface typeface) {
        TextView view = getView(viewId);
        view.setTypeface(typeface);
        view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        return this;
    }

    /**
     * 批量设置TextView的字体，并启用子像素渲染。
     *
     * @param typeface 字体
     * @param viewIds  TextView的id
     */
    public BaseViewHolder setTypeface(Typeface typeface, int... viewIds) {
        for (int viewId : viewIds) {
            TextView view = getView(viewId);
            view.setTypeface(typeface);
            view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        }
        return this;
    }

    /**
     * 设置ImageView的图片
     *
     * @param viewId     ImageView的id
     * @param imageResId 资源文件id
     */
    public BaseViewHolder setImageResource(@IdRes int viewId, @DrawableRes int imageResId) {
        ImageView view = getView(viewId);
        view.setImageResource(imageResId);
        return this;
    }

    /**
     * 设置ImageView的图片
     *
     * @param viewId ImageView的id
     * @param bitmap bitmap
     */
    public BaseViewHolder setImageBitmap(@IdRes int viewId, Bitmap bitmap) {
        ImageView view = getView(viewId);
        view.setImageBitmap(bitmap);
        return this;
    }

}
