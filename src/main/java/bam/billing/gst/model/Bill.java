package bam.billing.gst.model;

import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Bill {
    private String billId;
    private String invoice;
    private String date;
    private String customerName;
    private String customerId;
    private String address;
    private String mobile;
    private String gstNo;
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
    private String time;
    private LocalDate localDate;
    private String totalAmountBeforeRoundOff;
    private double total;
    private String roundedOff;
    private String place;
    private String tax12BeforeTotal;
    private String tax18BeforeTotal;
    private String tax28BeforeTotal;


    public Bill(String billId, String invoice, String date
            , String customerName, String customerId
            , String address, String mobile
            , String gstNo, ObservableList<Product> products, String userName) {
        this.billId = billId;
        this.invoice = invoice;
        this.date = date;
        this.customerName = customerName;
        this.customerId = customerId;
        this.address = address;
        this.mobile = mobile;
        this.gstNo = gstNo;
        this.products = products;
        this.userName = userName;

        float gst12Hal = 0;
        float gst12Tota;
        float gst18Hal = 0;
        float gst18Tota;
        float gst28Hal = 0;
        float gst28Tota;
        float gstTotal;
        float tax12BTotal = 0;
        float tax18BTotal = 0;
        float tax28BTotal = 0;
        double totalInner = 0, r = 0.00f;

        for (Product product : products) {
            switch (product.getTax()) {
                case "12": {
                    gst12Hal = gst12Hal + Float.parseFloat(product.getHalfTaxAmt());
                    tax12BTotal += product.getTotalOriginalAmount();
                    break;
                }
                case "18": {
                    gst18Hal = gst18Hal + Float.parseFloat(product.getHalfTaxAmt());
                    tax18BTotal += product.getTotalOriginalAmount();
                    break;
                }
                case "28": {
                    gst28Hal = gst28Hal + Float.parseFloat(product.getHalfTaxAmt());
                    tax28BTotal += product.getTotalOriginalAmount();
                    break;
                }
                default:
                    break;
            }
            totalInner = totalInner + Float.parseFloat(product.getTotalAmount());
        }
        tax12BeforeTotal = String.format("%.2f", tax12BTotal);
        tax18BeforeTotal = String.format("%.2f", tax18BTotal);
        tax28BeforeTotal = String.format("%.2f", tax28BTotal);

        gst12Tota = gst12Hal * 2;
        gst18Tota = gst18Hal * 2;
        gst28Tota = gst28Hal * 2;
        gstTotal = gst12Tota + gst18Tota + gst28Tota;
        gross = "" + String.format("%.2f", totalInner - gstTotal);
        halfTax = "" + String.format("%.2f", gst12Hal + gst18Hal + gst28Hal);

        totalTaxAmount = "" + String.format("%.2f", gstTotal);
        gst12Half = "" + String.format("%.2f", gst12Hal);
        gst12Total = "" + String.format("%.2f", gst12Tota);
        gst18Half = "" + String.format("%.2f", gst18Hal);
        gst18Total = "" + String.format("%.2f", gst18Tota);
        gst28Half = "" + String.format("%.2f", gst28Hal);
        gst28Total = "" + String.format("%.2f", gst28Tota);
        totalAmountBeforeRoundOff = String.format("%.2f", totalInner);
        String sign = "";
        if ((totalInner - (int) totalInner) != 0.00) {
            r = (totalInner - (int) totalInner);
            if (r >= 0.50) {
                r = 1 - r;
                totalInner = Math.ceil(totalInner);
                sign = "+";
            } else {
                totalInner = Math.floor(totalInner);
                sign = "-";
            }
        }
        roundedOff = String.format("%s %.2f", sign, r);
        this.totalAmount = String.format("%.2f", totalInner);
        this.total = totalInner;
        try {
            this.place = address.split("\n")[2];
        } catch (Exception ignored) {
        }
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

    public String getGstNo() {
        return gstNo;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        this.time = time;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public double getTotal() {
        return total;
    }

    public String getTotalAmountBeforeRoundOff() {
        return totalAmountBeforeRoundOff;
    }

    public String getRoundedOff() {
        return roundedOff;
    }

    public String getPlace() {
        return place;
    }

    public String getTax12BeforeTotal() {
        return tax12BeforeTotal;
    }

    public String getTax18BeforeTotal() {
        return tax18BeforeTotal;
    }

    public String getTax28BeforeTotal() {
        return tax28BeforeTotal;
    }

    @Override
    public String toString() {
        return "bill{" +
                "billId='" + billId + '\'' +
                ", invoice='" + invoice + '\'' +
                ", date='" + date + '\'' +
                ", customerName='" + customerName + '\'' +
                ", totalAmount='" + totalAmount + '\'' +
                ", gross='" + gross + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
