package cn.zt.pos.utils;
import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;

public final class FileUtils {
	public static boolean copyApkFromAssetsToData(Context context ,String fileName) {
        try {
            AssetManager as = context.getAssets();
            InputStream ins = as.open(fileName);
            String dstFilePath = context.getFilesDir().getAbsolutePath()
                    + "/" + fileName;
            OutputStream outs =context.openFileOutput(fileName,Context.MODE_WORLD_READABLE);
            byte[] data = new byte[1 << 20];
            int length = ins.read(data);
            outs.write(data, 0, length);
            ins.close();
            outs.flush();
            outs.close();
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
        	Log.e("APP", "copy success");
            return false;
        }
    }
}
