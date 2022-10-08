package com.hcy.copy;

import static com.hcy.copy.CopyService.KEY_PATH;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ShellUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ThreadUtils;
import com.blankj.utilcode.util.ToastUtils;

import java.io.File;

public class BaiService extends Service {
    private static String DES_FILE= "/sdcard/BYHUBYENG";
    public static String TAG_NAME="BYHUBYENG";
    private String uPath="";
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
        ActivityUtils.startActivity(BaiTipActivity.class);
        if(intent!=null){
            uPath = intent.getStringExtra(KEY_PATH);
        }
        if(StringUtils.isTrimEmpty(uPath)){
            uPath= SPUtils.getInstance().getString(KEY_PATH,"");
        }
        if(StringUtils.isTrimEmpty(uPath)){
            stopSelf();
        }
        Log.e("dxsTag","uPath:"+uPath);
        ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Boolean>() {
            @Override
            public Boolean doInBackground() throws Throwable {
                //删除本地文件夹
                FileUtils.deleteAllInDir(DES_FILE);
                //拷贝u盘目录
                String sh3="rm -rf /sdcard/BYHUBYENG";
                String sh1="tar -cf /sdcard/BY.tar -C"+uPath+"/ BYHUBYENG/";
                String sh2="tar xf /sdcard/BY.tar -C /sdcard/";
                String sh4="rm -rf /sdcard/BY.tar";
                ShellUtils.CommandResult commandResult = ShellUtils.execCmd(new String[]{sh1,sh2,sh4},false);
                Log.e("dxsTag","accept------->"+commandResult);
                return commandResult.result==0;
            }

            @Override
            public void onSuccess(Boolean result) {
                Log.e("dxsTag","result:"+result);
                stopSelf();
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        ActivityUtils.finishActivity(BaiTipActivity.class);
        super.onDestroy();
    }
}
