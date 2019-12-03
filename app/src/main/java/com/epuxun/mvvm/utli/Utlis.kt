@file: JvmName("Utils")

package com.epuxun.mvvm.utli

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import java.io.InputStream
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.annotation.NonNull
import java.io.ByteArrayOutputStream
import java.io.File

lateinit var application: Application

//网络是否连接
private var isNetworkConnected: Boolean? = null

//提前初始化sharedPreferences优化性能
private lateinit var sharedPreferences: SharedPreferences

//8.0以上安装授权的请求code
const val PERMISSION_REQUEST_CODE = 10002

/**
 * MyApp中初始化
 */
fun initApplication(app: Application) {
    application = app
    sharedPreferences =
        application.getSharedPreferences(application.packageName, Context.MODE_PRIVATE)
}

/**
 * 设置网络状态
 */
fun setNetworkConnected(boolean: Boolean){
    isNetworkConnected = boolean
}

/**
 * 检测网络是否连接
 * 如果未注册[registerNetworkMonitoring] 会使用isNetworkConnectedLOLLIPOP
 */
fun isNetworkConnected(): Boolean {
    isNetworkConnected?.let {
        return it
    }
    return isNetworkConnectedLOLLIPOP()
}

/**
 * 5.0以下检测网络是否连接
 */
private fun isNetworkConnectedLOLLIPOP(): Boolean {
    val mConnectivityManager =
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    mConnectivityManager.getActiveNetworkInfo()?.let {
        return it.isAvailable()
    }
    return false
}

/**
 * 获取SharedPreferences
 */
fun sharedPreferences(): SharedPreferences {
    return sharedPreferences
}

/**
 * 保存数据到SharedPreferences
 */
fun putSharedPreferences(vararg anys: Any) {
    sharedPreferences.edit().run {
        for (any in anys) {
            when (any) {
                is String -> this.putString(any, any)
                is Int -> this.putInt("$any", any)
                is Long -> this.putLong("$any", any)
                is Float -> this.putFloat("$any", any)
                is Boolean -> this.putBoolean("$any", any)
            }
        }
        apply()
    }
}

/**
 * 从流中获取Bitmap
 */
fun getBitmap(@NonNull inputStream: InputStream): Bitmap {
    inputStream.buffered().use {
        return BitmapFactory.decodeStream(it)
    }
}

/**
 * 从资源文件中获取Bitmap
 */
fun getBitmap(@NonNull id: Int): Bitmap {
    return getBitmap(application.resources.openRawResource(id))
}

/**
 * 从文件中获取Bitmap
 */
fun getBitmap(@NonNull file: File): Bitmap {
    return getBitmap(file.inputStream())
}

/**
 * 从流中获取RGB的Bitmap
 */
fun getRGBBitmap(@NonNull inputStream: InputStream): Bitmap {
    val options = BitmapFactory.Options()
    options.inPreferredConfig = Bitmap.Config.RGB_565
    inputStream.buffered().use {
        return BitmapFactory.decodeStream(it, null, options)!!
    }
}

/**
 * 从资源文件中获取RGB的Bitmap
 */
fun getRGBBitmap(@NonNull id: Int): Bitmap {
    return getRGBBitmap(application.resources.openRawResource(id))
}

/**
 * 从文件中获取RGB的Bitmap
 */
fun getRGBBitmap(@NonNull file: File): Bitmap {
    return getRGBBitmap(file.inputStream())
}

/**
 * 从流中获取尺寸压缩的Bitmap
 */
fun getSampleBitmap(@NonNull inputStream: InputStream): Bitmap {
    val options = BitmapFactory.Options()
    //设置仅加载图片的宽高信息不将图片加载的内存中
    options.inJustDecodeBounds = true

    ByteArrayOutputStream().use { byteArrayOutputStream ->
        //拷贝数据到ByteArrayOutputStream
        inputStream.use { inputStream ->
            inputStream.copyTo(byteArrayOutputStream)
        }
        //转为byte数组
        val byte = byteArrayOutputStream.toByteArray()

        BitmapFactory.decodeByteArray(byte, 0, byte.size, options)

        //设置缩放比例
        options.inSampleSize = calculateInSampleSize(options, 400, 400)
        options.inJustDecodeBounds = false

        return BitmapFactory.decodeByteArray(byte, 0, byte.size, options)
    }
}

/**
 * 从资源文件中获取尺寸压缩的Bitmap
 */
fun getSampleBitmap(@NonNull id: Int): Bitmap {
    return getSampleBitmap(application.resources.openRawResource(id))
}

/**
 * 从文件中获取尺寸压缩的Bitmap
 */
fun getSampleBitmap(@NonNull file: File): Bitmap {
    return getSampleBitmap(file.inputStream())
}

/**
 * 获取缩放比例
 *
 * @param options   BitmapFactory.Options
 * @param maxWidth  最大宽度 建议为屏幕宽度
 * @param maxHeight 最大高度 建议为屏幕宽度
 * @return inSampleSize
 */
private fun calculateInSampleSize(
    options: BitmapFactory.Options,
    maxWidth: Int,
    maxHeight: Int
): Int {
    var height = options.outHeight
    var width = options.outWidth
    var inSampleSize = 1
    while (height > maxHeight || width > maxWidth) {
        height = height shr 1
        width = width shr 1
        inSampleSize = inSampleSize shl 1
    }
    return inSampleSize
}

/**
 * 显示长Toast
 *
 * @param msg 显示的信息
 */
fun showLongToast(msg: String) {
    Toast.makeText(application, msg, Toast.LENGTH_LONG).show()
}

/**
 * 显示短Toast
 *
 * @param msg 显示的信息
 */
fun showShortToast(msg: String) {
    Toast.makeText(application, msg, Toast.LENGTH_SHORT).show()
}


