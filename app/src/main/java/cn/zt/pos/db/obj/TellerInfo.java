package cn.zt.pos.db.obj;

/**
 * Created by zhou on 2016/6/17.
 * 柜员类
 */
public class TellerInfo {

    private String tellerNo;//柜台号
    private String tellerPwd;//柜员密码

    public TellerInfo(String tellerNo, String tellerPwd) {
        this.tellerNo = tellerNo;
        this.tellerPwd = tellerPwd;
    }

    public TellerInfo() {
    }

    public String getTellerNo() {
        return tellerNo;
    }

    public void setTellerNo(String tellerNo) {
        this.tellerNo = tellerNo;
    }

    public String getTellerPwd() {
        return tellerPwd;
    }

    public void setTellerPwd(String tellerPwd) {
        this.tellerPwd = tellerPwd;
    }
}
