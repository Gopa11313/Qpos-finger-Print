package cn.zt.pos.utils;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.szzt.sdk.device.pinpad.PinPad;

import cn.zt.pos.MainActivity;
import cn.zt.pos.R;

public class KeyImport {


    //下载主密钥
    public static void PinpadUpdateMasterKey(PinPad mPinpad, Context context, String masterKey) {

        byte[] mainKey = HexDump.hexStringToByteArray(masterKey);///*"7EE8E78756FFF9B79FDD12F6778B41D8"*/

        int ret = mPinpad.updateMasterKey(2, mainKey, null);
        if (ret >= 0) {
            Log.d("PinpadUpdateMasterKey", "PinpadUpdateMasterKey: " + ret);

            Toast.makeText(context, context.getString(R.string.msg_pinpad_main_key_succ), Toast.LENGTH_LONG).show();


            PinpadUpdateUserKey(mPinpad, context, PrefUtil.getEmvPayload(), PrefUtil.getCheckVal().replace(" ", "").trim());


        } else {
            Toast.makeText(context, context.getString(R.string.msg_pinpad_main_key_error), Toast.LENGTH_LONG).show();

        }
    }

    //下载工作密钥
    public static void PinpadUpdateUserKey(PinPad mPinpad, Context context, String workingKey, String chValue) {

        Log.d("PinpadUpdateUserKey", "PinpadUpdateUserKey: " + workingKey + " ch " + chValue);
        byte[] keys = HexDump.hexStringToByteArray(workingKey);///*"89EDEECE534D0BA5378DDDCBCA8AF1E2"*/
        byte[] chkValue = HexDump.hexStringToByteArray(chValue);
        //下载pin类型密钥
        int ret=mPinpad.updateUserKey(2,PinPad.KeyType.PIN,0,keys,chkValue, PinPad.DES_TYPE_DES3);

//        int ret = mPinpad.updateUserKey(2, 0, 2, keys);

        if (ret >= 0) {
            Toast.makeText(context, context.getString(R.string.msg_pinpad_wk_0_succ), Toast.LENGTH_LONG).show();
            Log.d("PinpadUpdateUserKey", "PinpadUpdateUserKey: "+ret);
        } else {
            Toast.makeText(context, context.getString(R.string.msg_pinpad_wk_0_error), Toast.LENGTH_LONG).show();
            Log.d("PinpadUpdateUserKey", "PinpadUpdateUserKey: " + ret);

        }
    }

}
