package cn.zt.pos.db.obj;

import java.io.Serializable;

/**
 * 主管密码
 * Created by zhou on 2016/4/22.
 */
public class ManagerInfo implements Serializable {
    private String managerName;//管理员姓名
    private String managerMobile;//管理员手机号
    private String managerPwd;//管理员密码
    private String managerAuthCode;//管理员分配的权限  使用1111111111来标识相应权限，方便扩展

    public ManagerInfo() {
    }

    public ManagerInfo(String managerName, String managerMobile, String managerPwd, String managerAuthCode) {
        this.managerName = managerName;
        this.managerMobile = managerMobile;
        this.managerPwd = managerPwd;
        this.managerAuthCode = managerAuthCode;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public String getManagerMobile() {
        return managerMobile;
    }

    public void setManagerMobile(String managerMobile) {
        this.managerMobile = managerMobile;
    }

    public String getManagerPwd() {
        return managerPwd;
    }

    public void setManagerPwd(String managerPwd) {
        this.managerPwd = managerPwd;
    }

    public String getManagerAuthCode() {
        return managerAuthCode;
    }

    public void setManagerAuthCode(String managerAuthCode) {
        this.managerAuthCode = managerAuthCode;
    }
}
