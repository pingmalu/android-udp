package com.malu.xmllayout;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.telecom.Call;
import android.util.Log;
import android.view.Gravity;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private DatagramSocket datagramSocket;
    public boolean S1 = false;
    public boolean S2 = false;
    public boolean isOnLongClick = false;
    PlusThread plusThread;
    public boolean isOnLongClick_lr = false;
    LrThread lrThread;
    public String IP = "";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // 获取远程ip配置
        final SharedPreferences sp = getSharedPreferences("malu", MODE_PRIVATE);
        IP = sp.getString("malu", null);

        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            //e.printStackTrace();
            Log.v("kuku", e.toString());
        }

        setBtnListen((Button) findViewById(R.id.button));
        setBtnListen((Button) findViewById(R.id.button2));
        setBtnListen((Button) findViewById(R.id.button3));
        setBtnListen((Button) findViewById(R.id.button4));
        setBtnListen((Button) findViewById(R.id.button5));
        setBtnListen((Button) findViewById(R.id.button6));

        setSwitchListen((Switch) findViewById(R.id.switch1));
        setSwitchListen((Switch) findViewById(R.id.switch2));

        TextView tv = findViewById(R.id.textView);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // TAB按键
        Button bt7 = findViewById(R.id.button7);
        bt7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, FullscreenActivity.class);
//                startActivity(intent);
//                finish();
                Button bt = findViewById(R.id.button);
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT);
//                params.gravity = Gravity.CENTER;
/*                LinearLayout.LayoutParams layoutParams = (.LayoutParams)bt.getLayoutParams();
                layoutParams.setMargins(100,20,10,5);//4个参数按顺序分别是左上右下
                bt.setLayoutParams(layoutParams);*/
//                FrameLayout frameLayout = new FrameLayout(this);
//                bt.setPadding(0, 0, 0, 0);
//                bt.setAlpha(0);
//                bt.();

            }
        });

    }

    @SuppressLint("ClickableViewAccessibility")
    public void setBtnListen(final Button btn) {
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String btn_name = btn.getText().toString();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (btn_name.equals("UP") || btn_name.equals("DOWN")) {
                        if (!isOnLongClick) {
                            plusThread = new PlusThread(btn_name);
                            plusThread.start();
                            isOnLongClick = true;
                        }
                    } else {
                        if (!isOnLongClick_lr) {
                            lrThread = new LrThread(btn_name);
                            lrThread.start();
                            isOnLongClick_lr = true;
                        }
                    }
                    Log.i("kuku", btn_name + ":1");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (btn_name.equals("UP") || btn_name.equals("DOWN")) {
                        isOnLongClick = false;
                    } else {
                        isOnLongClick_lr = false;
                    }
//                    if (plusThread != null) {
//                        isOnLongClick = false;
//                    }
                    send_msg(btn_name + ":0");
                    Log.i("kuku", btn_name + ":0");
                }
//                else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                    Log.i("kuku", btn.getText() + ":1");
//                }
                return true;
            }
        });
    }

    // 上下线程
    class PlusThread extends Thread {

        private String name;

        PlusThread(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            while (isOnLongClick) {
                try {
                    send_msg(name + ":1");
                    Thread.sleep(200);
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                }
                super.run();
            }
        }
    }

    // 左右线程
    class LrThread extends Thread {

        private String name;

        LrThread(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            while (isOnLongClick_lr) {
                try {
                    send_msg(name + ":1");
                    Thread.sleep(200);
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                }
                super.run();
            }
        }
    }

    public void setSwitchListen(final Switch sw) {
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Log.i("kuku", sw.getText() + ":1");
                    send_msg(sw.getText() + ":1");
                } else {
                    Log.i("kuku", sw.getText() + ":0");
                    send_msg(sw.getText() + ":0");
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int buttonPress, KeyEvent event) {
        Log.i("kuku", buttonPress + "");
//        show(buttonPress + "");
        boolean handled = true;
        String btn_name = "";
        switch (buttonPress) {
            case 106:
            case 98:
            case KeyEvent.KEYCODE_DPAD_UP:
                btn_name = "UP";
                break;
            case 107:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                btn_name = "DOWN";
                break;
            case 188:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                btn_name = "LEFT";
                break;
            case 189:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                btn_name = "RIGHT";
                break;
            case KeyEvent.KEYCODE_BUTTON_X:
                btn_name = "A";
                break;
            case KeyEvent.KEYCODE_BUTTON_B:
                btn_name = "B";
                break;
            case 25:
            case KeyEvent.KEYCODE_BUTTON_A:
                S1 = !S1;
                if (S1) {
                    send_msg("S1:1");
                } else {
                    send_msg("S1:0");
                }
                return handled;
            case 24:
            case KeyEvent.KEYCODE_BUTTON_Y:
                S2 = !S2;
                if (S2) {
                    send_msg("S2:1");
                } else {
                    send_msg("S2:0");
                }
                return handled;
            default:
                return handled;
        }
        if (btn_name.equals("UP") || btn_name.equals("DOWN")) {
            if (!isOnLongClick) {
                plusThread = new PlusThread(btn_name);
                plusThread.start();
                isOnLongClick = true;
            }
        } else {
            if (!isOnLongClick_lr) {
                lrThread = new LrThread(btn_name);
                lrThread.start();
                isOnLongClick_lr = true;
            }
        }
        return handled;
//        return super.onKeyDown(buttonPress, event);
    }

    @Override
    public boolean onKeyUp(int buttonPress, KeyEvent event) {
        Log.i("kuku", buttonPress + "up");
        boolean handled = true;
        String btn_name = "";
        switch (buttonPress) {
            case 106:
            case 98:
            case KeyEvent.KEYCODE_DPAD_UP:
                btn_name = "UP";
                break;
            case 107:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                btn_name = "DOWN";
                break;
            case 188:
            case KeyEvent.KEYCODE_DPAD_LEFT:
                btn_name = "LEFT";
                break;
            case 189:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                btn_name = "RIGHT";
                break;
            case KeyEvent.KEYCODE_BUTTON_X:
                btn_name = "A";
                break;
            case KeyEvent.KEYCODE_BUTTON_B:
                btn_name = "B";
                break;
            default:
                return handled;
        }
        if (btn_name.equals("UP") || btn_name.equals("DOWN")) {
            isOnLongClick = false;
        } else {
            isOnLongClick_lr = false;
        }
        send_msg(btn_name + ":0");
        return handled;
//        return super.onKeyUp(buttonPress, event);
    }

