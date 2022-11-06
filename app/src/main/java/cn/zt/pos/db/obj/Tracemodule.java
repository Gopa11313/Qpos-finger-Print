package cn.zt.pos.db.obj;

/**
 * Created by deadlydragger on 1/3/19.
 */

public class Tracemodule {
    String stan,crrn,txnType,status, card_no,amount;

    public Tracemodule(String stan, String crrn, String txnType, String card_no, String amount, String status) {
        this.stan = stan;
        this.crrn = crrn;
        this.txnType = txnType;
        this.card_no = card_no;
        this.amount = amount;
        this.status=status;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public String getCrrn() {
        return crrn;
    }

    public void setCrrn(String crrn) {
        this.crrn = crrn;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCard_no() {
        return card_no;
    }

    public void setCard_no(String card_no) {
        this.card_no = card_no;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}