package com.example.test_ssdp.first;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.test_ssdp.second.SecondImplementationActivity;
import com.example.test_ssdp.R;

/*import android.support.v7.app.AppCompatActivity;*/
/*import android.support.v7.widget.ListViewCompat;*/


public class FirstImplementationActivity extends Activity implements View.OnClickListener,NetBroadcastReceiver.NetEvent {

    public static final String ADDRESS = "239.255.255.250";
    public static final int PORT = 1900;
    public static final String SL_OK = "HTTP/1.1 200 OK";
    public static final String SL_M_SEARCH = "M-SEARCH * HTTP/1.1";
    public static final String HOST = "Host:" + ADDRESS + ":" + PORT;
    public static final String MAN = "Man:\"ssdp:discover\"";
    public static final String NEWLINE = "\r\n";
    public static final String ST_Product = "ST:urn:schemas-upnp-org:device:Server:1";
    public static final String Found = "ST=urn:schemas-upnp-org:device:";
    public static final String Root = "ST: urn:schemas-upnp-org:device:Server:1";
    public static final String ALL = "ST:miivii";
    public String ip="000.000.000.000";

    private LinearLayout net;
    private int netMobile;
    private NetBroadcastReceiver netBroadcastReceiver;
    private TextView connectWifiInfo;


    private String WIP ;
    private String SSID ;
    private String ssid ;
    private String bssid ;
    private String myIp;

    private String mac1;
    private String ssid1;

    public static final String TAG = "First Implementation";


