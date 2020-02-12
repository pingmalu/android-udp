package com.malu.xmllayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private TextView ipTextView = null;
    private TextView nameTextView = null;
    private NetworkInfo mActiveNetInfo = null;
    private ConnectivityManager mConnectivityManager = null;
    private ListView mylistview;
    private ArrayList<String> list = new ArrayList<>();
    private String myIpaddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.settings, new SettingsFragment())
//                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        final EditText ipadd = findViewById(R.id.editText);
//        Log.i("kuku",ipadd.getText().toString());

        final SharedPreferences sp = getSharedPreferences("malu", MODE_PRIVATE);
        String ipadd_str = sp.getString("malu", null);
        ipadd.setText(ipadd_str);

        Button bt = findViewById(R.id.button7);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("malu", ipadd.getText().toString());
                editor.apply();
                // 传值
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
//                intent.putExtra("MainActivity", ipadd.getText().toString());
                startActivity(intent);
                finish();
            }
        });


        nameTextView = findViewById(R.id.nametextView);
        ipTextView = findViewById(R.id.iptextView);

        mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);//获取系统的连接服务
        mActiveNetInfo = mConnectivityManager.getActiveNetworkInfo();//获取网络连接的信息
        myIpaddress = getIPAddress();
        if (mActiveNetInfo != null && !myIpaddress.equals("")) {
            setUpInfo();
            Log.i("kuku", myIpaddress);
            discover(getIPAddress());
            readArp();
            init();
        }


    }

//    private String[] data = new String[0];

    //往字符串数组追加新数据
    private static String[] insert(String[] arr, String str) {
        int size = arr.length;  //获取数组长度
        String[] tmp = new String[size + 1];  //新建临时字符串数组，在原来基础上长度加一
        for (int i = 0; i < size; i++) {  //先遍历将原来的字符串数组数据添加到临时字符串数组
            tmp[i] = arr[i];
        }
        tmp[size] = str;  //在最后添加上需要追加的数据
        return tmp;  //返回拼接完成的字符串数组
    }


    /**
     * 初始化数据
     */
    private void init() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                SettingsActivity.this, android.R.layout.simple_list_item_1, list);
        mylistview = findViewById(R.id.list);
        mylistview.setAdapter(adapter);

        mylistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
//                if(list.get(arg2).equals("LinearLayout"))
//                {
//                    Intent intent = new Intent("com.wps.android.LINEARLAYOUT");
//                    startActivity(intent);
//                }
                Log.i("kuku", list.get(arg2));
                String[] split1 = list.get(arg2).split(" \\| ");
//                Log.i("kuku", split1[1]);
//                Log.i("kuku", Arrays.toString(split1));
                final EditText ipadd = findViewById(R.id.editText);
                ipadd.setText(split1[1]);

            }

        });
    }

    public void setUpInfo() {
        if (mActiveNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            nameTextView.setText("网络类型：WIFI");
            ipTextView.setText("IP地址：" + getIPAddress());
        } else if (mActiveNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
            nameTextView.setText("网络类型：3G/4G");
            ipTextView.setText("IP地址：" + getIPAddress());
        } else {
            nameTextView.setText("网络类型：未知");
            ipTextView.setText("IP地址：");
        }
    }

    public String getIPAddress() {
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if ((info.getType() == ConnectivityManager.TYPE_MOBILE) || (info.getType() == ConnectivityManager.TYPE_WIFI)) {//当前使用2G/3G/4G网络
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                if (inetAddress.getHostAddress().equals("10.0.2.15")) {
                                    continue;
                                }
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }
        } else { //当前无网络连接,请在设置中打开网络
            return null;
        }
        return null;
    }

    public static String getIpAddressString() {
        try {
            for (Enumeration<NetworkInterface> enNetI = NetworkInterface
                    .getNetworkInterfaces(); enNetI.hasMoreElements(); ) {
                NetworkInterface netI = enNetI.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = netI
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }

    // 根据ip 网段去 发送arp 请求
    private void discover(String ip) {
        String newip = "";
        if (!ip.equals("")) {
            String ipseg = ip.substring(0, ip.lastIndexOf(".") + 1);
            for (int i = 2; i < 255; i++) {
                newip = ipseg + i;
                if (newip.equals(ip)) continue;
                Thread ut = new UDPThread(newip);
                ut.start();
            }
        }
    }

    // UDPThread
    public class UDPThread extends Thread {
        private String target_ip = "";

        public final byte[] NBREQ = {(byte) 0x82, (byte) 0x28, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x1,
                (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x20, (byte) 0x43, (byte) 0x4B,
                (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41,
                (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41,
                (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41,
                (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x0, (byte) 0x0, (byte) 0x21, (byte) 0x0, (byte) 0x1};

        public static final short NBUDPP = 137;

        public UDPThread(String target_ip) {
            this.target_ip = target_ip;
        }

        @Override
        public synchronized void run() {
            if (target_ip == null || target_ip.equals("")) return;
            DatagramSocket socket = null;
            InetAddress address = null;
            DatagramPacket packet = null;
            try {
                address = InetAddress.getByName(target_ip);
                packet = new DatagramPacket(NBREQ, NBREQ.length, address, NBUDPP);
                socket = new DatagramSocket();
                socket.setSoTimeout(200);
                socket.send(packet);
                socket.close();
            } catch (SocketException se) {
            } catch (UnknownHostException e) {
            } catch (IOException e) {
            } finally {
                if (socket != null) {
                    socket.close();
                }
            }
        }
    }

    private void readArp() {
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader("/proc/net/arp"));
            String last_str = "";
            String line = "";
            String ip = "";
            String flag = "";
            String mac = "";
            while ((line = br.readLine()) != null) {
                try {
                    line = line.trim();
                    if (line.length() < 63) continue;
                    if (line.toUpperCase(Locale.US).contains("IP")) continue;
                    ip = line.substring(0, 17).trim();
                    flag = line.substring(29, 32).trim();
                    mac = line.substring(41, 63).trim();
                    if (mac.contains("00:00:00:00:00:00")) continue;
                    //执行数据添加
                    String isesp = "";
                    if (mac.contains("2C:F4:32:14:78:76")) isesp = "小方车 | ";
                    if (mac.contains("02:15:b2:00:01:00")) isesp = "坦克 | ";
                    last_str = mac + " | " + ip + " | " + isesp;
                    list.add(last_str);
//                    data = insert(data, last_str);
//                    Log.e("kuku", "readArp: mac= " + mac + " ; ip= " + ip + " ;flag= " + flag + isesp);
                } catch (Exception e) {
                }
            }
            br.close();

        } catch (Exception e) {
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }
}