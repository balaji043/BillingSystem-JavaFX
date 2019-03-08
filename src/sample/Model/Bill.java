package sample.Model;

import javafx.collections.ObservableList;

public class Bill {
    private String billId;
    private String invoice;
    private String date;
    private String customerName;
    private String customerId;
    private String address;
    private String mobile;
    private String GSTNo;
    private ObservableList<Product> products;
    private String totalAmount;
    private String gst12Half;
    private String gst12Total;
    private String gst18Half;
    private String gst18Total;
    private String gst28Half;
    private String gst28Total;
    private String totalTaxAmount;
    private String halfTax;
    private String gross;
    private String userName;

    public Bill(String billId, String invoice, String date
            , String customerName, String customerId
            , String address, String mobile
            , String GSTNo, ObservableList<Product> products,String userName) {
        this.billId = billId;
        this.invoice = invoice;
        this.date = date;
        this.customerName = customerName;
        this.customerId = customerId;
        this.address = address;
        this.mobile = mobile;
        this.GSTNo = GSTNo;
        this.products = products;
        this.userName = userName;

        float gst12Hal = 0;
        float gst12Tota;
        float gst18Hal = 0;
        float gst18Tota;
        float gst28Hal = 0;
        float gst28Tota;
        float gstTotal;
        float total = 0;

        for (Product product : products) {
            switch (product.getTax()) {
                case "12": {
                    gst12Hal = gst12Hal + Float.parseFloat(product.getHalfTaxAmt());
                    break;
                }
                case "18": {
                    gst18Hal = gst18Hal + Float.parseFloat(product.getHalfTaxAmt());
                    break;
                }
                case "28": {
                    gst28Hal = gst28Hal + Float.parseFloat(product.getHalfTaxAmt());
                    break;
                }
                default:
                    break;
            }
            total = total + Float.parseFloat(product.getTotalAmount());
        }
        gst12Tota = gst12Hal * 2;
        gst18Tota = gst18Hal * 2;
        gst28Tota = gst28Hal * 2;

        gstTotal = gst12Tota + gst18Tota + gst28Tota;
        gross = "" + String.format ("%.2f", total - gstTotal);
        halfTax = "" + String.format ("%.2f", gst12Hal + gst18Hal + gst28Hal);
        totalAmount = "" + String.format ("%.2f", total);
        totalTaxAmount = "" +String.format ("%.2f", gstTotal);
        gst12Half = "" + String.format ("%.2f", gst12Hal);
        gst12Total = "" + String.format ("%.2f", gst12Tota);
        gst18Half = "" + String.format ("%.2f", gst18Hal);
        gst18Total = "" + String.format ("%.2f", gst18Tota);
        gst28Half = "" + String.format ("%.2f", gst28Hal);
        gst28Total = "" + String.format ("%.2f", gst28Tota);
    }

    public String getBillId() {
        return billId;
    }

    public String getInvoice() {
        return invoice;
    }

    public String getDate() {
        return date;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getAddress() {
        return address;
    }

    public String getMobile() {
        return mobile;
    }

    public String getGSTNo() {
        return GSTNo;
    }

    public ObservableList<Product> getProducts() {
        return products;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getGst12Half() {
        return gst12Half;
    }

    public String getGst12Total() {
        return gst12Total;
    }

    public String getGst18Half() {
        return gst18Half;
    }

    public String getGst18Total() {
        return gst18Total;
    }

    public String getGst28Half() {
        return gst28Half;
    }

    public String getGst28Total() {
        return gst28Total;
    }

    public String getTotalTaxAmount() {
        return totalTaxAmount;
    }

    public String getHalfTax() {
        return halfTax;
    }

    public String getGross() {
        return gross;
    }

    public String getUserName() {
        return userName;
    }
}
