package cn.zt.upi;

public class UPIQR {
    private String tag;
    private String length;
    private String value;

    public UPIQR(String tag, String length, String value) {
        this.tag = tag;
        this.length = length;
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLength() {
        return length;
    }

    public void setLength(int length) {
        if (length <= 9){
            this.length = "0" + length;
        }else{
            this.length = "" + length;
        }
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "UPIQR{" +
                "tag='" + tag + '\'' +
                ", length='" + length + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
