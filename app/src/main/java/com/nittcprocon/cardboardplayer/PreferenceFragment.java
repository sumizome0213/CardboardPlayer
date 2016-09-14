package com.nittcprocon.cardboardplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.support.annotation.NonNull;
import android.util.Log;

import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import java.util.Objects;

public class PreferenceFragment extends android.preference.PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, DirectoryChooserFragment.OnFragmentInteractionListener {

    Context context;
    private DirectoryChooserFragment mDialog;

    //getSystemServiceを使うため
    @SuppressLint("ValidFragment")
    public PreferenceFragment(Context context){
        this.context = context;
    }

    //default constructor を提供しなければならないため
    public PreferenceFragment(){}

    //path名
    String path = "";


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

        //pathに代入
        getDirectory();
        //summaryにpathを表示
        showDirectory();

        //DirectorySelect
        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("DialogSample")
                .initialDirectory(path)
                .build();
        mDialog = DirectoryChooserFragment.newInstance(config);

        Preference directory = (findPreference("Directory"));
        directory.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                //open browser or intent here

                mDialog.show(getFragmentManager(), null);



                return true;
            }
        });

    }

    //Summaryを更新するメソッド
    public void portget() {
        EditTextPreference port = (EditTextPreference)getPreferenceScreen().findPreference("Port");
        port.setSummary(port.getText());
    }

    //pathの更新
    public String getDirectory() {

        CharSequence charpath;
        SharedPreferences pathPref = context.getSharedPreferences("pathPref", Context.MODE_PRIVATE);

        //Summaryをpathに
        try {

            path = pathPref.getString("path","");

        }catch (RuntimeException e) {
            Log.d("Directory", "getDirectory: error");

        }

        //""の時の処理
        if(Objects.equals(path, "")) path = "/storage/emulated/0/Movies";

        //pathPrefのpathに値を保存
        SharedPreferences.Editor editor = pathPref.edit();
        editor.putString("path",path);
        editor.commit();

        return path;
    }

    //summaryへのpathの表示
    public void showDirectory() {

        Preference directory = (findPreference("Directory"));
        directory.setSummary(path);

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

    //OKを押した時の処理
    @Override
    public void onSelectDirectory(@NonNull final String newpath) {
        Log.d("Directory", "select");

        //変数pathを更新
        path = newpath;
        //pathに代入
        getDirectory();
        //summaryにpathを表示
        showDirectory();

        mDialog.dismiss();

    }

    //Cancelを押した時の処理
    @Override
    public void onCancelChooser() {
        Log.d("Directory", "cancel");
        mDialog.dismiss();

    }
}
