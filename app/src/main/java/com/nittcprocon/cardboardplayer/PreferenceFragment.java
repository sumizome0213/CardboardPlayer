package com.nittcprocon.cardboardplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;

public class PreferenceFragment extends android.preference.PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    Context context;

    //getSystemServiceを使うため
    @SuppressLint("ValidFragment")
    public PreferenceFragment(Context context){
        this.context = context;
    }

    //default constructor を提供しなければならないため
    public PreferenceFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 設定画面の作成
        addPreferencesFromResource(R.xml.preference);

        portget();

       // IP取得
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String strIPAddress =
                ((ipAddress >> 0) & 0xFF) + "." +
                        ((ipAddress >> 8) & 0xFF) + "." +
                        ((ipAddress >> 16) & 0xFF) + "." +
                        ((ipAddress >> 24) & 0xFF);

        // SummaryにIP表示
        Preference ip = (findPreference("IPAddress"));
        ip.setSummary(strIPAddress);

    }

    //Summaryを更新するメソッド
    public void portget() {
        EditTextPreference port = (EditTextPreference)getPreferenceScreen().findPreference("Port");
        port.setSummary(port.getText());
    }

    //設定値を変えた時に実行される
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        portget();

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
