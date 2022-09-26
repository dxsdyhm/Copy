/*******************************************************************
* Company:     Fuzhou Rockchip Electronics Co., Ltd
* Description:   
* @author:     fxw@rock-chips.com
* Create at:   2014年5月13日 下午5:51:34  
* 
* Modification History:  
* Date         Author      Version     Description  
* ------------------------------------------------------------------  
* 2014年5月13日      fxw         1.0         create
*******************************************************************/   

package com.hcy.copy.led;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.blankj.utilcode.util.StringUtils;

public class LedTest  {

	private static final int MSG_ON_OFF = 1;
	private LEDSettings.LEDMode mLEDMode;
	private boolean mStartOnOff;
	private LEDHandler mLEDHandler;
	public static String SYS_LED_FILE = "/sys/class/leds/sys_led/brightness";
	private String path_led=SYS_LED_FILE;
	
	public LedTest(Handler handler) {
		mLEDHandler = new LEDHandler(handler.getLooper());
	}

	public boolean start(String path){
		if(!StringUtils.isTrimEmpty(path)){
			path_led=path;
		}
		mStartOnOff = true;
		mLEDHandler.sendEmptyMessageDelayed(MSG_ON_OFF, 50);
		return false;
	}

	public void stop(){
		mStartOnOff = false;
	}

	
	class LEDHandler extends Handler {
		
		public LEDHandler(Looper looper) {
			super(looper);
		}
		
		public void handleMessage(Message msg) {
			switch(msg.what){
			case MSG_ON_OFF:
				if(mStartOnOff){
					if(mLEDMode== LEDSettings.LEDMode.OFF){
						mLEDMode = LEDSettings.LEDMode.ON;
						LEDSettings.onLed(path_led);
					}else{
						mLEDMode = LEDSettings.LEDMode.OFF;
						LEDSettings.offLed(path_led);
					}
					mLEDHandler.sendEmptyMessageDelayed(MSG_ON_OFF, 1000);
				}
				break;
			}
		}
	}
}
