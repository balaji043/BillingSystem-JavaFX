package sample.UI.Billing;

import com.jfoenix.controls.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.controlsfx.control.textfield.TextFields;
import sample.Alert.AlertMaker;
import sample.CustomUI.SingleProduct.SingleProduct;
import sample.Database.DatabaseHelper;
import sample.Main;
import sample.Model.Bill;
import sample.Model.Customer;
import sample.Model.Product;
import sample.Utils.BillingSystemUtils;
import sample.Utils.Preferences;

import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

public class BillingController {

    public JFXButton add, delete, submit;
    public JFXCheckBox checkBoxGST, isManual;
    public JFXComboBox<String> comboBills, comboBoxCustomer;
    public JFXListView<SingleProduct> listView;
    public JFXDatePicker manualDate;
    public Text labelBillFor, totalAmount;
    public BorderPane borderPane, root;
    public HBox hBox, outerTopHBox;
    public VBox manualD;
    private Main mainApp;
    private Customer customer = null;
    private Bill bill = null;
    private boolean isNewBill = true, ready = false;
    private Date date;
    private String billId, invoice, tableName = "Bills";
    private ObservableList<String> gst = FXCollections.observableArrayList(), nonGst = FXCollections.observableArrayList();
    private Preferences preferences = Preferences.getPreferences();
    private int limit = Integer.parseInt(preferences.getLimit());


    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;

        borderPane.setVisible(false);
        borderPane.setDisable(true);
        checkBoxGST.setSelected(true);
        manualDate.setDisable(true);
        comboBills.getItems().addAll("GST", "I-GST");
        comboBills.setEditable(false);
        gst = DatabaseHelper.getCustomerNameList(0);
        nonGst = DatabaseHelper.getCustomerNameList(1);

        comboBoxCustomer.getItems().addAll(gst);
        TextFields.bindAutoCompletion(comboBoxCustomer.getEditor(), comboBoxCustomer.getItems());

