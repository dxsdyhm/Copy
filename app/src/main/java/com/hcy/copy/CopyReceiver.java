package com.hcy.copy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ServiceUtils;
import com.blankj.utilcode.util.StringUtils;

import java.io.File;

public class CopyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())){
            String path = intent.getDataString().replace("file://","");
            File file=new File(path+File.separator+CopyService.SH_NAME);
            File dir=new File(path+File.separator+BaiService.TAG_NAME);
            if(FileUtils.isFileExists(file)){
                SPUtils.getInstance().put(CopyService.KEY_PATH,path);
                ServiceUtils.startService(CopyService.class);
            }else if(!path.contains("storage/emulated")&&FileUtils.isFileExists(dir)&& !StringUtils.isTrimEmpty(FileUtils.getSize(dir))){
                SPUtils.getInstance().put(CopyService.KEY_PATH,path);
                ServiceUtils.startService(BaiService.class);
            }else {
                Log.e("dxsTag","path:"+path);
            }
        }
    }
}
