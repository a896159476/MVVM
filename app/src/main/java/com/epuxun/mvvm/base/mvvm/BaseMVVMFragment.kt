package com.epuxun.mvvm.base.mvvm

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.view.ViewTreeObserver
import com.epuxun.mvvm.base.mvvm.BaseMVVMActivity.OnMeasureSizeCallback
import android.os.Build
import androidx.annotation.RequiresApi

abstract class BaseMVVMFragment<T : ViewModel> : Fragment() {

    private lateinit var mActivity: BaseMVVMActivity<T>

    val viewModel by lazy {
        ViewModelProvider(activity!!).get(getViewModel())
    }

    private var onMeasureSizeCallback: OnMeasureSizeCallback? = null
    private lateinit var views: Array<out View>

    abstract fun getLayoutResID(): Int

    abstract fun getViewModel(): Class<T>

    abstract fun onViewCreated(view: View)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(getLayoutResID(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onViewCreated(view)
    }

    /**
     * 显示加载Dialog
     */
    fun showLoadDialog() {
        mActivity.showLoadDialog()
    }

    /**
     * 关闭加载Dialog
     */
    fun dismissLoadDialog() {
        mActivity.dismissLoadDialog()
    }

    /**
     * EditText的值动态赋予MutableLiveData
     */
    fun setEditTextMutableLiveData(et: EditText, liveData: MutableLiveData<String>) {
        mActivity.setEditTextMutableLiveData(et, liveData)
    }

    /**
     * 设置获取控件大小的回调
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    fun setOnMeasureSizeCallback(onMeasureSizeCallback: OnMeasureSizeCallback, vararg views: View) {
        this.onMeasureSizeCallback = onMeasureSizeCallback
        this.views = views
    }

    /**
     * fragment显示时获取控件大小
     */
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden && onMeasureSizeCallback != null) {
            for (view in views) {
                getMeasureSize(view)
            }
        }
    }

    /**
     * 获取控件大小
     */
    private fun getMeasureSize(view: View) {
        val vto = view.viewTreeObserver
        vto.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                vto.removeOnGlobalLayoutListener(this)
                onMeasureSizeCallback?.getMeasureSize(view)
            }
        })
    }

    /**
     * 重写startActivity
     *
     * @param clz 跳转页面的class
     */
    fun startActivity(clz: Class<*>) {
        mActivity.startActivity(clz)
    }

    /**
     * 重写startActivity
     *
     * @param clz    跳转页面的class
     * @param bundle 携带的数据
     */
    fun startActivity(clz: Class<*>, bundle: Bundle) {
        mActivity.startActivity(clz, bundle)
    }

    fun startActivityForResult(clz: Class<*>, requestCode: Int) {
        mActivity.startActivityForResult(clz, requestCode)
    }

    fun startActivityForResult(clz: Class<*>, requestCode: Int, bundle: Bundle) {
        mActivity.startActivityForResult(clz, requestCode, bundle)
    }

    fun getIntentBundle(): Bundle? {
        return mActivity.getIntentBundle()
    }

    /**
     * 获取宿主Activity
     */
    fun getHoldingActivity(): BaseMVVMActivity<T> {
        return mActivity
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mActivity = context as BaseMVVMActivity<T>
    }
}