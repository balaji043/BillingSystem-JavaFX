package bam.billing.se.models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

public class Product extends RecursiveTreeObject<Product> {

    public boolean ready;
    private String sl;
    private String name;
    private String hsn;
    private String qty;
    private String rate;
    private String per;
    private String totalAmount;

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
        initialize();
    }

    public void initialize() {
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

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void setHsn(String hsn) {
        this.hsn = hsn;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public void setPer(String per) {
        this.per = per;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }
}
