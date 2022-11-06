package cn.zt.pos.db.obj;

/**
 * Created by deadlydragger on 1/2/19.
 */

public class PreAuthModel {
    String authCode,date,amount,cardNumber,emvLoad;

    public PreAuthModel(String authCode, String date, String amount, String cardNumber, String emvLoad) {
        this.authCode = authCode;
        this.date = date;
        this.amount = amount;
        this.cardNumber = cardNumber;
        this.emvLoad=emvLoad;
    }

    public String getEmvLoad() {
        return emvLoad;
    }

    public void setEmvLoad(String emvLoad) {
        this.emvLoad = emvLoad;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
