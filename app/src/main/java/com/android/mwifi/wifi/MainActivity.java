package com.android.mwifi.wifi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecycler;
    private List<Person>mList=new ArrayList<>();
    StringBuilder mBuilder=new StringBuilder();
    private TextView wifiCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean rootPermission = getRootPermission();
        if (rootPermission){
            initView();
            initPersonData();
            initRecycler();
        }
    }

    //获取UI控件
    private void initView() {
        mRecycler = (RecyclerView) findViewById(R.id.recyclerView);
        wifiCount = (TextView) findViewById(R.id.textView_WifiCount);
    }
    //初始化Adapter中的Person数据
    private void initPersonData() {
        String WifiData = mBuilder.toString();//WIFI热点+密码
        if (WifiData!=null) {
            if (WifiData.indexOf("network")!=-1){
            /**
             * 从network={ 截取到 数据末( } );
             * 每个WIFI热点 就对应这一个 network={ ssid=热点名 psk=密码}
             * **/
            String network = WifiData.substring(WifiData.indexOf("network={"), WifiData.length());
            String[] networks = network.split("network=");//分割成多个 network={ ssid=热点名 psk=密码}
            /**
             * 循环获取每个 network={ ssid=热点名 psk=密码} 中的热点和密码
             * 由于networks[0] = “” 因此 ，跳过第一个，从networks[1]开始获取
             * **/
            for (int i = 1; i < networks.length; i++) {
                String Wifi = networks[i];
                int index = Wifi.indexOf("\tpsk");//先判断 \tpsk 字段 == -1，等于-1表示 该热点不存在密码
                if (index!=-1){ //存在Wifi密码
                    //下面是分割 network中的 ssid 热点名 ，psk密码
                    String[] split = Wifi.split("\t");
                    String Wifi_name = split[1];
                    String Wifi_Password = split[2];
                    String[] name = Wifi_name.split("ssid=");
                    String[] password = Wifi_Password.split("psk=");
                    String s_name = name[1].replace("\"","");  //replace("\"","");  双引号
                    String s_password = password[1].replace("\"","");;

                    Person p =new Person();
                    p.setName("WIFI热点:"+s_name);
                    p.setPassword("WIFI密码:"+s_password);
                    mList.add(p);
                }
                if (index == -1){  //不存在Wifi密码
                    String[] split = Wifi.split("\t");
                    String Wifi_name = split[1];
                    String[] name = Wifi_name.split("ssid=");
                    String s_name = name[1].replace("\"","");
                    Person p =new Person();
                    p.setName("WIFI热点:"+s_name);
                    p.setPassword("WIFI密码:无");
                    mList.add(p);
                }
            }
        }
        wifiCount.setText("当前Wifi热点共:"+mList.size()+"个");
    }
    }
    //初始化RecyclerView
    private void initRecycler() {
        mAdapter adapter=new mAdapter(mList);
        LinearLayoutManager manager=new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecycler.setLayoutManager(manager);
        mRecycler.setAdapter(adapter);
    }

    //申请ROOT权限，前提是设备以ROOT
    private boolean getRootPermission() {
        java.lang.Process process = null;
        DataOutputStream os = null;
        try {
            //cat 表示查看 ，/data/misc/wifi/wpa_supplicant.conf 表示具体查看那个文件
            String command = "cat /data/misc/wifi/wpa_supplicant.conf";
            Runtime rt = Runtime.getRuntime();
            process = rt.exec("su"); //申请ROOT权限
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            //读取数据
            InputStreamReader IS = new InputStreamReader(process.getInputStream());
            BufferedReader reader=new BufferedReader(IS);
            String line;
            while ((line=reader.readLine())!=null){
                mBuilder.append(line);//写入到StringBuilder中
            }
            IS.close();
            reader.close();

            process.waitFor();
        } catch (Exception e) {

        } finally {
            try {
                if (os != null) {
                    os.close();

                }
                if(process != null){
                    process.destroy();
                }
            } catch (Exception e) {
            }
        }
        return true;
    }
    /**
     * 重写finish()方法，按2次退出程序
     * **/
    private int isFinish=1;
    @Override
    public void finish() {
        isFinish++;
        Toast.makeText(this,"再按一次退出程序哟",0).show();
        if (isFinish==2){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                        isFinish=1;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        if (isFinish==3){
            super.finish();
        }

    }
}
