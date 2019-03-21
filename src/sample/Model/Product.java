package sample.Model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

public class Product extends RecursiveTreeObject<Product> {

    private String sl;
    private String name;
    private String hsn;
    private String qty;

    private String rate;
    private String per;

    private String totalAmount;

    public boolean ready;

    public Product() {
        sl = "";
        name = "";
        hsn = "";
        qty = "";
        rate = "";
        per = "";
        totalAmount = "";
        ready = false;
    }

    public Product(String name, String hsn, String qty, String rate, String per) {

        this.name = name;
        this.hsn = hsn;
        this.qty = qty;
        this.rate = rate;
        this.per = per;

        float q = Integer.parseInt(qty);
        float r = Float.parseFloat(this.rate);
        float originalAmount = q * r;


        totalAmount = String.format("%.2f", originalAmount);
        ready = true;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHsn() {
        return hsn;
    }

    public String getQty() {
        return qty;
    }

    public String getRate() {
        return rate;
    }

    public String getPer() {
        return per;
    }

    public String getSl() {
        return sl;
    }

    public void setSl(String sl) {
        this.sl = sl;
    }

    public String getTotalAmount() {
        return totalAmount;
    }


}
