package cn.zt.pos.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import cn.zt.pos.db.obj.AID;
import cn.zt.pos.db.obj.BIN;
import cn.zt.pos.db.obj.PreAuthModel;
import cn.zt.pos.db.obj.TellerInfo;
import cn.zt.pos.db.obj.Tracemodule;


/**
 * 柜员信息 数据库包含99系统管理员密码。
 * 其中柜员密码为4位数字，系统管理员密码为8位数字
 *
 * @author zhou
 */
public class TellersDbHelper extends SQLiteOpenHelper {
    String TAG = "dinesh";

    private final static String DATABASE_NAME = "AllinpayTellersInfoDb";
    private final static int DATABASE_VERSION = 1;
    private final static String TABLE_NAME = "AllinpayTellersInfo";
    private final static String TABLE_NAME_PREAUTH = "preAuth";
    private final static String TABLE_AID = "aid";
    private final static String TABLE_BIN = "bin";
    private final static String TABLE_NAME_TRACE = "trace";
    private final static String FIELD_ID = "_id";

    private String secret_key = "allinpayCashier";

    private final static String FIELD_TELLER_N0 = "tellerNo";//柜员编号
    private final static String FIELD_TELLER_PASSWORD = "tellerPwd";//柜员密码

    /*pre auth*/
    String CARD_NO = "cardName";
    String AUTH_CODE = "authCode";
    String AUTH_AMOUNT = "authAmount";
    String AUTH_DATE = "authDate";
    String EMV = "emvLoad";


    /*trace data*/
      /*pre auth*/
    String STAN = "stan";
    String CRRN = "crrn";
    String TXNTYPE = "txnType";
    String STATUS = "status";
    String EMVPAYLOAD = "emvPayload";
    String AMOUNT = "amount";
    /*aid cvm*/
    String AID = "aid";
    String CVM = "cvm";
    /*bin*/
    String NAME="name";
    String FLAG="flag";

    private SQLiteDatabase database;
    private String[] columns = {FIELD_TELLER_N0, FIELD_TELLER_PASSWORD};
    private String[] columnsAuth = {CARD_NO, AUTH_CODE, AUTH_AMOUNT, EMV, AUTH_DATE};

    String sqlPreAuth = "Create table if not exists  " + TABLE_NAME_PREAUTH +
            "(" + FIELD_ID + " integer primary key,"
            + CARD_NO + " text,"
            + AUTH_CODE + " text,"
            + AUTH_AMOUNT + " text,"
            + EMV + " text,"

            + AUTH_DATE + " text );";


    String sqlTrace = "Create table if not exists  " + TABLE_NAME_TRACE +
            "(" + FIELD_ID + " integer primary key,"
            + STAN + " text,"
            + CRRN + " text,"
            + TXNTYPE + " text,"
            + EMVPAYLOAD + " text,"

            + AMOUNT + " text,"

            + STATUS + " text );";

    String sqlAID = "Create table if not exists  " + TABLE_AID +
            "(" + FIELD_ID + " integer primary key,"
            + AID + " text,"
            + CVM + " text );";


    String sqlBIN = "Create table if not exists  " + TABLE_BIN +
            "(" + FIELD_ID + " integer primary key,"
            + NAME + " text,"
            + FLAG + " text );";


    public TellersDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //创建数据库
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "Create table if not exists  " + TABLE_NAME +
                "(" + FIELD_ID + " integer primary key,"
                + FIELD_TELLER_N0 + " text,"
                + FIELD_TELLER_PASSWORD + " text );";
        db.execSQL(sqlPreAuth);
        db.execSQL(sqlTrace);
        db.execSQL(sqlAID);
        db.execSQL(sqlBIN);
        db.execSQL(sql);
    }

    //删除数据库
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = " DROP TABLE IF EXISTS " + TABLE_NAME;
        String sqlAuth = " DROP TABLE IF EXISTS " + TABLE_NAME_PREAUTH;
        String sqlTrace = " DROP TABLE IF EXISTS " + TABLE_NAME_TRACE;
        String sqlAid = " DROP TABLE IF EXISTS " + TABLE_AID;
        String sqlBIN = " DROP TABLE IF EXISTS " + TABLE_BIN;
        db.execSQL(sqlAuth);
        db.execSQL(sql);
        db.execSQL(sqlTrace);
        db.execSQL(sqlAid);
        db.execSQL(sqlBIN);
        onCreate(db);
    }
