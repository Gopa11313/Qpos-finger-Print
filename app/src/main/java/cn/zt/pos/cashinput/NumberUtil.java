package cn.zt.pos.cashinput;

import android.text.TextUtils;

public class NumberUtil {
    private NumberUtil() {
    }

    public static long parseLong(String sLong) {
        if (TextUtils.isEmpty(sLong)) {
            return 0L;
        } else {
            long result = 0L;

            try {
                result = Long.parseLong(sLong);
            } catch (NumberFormatException var4) {
                var4.printStackTrace();
            }

            return result;
        }
    }

    public static int parseInt(String sInt) {
        if (TextUtils.isEmpty(sInt)) {
            return 0;
        } else {
            int result = 0;

            try {
                result = Integer.parseInt(sInt);
            } catch (NumberFormatException var3) {
                var3.printStackTrace();
            }

            return result;
        }
    }
}
