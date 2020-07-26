package bam.billing.nongst.models;

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
    private String totalAmountBeforeRoundOff;
    private String userName;
    private String time;
    private LocalDate localDate;
    private double total;

    private String roundOff;
    private String place;

    public Bill(String billId, String invoice, String date
            , Customer customer, ObservableList<Product> products, String userName) {
        this.billId = billId;
        this.invoice = invoice;
        this.date = date;
        this.customerName = customer.getName();
        this.customerId = customer.getId();
        this.address = customer.getStreetAddress() + "\n" + customer.getCity() + "\n" + customer.getState();
        this.mobile = customer.getPhone();
        this.GSTNo = customer.getGstIn();
        this.products = products;
        this.userName = userName;

        initialize();
    }

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
        this.initialize();
    }

    public Bill() {
    }

    public void initialize() {
        double total = 0, r = 0.00f;

        for (Product product : products) {
            total = total + Float.parseFloat(product.getTotalAmount());
        }

        totalAmountBeforeRoundOff = String.format("%.2f", total);

        String sign = "";
        if ((total - (int) total) != 0.00) {
            r = (total - (int) total);
            if (r >= 0.50) {
                r = 1 - r;
                total = Math.ceil(total);
                sign = "+";
            } else {
                total = Math.floor(total);
                sign = "-";
            }
        }
        roundOff = String.format("%s%.2f", sign, r);

        totalAmount = String.format("%.2f", total);
        this.total = total;

        try {
            this.place = address.split("\n")[2];
        } catch (Exception e) {
            System.out.println(e.getMessage());
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


    public String getRoundOff() {
        return roundOff;
    }

    public String getTotalAmountBeforeRoundOff() {
        return totalAmountBeforeRoundOff;
    }

    //DO NOT DELETE. THIS IS FOR TABLE VIEW TOTAL AMOUNT SORT AND DATE SORT
    public double getTotal() {
        return total;
    }

    public LocalDate getLocalDate() {
        return localDate;
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

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }


    public void setBillId(String billId) {
        this.billId = billId;
    }

    public void setInvoice(String invoice) {
        this.invoice = invoice;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setGSTNo(String GSTNo) {
        this.GSTNo = GSTNo;
    }

    public void setProducts(ObservableList<Product> products) {
        this.products = products;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setTotalAmountBeforeRoundOff(String totalAmountBeforeRoundOff) {
        this.totalAmountBeforeRoundOff = totalAmountBeforeRoundOff;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setRoundOff(String roundOff) {
        this.roundOff = roundOff;
    }
}
