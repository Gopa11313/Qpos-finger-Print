package cn.zt.upi;

/**
 * Created by QPay on 11/14/2018.
 */

public class UPIPOJO {

    private String crrn;
    private String customerName;
    private String metrchantName;
    private String stan;
    private String dateTime;
    private double transactionAmount;
    private String type;
    private double refundAmount;

    public UPIPOJO() {
    }

    public String getDateTime() {
        return dateTime;
    }

    public UPIPOJO setDateTime(String dateTime) {
        this.dateTime = dateTime;
        return this;
    }

    public String getCrrn() {
        return crrn;
    }

    public UPIPOJO setCrrn(String crrn) {
        this.crrn = crrn;
        return this;
    }

    public String getCustomerName() {
        return customerName;
    }

    public UPIPOJO setCustomerName(String customerName) {
        this.customerName = customerName;
        return this;
    }

    public String getMetrchantName() {
        return metrchantName;
    }

    public UPIPOJO setMetrchantName(String metrchantName) {
        this.metrchantName = metrchantName;
        return this;
    }

    public String getStan() {
        return stan;
    }

    public UPIPOJO setStan(String stan) {
        this.stan = stan;
        return this;
    }

    public double getTransactionAmount() {
        return transactionAmount;
    }

    public UPIPOJO setTransactionAmount(double transactionAmount) {
        this.transactionAmount = transactionAmount;
        return this;
    }

    public String getType() {
        return type;
    }

    public UPIPOJO setType(String type) {
        this.type = type;
        return this;
    }

    public double getRefundAmount() {
        return refundAmount;
    }

    public UPIPOJO setRefundAmount(double refundAmount) {
        this.refundAmount = refundAmount;
        return  this;
    }
}
