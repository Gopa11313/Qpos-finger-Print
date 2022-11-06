package cn.zt.pos.db.obj;

/**
 * Created by deadlydragger on 1/20/19.
 */

public class AID {
    String aid,cvm;
   public AID(String aid, String cvm){
        this.aid=aid;
        this.cvm=cvm;
    }

    public String getAid() {
        return aid;
    }

    public String getCvm() {
        return cvm;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public void setCvm(String cvm) {
        this.cvm = cvm;
    }
}
