package sample.Model;

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
    private String GSTNo;
    private ObservableList<Product> products;
    private String totalAmount;
    private String total;

    private String userName;
    private String time;
    private LocalDate localDate;

    public Bill(String billId, String invoice, String date
            , String customerName, String customerId
            , String address, String mobile
            , String GSTNo, ObservableList<Product> products, String userName) {
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

        float total = 0;

        for (Product product : products) {
            total = total + Float.parseFloat(product.getTotalAmount());
        }
        totalAmount = "" + String.format("%.2f", total);
        this.total = String.format("%.2f", Math.ceil(Double.parseDouble(getTotalAmount())));
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

    public String getTotal() {
        return total;
    }

    @Override
    public String toString() {
        return "Bill{" +
                "billId='" + billId + '\'' +
                ", invoice='" + invoice + '\'' +
                ", date='" + date + '\'' +
                ", customerName='" + customerName + '\'' +
                ", totalAmount='" + totalAmount + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
