package com.epuxun.mvvm.base.recycler;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.app.Application;
import android.content.Context;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.IntDef;
import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.epuxun.mvvm.base.recycler.animation.AlphaInAnimation;
import com.epuxun.mvvm.base.recycler.animation.ScaleInAnimation;
import com.epuxun.mvvm.base.recycler.animation.SlideInBottomAnimation;
import com.epuxun.mvvm.base.recycler.animation.SlideInLeftAnimation;
import com.epuxun.mvvm.base.recycler.animation.SlideInRightAnimation;
import com.epuxun.mvvm.base.recycler.multi.BaseMultiItemEntity;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * RecyclerView适配器
 */
public abstract class BaseRecyclerViewAdapter<T,K extends BaseViewHolder> extends RecyclerView.Adapter<K>{

    public static final int TAG = 0x00000000;

    private Context context;

    //点击位置
    private int clickLocation = 0;

    //单布局id
    private int resource;

    //是否是多布局
    private boolean isMultiItem = false;
    //多布局所有的id集合
    private SparseIntArray layouts;

    public List<T> data;

    //绑定数据的抽象方法
    protected abstract void convert(@NonNull K holder, T item, int position);

    /**
     * item 点击事件
     */
    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * item 长按事件
     */
    private OnItemLongClickListener onItemLongClickListener;
    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    /**
     * item中子view的点击事件
     * 必须先在adapter的convert方法里面通过viewHolder.addOnClickListener绑定一下的控件id
     */
    private OnItemChildClickListener mOnItemChildClickListener;
    public interface OnItemChildClickListener {
        void onItemChildClick(BaseRecyclerViewAdapter adapter, View view, int position);
    }
    public void setOnItemChildClickListener(OnItemChildClickListener listener) {
        mOnItemChildClickListener = listener;
    }
    @Nullable
    OnItemChildClickListener getOnItemChildClickListener() {
        return mOnItemChildClickListener;
    }

    /**
     * item中子view的长按事件
     * 必须先在adapter的convert方法里面通过viewHolder.addOnLongClickListener绑定一下的控件id
     */
    private OnItemChildLongClickListener mOnItemChildLongClickListener;
    public interface OnItemChildLongClickListener {
        /**
         * 长按事件
         * @return 如果回调消耗了长按，则返回true，否则返回false
         */
        boolean onItemChildLongClick(BaseRecyclerViewAdapter adapter, View view, int position);
    }
    public void setOnItemChildLongClickListener(OnItemChildLongClickListener listener) {
        mOnItemChildLongClickListener = listener;
    }
    @Nullable
    OnItemChildLongClickListener getOnItemChildLongClickListener() {
        return mOnItemChildLongClickListener;
    }

    /**
     * 单布局构造函数
     * @param resource 布局文件
     * @param data item数据
     */
    public BaseRecyclerViewAdapter(@LayoutRes int resource, @NonNull List<T> data){
        this.data = data;
        if (resource != 0) {
            this.resource = resource;
            isMultiItem = false;
        }
    }

    /**
     * 多布局构造函数
     * @param data item数据
     */
    public BaseRecyclerViewAdapter(@NonNull List<BaseMultiItemEntity> data) {
        this(0, (List<T>) data);
        isMultiItem = true;
        layouts = new SparseIntArray();
        for (BaseMultiItemEntity t : data){
            layouts.put(t.getItemType(), t.getItemType());
        }
    }

    @NonNull
    @Override
    public K onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();
        Application application = (Application) context.getApplicationContext();

        //获取布局View
        View view;
        LayoutInflater mLayoutInflater = LayoutInflater.from(context);
        if (isMultiItem){
            view = mLayoutInflater.inflate(getLayoutId(viewType), viewGroup, false);
        }else {
            view = mLayoutInflater.inflate(resource, viewGroup, false);
        }

        //获取ViewHolder
        K baseViewHolder = createBaseViewHolder(view);

        //设置item的点击事件/长按事件
        bindViewClickListener(baseViewHolder);

