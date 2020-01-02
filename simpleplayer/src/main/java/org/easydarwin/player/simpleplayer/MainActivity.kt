package org.easydarwin.player.simpleplayer

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.preference.PreferenceManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.TextureView
import android.view.View
import android.widget.Button
import android.widget.EditText

import org.easydarwin.video.EasyPlayerClient

import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date

class MainActivity : AppCompatActivity() {

    private var client: EasyPlayerClient? = null

    //聊天室参数
    private var rv: RecyclerView? = null
    private var et: EditText? = null
    private var btn: Button? = null
    private var socket: Socket? = null
    private var list: ArrayList<MyBean>? = null
    private var adapter: MyAdapter? = null

    private inner class MyHandler : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what == 1) {
                //
                val localPort = socket!!.localPort
                val split = (msg.obj as String).split("//".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()
                if (split[0] == localPort.toString() + "") {
                    val bean = MyBean(split[1], 1, split[2], "我：")
                    list!!.add(bean)
                } else {
                    val bean = MyBean(split[1], 2, split[2], "来自：" + split[0])
                    list!!.add(bean)
                }

                // 向适配器set数据
                adapter!!.setData(list!!)
                rv!!.adapter = adapter
                val manager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
                rv!!.layoutManager = manager
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textureView = findViewById<TextureView>(R.id.texture_view)
        /**
         * 参数说明
         * 第一个参数为Context,第二个参数为KEY
         * 第三个参数为的textureView,用来显示视频画面
         * 第四个参数为一个ResultReceiver,用来接收SDK层发上来的事件通知;
         * 第五个参数为I420DataCallback,如果不为空,那底层会把YUV数据回调上来.
         */
        client = EasyPlayerClient(this, BuildConfig.KEY, textureView, null, null)

        val button = findViewById<Button>(R.id.button)
        val url = findViewById<EditText>(R.id.url)

        button.setOnClickListener { client!!.play(url.text.toString()) }

        //聊天室初始化
        rv = findViewById<View>(R.id.rv) as RecyclerView
        et = findViewById<View>(R.id.et) as EditText
        btn = findViewById<View>(R.id.btn) as Button
        list = ArrayList()
        adapter = MyAdapter(this)

        val handler = MyHandler()

        Thread(Runnable {
            try {
                socket = Socket("134.175.229.251", 10010)
                val inputStream = socket!!.getInputStream()
                val buffer = ByteArray(1024)
                var len: Int
                do{
                    len = inputStream.read(buffer)
                    if(len != -1){
                        val data = String(buffer, 0, len)
                        // 发到主线程中 收到的数据
                        val message = Message.obtain()
                        message.what = 1
                        message.obj = data
                        handler.sendMessage(message)
                    }
                }while (true)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }).start()

        btn!!.setOnClickListener {
            val data = et!!.text.toString()
            Thread(Runnable {
                try {
                    val outputStream = socket!!.getOutputStream()
                    val df = SimpleDateFormat("HH:mm:ss")    //设置日期格式
                    outputStream.write((socket!!.localPort.toString() + "//" + data + "//" + df.format(Date())).toByteArray(charset("utf-8")))
                    outputStream.flush()

                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }).start()
        }
    }
}
