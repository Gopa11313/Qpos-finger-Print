package cn.zt.pos.cashinput;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;

public class MoneyUtil {
    private static DecimalFormat doubleDF = new DecimalFormat("#0.00");

    public MoneyUtil() {
    }

    public static String formatDouble2Str4Money(double d) {
        return doubleDF.format(d);
    }

    public static String fen2yuan(long fen) {
        new BigDecimal(fen);
        return formatDouble2Str4Money((double)fen / 100.0D);
    }

    public static Double fenTrans2Yuan(Long fen) {
        return Double.parseDouble(fen2yuan(fen));
    }

    public static String toCent(String dollar) {
        String cent = "";
        long cents = 0L;
        if (TextUtils.isEmpty(dollar)) {
            cents = 0L;
        } else {
            int index = dollar.indexOf(".");
            if (index >= 0) {
                int gap = dollar.length() - index - 1;
                if (gap == 0) {
                    cent = dollar + "00";
                } else if (gap == 1) {
                    cent = dollar.replace(".", "") + "0";
                } else if (gap == 2) {
                    cent = dollar.replace(".", "");
                } else {
                    cent = dollar.substring(0, index + 3).replace(".", "");
                }
            } else {
                cent = dollar + "00";
            }

            cents = NumberUtil.parseLong(cent);
        }

        return String.format(Locale.US, "%012d", cents);
    }

    public static long yuan2fen(double amount) {
        return BigDecimal.valueOf(amount).multiply(new BigDecimal(100)).longValue();
    }
}