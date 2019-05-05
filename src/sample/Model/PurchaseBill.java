package sample.Model;

public class PurchaseBill {
    private String dateInLong;
    private String companyName;
    private String invoiceNo;
    private String amountBeforeTax;
    private String[] taxAmounts;
    private String totalAmount;

    public PurchaseBill(String dateInLong, String companyName, String invoiceNo, String amountBeforeTax, String[] taxAmounts, String totalAmount) {
        this.dateInLong = dateInLong;
        this.companyName = companyName;
        this.invoiceNo = invoiceNo;
        this.amountBeforeTax = amountBeforeTax;
        this.taxAmounts = taxAmounts;
        this.totalAmount = totalAmount;
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

    public String[] getTaxAmounts() {
        return taxAmounts;
    }

    public String getTotalAmount() {
        return totalAmount;
    }
}