/*

    String STAN = "stan";
    String CRRN = "crrn";
    String TXNTYPE = "txnType";
    String STATUS="status";
    String EMVPAYLOAD="emvPayload";
    String AMOUNT="amount";
*/

/*bin table*/
public long insertBin(BIN str) {
    database = this.getWritableDatabase();
    ContentValues cv = new ContentValues();
    cv.put(NAME, str.getName());
    cv.put(FLAG, str.getFlag());
    long row = database.insert(TABLE_BIN, null, cv);
    database.close();
    return row;
}

    public boolean getBin(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = " select * from  bin where name = '"+name+"'";// where txnType = '" + id + "'"
        Cursor cursor = db.rawQuery(selectQuery, null);
        cn.zt.pos.db.obj.AID tracemodule = null;
        if (cursor != null && cursor.getCount() > 0) {
           return true;
        }
        return false;
    }

    /*insert aid*/
    public long insertAid(AID aid) {
        database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(AID, aid.getAid());
        cv.put(CVM, aid.getCvm());
        long row = database.insert(TABLE_AID, null, cv);
        database.close();
        return row;
    }
    /*get aid*/
    public AID getCvm(String aid) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = " select * from  aid where aid = '"+aid+"'";// where txnType = '" + id + "'"
        Cursor cursor = db.rawQuery(selectQuery, null);
        AID tracemodule = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToPosition(0);
            do {
                String aId = cursor.getString(cursor.getColumnIndex(AID));
                String cvm = cursor.getString(cursor.getColumnIndex(CVM));

                tracemodule = new AID(aId,cvm);

            } while (cursor.moveToNext());
        }
        return tracemodule;
    }

    /*trace*/
    public long insertTrace(Tracemodule tracemodule) {
        database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(STAN, tracemodule.getStan());
        cv.put(CRRN, tracemodule.getCrrn());
        cv.put(TXNTYPE, tracemodule.getTxnType());
        cv.put(EMVPAYLOAD, tracemodule.getCard_no());
        cv.put(AMOUNT, tracemodule.getAmount());
        cv.put(STATUS, tracemodule.getStatus());
        long row = database.insert(TABLE_NAME_TRACE, null, cv);
        database.close();
        return row;
    }

    /*has data*/
    public int hasTracedata(String id) {
        String image = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = " select * from  trace ";//where txnType = '" + id + "'"
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.getCount() > 0) {
            return 1;
        }
        return 0;
    }

    /*get trace module*/
    public Tracemodule getTraceTxn(String id) {
        String image = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = " select * from  trace";// where txnType = '" + id + "'"
        Cursor cursor = db.rawQuery(selectQuery, null);
        Tracemodule tracemodule = null;
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToPosition(0);
            do {
                String stan = cursor.getString(cursor.getColumnIndex(STAN));
                String crrn = cursor.getString(cursor.getColumnIndex(CRRN));
                String emv = cursor.getString(cursor.getColumnIndex(EMVPAYLOAD));
                String amount = cursor.getString(cursor.getColumnIndex(AMOUNT));
                String status = cursor.getString(cursor.getColumnIndex(STATUS));
                String txnType = cursor.getString(cursor.getColumnIndex(TXNTYPE));
                tracemodule = new Tracemodule(stan, crrn, txnType, emv, amount, status);

            } while (cursor.moveToNext());
        }
        return tracemodule;
    }

    /*delet complete trace*/
    public void deletTrace(String crrn) {
        Log.d(TAG, "deletTrace: ");
        database = this.getWritableDatabase();
        String where = CRRN + "=?";
        String[] whereValue = {crrn};
        database.delete(TABLE_NAME_TRACE, where, whereValue);
        database.close();
    }


    /*pre auth*/
    public long insertPreauthInfo(PreAuthModel preAuth) {
        database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(CARD_NO, preAuth.getCardNumber());
        cv.put(AUTH_CODE, preAuth.getAuthCode());
        cv.put(AUTH_AMOUNT, preAuth.getAmount());
        cv.put(EMV, preAuth.getEmvLoad());
        cv.put(AUTH_DATE, preAuth.getDate());
        long row = database.insert(TABLE_NAME_PREAUTH, null, cv);
        database.close();
        return row;
    }


	/*get all pre auth*/

    public ArrayList<PreAuthModel> getAllPreAuth() {
        ArrayList<PreAuthModel> preAuthModelArrayList = new ArrayList();
        preAuthModelArrayList.clear();
        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME_PREAUTH, columnsAuth, null,
                null, null, null, null);
        if (null != cursor && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String card_no = cursor.getString(cursor.getColumnIndex(CARD_NO));
                String auth_code = cursor.getString(cursor.getColumnIndex(AUTH_CODE));
                String auth_amount = cursor.getString(cursor.getColumnIndex(AUTH_AMOUNT));
                String auth_date = cursor.getString(cursor.getColumnIndex(AUTH_DATE));
                String emv = cursor.getString(cursor.getColumnIndex(EMV));
                PreAuthModel preAuthModel = new PreAuthModel(auth_code, auth_date, auth_amount, card_no, emv);
                preAuthModelArrayList.add(preAuthModel);
            }
        }
        cursor.close();
        database.close();
        return preAuthModelArrayList;
    }



    /*update pre auth table*/


    public void deleteAuthComplete(String auth_code) {
        database = this.getWritableDatabase();
        String where = AUTH_CODE + "=?";
        String[] whereValue = {auth_code};
        database.delete(TABLE_NAME_PREAUTH, where, whereValue);
        database.close();
    }


    //新增柜员
    public long insertTellerInfo(TellerInfo tellerInfo) {
        database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(FIELD_TELLER_N0, tellerInfo.getTellerNo());
        cv.put(FIELD_TELLER_PASSWORD, tellerInfo.getTellerPwd());

        long row = database.insert(TABLE_NAME, null, cv);
        database.close();
        return row;
    }

    //删除柜员
    public void delete(String tellerNo) {
        database = this.getWritableDatabase();
        String where = FIELD_TELLER_N0 + "=?";
        String[] whereValue = {tellerNo};
        database.delete(TABLE_NAME, where, whereValue);
        database.close();

    }

    //修改柜员密码
    public int updateManagerPwd(String newPwd, String tellerNo) {
        database = this.getWritableDatabase();
        String where = FIELD_TELLER_N0 + "=?";
        String[] whereValue = {tellerNo};
        ContentValues cv = new ContentValues();
        cv.put(FIELD_TELLER_PASSWORD, newPwd);

        int count = database.update(TABLE_NAME, cv, where, whereValue);
        database.close();
        return count;
    }

    //查找柜员编号是否存在，是则返回
    public TellerInfo qureyTellerNoExist(String tellerNo) {
        database = this.getReadableDatabase();

        TellerInfo tellerInfo = null;

        Cursor cursor = database.query(TABLE_NAME, columns, " " + FIELD_TELLER_N0 + " = ?",
                new String[]{tellerNo}, null, null, null);

        if (null != cursor && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                tellerInfo = new TellerInfo();
                tellerInfo.setTellerNo(cursor.getString(cursor.getColumnIndex(FIELD_TELLER_N0)));
                tellerInfo.setTellerPwd(cursor.getString(cursor.getColumnIndex(FIELD_TELLER_PASSWORD)));
                ;
            }
        }
        cursor.close();
        database.close();
        return tellerInfo;
    }

    //查找柜员编号是否存在，是则返回
    public ArrayList<TellerInfo> qureyAllTeller() {
        ArrayList<TellerInfo> tellerInfoList = new ArrayList<TellerInfo>();
        tellerInfoList.clear();

        database = this.getReadableDatabase();
        Cursor cursor = database.query(TABLE_NAME, columns, null,
                null, null, null, null);

        if (null != cursor && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                TellerInfo tellerInfo = new TellerInfo();
                String tellerNoTemp = cursor.getString(cursor.getColumnIndex(FIELD_TELLER_N0));

                if (!tellerNoTemp.equals("99") && !tellerNoTemp.equals("888")) {
                    tellerInfo.setTellerNo(tellerNoTemp);
                    tellerInfo.setTellerPwd(cursor.getString(cursor.getColumnIndex(FIELD_TELLER_PASSWORD)));
                    tellerInfoList.add(tellerInfo);
                }
            }
        }
        cursor.close();
        database.close();
        return tellerInfoList;
    }

    //删除数据库所有记录
    public void deleteGeneralReqParam() {
        database = this.getWritableDatabase();
        String sql = "delete from " + TABLE_NAME;
        database.execSQL(sql);

        String sql1 = "select * from " + TABLE_NAME;
        database.execSQL(sql1);

        String sql2 = "update " + TABLE_NAME + " set " + FIELD_ID + "=0";
        database.execSQL(sql2);
        database.close();
    }

}
