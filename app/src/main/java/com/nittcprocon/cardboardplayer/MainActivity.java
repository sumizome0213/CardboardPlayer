package com.nittcprocon.cardboardplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.vr.sdk.widgets.common.VrWidgetView;
import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private VrVideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = (VrVideoView) findViewById(R.id.vr_video_view);
        videoView.setEventListener(new VideoEventListener());
        try {
            VrVideoView.Options videoOptions = new VrVideoView.Options();
            videoOptions.inputType = VrVideoView.Options.TYPE_MONO;

            /*
            // HLS 配信の場合は、inputFormat に FORMAT_HLS を指定する。
            videoOptions.inputFormat = VrVideoView.Options.FORMAT_HLS;
            Uri uri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.drawable.ride);
            videoView.loadVideo(uri, videoOptions);
            */


            // HSL 配信以外は FORMAT_DEFAULT を指定する。
            videoOptions.inputFormat = VrVideoView.Options.FORMAT_DEFAULT;
            videoView.loadVideoFromAsset("ride.mp4", videoOptions);


        } catch (IOException e) {
            e.printStackTrace();
            Log.d("VR", "Video Load Error.");
        }
    }

   /* protected void onResume() {
        super.onResume();

        int port = 12345;      // 送信側と揃える
        int bufferSize = 1024;

        UDPObjectTransfer udpObjectTransfer = new UDPObjectTransfer();

        Object obj = udpObjectTransfer.receive(port, bufferSize);

        String mode = obj.toString();

        switch (mode){
            case "Play":


            case "Stop":


        }


    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                // インテントの生成
                Intent setting = new Intent();
                setting.setClassName("com.nittcprocon.cardboardplayer", "com.nittcprocon.cardboardplayer.SettingPreferenceActivity");

                
                // SubActivity の起動
                startActivity(setting);

                return true;
            case R.id.help:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private class VideoEventListener extends VrVideoEventListener {
        @Override
        public void onLoadSuccess() {
            // コンテンツの読み込みに成功。
            super.onLoadSuccess();
            Log.d("VR", "Content Load Success.");
        }

        @Override
        public void onLoadError(String errorMessage) {
            // コンテンツの読み込みに失敗。
            super.onLoadError(errorMessage);
            Log.d("VR", errorMessage);
        }

        @Override
        public void onClick() {
            // View がタップされた時に呼ばれる。
            super.onClick();
            Log.d("VR", "Click");
        }

        @Override
        public void onDisplayModeChanged(int newDisplayMode) {
            // 表示モードが切り替わった時に呼ばれる。
            super.onDisplayModeChanged(newDisplayMode);
            String displayModeName;
            switch (newDisplayMode) {
                case VrWidgetView.DisplayMode.EMBEDDED:
                    displayModeName = "EMBEDDED";
                    break;
                case VrWidgetView.DisplayMode.FULLSCREEN_MONO:
                    displayModeName = "FULLSCREEN_MONO";
                    break;
                case VrWidgetView.DisplayMode.FULLSCREEN_STEREO:
                    displayModeName = "FULLSCREEN_VR";
                    break;
                default:
                    displayModeName = "UNKNOWN";
                    break;
            }
            Log.d("VR", "Display Mode = " + displayModeName);
        }

        @Override
        public void onNewFrame() {
            // 動画再生位置を取得
            super.onNewFrame();
            Log.d("VR", "Position : " + videoView.getCurrentPosition());
        }

        @Override
        public void onCompletion() {
            // 動画再生が完了
            super.onCompletion();
        }

    }


}



