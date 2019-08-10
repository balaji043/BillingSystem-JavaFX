package sample.Model;


import sample.Utils.BillingSystemUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class PurchaseBill {
    private String dateInLong;
    private String companyName;
    private String invoiceNo;
    private String amountBeforeTax;
    private String twelve;
    private String eighteen;
    private String twentyEight;
    private String totalAmount;
    private LocalDate date;
    private String hasSentToAuditor;
    private String hasGoneToAuditorString;
    private String status;
    private String dateCleared;
    private String others;


    public PurchaseBill(String dateInLong
            , String companyName
            , String invoiceNo
            , String amountBeforeTax
            , String twelve
            , String eighteen
            , String twentyEight
            , String totalAmount
            , String hasSentToAuditor
            , String others
            , String dateCleared
            , String status) {

        this.dateInLong = dateInLong;
        this.companyName = companyName;
        this.invoiceNo = invoiceNo;
        this.amountBeforeTax = amountBeforeTax;
        this.twelve = twelve;
        this.eighteen = eighteen;
        this.twentyEight = twentyEight;
        this.totalAmount = totalAmount;
        this.hasSentToAuditor = hasSentToAuditor;
        this.hasGoneToAuditorString = hasGoneToAuditor() ? "YES" : "NO";
        this.status = status;
        this.dateCleared = dateCleared;
        this.others = others;

        Date d = new Date(Long.parseLong(dateInLong));
        date = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public String getDateInLong() {
        return dateInLong;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public String getAmountBeforeTax() {
        return amountBeforeTax;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getTwelve() {
        return twelve;
    }

    public String getEighteen() {
        return eighteen;
    }

    public String getTwentyEight() {
        return twentyEight;
    }

    public String getHasSentToAuditor() {
        return hasSentToAuditor;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDateClearedAsLong() {
        return dateCleared;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public String getDateCleared() {
        String dateString = "";
        try {
            dateString = BillingSystemUtils.formatDateTimeString(Long.parseLong(dateCleared));
        } catch (Exception e) {
            System.out.println(dateString);
        }
        return dateString;


    }

    @Override
    public String toString() {
        return "PurchaseBill{" +
                "dateInLong='" + dateInLong + '\'' +
                ", companyName='" + companyName + '\'' +
                ", invoiceNo='" + invoiceNo + '\'' +
                ", amountBeforeTax='" + amountBeforeTax + '\'' +
                ", twelve='" + twelve + '\'' +
                ", eighteen='" + eighteen + '\'' +
                ", twentyEight='" + twentyEight + '\'' +
                ", totalAmount='" + totalAmount + '\'' +
                '}';
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean hasGoneToAuditor() {
        return hasSentToAuditor.equals("true");
    }

    public String getHasGoneToAuditorString() {
        return hasGoneToAuditorString;
    }

    public String getDateAsString() {
        return BillingSystemUtils.formatDateTimeString(Long.parseLong(dateInLong));
    }

    public void setDateCleared(String dateCleared) {
        this.dateCleared = dateCleared;
    }
}
