package com.hcy.copy.led;

import com.blankj.utilcode.util.FileIOUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class LEDSettings {

	public enum LEDMode {
		ON("3"),
		OFF("0");
		
		public String value;
		private LEDMode(String value){
			this.value = value;
		}
		
		public static LEDMode getMode(String mode){
			for(LEDMode um : LEDMode.values()){
				if(um.value.equals(mode)){
					return um;
				}
			}
			return null;
		}
	}
    
    /**
     * 开灯
     */
    public static boolean onLed(String path){
		return setLedMode(LEDMode.ON,path);
    }
    
    /**
     * 关灯
     */
    public static boolean offLed(String path){
    	return setLedMode(LEDMode.OFF,path);
    }
    
    /**
     * 修改LED状态
     */
    public static boolean setLedMode(LEDMode mode,String ledPath){
    	File file = new File(ledPath);
    	return write2File(file, mode.value);
    }

	public static boolean write2File(File file, String value) {
		if((file == null) || (!file.exists())) return false;
		try {
			FileOutputStream fout = new FileOutputStream(file);
			PrintWriter pWriter = new PrintWriter(fout);
			pWriter.println(value);
			pWriter.flush();
			pWriter.close();
			fout.close();
			return true;
		} catch(IOException re) {
			return false;
		}
	}
}
