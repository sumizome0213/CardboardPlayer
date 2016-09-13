package com.nittcprocon.cardboardplayer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.vr.sdk.widgets.common.VrWidgetView;
import com.google.vr.sdk.widgets.video.VrVideoEventListener;
import com.google.vr.sdk.widgets.video.VrVideoView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private VrVideoView videoView;
    public SocketUDP Listener = new SocketUDP();
    public String receiveValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartUDP();

        videoView = (VrVideoView) findViewById(R.id.vr_video_view);
        videoView.setEventListener(new VideoEventListener());

        //VRModeにする
        videoView.setDisplayMode(3);

    }

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
                setting.setClassName("com.nittcprocon.cardboardplayer", "com.nittcprocon.cardboardplayer.SettingsActivity");

                // SubActivity の起動
                startActivity(setting);

                return true;
            case R.id.about:

                alert("このアプリについて", "シンクロアスリートの非リアルタイム時のCardBoard再生アプリです");

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void alert(String title, String message) {
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setTitle(title);
        alertDlg.setMessage(message);
        alertDlg.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // OK ボタンクリック処理
                    }
                });

        // 表示
        alertDlg.create().show();
    }

    public void StartUDP() {
        //Portの数値を"strport"(string型)へ(""の場合は"12345"とする)
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String strport = sharedPreferences.getString("Port", "12345");
        if (Objects.equals(strport, "")) strport = "12345";

        //"strport"(string型)を"port"(int型)へ
        final int port = Integer.parseInt(strport);
        Log.d("UDP", "port# " + port);

        //SocketUDPへportを送りながら起動
        (new Thread(new Runnable() {
            @Override
            public void run() {

                while(true) {

                    receiveValue = new String(Listener.getMessage(port));

                    messagecase(receiveValue);

                }

            }
        })).start();
    }

    public void messagecase(String message) {

        Log.d("UDP", "messagecase :  " + message);

        //改行コード削除
        message = message.replaceAll("\n","");
        message = message.replaceAll("\r","");

        //正規表現(loadするファイルの読み取りのため)
        Pattern pattern = Pattern.compile("(load: +)(.*)");
        Matcher matcher = pattern.matcher(message);

        if(matcher.find()) {

            Log.d("UDP", "loadname: " + matcher.group(2));
            view(matcher.group(2));

        } else {
            //switch文
            switch (message) {
                case "start":
                    start();
                    break;

                case "stop":
                    stop();
                    break;

                default:
                    break;

            }
        }


    }

    public void view(String filename) {
        try {
            VrVideoView.Options videoOptions = new VrVideoView.Options();

            //映像の種類
            videoOptions.inputType = VrVideoView.Options.TYPE_MONO;

            /*
            // HLS 配信の場合は、inputFormat に FORMAT_HLS を指定する。
            videoOptions.inputFormat = VrVideoView.Options.FORMAT_HLS;
            Uri uri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.drawable.ride);
            videoView.loadVideo(uri, videoOptions);
            */

            // HSL 配信以外は FORMAT_DEFAULT を指定する。
            videoOptions.inputFormat = VrVideoView.Options.FORMAT_DEFAULT;
            videoView.loadVideoFromAsset(filename, videoOptions);

            //一時停止させる
            stop();

            //最初に戻す
            videoView.seekTo(0);


        } catch (IOException e) {
            e.printStackTrace();
            Log.d("VR", "Video Load Error.");
        }
    }

    public void start() {

        //再生する
        videoView.playVideo();
        Log.d("VR", "start");

    }

    public void stop() {

        //一時停止する
        videoView.pauseVideo();
        Log.d("VR", "stop");

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
            alert("コンテンツの読み込みに失敗", "コンテンツの読み込みに失敗しました。再生ファイルが本体に含まれているか、ディレクトリの場所を確認して下さい。");
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
            // Log.d("VR", "Position : " + videoView.getCurrentPosition());
        }

        @Override
        public void onCompletion() {
            // 動画再生が完了
            super.onCompletion();
        }

    }


}



