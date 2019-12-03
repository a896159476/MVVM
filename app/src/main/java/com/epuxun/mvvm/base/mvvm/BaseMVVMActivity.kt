package com.epuxun.mvvm.base.mvvm

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkRequest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.epuxun.mvvm.utli.http.NetworkCallbackImpl
import java.io.File
import java.util.*

abstract class BaseMVVMActivity<T : ViewModel> : AppCompatActivity() {

    //下载的apk的文件名
    private val apkName = "mvvm.apk"
    private var apkId = -1L
    private var downloadManager:DownloadManager? = null

    private val loadDialog by lazy {
        val dialog = LoadDialog
        dialog.isCancelable = false
        dialog
    }

    private var onMeasureSizeCallback: OnMeasureSizeCallback? = null
    private lateinit var views: Array<out View>

    private var connectivityManager: ConnectivityManager? = null
    private val networkCallback by lazy {
        NetworkCallbackImpl
    }

    private var receiver: BroadcastReceiver? = null
    private var externalReceiver: BroadcastReceiver? = null

    //获取ViewModel,implementation 'androidx.lifecycle:lifecycle-extensions:2.2.0-rc02'
    val viewModel by lazy {
        ViewModelProvider(this).get(getViewModel())
    }

    abstract fun getLayoutResID(): Int

    abstract fun addObserver(): LifecycleObserver?

    abstract fun getViewModel(): Class<T>

    abstract fun onCreate()

    override fun onCreate(savedInstanceState: Bundle?) {
        //防止截屏攻击风险
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        //仿系统自带短信界面 可以完全漂浮在软键盘之上
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
        super.onCreate(savedInstanceState)

        setContentView(getLayoutResID())
        //绑定组件的声明周期
        addObserver()?.let {
            lifecycle.addObserver(it)
        }

        onCreate()
    }

    /**
     * 显示加载Dialog
     */
    fun showLoadDialog() {
        loadDialog.show(supportFragmentManager, localClassName)
    }

    /**
     * 关闭加载Dialog
     */
    fun dismissLoadDialog() {
        loadDialog.dismiss()
    }

    /**
     * EditText的值动态赋予MutableLiveData
     */
    fun setEditTextMutableLiveData(et: EditText, liveData: MutableLiveData<String>) {
        et.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                liveData.value = s.toString()
            }
        })
    }

    /**
     * 获取控件大小接口
     */
    interface OnMeasureSizeCallback {
        fun getMeasureSize(view: View)
    }

    /**
     * 设置获取控件大小的回调
     */
    fun setOnMeasureSizeCallback(onMeasureSizeCallback: OnMeasureSizeCallback, vararg views: View) {
        this.onMeasureSizeCallback = onMeasureSizeCallback
        this.views = views
    }

    /**
     * 获取焦点时调用获取控件大小的回调
     */
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            onMeasureSizeCallback?.let { onMeasureSizeCallback ->
                for (view in views) {
                    onMeasureSizeCallback.getMeasureSize(view)
                }
            }
        }
        //沉浸状态栏
