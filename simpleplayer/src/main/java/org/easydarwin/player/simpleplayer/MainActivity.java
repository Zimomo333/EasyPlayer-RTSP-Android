package org.easydarwin.player.simpleplayer;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.easydarwin.video.EasyPlayerClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity{

    private EasyPlayerClient client;

    //聊天室参数
    private RecyclerView rv;
    private EditText et;
    private Button btn;
    private Socket socket;
    private ArrayList<MyBean> list;
    private MyAdapter adapter;

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                //
                int localPort = socket.getLocalPort();
                String[] split = ((String) msg.obj).split("//");
                if (split[0].equals(localPort + "")) {
                    MyBean bean = new MyBean(split[1],1,split[2],"我：");
                    list.add(bean);
                } else {
                    MyBean bean = new MyBean(split[1],2,split[2],("来自：" + split[0]));
                    list.add(bean);
                }

                // 向适配器set数据
                adapter.setData(list);
                rv.setAdapter(adapter);
                LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.VERTICAL, false);
                rv.setLayoutManager(manager);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextureView textureView = findViewById(R.id.texture_view);
        /**
         * 参数说明
         * 第一个参数为Context,第二个参数为KEY
         * 第三个参数为的textureView,用来显示视频画面
         * 第四个参数为一个ResultReceiver,用来接收SDK层发上来的事件通知;
         * 第五个参数为I420DataCallback,如果不为空,那底层会把YUV数据回调上来.
         */
        client = new EasyPlayerClient(this, BuildConfig.KEY, textureView, null, null);

        Button button = findViewById(R.id.button);
        final EditText url = findViewById(R.id.url);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                client.play(url.getText().toString());
            }
        });

        //聊天室初始化
        rv = (RecyclerView) findViewById(R.id.rv);
        et = (EditText) findViewById(R.id.et);
        btn = (Button) findViewById(R.id.btn);
        list = new ArrayList<>();
        adapter = new MyAdapter(this);

        final Handler handler = new MyHandler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("134.175.229.251", 10010);
                    InputStream inputStream = socket.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        String data = new String(buffer, 0, len);
                        // 发到主线程中 收到的数据
                        Message message = Message.obtain();
                        message.what = 1;
                        message.obj = data;
                        handler.sendMessage(message);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String data = et.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OutputStream outputStream = socket.getOutputStream();
                            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");    //设置日期格式
                            outputStream.write((socket.getLocalPort() + "//" + data + "//" + df.format(new Date())).getBytes("utf-8"));
                            outputStream.flush();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }
}