        baseViewHolder.setAdapter(this, application);
        return baseViewHolder;
    }

    @Override
    public void onBindViewHolder(@NotNull K holder, int position) {
        T item = null;
        if (position >= 0 && position < data.size()){
            item = data.get(position);
        }
        convert(holder,item ,position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    /**
     * 获取上下文
     */
    public Context getContext(){
        return context;
    }

    /**
     * 设置点击位置
     */
    public void setClickLocation(int clickLocation){
        if (this.clickLocation != clickLocation){
            //获取前一次点击的position
            int beforePosition = this.clickLocation;
            //获取当前点击position
            this.clickLocation = clickLocation;
            //刷新点击项
            notifyItemChanged(clickLocation);
            if (beforePosition >= 0){
                //刷新前一次点击位置
                notifyItemChanged(beforePosition);
            }
        }
    }

    /**
     * 获取点击位置
     */
    public int getClickLocation(){
        return clickLocation;
    }

    /**
     * 添加新布局
     * @param viewType 新布局
     */
    public void setLayouts(@LayoutRes int viewType){
        layouts.put(viewType,viewType);
    }

    /**
     * 是否是新布局
     * @param element BaseMultiItemEntity
     */
    private void isNewLayout(@NonNull T element){
        if (isMultiItem){
            int itemType = ((BaseMultiItemEntity) element).getItemType();
            if (getLayoutId(itemType) == 0 ){
                layouts.put(itemType,itemType);
            }
        }
    }

    /**
     * 是否是新布局
     * @param newData BaseMultiItemEntity集合
     */
    private void isNewLayout(@NonNull Collection<? extends T> newData){
        if (isMultiItem){
            for (T element :newData){
                int itemType = ((BaseMultiItemEntity) element).getItemType();
                if (getLayoutId(itemType) == 0 ){
                    layouts.put(itemType,itemType);
                }
            }
        }
    }

    /**
     * 在列表最后添加一个item
     * @param element item的数据
     */
    public void addNewItem(@NonNull T element) {
        isNewLayout(element);
        data.add(element);
        notifyItemInserted(data.size());
        compatibilityDataSizeChanged(1);
    }

    /**
     * 在指定位置添加一个item，如果position越界则添加到列表最后
     * @param position item添加的位置
     * @param element item的数据
     */
    public void addNewItem(@IntRange(from = 0) int position, @NonNull T element) {
        if (position < data.size()){
            isNewLayout(element);
            data.add(position, element);
            //更新数据集用notifyItemInserted(position)与notifyItemRemoved(position) 否则没有动画效果。
            //首个Item位置做增加操作
            notifyItemInserted(position);
            compatibilityDataSizeChanged(1);
        }else {
            addNewItem(element);
        }
    }

    /**
     * 在指定位置添加多个item，如果position越界则添加到列表最后
     * 示例：mAdapter.addData(1, listOf(Person("xin","1"),Person("xin","2")))
     * @param position 插入位置
     * @param newData 多个item的集合
     */
    public void addNewData(@IntRange(from = 0) int position, @NonNull Collection<? extends T> newData) {
        if (position < data.size()){
            isNewLayout(newData);
            data.addAll(position, newData);
            notifyItemRangeInserted(position, newData.size());
            compatibilityDataSizeChanged(newData.size());
        }else {
            addNewData(newData);
        }
    }

    /**
     * 在列表最后添加多个item
     * 示例：mAdapter.addData(listOf(Person("xin","1"),Person("xin","2")))
     * @param newData 多个item的集合
     */
    public void addNewData(@NonNull Collection<? extends T> newData) {
        isNewLayout(newData);
        data.addAll(newData);
        notifyItemRangeInserted(data.size() - newData.size() , newData.size());
        compatibilityDataSizeChanged(newData.size());
    }

    /**
     * 删除一个指定位置的item，如果position越界则无效
     * @param position 指定item的位置
     */
    public void removeItem(@IntRange(from = 0) int position) {
        if (position < data.size()){
            data.remove(position);
            notifyItemRemoved(position);
            compatibilityDataSizeChanged(0);
            notifyItemRangeChanged(position, data.size() - position);
        }
    }

    /**
     * 修改某个item的数据，如果position越界则无效
     * @param position item位置
     * @param element 要修改的item数据
     */
    public void replaceItem(@IntRange(from = 0) int position, @NonNull T element) {
        if (position < data.size()){
            isNewLayout(element);
            data.set(position, element);
            notifyItemChanged(position);
        }
    }

    /**
     * 修改所有item的数据
     * @param newData 更新item数据的集合
     */
    public void replaceData(@NonNull Collection<? extends T> newData) {
        // 不是同一个引用才清空列表
        if (newData != data) {
            isNewLayout(newData);
            data.clear();
            data.addAll(newData);
        }
        notifyDataSetChanged();
    }

    private void compatibilityDataSizeChanged(int size) {
        int dataSize = data == null ? 0 : data.size();
        if (dataSize == size) {
            notifyDataSetChanged();
        }
    }

    /**
     * 渐显动画，使用 {@link #openLoadAnimation}
     */
    public static final int ALPHAIN = 0x00000001;
    /**
     * 缩放动画，使用 {@link #openLoadAnimation}
     */
    public static final int SCALEIN = 0x00000002;
    /**
     * 从下到上动画，使用 {@link #openLoadAnimation}
     */
    public static final int SLIDEIN_BOTTOM = 0x00000003;
    /**
     * 从左到右动画，使用 {@link #openLoadAnimation}
     */
    public static final int SLIDEIN_LEFT = 0x00000004;
    /**
     * 从右到左动画，使用 {@link #openLoadAnimation}
     */
    public static final int SLIDEIN_RIGHT = 0x00000005;

    /**
     * 五种默认动画类型
     */
    @IntDef({ALPHAIN, SCALEIN, SLIDEIN_BOTTOM, SLIDEIN_LEFT, SLIDEIN_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    private  @interface AnimationType {
    }

    /**
     * 是否开启动画标志，默认不打开
     */
    private boolean mOpenAnimationEnable = false;

    /**
     * 设置是否开启动画
     * @param mOpenAnimationEnable true为开启
     */
    public void isOpenAnimationEnable(boolean mOpenAnimationEnable){
        this.mOpenAnimationEnable = mOpenAnimationEnable;
    }

    /**
     * 自定义动画基类
     */
    private BaseAnimation mCustomAnimation;
    public interface BaseAnimation {
        Animator[] getAnimators(View view);
    }

    /**
     * 自定义的默认动画
     */
    private BaseAnimation mSelectAnimation;

    /**
     * 使用默认的五种动画
     * 1.开启动画，2.使用自定义的默认动画，3.根据animationType选中动画类型
     * @param animationType ALPHAIN, SCALEIN, SLIDEIN_BOTTOM, SLIDEIN_LEFT, SLIDEIN_RIGHT
     */
    public void openLoadAnimation(@AnimationType int animationType) {
        this.mOpenAnimationEnable = true;
        mCustomAnimation = null;
        switch (animationType) {
            case ALPHAIN:
                mSelectAnimation = new AlphaInAnimation();
                break;
            case SCALEIN:
                mSelectAnimation = new ScaleInAnimation();
                break;
            case SLIDEIN_BOTTOM:
                mSelectAnimation = new SlideInBottomAnimation();
                break;
            case SLIDEIN_LEFT:
                mSelectAnimation = new SlideInLeftAnimation();
                break;
            case SLIDEIN_RIGHT:
                mSelectAnimation = new SlideInRightAnimation();
                break;
            default:
                break;
        }
    }

    /**
     * 使用自定义动画
     *
     * @param animation ObjectAnimator
     */
    public void openLoadAnimation(BaseAnimation animation) {
        this.mOpenAnimationEnable = true;
        this.mCustomAnimation = animation;
    }

    /**
     * 当列表项出现到可视界面的时候调用
     * @param holder holder
     */
    @Override
    public void onViewAttachedToWindow(@NonNull K holder) {
        super.onViewAttachedToWindow(holder);
        addAnimation(holder);
    }

    /**
     * true加载新数据item的动画才生效，false所有重新加载的item都生效
     * 刷新数据时（如notifyDataSetChanged），true：只有加载新数据的item显示动画,false：所有item显示动画
     */
    private boolean mFirstOnlyEnable = true;
    public void isFirstOnly(boolean firstOnly) {
        this.mFirstOnlyEnable = firstOnly;
    }

    /**
     * 统计有多少个动画
     */
    private int mLastPosition = -1;

    /**
     * 显示动画
     */
    private void addAnimation(RecyclerView.ViewHolder holder) {
        if (mOpenAnimationEnable) {
            if (!mFirstOnlyEnable || holder.getLayoutPosition() > mLastPosition) {
                BaseAnimation animation;
                if (mCustomAnimation != null) {
                    animation = mCustomAnimation;
                } else {
                    animation = mSelectAnimation;
                }
                for (Animator anim : animation.getAnimators(holder.itemView)) {
                    startAnim(anim, holder.getLayoutPosition());
                }
                mLastPosition = holder.getLayoutPosition();
            }
        }
    }

    /**
     * 动画时间
     */
    private int mDuration = 1000;
    public void setDuration(int duration) {
        mDuration = duration;
    }

    /**
     * 插值器,默认速率恒定
     */
    private TimeInterpolator mInterpolator = new LinearInterpolator();
    public void setInterpolator(TimeInterpolator mInterpolator){
        this.mInterpolator = mInterpolator;
    }

    /**
     * 设置动画时间，设置插值器，开启动画
     */
    private void startAnim(Animator anim, int position) {
        anim.setDuration(mDuration).start();
        anim.setInterpolator(mInterpolator);
    }

    /**
     * 获取多布局中指定多布局
     */
    private int getLayoutId(@LayoutRes int viewType) {
        return layouts.get(viewType);
    }

    /**
     * 如果要在适配器中使用BaseViewHolder的子类，
     * 必须覆盖该方法以创建新的ViewHolder。
     */
    protected K createBaseViewHolder(View view) {
        Class temp = getClass();
        Class z = null;
        while (z == null && null != temp) {
            z = getInstancedGenericKClass(temp);
            temp = temp.getSuperclass();
        }
        K k;
        // 泛型擦除会导致z为null
        if (z == null) {
            k = (K) new BaseViewHolder(view);
        } else {
            k = createGenericKInstance(z, view);
        }
        return k != null ? k : (K) new BaseViewHolder(view);
    }

    /**
     * 得到通用参数.
     */
    private Class getInstancedGenericKClass(Class z) {
        Type type = z.getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] types = ((ParameterizedType) type).getActualTypeArguments();
            for (Type temp : types) {
                if (temp instanceof Class) {
                    Class tempClass = (Class) temp;
                    if (BaseViewHolder.class.isAssignableFrom(tempClass)) {
                        return tempClass;
                    }
                } else if (temp instanceof ParameterizedType) {
                    Type rawType = ((ParameterizedType) temp).getRawType();
                    if (rawType instanceof Class && BaseViewHolder.class.isAssignableFrom((Class<?>) rawType)) {
                        return (Class<?>) rawType;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 创建新的ViewHolder
     */
    private K createGenericKInstance(Class z, View view) {
        try {
            Constructor constructor;
            // inner and unstatic class
            if (z.isMemberClass() && !Modifier.isStatic(z.getModifiers())) {
                constructor = z.getDeclaredConstructor(getClass(), View.class);
                constructor.setAccessible(true);
                return (K) constructor.newInstance(this, view);
            } else {
                constructor = z.getDeclaredConstructor(View.class);
                constructor.setAccessible(true);
                return (K) constructor.newInstance(view);
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * item的点击事件/长按事件
     */
    private void bindViewClickListener(final BaseViewHolder baseViewHolder) {
        if (baseViewHolder == null) {
            return;
        }
        final View view = baseViewHolder.itemView;
        //事件
        if (onItemClickListener != null) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(view, baseViewHolder.getLayoutPosition());
                }
            });
        }
        if (onItemLongClickListener != null){
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemLongClickListener.onItemLongClick(view, baseViewHolder.getLayoutPosition());
                    return false;
                }
            });
        }
    }
}
