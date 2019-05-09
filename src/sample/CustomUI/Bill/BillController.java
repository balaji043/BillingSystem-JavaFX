package sample.CustomUI.Bill;

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
import sample.Database.DatabaseHelper_Cutomer;
import sample.Model.Bill;
import sample.Model.Customer;
import sample.Model.Product;
import sample.Utils.BillingSystemUtils;
import sample.Utils.Preferences;

@SuppressWarnings("Duplicates")
public class BillController {
    @FXML
    private VBox root, main;
    @FXML
    private GridPane pane;
    @FXML
    private Text lStoreName, lStoreStreet, lStoreAddandPhone, lStoreGSTin;
    @FXML
    private Text cGstper, cGSTAmt, sGSTPer, sGSTAmt;
    @FXML
    private Text lCusStreet;
    @FXML
    private Text lInvoiceNo, lDate, lCopy;
    @FXML
    private Text lCusName, lCusMob, lGSTin;
    @FXML
    private Text lTotalAmountNum, lTotalAmountWords, lTotalPlusTaxNum;
    @FXML
    private Text lTaxAmount12, lTaxAmount18, lTaxAmount28;
    @FXML
    private Text lTax6perCAmount, lTax8perCAmount, lTax12perCAmount;
    @FXML
    private Text lTax6perSAmount, lTax8perSAmount, lTax12perSAmount;
    @FXML
    private Text lBankName, lBankAccNo, lBranchName, lBankIFSC, forStoreName;
    @FXML
    private Text lgrossAmount, laSGST, laCGST, roundedOff;

    private Preferences preferences = Preferences.getPreferences();

    public void setBill(Bill bill) {
        //Top Header
        lStoreName.setText(preferences.getName());
        lStoreStreet.setText(preferences.getAddress1());
        lStoreAddandPhone.setText(preferences.getAddress2()
                + " PH : " + preferences.getPhone());
        lStoreGSTin.setText("GSTIN : " + preferences.getGstin());

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

        Customer customer = DatabaseHelper_Cutomer.getCustomerInfo(bill.getCustomerName());
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
        lTotalAmountNum.setText("RS. " + bill.getTotalAmountBeforeRoundOff());
        //TAX section
        //TAX OVERALL TOTAL

        roundedOff.setText(bill.getRoundedOff());
        lTotalPlusTaxNum.setText("RS. " + bill.getTotalAmount());
        lgrossAmount.setText(bill.getGross());
        lTotalAmountWords.setText("( " + BillingSystemUtils.convert((int) Double.parseDouble(bill.getTotalAmount())) + " Rupees Only )");//Bank Details
        lBankName.setText(preferences.getBank());
        lBranchName.setText(preferences.getBranch());
        lBankAccNo.setText(preferences.getAcc());
        lBankIFSC.setText(preferences.getIfsc());
        forStoreName.setText("For " + preferences.getName());

        //Central Half

        laCGST.setText(bill.getHalfTax());
        laSGST.setText(bill.getHalfTax());
        lTax6perCAmount.setText(bill.getGst12Half());
        lTax8perCAmount.setText(bill.getGst18Half());
        lTax12perCAmount.setText(bill.getGst28Half());

        //State Half

        lTax6perSAmount.setText(bill.getGst12Half());
        lTax8perSAmount.setText(bill.getGst18Half());
        lTax12perSAmount.setText(bill.getGst28Half());
    }

    private void initGridPane(Bill bill) {
        GridPane.setRowSpan(pane, 30);
        sGSTAmt.setText("S-GST\n AMT");
        cGSTAmt.setText("C-GST\n AMT");
        sGSTPer.setText("S-GST\n %");
        cGstper.setText("C-GST\n %");

        sGSTAmt.setTextAlignment(TextAlignment.CENTER);
        cGSTAmt.setTextAlignment(TextAlignment.CENTER);
        sGSTPer.setTextAlignment(TextAlignment.CENTER);
        cGstper.setTextAlignment(TextAlignment.CENTER);

        int i = 0;
        Product product = new Product();
        ObservableList<Product> products = FXCollections.observableArrayList();
        products.addAll(bill.getProducts());
        int s = Integer.parseInt(preferences.getLimit()) - products.size() + 1;
        for (; i <= s; i++) products.add(product);

        i = 1;
        for (Product p : products) {

            Text sl = new Text();
            sl.setText("" + p.getSl());
            sl.setStyle("-fx-font-size:8px;");
            StackPane stackPane = new StackPane(sl);
            stackPane.setAlignment(Pos.CENTER);
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
            rate.setText(p.getSingleOrg());
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

            Text cgstPer = new Text();
            cgstPer.setText(p.getHalfTaxPer());
            cgstPer.setStyle("-fx-font-size:8px;");
            stackPane = new StackPane(cgstPer);
            stackPane.setPadding(new Insets(0, 10, 0, 0));
            stackPane.setAlignment(Pos.CENTER);
            pane.add(stackPane, 6, i);

            Text cgstAmt = new Text();
            cgstAmt.setText(p.getHalfTaxAmt());
            cgstAmt.setTextAlignment(TextAlignment.RIGHT);
            cgstAmt.setStyle("-fx-font-size:8px;");
            stackPane = new StackPane(cgstAmt);
            stackPane.setAlignment(Pos.CENTER_RIGHT);
            pane.add(new StackPane(cgstAmt), 7, i);

            Text sgstPer = new Text();
            sgstPer.setText(p.getHalfTaxPer());
            sgstPer.setStyle("-fx-font-size:8px;");
            stackPane = new StackPane(sgstPer);
            stackPane.setAlignment(Pos.CENTER);
            pane.add(stackPane, 8, i);

            Text sgstAmt = new Text();
            sgstAmt.setTextAlignment(TextAlignment.RIGHT);
            sgstAmt.setText(p.getHalfTaxAmt());
            sgstAmt.setStyle("-fx-font-size:8px;");
            stackPane = new StackPane(sgstAmt);
            stackPane.setAlignment(Pos.CENTER_RIGHT);
            stackPane.setPadding(new Insets(0, 10, 0, 0));
            pane.add(stackPane, 9, i);

            Text total = new Text();
            total.setText(p.getTotalAmount());
            total.setTextAlignment(TextAlignment.RIGHT);
            total.setStyle("-fx-font-size:8px;");
            stackPane = new StackPane(total);
            stackPane.setPadding(new Insets(0, 8, 0, 0));
            stackPane.setAlignment(Pos.CENTER_RIGHT);
            pane.add(stackPane, 10, i);
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
