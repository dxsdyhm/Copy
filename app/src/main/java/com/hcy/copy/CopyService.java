package com.hcy.copy;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ShellUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.Utils;
import com.hcy.copy.led.LEDSettings;
import com.hcy.copy.led.LedTest;

import java.io.File;

public class CopyService extends Service {
    //tar -xzf @1/module_sdk.tar.gz -C /sdcard/
    public static String KEY_PATH="KEY_PATH";
    private LedTest test=new LedTest(new Handler());
    private String uPath="";
    public static String SH_NAME="copy.hcysh";

    private ThreadUtils.Task copy=new ThreadUtils.SimpleTask<Boolean>() {
        @Override
        public Boolean doInBackground() throws Throwable {
            //灯语
            if(test!=null){
                test.start("");
            }
            //执行命令
            String sh= FileIOUtils.readFile2String(uPath+ File.separator+SH_NAME);
            sh=sh.replace("@1",uPath);
            ShellUtils.CommandResult commandResult = ShellUtils.execCmd(sh,false);
            Log.e("dxsTag","accept------->"+commandResult);
            stop(commandResult.result==0);
            return commandResult.result==0;
        }

        @Override
        public void onSuccess(Boolean result) {
            stop(result);
        }
    };

    private void stop(boolean result){
        //结束灯语
        if(result){
            LEDSettings.onLed(LedTest.SYS_LED_FILE);
        }else {
            LEDSettings.offLed(LedTest.SYS_LED_FILE);
        }
        if(test!=null){
            test.stop();
        }
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null){
            uPath = intent.getStringExtra(KEY_PATH);
        }
        if(StringUtils.isTrimEmpty(uPath)){
            uPath= SPUtils.getInstance().getString(KEY_PATH,"");
        }
        if(StringUtils.isTrimEmpty(uPath)){
            stopSelf();
        }
        ThreadUtils.executeByIo(copy);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ThreadUtils.cancel(copy);
    }
}