    private WifiManager.MulticastLock multicastLock;
    private List<String> listReceive = new ArrayList<String>();
    private TextView tvReceive;//Mostrar resultados de pesquisa
/*    private TextView ipReceive;*/


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_implementation);


        tvReceive = findViewById(R.id.tv_show_receive);
        connectWifiInfo = (TextView) findViewById(R.id.connectWifiInfo);
        getINfo();
        getNetworkInfo();

        Log.d(TAG, String.valueOf(connectWifiInfo));

        net = (LinearLayout) findViewById(R.id.net);

    }
        /*ipReceive = findViewById(R.id.ip_show_receive);*/
       /* Button btn = findViewById(R.id.btnSendSSDPSearch);
        btn.setOnClickListener(this);*/
       /* Button pingt=findViewById(R.id.ping);
        pingt.setOnClickListener(this);*/



    public void onClick(View v) {
        if(v.getId()==R.id.btnSendSSDPSearch){
        new Thread(new Runnable() {
            @Override
            public void run() {
                SendMSearchMessage();
            }
        }).start();
        }
        if(v.getId()==R.id.btnRefresh){
            getINfo();
            getNetworkInfo();
        }
        if(v.getId()==R.id.info){
            startActivity(new Intent(this, SecondImplementationActivity.class));
        }
    }

    /**
     * Acquire the group lock, and remember to release it in time after use, otherwise it will increase power consumption. To save power, Android devices are turned off by default
     */
    private void acquireMultiLock() {
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        assert wm != null;
        multicastLock = wm.createMulticastLock("multicastLock");
        multicastLock.setReferenceCounted(true);
        multicastLock.acquire();//使用后，需要及时关闭
    }

    /**
     * Release group lock
     */
    private void releaseMultiLock() {
        if (null != multicastLock) {
            multicastLock.release();
        }
    }


    private void SendMSearchMessage() {
        acquireMultiLock();
        SSDPSearchMsg searchMsg = new SSDPSearchMsg(SSDPConstants.ALL);
        SSDPSocket sock = null;
        try {
            //send
            sock = new SSDPSocket();
            sock.send(searchMsg.toString());
            Log.i(TAG, "The message to be sent is：" + searchMsg.toString());
            //take over
            listReceive.clear();
            while (true) {
                DatagramPacket dp = sock.receive();
                String ipresv = dp.getAddress().toString().trim();
                ip=ipresv.replace("/","");    // Here, I only receive the same packets I initially sent above
                String c = new String(dp.getData()).trim();
                Log.e(TAG, "The received message is：\n" + c + "\nSource IP address：" + ip);
                // After receiving it again, jump out of the loop directly
                if (listReceive.contains(c + NEWLINE + ip)) {
                    break;}
                else listReceive.add(c + NEWLINE + ip);
            }
            sock.close();
            releaseMultiLock();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Display the reception result
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i <listReceive.size(); i++) {
                    sb.append(i).append("\r\t").append(listReceive.get(i))
                            .append(NEWLINE).append("-----------------------").append(NEWLINE);
                }
                String s = sb.toString();

                tvReceive.setText(s);

                Log.d(TAG, "result = " + s);
            }
        });


    }

    private void getNetworkInfo() {
        try {
            WifiManager wm = null;

            try {
                wm = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            } catch (Exception ignored) {
            }
            if (wm != null && wm.isWifiEnabled()) {
                WifiInfo wifi = wm.getConnectionInfo();
                if (wifi.getRssi() != -200) {
                    myIp = getWifiIPAddress(wifi.getIpAddress());
                }
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 10);
                }else
                {
                    SSID = wm.getConnectionInfo().getSSID();
                    String BSSID = wm.getConnectionInfo().getBSSID();
                    Log.d(TAG, "IP: "+String.valueOf(myIp));
                    Log.d(TAG, "WIFIname: "+String.valueOf(SSID));
                    Log.d(TAG, "MAC: "+String.valueOf(BSSID));

                    String str = "WIFI: "+ssid+"\n"+"WiFiIP: "+myIp+"\n"+"MAC: "+ BSSID;
                    connectWifiInfo.setText(str);

                }


            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
    private  void getINfo() {
        WifiManager mWifiManager = null;


        ConnectivityManager mConnectivityManager = null;
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 10);
        }else
        {

            mWifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            mConnectivityManager = (ConnectivityManager) this.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            assert mWifiManager != null;
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();

            ssid = wifiInfo.getSSID();
            bssid = wifiInfo.getBSSID();
            int ID= wifiInfo.getNetworkId();
            int networkId = wifiInfo.getNetworkId();

            List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();

            for (WifiConfiguration wifiConfiguration:configuredNetworks){

                if (wifiConfiguration.networkId==networkId){

                    ssid=wifiConfiguration.SSID;
                    bssid=wifiConfiguration.BSSID;

                    break;

                }
                Log.d(TAG, "HUAweiSSID: "+ssid);
                Log.d(TAG, "HUAweiBSSID: "+bssid);
            }
            mWifiManager.startScan();
            List<ScanResult> scanList = mWifiManager.getScanResults();
            for(ScanResult scanResult : scanList){
                mac1 = scanResult.BSSID;
                ssid1 = scanResult.SSID;
                int rssi = scanResult.level;
                Log.d(TAG, "SSID1: "+ssid1);
                Log.d(TAG, "BSSID1s: "+mac1);
                Log.d(TAG, "RSSI: "+rssi);
                Log.d(TAG, "ID: "+ID);
            }



        }
    }

    private String getWifiIPAddress(int ipaddr) {
        String ip = "";
        if (ipaddr == 0) return ip;
        byte[] addressBytes = {(byte)(0xff & ipaddr), (byte)(0xff & (ipaddr >> 8)),
                (byte)(0xff & (ipaddr >> 16)), (byte)(0xff & (ipaddr >> 24))};
        try {
            ip = InetAddress.getByAddress(addressBytes).toString();
            if (ip.length() > 1) {
                ip = ip.substring(1, ip.length());
            } else {
                ip = "";
            }
        } catch (Exception e) {
            ip = "";
        }
        Log.d(TAG, "IP: "+String.valueOf(ip));
        return ip;
    }

    @Override
    protected  void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");

        if (netBroadcastReceiver == null) {
            //Instantiate the network receiver
            netBroadcastReceiver = new NetBroadcastReceiver();
            //Instantiation intent
            IntentFilter filter = new IntentFilter();
            //Set the type of broadcast
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            //Register to broadcast, onReceive will be triggered when there is a network change
            registerReceiver(netBroadcastReceiver, filter);
            // Set up monitor
            netBroadcastReceiver.setNetEvent((NetBroadcastReceiver.NetEvent) this);
        }


    }

    public void onNetChange(int netMobile) {
        // TODO Auto-generated method stub
        this.netMobile = netMobile;
        isNetConnect();
    }
    private void isNetConnect() {
        switch (netMobile) {
            case 1:// wifi
            case 0:// Mobile data
                net.setVisibility(View.GONE);
                break;
            case -1:// No internet
                net.setVisibility(View.VISIBLE);
                break;
        }
    }
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");

        if (netBroadcastReceiver != null) {
            unregisterReceiver(netBroadcastReceiver);
        }

    }
}

