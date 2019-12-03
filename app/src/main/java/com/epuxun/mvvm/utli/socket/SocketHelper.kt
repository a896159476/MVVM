package com.epuxun.mvvm.utli.socket

import androidx.annotation.IntDef
import com.epuxun.mvvm.utli.showShortToast
import java.io.IOException
import java.io.PrintWriter
import java.lang.Exception
import java.net.Socket
import java.net.UnknownHostException


/**
 * Socket帮助类，运行在子线程
 */
object SocketHelper {

    const val TEXT = 0
    const val LINES = 1

    @IntDef(TEXT, LINES)
    annotation class ReadState

    lateinit var socket: Socket

    fun initSocket():Boolean{
        try {
            socket = Socket("192.168.31.242", 1008)
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: UnknownHostException) {//ip地址错误
            e.printStackTrace()
        } catch (e: IllegalArgumentException) {//端口错误
            e.printStackTrace()
        } catch (e: SecurityException) {//安全错误
            e.printStackTrace()
        }
        return false
    }

    /**
     * 是否重新连接
     */
    private fun isReconnect() {
        if (socket.isClosed) {
            initSocket()
        }
    }

    /**
     * 向服务器发送信息
     */
    fun sendSocket(string: String) {
        isReconnect()

        if (!socket.isOutputShutdown) {
            val out = socket.getOutputStream().bufferedWriter()
            val pw = PrintWriter(out, true)
            pw.println(string)
            pw.close()
        }
    }

    fun socketReadString(): String {
        isReconnect()

        if (socket.isInputShutdown) {
            val sb = StringBuffer()
            socket.getInputStream().bufferedReader().forEachLine {
                sb.append(it)
            }
            return sb.toString()
        }
        return ""
    }

    /**
     * 将读取的数据存入String，处理完成后会关闭流，不能读取大文件
     */
    fun socketReadText(): String {
        val br = socket.getInputStream().bufferedReader()
        val text = br.readText()
        br.close()
        return text
    }

    /**
     * 将读取的数据存入List，处理完成后会关闭流，不能读取大文件
     */
    fun socketReadLines(): List<String> {
        return socket.getInputStream().bufferedReader().readLines()
    }

}