/*    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {

        // Check that the event came from a game controller
        if ((event.getSource() & InputDevice.SOURCE_JOYSTICK) ==
                InputDevice.SOURCE_JOYSTICK &&
                event.getAction() == MotionEvent.ACTION_MOVE) {

            // Process all historical movement samples in the batch
            final int historySize = event.getHistorySize();

            // Process the movements starting from the
            // earliest historical position in the batch
            for (int i = 0; i < historySize; i++) {
                // Process the event at historical position i
                processJoystickInput(event, i);
            }

            // Process the current movement sample in the batch (position -1)
            processJoystickInput(event, -1);
            return true;
        }
        return super.onGenericMotionEvent(event);
    }

    private static float getCenteredAxis(MotionEvent event,
                                         InputDevice device, int axis, int historyPos) {
        final InputDevice.MotionRange range =
                device.getMotionRange(axis, event.getSource());

        // A joystick at rest does not always report an absolute position of
        // (0,0). Use the getFlat() method to determine the range of values
        // bounding the joystick axis center.
        if (range != null) {
            final float flat = range.getFlat();
            final float value =
                    historyPos < 0 ? event.getAxisValue(axis) :
                            event.getHistoricalAxisValue(axis, historyPos);

            // Ignore axis values that are within the 'flat' region of the
            // joystick axis center.
            if (Math.abs(value) > flat) {
                return value;
            }
        }
        return 0;
    }

    private void processJoystickInput(MotionEvent event,
                                      int historyPos) {

        InputDevice inputDevice = event.getDevice();

        // Calculate the horizontal distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat axis, or the right control stick.
        float x = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_X, historyPos);
        if (x == 0) {
            x = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_HAT_X, historyPos);
        }
        if (x == 0) {
            x = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_Z, historyPos);
        }

        // Calculate the vertical distance to move by
        // using the input value from one of these physical controls:
        // the left control stick, hat switch, or the right control stick.
        float y = getCenteredAxis(event, inputDevice,
                MotionEvent.AXIS_Y, historyPos);
        if (y == 0) {
            y = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_HAT_Y, historyPos);
        }
        if (y == 0) {
            y = getCenteredAxis(event, inputDevice,
                    MotionEvent.AXIS_RZ, historyPos);
        }

        // Update the ship object based on the new x and y values
        show("x:"+x+" y:"+y,0,200);
    }*/

/*
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
*/

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
            InetAddress inet_address = InetAddress.getByName(IP);

            //UDPデータグラムは DatagramPacket クラスで表現する
            DatagramPacket datagram_packet = new DatagramPacket(data,
                    data.length, inet_address, 9999);

            //DatagramSocket datagram_socket = new DatagramSocket();
            //ネットワーク入出力口はソケットとして抽象化される
            if (null != datagramSocket) {
                //datagramSocket.close();
                datagramSocket = null;
                datagramSocket = new DatagramSocket();
                datagramSocket.send(datagram_packet);
            }
            //DatagramSocket は DatagramPacket を渡されると
            //指定された宛先アドレスに UDP データグラムとして送出する。
        } catch (IOException io_exception) {
            //問題が起きたら例外を捉えてログに出力
            Log.v("kuku", io_exception.toString());
        }
    }

    public void bt(View view) {
        Button b = (Button) view;
        String text = b.getText().toString();
        this.show(text);
//        this.send_msg(text);
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
