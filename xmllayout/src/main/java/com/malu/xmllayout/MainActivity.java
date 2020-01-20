package com.malu.xmllayout;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private DatagramSocket datagramSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
//        Button button_send_udp = (Button) this.findViewById(R.id.button3);
//        button_send_udp.setOnClickListener(this);
        try {
            datagramSocket = new DatagramSocket();
            Log.v("bbq", "as");
        } catch (SocketException e) {
            //e.printStackTrace();
            Log.v("SendUdp#onCreate", e.toString());
        }
    }

    public void showwebview() {
        //获得控件
        WebView webView = (WebView) findViewById(R.id.mwebview);
        //访问网页
        webView.loadUrl("http://malu.me/");
        webView.goBack();

        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //是否可以后退
//                view.canGoBack();
                //使用WebView加载显示url
                view.loadUrl(url);
                //返回true
                return true;
            }
        });
    }

    private void send_msg(String msg) {
        // 1,创建服务端+端口
//        DatagramSocket datagramSocket = new DatagramSocket();

        // 2,准备数据
//        String msg = "aaa";

        byte[] data = msg.getBytes();

        // 3,打包（发送的地点及端口）
//        DatagramPacket packet = new DatagramPacket(data, data.length, new InetSocketAddress("192.168.3.27", 1219));

        // 4,发送资源
//        datagramSocket.send(packet);

        // 5,关闭资源
//        datagramSocket.close();


        try {
            //IPアドレスは InetAddress クラスで表現する
            InetAddress inet_address = InetAddress.getByName("192.168.11.255");
//            InetAddress inet_address = InetAddress.getByName("192.168.3.27");

            //UDPデータグラムは DatagramPacket クラスで表現する
            DatagramPacket datagram_packet = new DatagramPacket(data,
                    data.length, inet_address, 9999);

            //DatagramSocket datagram_socket = new DatagramSocket();
            //ネットワーク入出力口はソケットとして抽象化される
            if (null != datagramSocket) {
                //datagramSocket.close();
                datagramSocket = null;
                datagramSocket = new DatagramSocket();
            }
            //DatagramSocket は DatagramPacket を渡されると
            //指定された宛先アドレスに UDP データグラムとして送出する。
            datagramSocket.send(datagram_packet);
        } catch (IOException io_exception) {
            //問題が起きたら例外を捉えてログに出力
            Log.v("SensorUdp#", io_exception.toString());
        }
    }

    public void bt(View view) {
        Button b = (Button) view;
        String text = b.getText().toString();
        this.show(text);
        this.send_msg(text);
        switch (text) {
            case "left":
                this.show("左触发");
//                showwebview();
                break;
            case "Button3":
                this.show("后退");
//                try {
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                WebView();
                break;
            default:
                this.show("未知触发" + text, 0, 40);
                Log.i("xiaobao", text);
        }
    }


    public void show(String msg) {
        int x = 0;
        int y = 0;
        show(msg, x, y);
    }

    public void show(String msg, int x, int y) {
        Toast toast = Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP, x, y);
        toast.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button3:
//                send_msg();
                Log.v("SensorUdp#onClick", "ButtonSendDebugMessage");
                break;
        }
    }
}
