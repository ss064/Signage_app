package com.example.suzukisusumu_sist.signage_app;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.Timer;
import java.util.TimerTask;

public class SignageActivity extends AppCompatActivity implements AsyncTaskGetJson.AsyncCallBack{
    public VideoView video;
    public TextView counter;
    public String[] urls;
    private int videocnt=0;
    private String androidId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signage);
        video = (VideoView) findViewById(R.id.videoView);
        counter = (TextView) findViewById(R.id.Counter);
        androidId=android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        AsyncTaskGetJson asyncTaskGetJson = new AsyncTaskGetJson(this,androidId);
        asyncTaskGetJson.execute();
        //JSONをダウンロードし終わるまで待機
    }

    public int VideoChange(Uri path) {
        video.setVideoURI(path);
        video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                video.seekTo(0);
                video.start();
            }
        });
        return 0;
    }

    @Override
    public void onPostExecute(String result){
        Log.d("async urls",result);
        urls=result.split("\r\n");
        Log.d("urls",urls[videocnt]);
        VideoChange(Uri.parse(urls[videocnt]));
        //再生時間表示に関する処理
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                counter.post(new Runnable() {
                    @Override
                    public void run() {
                        counter.setText(String.valueOf(video.getCurrentPosition() / 1000) + "s");
                    }
                });
            }
        }, 0, 50);


        //次動画自動再生処理
        video.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                VideoChange(Uri.parse(urls[++videocnt%urls.length]));
                videocnt=videocnt%urls.length;
            }
        });
    }
}

