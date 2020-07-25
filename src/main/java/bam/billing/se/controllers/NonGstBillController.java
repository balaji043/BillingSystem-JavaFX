package bam.billing.se.controllers;

import bam.billing.se.helpers.CustomerService;
import bam.billing.se.models.Bill;
import bam.billing.se.models.Customer;
import bam.billing.se.models.Product;
import bam.billing.se.utils.BillingSystemUtils;
import bam.billing.se.utils.Preferences;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class NonGstBillController {
    @FXML
    private VBox root, main;
    @FXML
    private GridPane pane;
    @FXML
    private Text lStoreName, lStoreStreet, lStoreAddandPhone, lStoreGSTin;
    @FXML
    private Text lCusStreet;
    @FXML
    private Text lInvoiceNo, lDate, lCopy;
    @FXML
    private Text lCusName, lCusMob, lGSTin;
    @FXML
    private Text billAmount, roundedOff;
    @FXML
    private Text lTotalAmountWords, lTotalPlusTaxNum;
    @FXML
    private Text lBankName, lBankAccNo, lBranchName, lBankIFSC, forStoreName;

    private Preferences preferences = Preferences.getPreferences();


    public void setBill(Bill bill) {

        //Top Header
        lStoreName.setText(preferences.getStoreName());
        lStoreStreet.setText(preferences.getAddressLineOne());
        lStoreAddandPhone.setText(preferences.getAddressLineTwo() + " PH : " + preferences.getPhoneNumber());
        lStoreGSTin.setText("GSTIN : " + preferences.getGstInNumber());

        //invoice and date

        lInvoiceNo.setText(": " + bill.getInvoice());
        boolean b;
        try {
            Long.parseLong(bill.getDate());
            b = true;
        } catch (Exception e) {
            b = false;
        }
        if (b)
            lDate.setText(": " + BillingSystemUtils.formatDateTimeString(Long
                    .parseLong(bill.getDate())));
        else
            lDate.setText(": " + bill.getDate());


        //billing address section

        Customer customer = CustomerService.getCustomerInfo(bill.getCustomerName());
        if (customer != null) {
            lCusName.setText(": " + customer.getName());
            lCusStreet.setText(": " + customer.getStreetAddress()
                    + "\n  " + customer.getCity() + "\n  " + customer.getState());
            lCusMob.setText(": " + customer.getPhone());
            lGSTin.setText(": " + customer.getGstIn());
        } else {
            lCusName.setText(": " + bill.getCustomerName());
            lCusStreet.setText(": " + bill.getAddress());
            lCusMob.setText(": " + bill.getMobile());
            lGSTin.setText(": " + bill.getGSTNo());
        }
        initGridPane(bill);
        //TAX section
        //TAX OVERALL TOTAL
        billAmount.setText(bill.getTotalAmountBeforeRoundOff());
        roundedOff.setText(bill.getRoundOff());

        double t = Double.parseDouble(bill.getTotalAmount());
        lTotalPlusTaxNum.setText(String.format("RS. %.2f", t));
        lTotalAmountWords.setText("( " + BillingSystemUtils.convert((int) t) + " Rupees Only )");//Bank Details
        lBankName.setText(preferences.getBankName());
        lBranchName.setText(preferences.getBankBranchName());
        lBankAccNo.setText(preferences.getBankAccountNumber());
        lBankIFSC.setText(preferences.getIfscCode());
        forStoreName.setText("For " + preferences.getStoreName());

        //Central Half

    }

    private void initGridPane(Bill bill) {
        GridPane.setRowSpan(pane, 30);

        int i = 0;
        Product product = new Product();
        ObservableList<Product> products = FXCollections.observableArrayList();
        products.addAll(bill.getProducts());
        int s = Integer.parseInt(preferences.getProductMaxLimitPerBill()) - products.size() + 1 + 5;
        for (; i <= s; i++) products.add(product);

        i = 1;
        for (Product p : products) {

            Text sl = new Text();
            sl.setText("" + p.getSl());
            sl.setStyle("-fx-font-size:8px;");
            StackPane stackPane = new StackPane(sl);
            stackPane.setMinHeight(9);
            pane.add(stackPane, 0, i);

            Text des = new Text();
            des.setText(p.getName());
            des.setStyle("-fx-font-size:8px;");
            StackPane pane1 = new StackPane(des);
            pane1.setAlignment(Pos.CENTER_LEFT);
            pane.add(pane1, 1, i);

            Text hsn = new Text();
            hsn.setText(p.getHsn());
            hsn.setStyle("-fx-font-size:8px;");
            stackPane = new StackPane(hsn);
            stackPane.setAlignment(Pos.CENTER_RIGHT);
            pane.add(stackPane, 2, i);

            Text qty = new Text();
            qty.setStyle("-fx-font-size:8px;");
            qty.setText(p.getQty());
            stackPane = new StackPane(qty);
            stackPane.setPadding(new Insets(0, 10, 0, 0));
            stackPane.setAlignment(Pos.CENTER_RIGHT);
            pane.add(stackPane, 3, i);

            Text rate = new Text();
            rate.setText(p.getRate());
            rate.setStyle("-fx-font-size:8px;");
            stackPane = new StackPane(rate);
            stackPane.setPadding(new Insets(0, 10, 0, 0));
            stackPane.setAlignment(Pos.CENTER_RIGHT);
            pane.add(stackPane, 4, i);

            Text per = new Text();
            per.setText(p.getPer());
            per.setStyle("-fx-font-size:8px;");
            stackPane = new StackPane(per);
            stackPane.setAlignment(Pos.CENTER);
            pane.add(stackPane, 5, i);


            Text total = new Text();
            total.setText(p.getTotalAmount());
            total.setTextAlignment(TextAlignment.RIGHT);
            total.setStyle("-fx-font-size:8px;");
            stackPane = new StackPane(total);
            stackPane.setPadding(new Insets(0, 8, 0, 0));
            stackPane.setAlignment(Pos.CENTER_RIGHT);
            pane.add(stackPane, 6, i);
            RowConstraints constraints = new RowConstraints(15);
            pane.getRowConstraints().addAll(constraints);
            i++;
        }
        GridPane.setRowSpan(pane, 30);
    }

    public VBox getRoot() {
        return root;
    }

    public VBox getMain() {
        return main;
    }

    public void setCopyText(String s) {
        lCopy.setText(s);
    }

}