//        if (hasFocus && Build.VERSION.SDK_INT >= 21) {
//            val decorView = window.decorView
//            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//            window.navigationBarColor = Color.TRANSPARENT//设置虚拟按键透明
//            window.statusBarColor = Color.TRANSPARENT//设置状态栏透明
//        } else if (hasFocus && Build.VERSION.SDK_INT >= 19) {
//            val decorView = window.decorView
//            decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                    or View.SYSTEM_UI_FLAG_FULLSCREEN
//                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
//        }
    }

    /**
     * 注册全局广播，关闭Activity时会自动注销本地广播
     */
    fun registerExternalReceiver(receiver: BroadcastReceiver, vararg actions: String) {
        this.externalReceiver = receiver
        val filter = IntentFilter()
        for (action in actions) {
            filter.addAction(action)
        }
        registerReceiver(receiver, filter)
    }

    /**
     * 自动注销全局广播
     */
    private fun unregisterExternalReceiver() {
        externalReceiver?.let {
            unregisterReceiver(it)
        }
    }

    /**
     * 安装apk Dialog
     */
    fun showInstallationDialog(url: String,requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (packageManager.canRequestPackageInstalls()) {
                showDialogN(url)
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("检测到新版本")
                    .setMessage("安装新版本需要打开未知来源权限，请去设置中开启权限")
                    .setPositiveButton("设置") { _, _ ->
                        val selfPackageUri = Uri.parse("package:$packageName")
                        val intentPermission =
                            Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, selfPackageUri)
                        //8.0以上跳转至“安装未知应用”权限界面，引导用户开启权限
                        startActivityForResult(intentPermission, requestCode)
                    }
                    .setNegativeButton("取消") { _, _ -> }
                    .setCancelable(false)
                    .show()
            }
        } else {
            showDialogN(url)
        }
    }

    /**
     * 7.0以下新版本提示
     */
    private fun showDialogN(url:String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("提示")
            .setMessage("检测到新版本是否更新")
            .setPositiveButton("更新") { _, _ -> apkUpdate(url) }
            .setNegativeButton("取消") { _, _ -> }
            .setCancelable(false)
            .show()
    }

    /**
     * 下载apk
     */
    private fun apkUpdate(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))

        //设置什么网络情况下可以下载
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)

        //设置通知栏的标题
        request.setTitle("版本更新")
        //设置通知栏的message
        request.setDescription("正在进行版本更新.....")
        //是否在通知栏显示下载进度，默认显示
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        //设置文件存放目录
        request.setDestinationInExternalFilesDir(
            application,
            Environment.DIRECTORY_DOWNLOADS,
            apkName
        )
        //获取系统服务
        downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        //进行下载
        downloadManager?.let {
            apkId = it.enqueue(request)
        }
    }

    /**
     * 取消下载apk
     */
    fun removeApk(){
        downloadManager?.remove(apkId)
    }

    /**
     * 安装apk
     */
    fun installation() {
        val path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath
        val file = File(path, apkName)

        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val authority = "$packageName.fileProvider"
            //生成File URI
            val apkUri = FileProvider.getUriForFile(this, authority, file)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            //Uri.fromFile(file) 生成File URI
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive")
        }

        startActivity(intent)
    }

    /**
     * 注册网络监听
     */
    fun registerNetworkMonitoring() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            //需要<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />权限
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                connectivityManager?.registerDefaultNetworkCallback(networkCallback)
            } else {
                connectivityManager?.registerNetworkCallback(
                    NetworkRequest.Builder().build(),
                    networkCallback
                )
            }
        }
    }

    /**
     * 注销网络监听
     */
    private fun unregisterNetworkMonitoring() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivityManager?.unregisterNetworkCallback(networkCallback)
        }
    }

    /**
     * 重写startActivity
     *
     * @param clz 跳转页面的class
     */
    fun startActivity(clz: Class<*>) {
        val intent = Intent(this, clz)
        startActivity(intent)
        enterTransitionAnim()
    }

    /**
     * 重写startActivity
     *
     * @param clz    跳转页面的class
     * @param bundle 携带的数据
     */
    fun startActivity(clz: Class<*>, bundle: Bundle) {
        val intent = Intent(this, clz)
        intent.putExtras(bundle)
        startActivity(intent)
        enterTransitionAnim()
    }

    fun startActivityForResult(clz: Class<*>, requestCode: Int) {
        val intent = Intent(this, clz)
        startActivityForResult(intent, requestCode)
        enterTransitionAnim()
    }

    fun startActivityForResult(clz: Class<*>, requestCode: Int, bundle: Bundle) {
        val intent = Intent(this, clz)
        startActivityForResult(intent, requestCode, bundle)
        enterTransitionAnim()
    }

    fun getIntentBundle(): Bundle? {
        return intent.extras
    }

    /**
     * 进入Activity过渡动画
     */
    private fun enterTransitionAnim() {
        overridePendingTransition(android.R.anim.fade_out, android.R.anim.fade_in)
    }

    /**
     * 退出Activity过渡动画
     */
    private fun quitTransitionAnim() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // Fragment 逐个出栈
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    override fun finish() {
        super.finish()
        quitTransitionAnim()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterExternalReceiver()
        unregisterNetworkMonitoring()
    }
}