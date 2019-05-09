package sample.Model;


import sample.Utils.BillingSystemUtils;

import java.time.LocalDate;
import java.time.ZoneId;
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

    public PurchaseBill(String dateInLong
            , String companyName
            , String invoiceNo
            , String amountBeforeTax
            , String twelve
            , String eighteen
            , String twentyEight
            , String totalAmount
            , String hasSentToAuditor) {
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
        Date d = new Date(Long.parseLong(dateInLong));
        date = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
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

    public String getTwelve() {
        return twelve;
    }

    public String getEighteen() {
        return eighteen;
    }

    public String getTwentyEight() {
        return twentyEight;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getHasSentToAuditor() {
        return hasSentToAuditor;
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

    public String getDateAsStrng() {
        return BillingSystemUtils.formatDateTimeString(Long.parseLong(dateInLong));
    }

    public boolean hasGoneToAuditor() {
        return hasSentToAuditor.equals("true");
    }

    public String getHasGoneToAuditorString() {
        return hasGoneToAuditorString;
    }
}