        checkBoxGST.setOnAction(e -> toggleCustomer());
        isManual.setOnAction(e -> {
            if (isManual.isSelected())
                manualDate.setDisable(false);
            else
                manualDate.setDisable(true);

        });

    }

    @FXML
    void handleAdd() {
        try {
            if (listView.getItems().size() < limit) {
                SingleProduct product = new SingleProduct();
                product.setSlNO(listView.getItems().size() + 1);
                listView.getItems().add(product);
            } else {
                mainApp.snackBar("INFO", "Can not add more than "
                        + limit + " items", "green");
            }
        } catch (Exception e) {
            e.printStackTrace();
            AlertMaker.showErrorMessage(e);
        }
    }

    @FXML
    void handleCalculate() {


        if (isNewBill) {
            int num = 1;
            String start = "K-";
            if (isManual.isSelected()) {
                if (manualDate.getValue() == null) {
                    mainApp.snackBar("INFO", "Select a date", "red");
                    return;
                }
                date = Date.from((manualDate.getValue()).atTime(Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.MILLISECOND)
                        .atZone(ZoneId.systemDefault()).toInstant());
            } else {
                date = new Date();
            }
            invoice = start + new SimpleDateFormat("ddMMyy/").format(date) + String.format("%03d", num);
            while (DatabaseHelper.ifInvoiceExist(invoice)) {
                invoice = start + new SimpleDateFormat("ddMMyy/").format(date) + String.format("%03d", num);
                num++;
            }
            billId = "Bill" + new SimpleDateFormat("yyyyMMddHHSSS").format(date) + num;
        }
        ready = false;
        ObservableList<SingleProduct> p = listView.getItems();

        if (p.size() == 0) {
            mainApp.snackBar("Info", "Fill at least One Product", "red");
            return;
        }

        String errorMsg;
        boolean errored = false;
        for (SingleProduct s : p) {
            errorMsg = s.isReady();
            if (!errorMsg.equals("s")) {
                mainApp.snackBar("Check the Following", errorMsg, "red");
                errored = true;
            }

        }

        if (errored) {
            bill = null;
            return;
        }


        ObservableList<Product> products = FXCollections.observableArrayList();

        int slNo = 1;
        for (SingleProduct s : p) {
            Product product = s.getProduct();
            product.setSl("" + slNo);
            products.add(product);
            slNo++;
        }


        bill = new Bill("" + billId
                , "" + invoice
                , "" + date.getTime()
                , "" + customer.getName()
                , "" + customer.getId()
                , "" + customer.getStreetAddress() + "\n" + customer.getCity() + "\n" + customer.getState()
                , "" + customer.getPhone()
                , "" + customer.getGstIn()
                , products
                , "" + mainApp.getUser().getUserName());

        totalAmount.setText(bill.getTotalAmount());
        ready = true;
    }

    @FXML
    void handleBillSubmit() {

        handleCalculate();
        if (bill == null) return;

        if (ready && AlertMaker.showBill(bill, mainApp, false, BillingSystemUtils.getN(comboBills.getValue()))) {
            mainApp.addSpinner();
            boolean success = false;
            if (isNewBill)
                success = DatabaseHelper.insertNewBill(bill, tableName);
            else if (DatabaseHelper.deleteBill(billId, tableName))
                success = DatabaseHelper.insertNewBill(bill, tableName);


            if (success) {
                mainApp.snackBar("Success", bill.getInvoice() +
                        " Bill is saved successfully!", "green");
                HashSet<String> desc = preferences.getDescriptions();
                HashSet<String> hsn = preferences.getHsn();

                for (Product p : bill.getProducts()) {
                    desc.add(p.getName());
                    hsn.add(p.getHsn());
                }

                preferences.setDescriptions(desc);
                preferences.setHsn(hsn);
                Preferences.setPreference(preferences);

                mainApp.handleRefresh();

            } else {
                mainApp.snackBar("Failed", bill.getInvoice() +
                        " Bill item is not saved !", "red");
            }

            mainApp.removeSpinner();

        }

    }

    @FXML
    void handleCustomerSubmit() {
        if (comboBoxCustomer.getValue() != null && !comboBoxCustomer.getValue().isEmpty() &&
                comboBills.getValue() != null && !comboBills.getValue().isEmpty()) {
            customer = DatabaseHelper.getCustomerInfo(comboBoxCustomer.getValue());
            if (customer == null) {
                mainApp.snackBar("Info", " Customer Data Doesn't Exists.\n Choose a Valid Customer", "red");
                return;
            }
            labelBillFor.setText("" + customer.getName().toUpperCase());
            borderPane.setVisible(true);
            borderPane.setDisable(false);
            listView.setExpanded(true);
            listView.setVerticalGap(5.0);

            tableName = BillingSystemUtils.getTableName(comboBills.getValue());
            if (isNewBill) root.setTop(null);
        }
    }

    @FXML
    void handleDelete() {
        if (listView.getSelectionModel().getSelectedItems().size() != 0
                && listView.getSelectionModel().getSelectedItem() != null) {
            int i = 1;
            listView.getItems().remove(listView.getSelectionModel().getSelectedItem());
            for (SingleProduct s : listView.getItems()) {
                s.setSlNO(i);
                i++;
            }
        } else {
            mainApp.snackBar("", "Select a row to Delete", "red");
        }
    }

    public void setBill(Bill bill, String isIGstBill) {
        isNewBill = false;
        checkBoxGST.setSelected(!bill.getGSTNo().equalsIgnoreCase("for own use"));
        toggleCustomer();
        comboBoxCustomer.getSelectionModel().select(bill.getCustomerName());
        comboBills.getSelectionModel().select(isIGstBill);
        handleCustomerSubmit();
        billId = bill.getBillId();
        invoice = bill.getInvoice();
        date = new Date(Long.parseLong(bill.getTime()));
        hBox.getChildren().remove(manualD);
        totalAmount.setText(bill.getTotalAmount());
        int i = 1;
        for (Product product : bill.getProducts()) {
            SingleProduct product1 = new SingleProduct();
            product1.setProduct(product);
            product1.setSlNO(i);
            listView.getItems().addAll(product1);
        }
    }

    private void toggleCustomer() {
        comboBoxCustomer.getItems().clear();
        if (checkBoxGST.isSelected())
            comboBoxCustomer.getItems().addAll(gst);
        else
            comboBoxCustomer.getItems().addAll(nonGst);
        TextFields.bindAutoCompletion(comboBoxCustomer.getEditor(), comboBoxCustomer.getItems());
    }

}