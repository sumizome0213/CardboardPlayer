package com.nittcprocon.cardboardplayer;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingPreferenceActivity extends PreferenceActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 設定画面の作成
        addPreferencesFromResource(R.xml.preference);
    }
}
