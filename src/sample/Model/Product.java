package sample.Model;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

public class Product extends RecursiveTreeObject<Product> {

    private String sl;
    private String name;
    private String hsn;
    private String qty;
    private String tax;
    private String rate;
    private String per;
    private String halfTaxPer;
    private String halfTaxAmt;
    private String totalAmount;
    private String singleOrg;
    private String gstAmount;
    public boolean ready;

    public Product() {
        sl = "";
        name = "";
        hsn = "";
        qty = "";
        tax = "";
        rate = "";
        per = "";
        totalAmount = "";
        halfTaxAmt = "";
        halfTaxPer = "";
        ready = false;
    }

    public Product(String name, String hsn, String qty
            , String tax, String rate, String per) {
        this.name = name;
        this.hsn = hsn;
        this.qty = qty;
        this.tax = tax;
        this.rate = rate;
        this.per = per;

        float q = Integer.parseInt(qty);

        float r = Float.parseFloat(rate);

        float t = Integer.parseInt(tax);

        float originalAmount = q * r;//r

        float gstAmount = originalAmount - originalAmount * (100 / (100 + t));

        singleOrg = String.format("%.2f",r-(r-r*(100/(100+t))));
        halfTaxPer = String.format("%.2f", t / 2);
        halfTaxAmt = String.format("%.2f", gstAmount / 2);
        totalAmount = String.format("%.2f", originalAmount);
        this.gstAmount = String.format("%.2f", gstAmount);
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

    public void setHsn(String hsn) {
        this.hsn = hsn;
    }

    public String getQty() {
        return qty;
    }

    public String getTax() {
        return tax;
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

    public String getHalfTaxPer() {
        return halfTaxPer;
    }

    public String getHalfTaxAmt() {
        return halfTaxAmt;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getSingleOrg() {
        return singleOrg;
    }

    public String getGstAmount() {
        return gstAmount;
    }
}
